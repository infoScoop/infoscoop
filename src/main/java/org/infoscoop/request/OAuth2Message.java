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

import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shindig.common.uri.Uri;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.collect.Maps;


public class OAuth2Message {
	  private static Log log = LogFactory.getLog(OAuth2Message.class);

	  private final Map<String, String> params;
	  private final Map<String, String> unparsedProperties;

	  public OAuth2Message() {
	    this.params = Maps.newHashMapWithExpectedSize(5);
	    this.unparsedProperties = Maps.newHashMapWithExpectedSize(0);
	  }

	  public String getAccessToken() {
	    return this.params.get(OAuth2Message.ACCESS_TOKEN);
	  }

	  public String getAuthorization() {
	    return this.params.get(OAuth2Message.AUTHORIZATION);
	  }

	  public String getErrorDescription() {
	    return this.params.get(OAuth2Message.ERROR_DESCRIPTION);
	  }

	  public String getErrorUri() {
	    return this.params.get(OAuth2Message.ERROR_URI);
	  }

	  public String getExpiresIn() {
	    return this.params.get(OAuth2Message.EXPIRES_IN);
	  }

	  public String getMacAlgorithm() {
	    return this.params.get(OAuth2Message.MAC_ALGORITHM);
	  }

	  public String getMacSecret() {
	    return this.params.get(OAuth2Message.MAC_SECRET);
	  }

	  public Map<String, String> getParameters() {
	    return this.params;
	  }

	  public String getRefreshToken() {
	    return this.params.get(OAuth2Message.REFRESH_TOKEN);
	  }

	  public String getState() {
		String tmpState = this.params.get(OAuth2Message.STATE);
		try{
			tmpState = new String(Base64.decodeBase64(tmpState.getBytes("UTF-8")));
		}catch(Exception e){
			this.params.put(OAuth2Message.ERROR, "getState Exception");
		}
	    return tmpState;
	  }

	  public String getTokenType() {
	    return this.params.get(OAuth2Message.TOKEN_TYPE);
	  }

	  public Map<String, String> getUnparsedProperties() {
	    // TODO ARC
	    return this.unparsedProperties;
	  }

	  public void parseJSON(final String response) {
	    try {
	      final JSONObject jsonObject = new JSONObject(response);
	      final String accessToken = jsonObject.optString(OAuth2Message.ACCESS_TOKEN, null);
	      if (accessToken != null) {
	        this.params.put(OAuth2Message.ACCESS_TOKEN, accessToken);
	      }

	      final String tokenType = jsonObject.optString(OAuth2Message.TOKEN_TYPE, null);
	      if (tokenType != null) {
	        this.params.put(OAuth2Message.TOKEN_TYPE, tokenType);
	      }

	      final String expiresIn = jsonObject.optString(OAuth2Message.EXPIRES_IN, null);
	      if (expiresIn != null) {
	        this.params.put(OAuth2Message.EXPIRES_IN, expiresIn);
	      }

	      final String refreshToken = jsonObject.optString(OAuth2Message.REFRESH_TOKEN, null);
	      if (refreshToken != null) {
	        this.params.put(OAuth2Message.REFRESH_TOKEN, refreshToken);
	      }

	      final String _error = jsonObject.optString(OAuth2Message.ERROR, null);
	      if (_error != null) {
	        this.params.put(OAuth2Message.ERROR, _error);
	      }

	      final String errorDescription = jsonObject.optString(OAuth2Message.ERROR_DESCRIPTION, null);
	      if (errorDescription != null) {
	        this.params.put(OAuth2Message.ERROR_DESCRIPTION, errorDescription);
	      }

	      final String errorUri = jsonObject.optString(OAuth2Message.ERROR_URI, null);
	      if (errorUri != null) {
	        this.params.put(OAuth2Message.ERROR_URI, errorUri);
	      }
	    } catch (final JSONException e) {
	      this.params.put(OAuth2Message.ERROR, "JSONException parsing response");
	    }
	  }

	  public void parseQuery(final String query) {
	    final Uri uri = Uri.parse(query);
	    final Map<String, List<String>> _params = uri.getQueryParameters();
	    for (final Entry<String, List<String>> entry : _params.entrySet()) {
	      this.params.put(entry.getKey(), entry.getValue().get(0));
	    }
	    if ((!this.params.containsKey(OAuth2Message.EXPIRES_IN))
	        && (this.params.containsKey("expires"))) {
	      this.params.put(OAuth2Message.EXPIRES_IN, this.params.get("expires"));
	    }
	  }

	  public void parseRequest(final HttpServletRequest request) {
	    @SuppressWarnings("unchecked")
	    final Enumeration<String> paramNames = request.getParameterNames();
	    while (paramNames.hasMoreElements()) {
	      final String paramName = paramNames.nextElement();
	      final String param = request.getParameter(paramName);
	      this.params.put(paramName, param);
	    }
	  }

	  public String buildStateJSON(final ProxyRequest request, final String consumerName) {
		  	String state = new String();
			try{
				JSONObject obj = new JSONObject();
				obj.put("__GADGET_URL__", request.getOauth2Config().getGadgetUrl());
				obj.put("consumer", consumerName);
				byte[] tmpByte = (obj.toString()).getBytes("UTF-8");
				byte[] tmpByte2 = Base64.encodeBase64(tmpByte);
				state = new String(Base64.encodeBase64((obj.toString()).getBytes("UTF-8")));
//				state = (Base64.encodeBase64((obj.toString()).getBytes("UTF-8"))).toString();
			}catch(JSONException e){
				this.params.put(OAuth2Message.ERROR, "JSONException build state");
			}catch(Exception e){
				this.params.put(OAuth2Message.ERROR, "JSONException build state : unsupported-charset");
			}
			return state;
	  }
	  
	  public String buildPostBody(Map<String, String> map){
		  StringBuffer postBody = new StringBuffer();
		  for(Iterator<String> i = map.keySet().iterator(); i.hasNext();){
			  String key = i.next();
			  String val = map.get(key);
			  postBody.append(key);
			  postBody.append("=");
			  postBody.append(val);
			  postBody.append("&");
		  }		  
		  if(postBody.length()>0){
			  postBody.deleteCharAt(postBody.length()-1);			  
		  }
		  return postBody.toString();
	  }
	  
	  public String getGadgetURL(){
		  String gadgetUrl = new String();
		  try{
			  JSONObject obj = new JSONObject(getState());
			  gadgetUrl = obj.getString("__GADGET_URL__");
		  }catch(JSONException e){
			  this.params.put(OAuth2Message.ERROR, "JSONException parse state");
		  }
		  return gadgetUrl;
	  }
	  
	  public String getServiceName(){
		  String serviceName = new String();
		  try{
			  JSONObject obj = new JSONObject(getState());
			  serviceName = obj.getString("consumer");
		  }catch(JSONException e){
			  this.params.put(OAuth2Message.ERROR, "JSONException parse state");
		  }
		  return serviceName;		  
	  }
	  
    public static final String AUTH_SCHEME = "OAuth2";

    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    public static final String DELETE = "DELETE";
    
    public final static String ACCESS_DENIED = "access_denied";
    public final static String ACCESS_TOKEN = "access_token";
    public final static String AUTHORIZATION = "code";
    public final static String AUTHORIZATION_CODE = "authorization_code";
    public final static String AUTHORIZATION_HEADER = "Authorization";
    public final static String BASIC_AUTH_TYPE = "Basic";
    public final static String BEARER_TOKEN_TYPE = "Bearer";
    public final static String BODYHASH = "bodyhash";
    public final static String CLIENT_CREDENTIALS = "client_credentials";
    public final static String CLIENT_ID = "client_id";
    public final static String CLIENT_SECRET = "client_secret";
    public final static String CONFIDENTIAL_CLIENT_TYPE = "confidential";
    public final static String ERROR = "error";
    public final static String ERROR_DESCRIPTION = "error_description";
    public final static String ERROR_URI = "error_uri";
    public final static String EXPIRES_IN = "expires_in";
    public final static String GRANT_TYPE = "grant_type";
    public final static String HMAC_SHA_1 = "hmac-sha-1";
    public final static String HMAC_SHA_256 = "hmac-sha-256";
    public final static String ID = "id";
    public final static String INVALID_CLIENT = "invalid_client";
    public final static String INVALID_GRANT = "invalid_grant";
    public final static String INVALID_REQUEST = "invalid_request";
    public final static String INVALID_SCOPE = "invalid_scope";
    public final static String MAC = "mac";
    public final static String MAC_ALGORITHM = "algorithm";
    public final static String MAC_EXT = "ext";
    public final static String MAC_HEADER = "MAC";
    public final static String MAC_SECRET = "secret";
    public final static String MAC_TOKEN_TYPE = "mac";
    public final static String NO_GRANT_TYPE = "NONE";
    public final static String NONCE = "nonce";
    public final static String PUBLIC_CLIENT_TYPE = "public";
    public final static String REDIRECT_URI = "redirect_uri";
    public final static String REFRESH_TOKEN = "refresh_token";
    public final static String RESPONSE_TYPE = "response_type";
    public final static String SCOPE = "scope";
    public final static String SERVER_ERROR = "server_error";
    public final static String STANDARD_AUTH_TYPE = "STANDARD";
    public final static String STATE = "state";
    public final static String TEMPORARILY_UNAVAILABLE = "temporarily_unavailable";
    public final static String TOKEN_RESPONSE = "token";
    public final static String TOKEN_TYPE = "token_type";
    public final static String UNAUTHORIZED_CLIENT = "authorized_client";
    public final static String UNSUPPORTED_GRANT_TYPE = "unsupported_grant_type";
    public final static String UNSUPPORTED_RESPONSE_TYPE = "unsupported_response_type";

    private static final String toString(Object from) {
        return (from == null) ? null : from.toString();
    }
}
