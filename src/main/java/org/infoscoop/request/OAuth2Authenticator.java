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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.oauth.OAuth;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthException;
import net.oauth.OAuthProblemException;
import net.oauth.OAuthServiceProvider;
import net.oauth.client.OAuthClient;
import net.oauth.client.httpclient3.HttpClient3;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.context.UserContext;
import org.infoscoop.dao.OAuth2TokenDAO;
import org.infoscoop.dao.OAuthConsumerDAO;
import org.infoscoop.dao.model.OAuth2Token;
import org.infoscoop.dao.model.OAuthConsumerProp;
import org.infoscoop.service.OAuthService;
import org.infoscoop.util.RequestUtil;

public class OAuth2Authenticator implements Authenticator {
	public static final OAuthClient CLIENT = new OAuthClient(new HttpClient3());
	private static String AUTH_CALLBACK_URL2 = "oauth2callback";
	private static Log log = LogFactory.getLog(OAuth2Authenticator.class);	
	private static Map<String, OAuthConsumer> consumers = new HashMap<String, OAuthConsumer>();

	public static OAuthConsumer getConsumer(String gadgetUrl, String serviceName) {
		return consumers.get(gadgetUrl + "\t" + serviceName);
	}
	
	public void doAuthentication(HttpClient client, ProxyRequest request,
			HttpMethod method, String uid, String pwd)
			throws ProxyAuthenticationException {
		ProxyRequest.OAuth2Config oauthConfig = request.getOauth2Config();
		try {
			OAuthConsumer consumer = newConsumer(oauthConfig.serviceName,oauthConfig, getCallbackURL(request));
			if (oauthConfig.accessToken == null) {
				returnApprovalUrl(request, consumer);
			}

			if(oauthConfig.validityPeriodUTC != null){
	            Calendar cal = Calendar.getInstance();
	            cal.setTime(new Date());
	            if(cal.getTimeInMillis() > oauthConfig.validityPeriodUTC){
	            	if(oauthConfig.refreshToken == null){
	    				OAuthService.getHandle().deleteOAuth2Token(request.getPortalUid(), oauthConfig.gadgetUrl, oauthConfig.serviceName);
	    				returnApprovalUrl(request, consumer);
	            	}else{
						log.error("AccessToken was expired, try re-get the token. [" + oauthConfig.serviceName + "]");
	            		getAccessTokenByRefreshToken(request,consumer);
	            		OAuth2Token token2 = OAuth2TokenDAO.newInstance().getAccessToken(request.getPortalUid(), oauthConfig.gadgetUrl, oauthConfig.serviceName);
	            		oauthConfig.setAccessToken(token2.getAccessToken());
	            		oauthConfig.setRefreshToken(token2.getRefreshToken());
	            		oauthConfig.setValidityPeriodUTC(token2.getValidityPeriodUTC());
	            	}
	            }
			}
			
			List<Entry<String, String>> parameters = null;
			String contentType = request.getRequestHeader("Content-Type");
			if (contentType != null
					&& contentType
							.startsWith("application/x-www-form-urlencoded")
					&& method.getName().equals("POST")) {
				// TODO analyze charset
				String charset = RequestUtil.getCharset(contentType);
				parameters = RequestUtil.parseRequestBody(request.getRequestBody(), charset);
			}
			
			if("Bearer".equalsIgnoreCase(oauthConfig.tokenType)){
				method.addRequestHeader("Authorization", oauthConfig.tokenType+" "+oauthConfig.accessToken);				
			}else{
				method.addRequestHeader("Authorization", "OAuth " + oauthConfig.accessToken);
				
				String queryString = method.getQueryString();
				queryString = "access_token=" + oauthConfig.accessToken + "&" + queryString;
				method.setQueryString(queryString);	
			}
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
	
	private void setOauthConfigAfterRefresh(){
		
	}
	
	protected OAuthConsumer newConsumer(String name, ProxyRequest.OAuth2Config oauthConfig, String callbackURL) throws ProxyAuthenticationException{
		OAuthServiceProvider serviceProvider = 
			new OAuthServiceProvider(
					null,
					oauthConfig.userAuthorizationURL,
					oauthConfig.accessTokenURL);
		OAuthConsumerProp consumerProp = OAuthConsumerDAO.newInstance().getConsumer(oauthConfig.getGadgetUrl(), name, UserContext.instance().getUserInfo().getCurrentSquareId());
		if(consumerProp == null)
			throw new ProxyAuthenticationException("Consumer key and secret is not set for " + oauthConfig.getGadgetUrl());

		String consumerKey = consumerProp.getConsumerKey();
		String consumerSecret = consumerProp.getConsumerSecret();
		OAuthConsumer consumer = new OAuthConsumer(callbackURL, consumerKey, consumerSecret, serviceProvider);
		consumer.setProperty("name", name);
		consumer.setProperty("request.scope", oauthConfig.scope);
		consumers.put(oauthConfig.getGadgetUrl() + "\t" + name, consumer);
		return consumer;
	}
	
    //approvalURLの返却
    private static void returnApprovalUrl(ProxyRequest request, OAuthConsumer consumer)
    				throws OAuthException, IOException, URISyntaxException, ProxyAuthenticationException{
		final String consumerName = request.getOauth2Config().serviceName;
		final String callbackURL = getCallbackURL(request);
		Object scope = consumer.getProperty("request.scope");
		final OAuth2Message msg = new OAuth2Message();		
		String gadgetUrl = request.getOauth2Config().getGadgetUrl();
		
		OAuthService.getHandle().saveOAuth2Token(request.getPortalUid(),
				gadgetUrl, consumerName, null, null, null, null, null);
		String authorizationURL = request.getOauth2Config().userAuthorizationURL;
		String state = msg.buildStateJSON(request, consumerName);
		authorizationURL = OAuth.addParameters(authorizationURL,
												"client_id", consumer.consumerKey,
												"redirect_uri", callbackURL,
												"response_type","code",
												"state", state);
		if (scope!=null) {
			authorizationURL = OAuth.addParameters(authorizationURL, "scope", scope.toString());
		}
		
		//for Google
		//If other server has some problem by follow parameter, thought corresponding.
		authorizationURL = OAuth.addParameters(authorizationURL, "access_type", "offline");
		authorizationURL = OAuth.addParameters(authorizationURL, "approval_prompt", "force");
		
		request.putResponseHeader("oauthApprovalUrl", authorizationURL);
		throw new ProxyAuthenticationException("Redirect to authorization url.", false);
    }

	private static String getCallbackURL(ProxyRequest request) throws IOException {
		String hostPrefix = request.getOauth2Config().getHostPrefix();
		URL base = new URL(hostPrefix + "/" + AUTH_CALLBACK_URL2);
		return base.toExternalForm();
	}
		
	private static void getAccessTokenByRefreshToken(ProxyRequest request, OAuthConsumer consumer)
																	throws OAuthException, IOException {
		try{
			ProxyRequest proxy = new ProxyRequest(request.getOauth2Config().accessTokenURL,"NoOperation");
			OAuth2Message msg = new OAuth2Message();
			Map<String, String> params = new HashMap<String,String>();
			params.put("client_id",consumer.consumerKey);
			params.put("client_secret",consumer.consumerSecret);
			params.put("refresh_token",request.getOauth2Config().refreshToken);
			params.put("grant_type","refresh_token");
			
			Object scope = consumer.getProperty("request.scope");
			if (scope != null) {
				params.put("scope", scope.toString());
			}
			String postData = msg.buildPostBody(params);
			proxy.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
			proxy.setReqeustBody( new ByteArrayInputStream( postData.getBytes("UTF-8")));
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
			
			String refreshToken = msg.getRefreshToken();
			if (refreshToken == null){
				refreshToken = request.getOauth2Config().refreshToken;
			}

			OAuthService.getHandle().saveOAuth2Token(request.getPortalUid(), 
					request.getOauth2Config().getGadgetUrl(),request.getOauth2Config().serviceName,
					msg.getTokenType(), request.getOauth2Config().code, msg.getAccessToken(),
					refreshToken, validityPeriodUTC);				
		} catch (Exception e){
			OAuthService.getHandle().deleteOAuth2Token(request.getPortalUid(),
					request.getOauth2Config().getGadgetUrl(),
					request.getOauth2Config().serviceName);
			log.error("unexpected error has occured.", e);
		}
	}
}