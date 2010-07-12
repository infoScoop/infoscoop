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
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthException;
import net.oauth.OAuthMessage;
import net.oauth.OAuthServiceProvider;
import net.oauth.client.OAuthClient;
import net.oauth.client.httpclient3.HttpClient3;
import net.oauth.signature.RSA_SHA1;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.RedirectException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.dao.OAuthCertificateDAO;
import org.infoscoop.dao.OAuthConsumerDAO;
import org.infoscoop.dao.model.OAuthCertificate;
import org.infoscoop.dao.model.OAuthConsumerProp;
import org.infoscoop.request.ProxyRequest.OAuthConfig;

public class OAuthAuthenticator implements Authenticator {
	public static final OAuthClient CLIENT = new OAuthClient(new HttpClient3());
	
	private static String AUTH_CALLBACK_URL = "oauthcallback";

	private static Log log = LogFactory.getLog(OAuthAuthenticator.class);
	
	private static Map<String, OAuthConsumer> consumers = new HashMap<String, OAuthConsumer>();
	
	public OAuthAuthenticator(){
	}

	public static OAuthConsumer getConsumer(String gadgetUrl, String serviceName) {
		return consumers.get(gadgetUrl + "\t" + serviceName);
	}
	
	public void doAuthentication(HttpClient client, ProxyRequest request,
			HttpMethod method, String uid, String pwd)
			throws ProxyAuthenticationException {
		ProxyRequest.OAuthConfig oauthConfig = request.getOauthConfig();
		try {
			OAuthConsumer consumer = newConsumer(oauthConfig.serviceName,oauthConfig);
			OAuthAccessor accessor = newAccessor(consumer, oauthConfig);
			if (accessor.accessToken == null) {
				getRequestToken(request, accessor);
			}
			
			Map<String, String> params = this.parseRequestBody(request.getRequestBody());
			OAuthMessage message = new OAuthMessage(method.getName(), request.getTargetURL(), params.entrySet());
			message.addRequiredParameters(accessor);
			String authHeader = message.getAuthorizationHeader(null);
			request.putRequestHeader("Authorization", authHeader);

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
	
	private Map<String, String> parseRequestBody(InputStream requestBody) throws IOException{
		Map<String, String> params = new HashMap<String, String>();
		if(requestBody != null){
			BufferedReader br = new BufferedReader(new InputStreamReader(requestBody));
			String postBodyStr = "";
			String s = null;
			while( ( s = br.readLine()) != null){
				postBodyStr += URLDecoder.decode(s,"UTF-8");
			}
			String[] keyvalues = postBodyStr.split("&");
			for (int i = 0; i < keyvalues.length; i++){
				String[] keyvalue = keyvalues[i].split("=");
				params.put(keyvalue[0].trim(), keyvalue[1].trim());
			}
			requestBody.reset();
		}
		return params;
	
	}
	
	protected OAuthConsumer newConsumer(String name, ProxyRequest.OAuthConfig oauthConfig) throws ProxyAuthenticationException{
		OAuthServiceProvider serviceProvider = 
			new OAuthServiceProvider(
					oauthConfig.requestTokenURL,
					oauthConfig.userAuthorizationURL,
					oauthConfig.accessTokenURL);
		
		OAuthConsumerProp consumerProp = OAuthConsumerDAO.newInstance()
				.getConsumer(oauthConfig.getGadgetUrl(), name);
		
		OAuthCertificate certificate = OAuthCertificateDAO.newInstance().get();
		
		String consumerKey;
		String consumerSecret;
		if("RSA-SHA1".equals(consumerProp.getSignatureMethod())){
			consumerKey = certificate.getConsumerKey();
			consumerSecret = null; 
		}else{
			consumerKey = consumerProp.getConsumerKey();
			consumerSecret = consumerProp.getConsumerSecret();
		}
		
		OAuthConsumer consumer = new OAuthConsumer(null, consumerKey, consumerSecret, serviceProvider);
		consumer.setProperty("name", name);
		consumer.setProperty("requestTokenMethod", oauthConfig.requestTokenMethod);
		consumer.setProperty("accessTokenMethod", oauthConfig.accessTokenMethod);
		
		if (consumerProp.getSignatureMethod() != null)
			consumer.setProperty("oauth_signature_method", consumerProp
					.getSignatureMethod());
		
		if("RSA-SHA1".equals(consumerProp.getSignatureMethod())){
			if (certificate == null)
				throw new ProxyAuthenticationException(
				"a container's certificate is not set.");
			consumer.setProperty(RSA_SHA1.PRIVATE_KEY, certificate.getPrivateKey());
		}
		
		consumers.put(consumerProp.getGadgetUrl() + "\t" + name, consumer);
		return consumer;
	}

	/**
	 * Construct an accessor from cookies. The resulting accessor won't
     * necessarily have any tokens.
     */
    private static OAuthAccessor newAccessor(OAuthConsumer consumer, OAuthConfig oauthConfig)
            throws OAuthException {
        OAuthAccessor accessor = new OAuthAccessor(consumer);
        accessor.requestToken = oauthConfig.requestToken;
        accessor.accessToken = oauthConfig.accessToken;
        accessor.tokenSecret = oauthConfig.tokenSecret;
        return accessor;
    }
    
	/**
     * Get from oauth example CookieConsumer.
     * @throws IOException 
     * @throws URISyntaxException 
	 * @throws ProxyAuthenticationException 
     * 
     * @throws RedirectException
     *             to obtain authorization
     */
    private static void getRequestToken(ProxyRequest request, OAuthAccessor accessor)
        throws OAuthException, IOException, URISyntaxException, ProxyAuthenticationException
    {
        final String consumerName = (String) accessor.consumer.getProperty("name");
        final String callbackURL = getCallbackURL(request, consumerName);
        List<OAuth.Parameter> parameters = OAuth.newList(OAuth.OAUTH_CALLBACK, callbackURL);
        // Google needs to know what you intend to do with the access token:
        Object scope = accessor.consumer.getProperty("request.scope");
        if (scope != null) {
            parameters.add(new OAuth.Parameter("scope", scope.toString()));
        }
        OAuthMessage response = CLIENT.getRequestTokenResponse(accessor, (String) accessor.consumer.getProperty("requestTokenMethod"), parameters);
		String gadgetUrl = request.getOauthConfig().getGadgetUrl();
		request.putResponseHeader(gadgetUrl + "¥t" + consumerName
				+ ".requesttoken", accessor.requestToken);
		request.putResponseHeader(gadgetUrl + "¥t" + consumerName
				+ ".tokensecret", accessor.tokenSecret);
		String authorizationURL = accessor.consumer.serviceProvider.userAuthorizationURL;
        authorizationURL = OAuth.addParameters(authorizationURL //
                , OAuth.OAUTH_TOKEN, accessor.requestToken);
        if (response.getParameter(OAuth.OAUTH_CALLBACK_CONFIRMED) == null) {
            authorizationURL = OAuth.addParameters(authorizationURL //
                    , OAuth.OAUTH_CALLBACK, callbackURL);
        }
        request.putResponseHeader("oauthApprovalUrl", authorizationURL);
        throw new ProxyAuthenticationException("Redirect to authorization url.");
    }

	private static String getCallbackURL(ProxyRequest request,
			String consumerName) throws IOException {
		String gadgetUrl = request.getOauthConfig().getGadgetUrl();
		String hostPrefix = request.getOauthConfig().getHostPrefix();
		URL base = new URL(hostPrefix + "/" + AUTH_CALLBACK_URL
				+ "?__GADGET_URL__=" + gadgetUrl);
		return OAuth.addParameters(base.toExternalForm() //
				, "consumer", consumerName //
				);
	}

}
