/* infoScoop OpenSource
 * Copyright (C) 2010 Beacon IT Inc.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0-standalone.html>.
 */

/**
 *
 */
package org.infoscoop.web;

import java.io.IOException;
import java.net.URLEncoder;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.security.auth.Subject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.account.AuthenticationService;
import org.infoscoop.account.DomainManager;
import org.infoscoop.account.SessionCreateConfig;
import org.infoscoop.acl.ISAdminPrincipal;
import org.infoscoop.acl.ISPrincipal;
import org.infoscoop.acl.SecurityController;
import org.infoscoop.admin.web.PreviewImpersonationFilter;
import org.infoscoop.dao.DomainDAO;
import org.infoscoop.dao.PropertiesDAO;
import org.infoscoop.dao.model.Domain;
import org.infoscoop.dao.model.Group;
import org.infoscoop.dao.model.Properties;
import org.infoscoop.dao.model.User;
import org.infoscoop.request.ProxyRequest;
import org.infoscoop.service.UserService;

/**
 * The filter which manages the login state.
 * We set the login module by a loginAuthentication that is in the initialization parameter.
 * <ul>
 *    <li>true:We perform the certification for the MSD login certification module.
 *    <li>false: In the case of Single-Sign-On environment.
 * </li>
 * @author dou
 * @author iwata
 * @author hr-endoh
 */
public class SessionManagerFilter implements Filter {
	private static Log log = LogFactory.getLog(SessionManagerFilter.class);
	public static String LOGINUSER_ID_ATTR_NAME = "Uid";
	public static String LOGINUSER_NAME_ATTR_NAME = "loginUserName";
	public static String LOGINUSER_DOMAIN_NAME_ATTR_NAME = "Domain";
	public static String LOGINUSER_SUBJECT_ATTR_NAME = "loginUser";

	private Collection excludePaths = new HashSet();
	private Collection<String> excludePathx = new HashSet<String>();
	private boolean withoutCotextPath = false;

	public void init(FilterConfig config) throws ServletException {

		String excludePathStr = config.getInitParameter("excludePath");
		if(excludePathStr != null){
			String[] pathArray = excludePathStr.split(",");
			for(int i = 0; i < pathArray.length; i++){
				String path = pathArray[i].trim();
				if( path.endsWith("*")) {
					excludePathx.add( path.substring(0,path.length() -1 ));
				} else {
					excludePaths.add( path );
				}
			}
		}
		String withoutCotextPath = config.getInitParameter("withoutCotextPath");
		if (withoutCotextPath != null)
			this.withoutCotextPath = Boolean.valueOf(withoutCotextPath);
	}

	private String getUidFromHeader(HttpServletRequest req){
		String uidHeader = SessionCreateConfig.getInstance().getUidHeader();
		String _uid = null;

		if(uidHeader != null){
			_uid = req.getHeader(uidHeader);

			if(log.isDebugEnabled()){
				log.debug("Got UID from Header : [" + _uid + "]");
			}
		} else {
			_uid = req.getRemoteUser();
			if(log.isDebugEnabled()){
				log.debug("Got UID from RemoteUser : [" + _uid + "]");
			}
		}
		if(_uid == null){
			if(log.isInfoEnabled())
				log.info("uidHeader is null");
			return null;
		}

		String uid = _uid.trim().toLowerCase();
				
		return _uid;
	}

	private String getUidFromSession(HttpServletRequest req){
		HttpSession session = req.getSession(true);
		String uid = (String)session.getAttribute("Uid");
				
		return uid;
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpReq = (HttpServletRequest) request;
		if(log.isDebugEnabled()){
			log.debug("Enter SessionManagerFilter form " + httpReq.getRequestURI());
		}
		
		String[] requestURI = httpReq.getRequestURI().split("/");
		if (httpReq.getRequestURI()
				.indexOf(httpReq.getContextPath() + "/admin") == 0
				|| httpReq.getRequestURI().indexOf(
						httpReq.getContextPath() + "/gapps_openid_login.jsp") == 0
				|| requestURI.length > 0
				&& "notready.jsp".equals(requestURI[requestURI.length - 1])) {
			chain.doFilter(request, response);
			return;
		}

		if (request instanceof javax.servlet.http.HttpServletRequest) {
			HttpServletRequest httpRequest = (HttpServletRequest) request;
			HttpServletResponse httpResponse = (HttpServletResponse)response;
			
			String uid = null;
			if(SessionCreateConfig.doLogin()){
				uid = getUidFromSession(httpReq);
				
				if( uid == null && !isExcludePath(httpReq.getServletPath())){
					if (httpRequest.getHeader("MSDPortal-Ajax") != null) {
						if(log.isInfoEnabled())
							log.info("session timeout has occured. logoff automatically.");
						httpResponse.setHeader(HttpStatusCode.HEADER_NAME,
								HttpStatusCode.MSD_SESSION_TIMEOUT);
						httpResponse.sendError(500);
						return;
					}
				}
			}else{
				uid = getUidFromHeader(httpReq);
				if (uid == null)
					uid = getUidFromSession(httpReq);
				if (uid != null) {
					addUidToSession(uid, request);
				}
			}
			
			
			if (uid == null && SessionCreateConfig.doLogin()
					&& !isExcludePath(httpReq.getServletPath())) {
				String loginUrl = this.withoutCotextPath ? "" : httpReq
						.getContextPath();
				loginUrl += "/login.jsp?url="
						+ URLEncoder.encode(httpReq.getRequestURI() + "?"
								+ httpReq.getQueryString(), "UTF-8");
				httpResponse.sendRedirect(loginUrl);
				return;
			}
			
			if(log.isInfoEnabled())log.info("### Access from user " + uid + " to " + httpReq.getRequestURL() );

			// fix #42
//			setUserInfo2Cookie(httpReq, (HttpServletResponse)response, uid);
			HttpSession session = httpRequest.getSession();


			Subject loginUser = (Subject)session.getAttribute(LOGINUSER_SUBJECT_ATTR_NAME);
			if(loginUser == null || ( isChangeLoginUser(uid, loginUser) && !(session instanceof PreviewImpersonationFilter.PreviewHttpSession) )){
				AuthenticationService service= AuthenticationService.getInstance();
				try {
					if (service != null){
						String domainName = (String) session.getAttribute(LOGINUSER_DOMAIN_NAME_ATTR_NAME);
						loginUser = service.getSubject(uid, domainName);
						setLoginUserName(httpRequest, loginUser);
						Domain domain = DomainDAO.newInstance().getByName(domainName);
						User user = UserService.getHandle().getUser(uid,
								domain.getId());
						if( (user != null && user.isAdministrator())){
							Principal adminPrincipal = new ISAdminPrincipal();
							loginUser.getPrincipals().add(adminPrincipal);
						}
						Set<Group> groups = user.getGroups();
						for (Group group : groups) {
							ISPrincipal groupPrincipal = new ISPrincipal(
									ISPrincipal.ORGANIZATION_PRINCIPAL, group
									.getEmail());
							groupPrincipal.setDisplayName(group.getName());
							loginUser.getPrincipals().add(groupPrincipal);
						}
					}
				} catch (Exception e) {
					log.error("",e);
					try {
						if( isAppsAdmin(uid, httpRequest) ){
							loginUser = new Subject();
							ISPrincipal up = new ISPrincipal(ISPrincipal.UID_PRINCIPAL, uid);
							String userName = (String)session.getAttribute(LOGINUSER_NAME_ATTR_NAME);
							up.setDisplayName(userName);
							loginUser.getPrincipals().add(up);

							Principal adminPrincipal = new ISAdminPrincipal();
							loginUser.getPrincipals().add(adminPrincipal);

							Domain domain = DomainDAO.newInstance().getByName((String) session.getAttribute(LOGINUSER_DOMAIN_NAME_ATTR_NAME));
							if(domain != null){
								ISPrincipal domainPrincipal = new ISPrincipal(ISPrincipal.DOMAIN_PRINCIPAL, domain.getId().toString());
								domainPrincipal.setDisplayName(domain.getName());
								loginUser.getPrincipals().add(domainPrincipal);
							}

							session.setAttribute(LOGINUSER_SUBJECT_ATTR_NAME, loginUser);
							httpResponse.sendRedirect("manager/user/setup");
							return;
						}else{
							httpResponse.sendRedirect("notready.jsp");
							return;
						}
					} catch (Exception e1) {
						log.error("",e1);
					}
				}

				for(Map.Entry entry : SessionCreateConfig.getInstance().getRoleHeaderMap().entrySet()){
					String headerName = (String)entry.getKey();
					String roleType = (String)entry.getValue();
					Enumeration headerValues = httpRequest.getHeaders(headerName);
					while(headerValues.hasMoreElements()){
						String headerValue = (String)headerValues.nextElement();
						try {
							Set principals = loginUser.getPrincipals();
							principals.add( new ISPrincipal(roleType, headerValue));
//							loginUser.getPrincipals().add( roleType.getConstructor(paramTypes).newInstance(initArgs) );
							if(log.isInfoEnabled())log.info("Set principal to login subject: " + roleType + "=" + headerValue);
						} catch (IllegalArgumentException e) {
							log.error("",e);
						} catch (SecurityException e) {
							log.error("",e);
						}
					}
					
				}
				session.setAttribute(LOGINUSER_SUBJECT_ATTR_NAME, loginUser);
			}
			SecurityController.registerContextSubject(loginUser);

			//TODO:This value is set by OpenIDFilter.
			if(loginUser != null)
				for(ISPrincipal p :loginUser.getPrincipals(ISPrincipal.class))
					if(ISPrincipal.DOMAIN_PRINCIPAL == p.getType())
						DomainManager.registerContextDomainId(Integer.valueOf(p.getName()));
			
		}

		
		chain.doFilter(request, response);

		//TODO:
		DomainManager.clearContextDomainId();
		if(log.isDebugEnabled()){
			log.debug("Exit SessionManagerFilter　form " + httpReq.getRequestURI());
		}

	}

	/**
	 *
	 * @param httpRequest
	 * @param loginUser
	 */
	private void setLoginUserName(HttpServletRequest httpRequest,
			Subject loginUser) {
		String loginUserName = null;
		String usernameHeader = SessionCreateConfig.getInstance().getUsernameHeader();
		if(usernameHeader != null)
			loginUserName = httpRequest.getHeader(usernameHeader);
		
		if(loginUserName == null)
			loginUserName = getUserNameFromSubject(loginUser);
		
		httpRequest.getSession().setAttribute(LOGINUSER_NAME_ATTR_NAME, (loginUserName != null) ? loginUserName : "");
	}

	private String getUserNameFromSubject(Subject loginUser){
		Collection principals = loginUser.getPrincipals(ISPrincipal.class);

		for(Iterator it = principals.iterator(); it.hasNext();){
			ISPrincipal p = (ISPrincipal)it.next();
			if(ISPrincipal.UID_PRINCIPAL.equals(p.getType())){
				ISPrincipal isp = (ISPrincipal)p;
				return isp.getDisplayName();
			}
		}
		return null;
	}

	private boolean isChangeLoginUser(String uid, Subject loginUser) {
		Set principals = loginUser.getPrincipals(ISPrincipal.class);
		for(Iterator it = principals.iterator(); it.hasNext();){
			ISPrincipal p = (ISPrincipal)it.next();
			if(ISPrincipal.UID_PRINCIPAL.equals(p.getType())){
				if(uid != null && !uid.equals(p.getName())) return true;
			}
		}
		return false;
	}

	/* fix #42
	private void setUserInfo2Cookie(HttpServletRequest request, HttpServletResponse response, String uid) {

        boolean isExistUid = false;
        Cookie[] cookies = request.getCookies();
        if(cookies != null){
            for(int i = 0; i < cookies.length; i++){
                if("Uid".equals(cookies[i].getName()) && cookies[i].getValue() != null && !"".equals(cookies[i].getValue())){
                    isExistUid = true;
                    break;
                }
            }
        }
        if(!isExistUid && uid != null){
            HttpServletResponse httpResponse = (HttpServletResponse)response;
            httpResponse.addCookie(new Cookie("Uid", uid));
            if(usernameHeader != null){
                String userName = request.getHeader(usernameHeader);
                if(userName != null){
                    httpResponse.addCookie(new Cookie("UserName", userName));
                }
            }

        }

	}
	*/

	private void addUidToSession(String uid, ServletRequest request) {
		HttpSession session = ((HttpServletRequest) request).getSession();
		if (session.getAttribute("Uid") != null)
			return;
		session.setAttribute("Uid", uid);
		if(log.isInfoEnabled()){
			log.info("Add Uid To session : [" + uid + "]");
		}
	}

	public void destroy() {
	}

	private boolean isExcludePath( String path ) {
		if( excludePaths.contains( path ))
			return true;

		for( String p : excludePathx ) {
			if( path.startsWith( p ))
				return true;
		}

		return false;
	}

	private boolean isAppsAdmin(String uid, HttpServletRequest request) throws Exception{
		HttpClient http = new HttpClient();
		Properties property = PropertiesDAO.newInstance().findProperty("appsServiceURL");
		String url = property.getValue();
		if (!url.endsWith("/"))
			url += "/";
		String response = singedRequest(url + "checkadmin", request);
		return Boolean.valueOf(response);
	}
	
	private String singedRequest(String url, HttpServletRequest request)
			throws Exception {
		ProxyRequest proxyRequest = null;
		try {
			proxyRequest = new ProxyRequest(url, "NoOperation");
			proxyRequest.setLocales(request.getLocales());
			proxyRequest.setPortalUid((String) request.getSession()
					.getAttribute("Uid"));
			proxyRequest.setTimeout(ProxyServlet.DEFAULT_TIMEOUT);
			proxyRequest.putRequestHeader("authType", "signed");

			int statusCode = proxyRequest.executeGet();

			if (statusCode != 200)
				throw new Exception("url=" + proxyRequest.getProxy().getUrl()
						+ ", statucCode=" + statusCode);

			if (log.isInfoEnabled())
				log.info("gadget url : " + proxyRequest.getProxy().getUrl());

			return proxyRequest.getResponseBodyAsStringWithAutoDetect();
		} finally {
			if (proxyRequest != null)
				proxyRequest.close();
		}
	}
}
