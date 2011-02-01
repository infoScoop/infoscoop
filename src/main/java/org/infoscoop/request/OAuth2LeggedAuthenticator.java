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

package org.infoscoop.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthException;
import net.oauth.OAuthMessage;
import net.oauth.client.OAuthClient;
import net.oauth.client.httpclient3.HttpClient3;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.URI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.dao.OAuth2LeggedConsumerDAO;
import org.infoscoop.dao.model.OAuth2LeggedConsumer;
import org.infoscoop.util.RequestUtil;

public class OAuth2LeggedAuthenticator implements Authenticator {
	public static final OAuthClient CLIENT = new OAuthClient(new HttpClient3());
	
	private static Log log = LogFactory.getLog(OAuth2LeggedAuthenticator.class);
	
	private static Map<String, OAuthConsumer> consumers = new HashMap<String, OAuthConsumer>();
	
	private static Pattern hasXOAuthRequestorId = Pattern.compile("([?&]xoauth_requestor_id=)[^&]+(&|$)");
	
	public OAuth2LeggedAuthenticator(){
	}
	
	public void doAuthentication(HttpClient client, ProxyRequest request,
			HttpMethod method, String uid, String pwd)
			throws ProxyAuthenticationException {
		ProxyRequest.OAuthConfig oauthConfig = request.getOauthConfig();
		String serviceName;
		if(oauthConfig != null)
			serviceName = oauthConfig.serviceName;
		else
			serviceName = request.getRequestHeader("serviceName");

		try {
			OAuth2LeggedConsumer consumerConf = OAuth2LeggedConsumerDAO.newInstance().getByServiceName(serviceName);
			OAuthConsumer consumer = new OAuthConsumer(null, consumerConf.getConsumerKey(), consumerConf.getConsumerSecret(), null);
			OAuthAccessor accessor = new OAuthAccessor(consumer);

			Matcher matcher = hasXOAuthRequestorId.matcher(method.getURI()
					.getURI());
			if (matcher.find()) {
				StringBuffer newUrl = new StringBuffer();
				matcher.appendReplacement(newUrl, matcher.group(1)
						+ request.getPortalUid() + matcher.group(2));
				matcher.appendTail(newUrl);
				method.setURI(new URI(newUrl.toString(), false));
			} else {
				method.setURI(new URI(method.getURI().getURI()
						+ (method.getURI().getQuery() == null ? "?" : "&")
						+ "xoauth_requestor_id=" + request.getPortalUid(),
						false));
			}

			Map<String, String> parameters = null;
			String contentType = request.getRequestHeader("Content-Type");
			if (contentType != null
					&& contentType
							.startsWith("application/x-www-form-urlencoded")
					&& method.getName().equals("POST")) {
				String charset = RequestUtil.getCharset(contentType);
				parameters = RequestUtil.parseRequestBody(request
						.getRequestBody(), charset);
			}
			
			OAuthMessage message = accessor.newRequestMessage(method.getName(),
					method.getURI().toString(), parameters != null ? parameters
							.entrySet() : null);
			String authHeader = message.getAuthorizationHeader(null);
			request.setRequestHeader("Authorization", authHeader);
			// Find the non-OAuth parameters:
		}catch (URISyntaxException e) {
			throw new ProxyAuthenticationException(e);
		} catch (OAuthException e) {
			throw new ProxyAuthenticationException(e);
		} catch (IOException e) {
			throw new ProxyAuthenticationException(e);
		} 
		
	}
	
	public int getCredentialType() {
		// TODO Auto-generated method stub
		return 3;
	}
}
