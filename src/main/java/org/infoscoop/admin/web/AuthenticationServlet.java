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

package org.infoscoop.admin.web;

import java.io.IOException;
import java.security.AccessControlException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AuthenticationServlet extends HttpServlet {
	private static Log logger = LogFactory.getLog(AuthenticationServlet.class);
//	private MSDService service;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = "jp.co.beacon_it.msd.admin.AuthenticationServlet"
			.hashCode();

	private String m_userid;
	private String m_password;

	public void init(ServletConfig conf) throws ServletException {


		m_userid = conf.getInitParameter("userid");
		m_password = conf.getInitParameter("password");
	}
	
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		//doPost(request, response);
		response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
	}
	
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		//Authenticator a = AuthenticatorManager.getInstance().getAuthenticator();
		
		String action = ((HttpServletRequest)request).getPathInfo();
		String uid = request.getParameter("uid");
		String password = request.getParameter("password");
		if(password != null){
			password = password.trim();
		}
		String new_password = request.getParameter("new_password");
		if(new_password != null){
			new_password = new_password.trim();
		}
		
		if(logger.isDebugEnabled()){
			logger.debug("uid=" + uid + ",password=" + password);
		}
		String errorPath = "/admin/login.jsp";
		try{
//			if("/changePassword".equals(action)){
//				errorPath = "/changePassword.jsp";
//				changePassword(uid, password, new_password);
//			}else{
				login(request, uid, password);
//			}
			String redirectPath = "/admin/index.jsp";
			Cookie[] cookies = request.getCookies();
			for(int i = 0; i < cookies.length; i++){
				if("redirect_path".equals(cookies[i].getName())){
					redirectPath = cookies[i].getValue();
					break;
				}
			}
			((HttpServletResponse)response).sendRedirect(request.getContextPath() + redirectPath);
		} catch (AccessControlException e) {
			logger.error(e);
			HttpSession session = request.getSession();
			session.setAttribute("errorMsg", e.getMessage());
			//getServletContext().getRequestDispatcher(errorPath).forward(request, response);
			((HttpServletResponse)response).sendRedirect(request.getContextPath() + errorPath);
		} catch (Exception e) {
			String logMsg = "Unexpected error occurred. ";
			logger.error(logMsg, e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,logMsg);
		}
	}
	
	private void login(HttpServletRequest request, String uid, String password){

		String[] _userid = null;
		if (m_userid != null)
			_userid = m_userid.split(",");
		String[] _password = null;
		if (m_password != null)
			_password = m_password.split(",");

		boolean success = false;
		for (int i = 0; i < _userid.length; i++) {
			if (uid.equals(_userid[i]) && password.equals(_password[i]))
				success = true;
		}

		if (!success)
			throw new AccessControlException("Failed to login.");

		HttpSession session = request.getSession();
		session.setAttribute("Uid", uid);
	}
	
	private void changePassword(String uid, String password, String newPassword){

	}

}
