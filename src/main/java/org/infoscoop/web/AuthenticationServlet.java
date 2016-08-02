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

package org.infoscoop.web;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.account.AuthenticationException;
import org.infoscoop.account.AuthenticationService;
import org.infoscoop.account.IAccount;
import org.infoscoop.context.UserContext;
import org.infoscoop.dao.PropertiesDAO;
import org.infoscoop.dao.model.AuthCredential;
import org.infoscoop.service.AuthCredentialService;
import org.infoscoop.service.PropertiesService;
import org.infoscoop.service.SquareService;
import org.infoscoop.util.RSAKeyManager;

public class AuthenticationServlet extends HttpServlet {

	private static Log log = LogFactory.getLog(AuthenticationServlet.class);

	private boolean isDenyEmptyPassword;
	private String logoutUrl;
	private String loginPath = "login.jsp";

	private static final long serialVersionUID = 1646514470595445974L;

	public void init(ServletConfig conf) throws ServletException {
		String denyEmptyPassword = conf.getInitParameter("denyEmptyPassword");
		if(denyEmptyPassword != null){
			isDenyEmptyPassword = Boolean.valueOf(denyEmptyPassword).booleanValue();
		}
		String logoutUrlParam = conf.getInitParameter("logoutUrl");
		if(logoutUrlParam != null && !"".equals(logoutUrlParam.trim())){
			logoutUrl = logoutUrlParam;
		}
		String loginUrlParam = conf.getInitParameter("loginUrl");
		if(loginUrlParam != null && !"".equals(loginUrlParam.trim())){
			loginPath = loginUrlParam;
		}
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String url = request.getRequestURI();

		// process to logout
		if( url.endsWith("/logout")){
			// get default square
			String logoutUrlStr = "index.jsp";
			String squareId = UserContext.instance().getUserInfo().getCurrentSquareId();

			request.getSession().invalidate();

			Cookie credentialCookie = new Cookie("portal-credential","");
			credentialCookie.setMaxAge(0);
			credentialCookie.setPath("/");
			response.addCookie(credentialCookie);

			if(logoutUrl != null && squareId != null) {
				logoutUrlStr = logoutUrl + "?squareId=" +squareId;
			}

			response.sendRedirect(logoutUrlStr);
			return;
		}

		//doPost(request, response);
		response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String action = ((HttpServletRequest)request).getPathInfo();
		String uid = request.getParameter("uid");
		if (uid != null) {
			uid = uid.trim();
		}
		String password = request.getParameter("password");
		if(password != null){
			password = password.trim();
		}
		String new_password = request.getParameter("new_password");
		if(new_password != null){
			new_password = new_password.trim();
		}
		String squareId = (String)request.getAttribute("squareId");
		if(squareId != null){
			squareId = squareId.trim();
		}

		if(log.isDebugEnabled()){
			log.debug("uid=" + uid + ",password=" + password);
		}
		String errorPath = loginPath;
		if(squareId != null && squareId.length() > 0) {
			errorPath = loginPath + squareId + "/";
		}

		if("/changePassword".equals(action))
			errorPath = "changePassword.jsp";
		
		HttpSession session = request.getSession();
		try{
			AuthenticationService service = AuthenticationService.getInstance();
			if(service == null){
				log.error("No bean named \"authenticationService\" is defined."
						+ " When loginAuthentication property is true,"
						+ " authenticationService must be defined.");
				session.setAttribute("errorMsg", "ms_authServiceAccessFailed");
				((HttpServletResponse)response).sendRedirect(request.getContextPath() + "/" + errorPath);
				return;
			}

			if(isDenyEmptyPassword&&"".equals(password)){
				session.setAttribute("errorMsg", "ms_noInputPassword");
				((HttpServletResponse)response).sendRedirect(request.getContextPath() + "/" + errorPath);
				return;
			}

			if("/changePassword".equals(action)){
				if(isDenyEmptyPassword && "".equals( new_password )){
					session.setAttribute("errorMsg", "ms_noInputPassword");
					((HttpServletResponse)response).sendRedirect(request.getContextPath() + "/" + errorPath);
					return;
				}
				
				service.changePassword(uid, new_password, password);

				session.setAttribute("errorMsg", "ms_passwordChanged");
				String loginUrl = "/" + loginPath;
				if(squareId != null && squareId.length() > 0) {
					loginUrl = loginUrl + squareId + "/";
				}
				((HttpServletResponse)response).sendRedirect(request.getContextPath() + loginUrl);
				return;
			}else{
				service.login(uid, password);
				
				request.getSession().setAttribute("Uid",uid );

				IAccount account = service.getAccountManager().getUser(uid);
				if(squareId == null || squareId.equals("") || squareId.length() < 0)
					squareId = account.getDefaultSquareId();

				//request.getSession().setAttribute("CurrentSquareId", squareId);
				//request.getSession().setAttribute(AuthenticationServlet.TMP_LOGINUSER_SUBJECT_ATTR_NAME, loginUser );
				String authType = PropertiesService.getHandle().getProperty("loginCredentialAuthType");
				if(authType != null){
					authType = authType.trim().toLowerCase();
					if(!"".equals(authType))
						AuthCredentialService.getHandle().addLoginCredential(uid, authType, password, null);
					else{
						AuthCredential c = AuthCredentialService.getHandle().getLoginCredential(uid);
						if(c != null)
							AuthCredentialService.getHandle().removeCredential(c);
					}
				}

				int keepPeriod = 7;
				try {
					keepPeriod = Integer.parseInt( PropertiesDAO.newInstance().findProperty("loginStateKeepPeriod", SquareService.SQUARE_ID_DEFAULT).getValue());
				} catch( Exception ex ) {
					log.warn("",ex );
				}

				if( keepPeriod > 0 ) {
					String saveLoginState = request.getParameter("saveLoginState");
					if("on".equalsIgnoreCase( saveLoginState )) {
						Cookie credentialCookie = new Cookie(
							"portal-credential",getCredentialString( uid,password ) );
						credentialCookie.setPath("/");
						credentialCookie.setMaxAge( keepPeriod*24*60*60 );
						response.addCookie( credentialCookie );
					}
				}
			}
			String redirectPath = "/index.jsp";
			Cookie[] cookies = request.getCookies();
			for(int i = 0; i < cookies.length; i++){
				if("redirect_path".equals(cookies[i].getName())){
					redirectPath = cookies[i].getValue();
					break;
				}
			}
			((HttpServletResponse)response).sendRedirect(request.getContextPath() + redirectPath);
		}catch (AuthenticationException e){
			String logMsg = "authentication failed. ";
			log.error(logMsg, e);
			
			String resourceId = e.getResourceId();
			session.setAttribute("errorMsg", (resourceId != null)? resourceId : "ms_invalidUsernameOrPassword");
			//getServletContext().getRequestDispatcher(errorPath).forward(request, response);
			((HttpServletResponse)response).sendRedirect(request.getContextPath() + "/" + errorPath);
		} catch (Exception e) {
			String logMsg = "unexpected error occured. ";
			log.error(logMsg, e);
			session.setAttribute("errorMsg", "ms_authServiceAccessFailed");
			//getServletContext().getRequestDispatcher(errorPath).forward(request, response);
			((HttpServletResponse)response).sendRedirect(request.getContextPath() + "/" + errorPath);
		}
	}

	private String getCredentialString( String uid,String password ) throws Exception {
		String credentialString = new String( Base64.encodeBase64( uid.getBytes("UTF-8")))
			+ ":" +RSAKeyManager.getInstance().encrypt( password );
		return credentialString;
	}
}
