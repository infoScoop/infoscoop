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
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
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
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.account.AuthenticationService;
import org.infoscoop.account.SessionCreateConfig;
import org.infoscoop.acl.ISPrincipal;
import org.infoscoop.acl.SecurityController;
import org.infoscoop.admin.web.PreviewImpersonationFilter;
import org.infoscoop.context.UserContext;
import org.infoscoop.dao.PropertiesDAO;
import org.infoscoop.dao.SessionDAO;
import org.infoscoop.properties.InfoScoopProperties;
import org.infoscoop.service.SquareService;
import org.infoscoop.util.RSAKeyManager;

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
	public static String LOGINUSER_SUBJECT_ATTR_NAME = "loginUser";
	public static String LOGINUSER_SESSION_ID_ATTR_NAME = "SessionId";
	public static String LOGINUSER_CURRENT_SQUARE_ID_ATTR_NAME = "CurrentSquareId";

	private Collection excludePaths = new HashSet();
	private Collection<String> excludePathx = new HashSet<String>();
	private Collection redirectPaths = new HashSet();

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

		String redirectPathStr = config.getInitParameter("redirectPath");
		if(redirectPathStr != null){
			String[] pathArray = redirectPathStr.split(",");
			for(int i = 0; i < pathArray.length; i++){
				redirectPaths.add(pathArray[i].trim());
			}
		}
	}

	private String getUidFromHeader(HttpServletRequest req){
		String uidHeader = SessionCreateConfig.getInstance().getUidHeader();
		boolean uidIgnoreCase = SessionCreateConfig.getInstance().isUidIgnoreCase();

		String uid = null;

		if(uidHeader != null){
			uid = req.getHeader(uidHeader);

			if(log.isDebugEnabled()){
				log.debug("Got UID from Header : [" + uid + "]");
			}
		} else {
			uid = req.getRemoteUser();
			if(log.isDebugEnabled()){
				log.debug("Got UID from RemoteUser : [" + uid + "]");
			}
		}
		if(uid == null){
			if(log.isInfoEnabled())
				log.info("uidHeader is null");
			return null;
		}

		if("true".equalsIgnoreCase( req.getParameter(CheckDuplicateUidFilter.IS_PREVIEW ))){
			HttpSession session = req.getSession(true);
			String sessionUid = (String)session.getAttribute("Uid");
			String uidParam = req.getParameter("Uid");
			if(uidParam.equalsIgnoreCase(sessionUid)){
				uid = uidParam;
				session.setAttribute("Uid",uid );
			}
		}else if( uidIgnoreCase && uid != null )
			uid = uid.toLowerCase();

		return uid.trim();
	}

	private Map<String, String> getSquareUidFromSession(HttpServletRequest req) {
		HttpSession session = req.getSession(true);
		String uid = (String)session.getAttribute("Uid");
		String currentSquareId = (String)session.getAttribute(LOGINUSER_CURRENT_SQUARE_ID_ATTR_NAME);
		String sessionId = req.getHeader("MSDPortal-SessionId");
		boolean uidIgnoreCase = SessionCreateConfig.getInstance().isUidIgnoreCase();
		Map<String, String> resultMap = new HashMap<String, String>();

		// 管理画面プレビュー
		if("true".equalsIgnoreCase( req.getParameter(CheckDuplicateUidFilter.IS_PREVIEW ))){
			String uidParam = req.getParameter("Uid");
			String squareidParam = req.getParameter(LOGINUSER_CURRENT_SQUARE_ID_ATTR_NAME);
			if(uid.equalsIgnoreCase(uidParam) && currentSquareId.equalsIgnoreCase(squareidParam)){
				uid = uidParam;
				session.setAttribute("Uid",uid );
				session.setAttribute(LOGINUSER_CURRENT_SQUARE_ID_ATTR_NAME,currentSquareId);
			}
		}else if( uidIgnoreCase && uid != null && currentSquareId != null) {
			uid = uid.toLowerCase();
			session.setAttribute("Uid",uid );
			session.setAttribute(LOGINUSER_CURRENT_SQUARE_ID_ATTR_NAME,currentSquareId );
		}

		if(!InfoScoopProperties.getInstance().isUseMultitenantMode() || currentSquareId == null) {
			// シングルテナント時、ユーザーのデフォルトスクエアを入れる
			currentSquareId = SquareService.SQUARE_ID_DEFAULT;
		}

		resultMap.put("Uid", uid);
		resultMap.put(LOGINUSER_CURRENT_SQUARE_ID_ATTR_NAME, currentSquareId);

		if (uid == null) {
			if (sessionId != null) {
				session.setAttribute(LOGINUSER_SESSION_ID_ATTR_NAME, sessionId);
				resultMap.put("Uid", SessionDAO.newInstance().getUid(sessionId, currentSquareId));
				resultMap.put(LOGINUSER_CURRENT_SQUARE_ID_ATTR_NAME, currentSquareId);
			}
		} else if (sessionId != null) {
			String oldSessionId = (String) session.getAttribute(LOGINUSER_SESSION_ID_ATTR_NAME);
			if (oldSessionId != null && !sessionId.equals(oldSessionId)) {
				session.invalidate();
				session = req.getSession(true);
				session.setAttribute(LOGINUSER_SESSION_ID_ATTR_NAME, sessionId);
				resultMap.put("Uid", SessionDAO.newInstance().getUid(sessionId, currentSquareId));
				resultMap.put(LOGINUSER_CURRENT_SQUARE_ID_ATTR_NAME, currentSquareId);
			}
		}

		return resultMap;
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpReq = (HttpServletRequest) request;
		if(log.isDebugEnabled()){
			log.debug("Enter SessionManagerFilter form " + httpReq.getRequestURI());
		}

		if (request instanceof javax.servlet.http.HttpServletRequest) {
			HttpServletRequest httpRequest = (HttpServletRequest) request;
			HttpServletResponse httpResponse = (HttpServletResponse)response;
			
			String uid = null;
			String currentSquareId = null;
			if(SessionCreateConfig.doLogin()){
				Map<String, String> result = getSquareUidFromSession(httpReq);
				uid = result.get("Uid");
				currentSquareId = result.get(LOGINUSER_CURRENT_SQUARE_ID_ATTR_NAME);

	            if (uid != null){
	                addUidToSession(uid, request);
	            }
				if (currentSquareId != null){
					addCurrentSquareIdToSession(currentSquareId, request);
				}
				
				if(redirectPaths.contains(httpReq.getServletPath())){
					httpResponse.addCookie(new Cookie("redirect_path", httpReq.getServletPath()));
				}
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
				currentSquareId = SquareService.SQUARE_ID_DEFAULT;
				Map<String, String> result = getSquareUidFromSession(httpReq);
				if (uid == null) {
					uid = result.get("Uid");
				}
				if (uid != null) {
					addUidToSession(uid, request);
				}

				// Headeによるログイン（あとで
//				currentSquareId = getCurrentSquareIdFromHeader(httpReq);
//				if (currentSquareId == null) {
//					currentSquareId = getCurrentSquareIdFromSesssion(httpReq);
//				}
//				if (currentSquareId != null) {
//					addCurrentSquareIdToSession(currentSquareId, request);
//				}
			}

			if( uid == null ) {
				Cookie[] cookies = httpReq.getCookies();
				if( cookies != null ) {
					for( Cookie cookie : cookies ) {
						if( cookie.getName().equals("portal-credential")) {
							int keepPeriod = 7;
							try {
								keepPeriod = Integer.parseInt( PropertiesDAO.newInstance()
										.findProperty("loginStateKeepPeriod", currentSquareId).getValue());
							} catch( Exception ex ) {
								log.warn("",ex );
							}

							if( keepPeriod <= 0 ) {
								Cookie credentialCookie = new Cookie("portal-credential","");
								credentialCookie.setMaxAge( 0 );
								credentialCookie.setPath("/");
								httpResponse.addCookie( credentialCookie );

								log.info("clear auto login credential ["+credentialCookie.getValue()+"]");
							} else {
								try {
									uid = tryAutoLogin( cookie );
									httpReq.getSession().setAttribute("Uid",uid );

									log.info("auto login success.");
								} catch( Exception ex ) {
									log.info("auto login failed.",ex );
								}
							}
						}
					}
				}
			}

			if( uid == null && SessionCreateConfig.doLogin() && !isExcludePath(httpReq.getServletPath())) {
				String requestUri = httpReq.getRequestURI();
				String loginUrl = requestUri.lastIndexOf("/manager/") > 0 ?
					requestUri.substring( 0,requestUri.lastIndexOf("/"))+"/../login.jsp" : "login.jsp";

				httpResponse.sendRedirect(loginUrl);
				return;
			}

			if(log.isInfoEnabled())log.info("### Access from user " + uid + " to " + httpReq.getRequestURL() );

			// fix #42
//			setUserInfo2Cookie(httpReq, (HttpServletResponse)response, uid);
			HttpSession session = httpRequest.getSession();

			Subject loginUser = (Subject)session.getAttribute(LOGINUSER_SUBJECT_ATTR_NAME);

			if(loginUser == null || ( isChangeLoginUser(uid, loginUser) && !(session instanceof PreviewImpersonationFilter.PreviewHttpSession) )){
				if( !SessionCreateConfig.getInstance().hasUidHeader() && uid != null ) {
					AuthenticationService service= AuthenticationService.getInstance();
					try {
						if (service != null)
							loginUser = service.getSubject(uid);
					} catch (Exception e) {
						log.error("",e);
					}
				}

				if( loginUser == null || isChangeLoginUser( uid, loginUser )) {
					loginUser = new Subject();
					loginUser.getPrincipals().add(new ISPrincipal(ISPrincipal.UID_PRINCIPAL, uid));
				}

				setLoginUserName(httpRequest, loginUser);

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
			if(httpRequest.getHeader("X-IS-TIMEZONE") != null){
				int timeZoneOffset = 0;
				try{
					timeZoneOffset = Integer.parseInt(httpRequest.getHeader("X-IS-TIMEZONE"));
				}catch(NumberFormatException e){
					if(log.isDebugEnabled())
						log.debug(httpRequest.getHeader("X-IS-TIMEZONE"),e);
				}finally{
					UserContext.instance().getUserInfo().setClientTimezoneOffset(timeZoneOffset);
				}				
			}

			// set current square id
			if(currentSquareId != null) {
				UserContext.instance().getUserInfo().setCurrentSquareId(currentSquareId);
			}
		}
		chain.doFilter(request, response);

		UserContext.destroy();
		
		if(log.isDebugEnabled()){
			log.debug("Exit SessionManagerFilter　form " + httpReq.getRequestURI());
		}

	}

	/**
	 *
	 * @param httpRequest
	 * @param loginUser
	 */
	private void setLoginUserName(HttpServletRequest httpRequest, Subject loginUser) {
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

	private void addCurrentSquareIdToSession(String currentSquareId, ServletRequest request) {
		HttpSession session = ((HttpServletRequest) request).getSession();
		if (session.getAttribute(LOGINUSER_CURRENT_SQUARE_ID_ATTR_NAME) != null)
			return;
		session.setAttribute(LOGINUSER_CURRENT_SQUARE_ID_ATTR_NAME, currentSquareId);
		if(log.isInfoEnabled()){
			log.info("Add SquareId To session : [" + currentSquareId + "]");
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

	private String tryAutoLogin( Cookie cookie ) throws Exception {
		String credentialStr = cookie.getValue();

		try {
			String[] credentialPair = credentialStr.split(":");
			String portalUid = new String(
					Base64.decodeBase64( credentialPair[0].getBytes("UTF-8")),"UTF-8");
			String portalPassword = RSAKeyManager.getInstance().decrypt( credentialPair[1] );

			AuthenticationService service = AuthenticationService.getInstance();
			if (service == null)
				throw new Exception(
						"No bean named \"authenticationService\" is defined."
								+ " When loginAuthentication property is true,"
								+ " authenticationService must be defined.");

			service.login( portalUid,portalPassword );

			portalUid = portalUid.trim();
			return portalUid;
		} catch( Exception ex ) {
//			log.info("Auto Login Failed by ["+credentialStr+"]");

			throw ex;
		}
	}
}
