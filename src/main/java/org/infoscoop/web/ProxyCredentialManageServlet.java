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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.commons.collections.MultiHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.dao.AuthCredentialDAO;
import org.infoscoop.dao.OAuthConsumerDAO;
import org.infoscoop.dao.model.AuthCredential;
import org.infoscoop.dao.model.OAuthConsumerProp;
import org.infoscoop.dao.model.OAuthGadgetUrl;
import org.infoscoop.service.AuthCredentialService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProxyCredentialManageServlet extends HttpServlet {

	/**
	 *
	 */
	private static final long serialVersionUID = -3566689441498752103L;

	private static Log log = LogFactory.getLog(ProxyCredentialManageServlet.class);

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String command = request.getParameter("command");
		String uid = (String) request.getSession().getAttribute("Uid");
		try {
			if("list".equals(command)){
				response.setHeader("Content-Type", "text/xml; charset=UTF-8");
				List<AuthCredential> credentialList = AuthCredentialDAO.newInstance().select(uid);
				List<OAuthConsumerProp> consumers = OAuthConsumerDAO.newInstance().getConsumersByUid(uid);
				List<String> idList = new ArrayList<String>();
				try {
					JSONArray json = new JSONArray();
					for(Iterator<AuthCredential> it = credentialList.iterator(); it.hasNext();){
						AuthCredential c = (AuthCredential)it.next();
						json.put(c.toJSON());
					}
					
					JSONObject oauthJSON = new JSONObject();					
					for(Iterator<OAuthConsumerProp> i = consumers.iterator(); i.hasNext();){
						OAuthConsumerProp consumerProp = i.next();
						String id = consumerProp.getId();
						if(!idList.contains(id)){
							idList.add(id);
							oauthJSON.put("service_name", consumerProp.getServiceName());
							oauthJSON.put("authType", "OAuth");
							oauthJSON.put("description", consumerProp.getDescription());
							Set<OAuthGadgetUrl> gadgetUrls = consumerProp.getOAuthGadgetUrl();
							JSONArray gadgetUrlArr = new JSONArray();
							for(Iterator<OAuthGadgetUrl> j = gadgetUrls.iterator(); j.hasNext();){
								gadgetUrlArr.put(j.next().getGadgetUrl());
							}
							oauthJSON.put("gadgetUrls", gadgetUrlArr);
						}
					}
					json.put(oauthJSON);
					
					response.getWriter().write(json.toString());
					response.getWriter().flush();
				} catch (JSONException e) {
					log.error("",e);
					response.sendError(500);
				}
			}else if("try".equals(command)){
				response.setHeader("Content-Type", "text/xml; charset=UTF-8");

				String url = request.getParameter("url");
				String authType = request.getParameter("authType");
				String authCredentialId = AuthCredentialService.getHandle().detectCredential(uid, authType, url);
				if(authCredentialId != null){
					response.getWriter().write(authCredentialId);
				}else{
					response.getWriter().write("cannot_detect_credential");
				}
				response.getWriter().flush();

			}else if("add".equals(command)){
				response.setHeader("Content-Type", "text/xml; charset=UTF-8");
				String authType = request.getParameter("authType");
				String authUid = request.getParameter("authUid");
				String authPasswd = request.getParameter("authPasswd");
				String authDomain = request.getParameter("authDomain");
				String url = request.getParameter("url");

				MultiHashMap headerMap = new MultiHashMap();
				Enumeration<String> headerNames = request.getHeaderNames();
				while (headerNames.hasMoreElements()) {
					String headerName = headerNames.nextElement();
					Enumeration<String> headers = request
							.getHeaders(headerName);
					while (headers.hasMoreElements()) {
						headerMap.put(headerName, headers.nextElement());
					}
				}

				String authCredentialId = AuthCredentialService.getHandle()
						.addCredential(uid, authType, authUid, authPasswd,
								authDomain, url, headerMap);
				if(authCredentialId != null){
					response.getWriter().write(authCredentialId);
				}else{
					response.getWriter().write("add_credential_failed");
				}
				response.getWriter().flush();
			}else if("rst".equals(command)){
				response.setHeader("Content-Type", "text/xml; charset=UTF-8");
				String credentialId = request.getParameter("id");
				String authPasswd = request.getParameter("authPasswd");
				String[] urlList = request.getParameterValues("url");
				Collection errorUrlList;
				errorUrlList = AuthCredentialService.getHandle().resetPassword(uid, credentialId, authPasswd, urlList);

				JSONArray json = new JSONArray();
				for(Iterator it = errorUrlList.iterator(); it.hasNext();){
					json.put((String)it.next());
				}
				response.getWriter().write(json.toString());
				//response.getWriter().write("reset_password_success");
				response.getWriter().flush();
			}else if("frst".equals(command)){
				String credentialId = request.getParameter("id");
				String authPasswd = request.getParameter("authPasswd");
				AuthCredentialService.getHandle().forceResetPassword(uid, credentialId, authPasswd);

			}else if("del".equals(command)){
				String credentialId = request.getParameter("id");
				AuthCredentialService.getHandle().removeCredential(uid, credentialId);
			}else{
				response.sendError(500);
			}
		} catch (Exception e) {
			log.error("",e);
			response.sendError(500, e.getMessage());
		}
	}


}
