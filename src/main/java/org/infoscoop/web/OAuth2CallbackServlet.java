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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.oauth.OAuth;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthProblemException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.request.OAuth2Authenticator;
import org.infoscoop.request.OAuth2Message;
import org.infoscoop.request.ProxyRequest;
import org.infoscoop.service.OAuthService;

public class OAuth2CallbackServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Log log = LogFactory.getLog(JsonProxyServlet.class);

	/**
	 * Exchange an OAuth request token for an access token, and store the latter
	 * in cookies.
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		OAuth2Message msg = new OAuth2Message();
		msg.parseRequest(request);
		HttpSession session = request.getSession();

		String uid = (String) session.getAttribute("Uid");
		String code = msg.getAuthorization();
		String gadgetUrl = msg.getGadgetURL();
		String consumerName = msg.getServiceName();
        
        try {
        	// ToDo 
        	// NOT RECOMMEND Request
			OAuthConsumer consumer = OAuth2Authenticator.getConsumer(gadgetUrl, consumerName);	
			ProxyRequest proxy = new ProxyRequest(consumer.serviceProvider.accessTokenURL,"NoOperation");
			Map<String, String> params = new HashMap<String,String>();
			params.put("code", code);
			params.put("client_id",consumer.consumerKey);
			params.put("client_secret",consumer.consumerSecret);
			params.put("redirect_uri",URLEncoder.encode(consumer.callbackURL,"UTF-8"));
			params.put("grant_type","authorization_code");
			String postData = msg.buildPostBody(params);
			proxy.setRequestHeader("Content-type", "application/x-www-form-urlencoded");

			proxy.setReqeustBody( new ByteArrayInputStream( postData.getBytes()));
			proxy.executePost();
			
			final String responseBody = proxy.getResponseBodyAsString("UTF-8");
			final String contentType = proxy.getResponseHeader("Content-Type");
            Long validityPeriodUTC = null;

            if(contentType.startsWith("text/plain")){	            	
            	//for facebook
            	msg.parseQuery('?'+responseBody);
            }else if(contentType.startsWith("application/json")){
            	// for google
            	msg.parseJSON(responseBody);
            }else{
            	log.warn("invalid content-type :" + contentType);
            	msg.parseJSON(responseBody);	            	
            }
        	final String expiresIn = msg.getExpiresIn();
        	if (expiresIn != null) {
        		Long expires = Long.parseLong(expiresIn);
	            Calendar cal = Calendar.getInstance();
	            cal.setTime(new Date());
	            validityPeriodUTC = cal.getTimeInMillis() + (expires*1000L);
        	}

            if (msg.getAccessToken() == null) {
           		OAuthProblemException problem = new OAuthProblemException(OAuth.Problems.PARAMETER_ABSENT);
           		problem.setParameter(OAuth.Problems.OAUTH_PARAMETERS_ABSENT, "access_token");
           		throw problem;
           	}
            
			OAuthService.getHandle().saveOAuth2Token(uid, gadgetUrl, consumerName,
					msg.getTokenType(),msg.getAuthorization(), msg.getAccessToken(),
					msg.getRefreshToken(),validityPeriodUTC);				
			
			response.setContentType("text/html");
			PrintWriter out = response.getWriter();
			out.println("<html><head><script> window.close();</script></head></html>");
			out.flush();
		} catch (Exception e) {
			OAuthService.getHandle().deleteOAuth2Token(uid, gadgetUrl,consumerName);
			log.error("unexpected error has occured.", e);
			RequestDispatcher dispatcher = request
					.getRequestDispatcher("/error.jsp");
			request.setAttribute("error_msg_id", "ms_oauthFailed");
			dispatcher.forward(request, response);
		}
	}
}