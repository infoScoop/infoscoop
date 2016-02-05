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

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthMessage;
import net.oauth.OAuthServiceProvider;
import net.oauth.client.OAuthClient;
import net.oauth.client.httpclient3.HttpClient3;
import net.oauth.signature.RSA_SHA1;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.acl.SecurityController;
import org.infoscoop.context.UserContext;
import org.infoscoop.dao.OAuthCertificateDAO;
import org.infoscoop.dao.model.OAuthCertificate;
import org.infoscoop.service.SquareService;
import org.infoscoop.util.RequestUtil;

public class SignedAuthenticator implements Authenticator {
	public static final OAuthClient CLIENT = new OAuthClient(new HttpClient3());
	private static final String PUBLIC_KEY_NAME = "public.cer";

	private static Log log = LogFactory.getLog(SignedAuthenticator.class);

	public SignedAuthenticator() {
	}

	public void doAuthentication(HttpClient client, ProxyRequest request,
			HttpMethod method, String uid, String pwd)
			throws ProxyAuthenticationException {
		try {
			OAuthConsumer consumer = newConsumer(request.getRequestHeader("x-is-useglobalkey"));
			OAuthAccessor accessor = new OAuthAccessor(consumer);

			List<Entry<String, String>> optionParams = null;
			Map<String, String> filterParameters = request.getFilterParameters();
			if(filterParameters != null){
			    optionParams = new ArrayList<Map.Entry<String,String>>(filterParameters.entrySet());
			}else{
			    optionParams = new ArrayList<Map.Entry<String,String>>();
			}
			String targetUrlPath = analyzeUrl(request.getTargetURL(),
					optionParams);

			Map<String, String> signedRequestParams = new HashMap<String, String>();
			
			String userId = SecurityController.getPrincipalByType("UIDPrincipal").getName();
			signedRequestParams.put("opensocial_viewer_id", userId);
			signedRequestParams.put("opensocial_owner_id", userId);
			signedRequestParams.put("opensocial_app_url", request.getRequestHeader("gadgetUrl"));
			signedRequestParams.put("opensocial_app_id", request.getRequestHeader("moduleId"));
			signedRequestParams.put("opensocial_instance_id", request.getRequestHeader("moduleId"));
			signedRequestParams.put("x_is_square_id", UserContext.instance().getUserInfo().getCurrentSquareId());
			signedRequestParams.put("xoauth_signature_publickey", PUBLIC_KEY_NAME);
			signedRequestParams.put("xoauth_public_key", PUBLIC_KEY_NAME);

			optionParams.addAll(signedRequestParams.entrySet());

			List<Entry<String, String>> postParams = new ArrayList<Map.Entry<String,String>>();
			String contentType = request.getRequestHeader("Content-Type");
			if (contentType != null
					&& contentType
							.startsWith("application/x-www-form-urlencoded")
					&& method.getName().equalsIgnoreCase("POST")) {
				
				String charset = RequestUtil.getCharset(contentType);
				postParams = RequestUtil.parseRequestBody(request.getRequestBody(), charset);
				optionParams.addAll(postParams);
			}
			
			OAuthMessage message = new OAuthMessage(method.getName(), targetUrlPath, optionParams);
			message.addRequiredParameters(accessor);
			List<Map.Entry<String, String>> authParams = message
					.getParameters();
			List<NameValuePair> queryParams = buildQueryParams(authParams, postParams);
			method.setQueryString((NameValuePair[]) queryParams
					.toArray(new NameValuePair[0]));
			
		} catch (Exception e) {
			throw new ProxyAuthenticationException(e);
		}
	}

	public int getCredentialType() {
		return 3;
	}

	protected OAuthConsumer newConsumer(String globalKeyFlg) throws ProxyAuthenticationException {
		boolean useGlobalKey = Boolean.valueOf(globalKeyFlg);
		OAuthCertificate certificate;

		if(useGlobalKey) {
			certificate = OAuthCertificateDAO.newInstance().get(SquareService.SQUARE_ID_DEFAULT);
		} else {
			certificate = OAuthCertificateDAO.newInstance().get(UserContext.instance().getUserInfo().getCurrentSquareId());
		}
		if (certificate == null)
			throw new ProxyAuthenticationException("a container's certificate is not set.");
		OAuthServiceProvider serviceProvider = new OAuthServiceProvider(null, null, null);
		OAuthConsumer consumer = new OAuthConsumer(null, certificate.getId().getConsumerKey(), null, serviceProvider);
		consumer.setProperty("oauth_signature_method", "RSA-SHA1");
		consumer.setProperty(RSA_SHA1.PRIVATE_KEY, certificate.getPrivateKey());

		return consumer;
	}

	private static String getRequestUrl(URL url) {
		StringBuilder requestUrl = new StringBuilder();
		String scheme = url.getProtocol();
		int port = url.getPort();

		requestUrl.append(scheme);
		requestUrl.append("://");
		requestUrl.append(url.getHost());

		if ((port != -1)
				&& ((scheme.equals("http") && port != 80) || (scheme
						.equals("https") && port != 443))) {
			requestUrl.append(":");
			requestUrl.append(port);
		}

		requestUrl.append(url.getPath());
		return requestUrl.toString();
	}

	private String analyzeUrl(String url, List<Entry<String, String>> optionParams)
			throws MalformedURLException {
		URL u = new URL(url);
		String query = u.getQuery();
		if (query != null) {
			String[] params = query.split("&");
			for (int i = 0; i < params.length; i++) {
				try {
					String[] param = splitParameter(params[i].split("="));
					String name = URLDecoder.decode(param[0], "UTF-8");
					if (name.startsWith("oauth") || name.startsWith("xoauth")
							|| name.startsWith("opensocial")){
						continue;
					}
	
					String value = URLDecoder.decode(param[1], "UTF-8");
					
					Map<String, String> tmp = new HashMap<String, String>();
					tmp.put(name, value);
					optionParams.addAll(tmp.entrySet());
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}
		return getRequestUrl(u);
	}

	private List<NameValuePair> buildQueryParams(
			List<Map.Entry<String, String>> authParams, List<Entry<String, String>> postParams) {
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		
		authParams:
		for (Map.Entry<String, String> entry : authParams) {
			for (Entry<String, String> param : postParams) {
				if(param.getKey() == entry.getKey()){
					postParams.remove(param);
					continue authParams;
				}
			}
			
			params.add(new NameValuePair(entry.getKey(), entry.getValue()));
		}
		return params;
	}
	
	private static String[] splitParameter(String[] params){
		String[] sp = new String[2];
		switch(params.length){
			case 1:
				sp[0] = params[0];
				sp[1] = "";
				break;
			case 2:
				sp = params;
				break;
		}
		return sp;
	}
}
