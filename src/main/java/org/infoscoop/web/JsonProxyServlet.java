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
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.dao.OAuth2TokenDAO;
import org.infoscoop.dao.OAuthTokenDAO;
import org.infoscoop.dao.model.OAuth2Token;
import org.infoscoop.dao.model.OAuthToken;
import org.infoscoop.request.Authenticator;
import org.infoscoop.request.ProxyRequest;
import org.infoscoop.request.filter.DetectTypeFilter;
import org.infoscoop.request.method.PatchMethod;
import org.infoscoop.service.OAuthService;
import org.json.JSONArray;
import org.json.JSONObject;

public class JsonProxyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static Log log = LogFactory.getLog(JsonProxyServlet.class);
	
	public static enum HttpMethods {
		GET,POST,PUT,DELETE, PATCH;
		
		public static HttpMethods as( String httpMethod ) {
			for( HttpMethods value : HttpMethods.values() ) {
				if( value.name().equalsIgnoreCase( httpMethod ))
					return value;
			}
			
			return GET;
		}
		public int invokeProxyRequest( ProxyRequest proxy ) throws Exception {
			switch( this ) {
			case POST: return proxy.executePost();
			case PUT: return proxy.executePut();
			case DELETE: return proxy.executeDelete();
			case PATCH: return proxy.executePatch();
			default: return proxy.executeGet();
			}
		}
		public HttpMethod makeMethod( String url ) {
			switch( this ) {
			case POST: return new PostMethod( url );
			case PUT: return new PutMethod( url );
			case DELETE: return new DeleteMethod( url );
			case PATCH: return new PatchMethod( url );
			default: return new GetMethod( url );
			}
		}
	}
	
	private static enum ContentType {
		TEXT,
		XML,
		FEED("FeedJson"),
		JSON;
		
		public static ContentType as( String contentType ) {
			for( ContentType value : ContentType.values() ) {
				if( value.name().equalsIgnoreCase( contentType ))
					return value;
			}
			
			return TEXT;
		}
		private ContentType() {
			this("NoOperation");
		}
		private ContentType( String filterType ) {
			this.filterType = filterType;
		}
		private String filterType;
		public String getFitlerType() { return filterType; }
	}
	
	private static enum AuthType {
		NONE,
		SIGNED,
		OAUTH,
		OAUTH2,
		SEND_PORTAL_UID_HEADER,
		POST_PORTAL_UID,
		BASIC;
		
		public static AuthType as( String authType ) {
			for( AuthType value : AuthType.values()) {
				if( value.name().equalsIgnoreCase( authType ))
					return value;
			}
			
			return NONE;
		}
	}
	
	private static final String OAUTH2_FORCE_REFRESH = "OAUTH2_FORCE_REFRESH";
	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet( req,resp );
	}
	@SuppressWarnings("unchecked")
	protected void doGet( HttpServletRequest req, HttpServletResponse resp )
			throws IOException, ServletException {
		String uid = ( String )req.getSession().getAttribute("Uid");
		Map<String,String> params = getSingleParams( req.getParameterMap() );
		
		Map<String,List<String>> headers = new HashMap<String,List<String>>();
		for( Enumeration names=req.getHeaderNames();names.hasMoreElements();)  {
			String name = ( String )names.nextElement();
			
			List<String> vlist = new ArrayList<String>();
			headers.put( name,vlist );
			for( Enumeration values=req.getHeaders( name );values.hasMoreElements(); ) {
				String value = ( String )values.nextElement();
				vlist.add( value );
			}
		}
		
		JSONObject result;
		try {
			result = invokeJSONProxyRequest( uid,params,headers );
		} catch( Exception ex ) {
			if( ex.getCause() != null )
				ex = ( Exception )ex.getCause();
			
			log.error( "",ex );
			
			throw new ServletException( ex );
		}
		
		byte[] body = ( "throw 1; < don't be evil' >" +result.toString() ).getBytes("UTF-8");

		resp.setStatus( 200 );
		resp.setHeader("Cache-Control","no-cache");
		resp.setContentType("text/plain;charset=UTF-8");
//		resp.setContentType("text/plain;charset=UTF-8");
		resp.setContentLength( body.length );
		resp.getOutputStream().write( body );
		resp.getOutputStream().flush();
	}
	private Map<String,String> getSingleParams( Map<String,String[]> parameters ) {
		Map<String,String> singles = new HashMap<String,String>();
		for( Map.Entry<String,String[]> p : parameters.entrySet() ) {
			if( p.getValue().length > 0 && p.getValue()[0] != null && p.getValue()[0].length() > 0)
				singles.put( p.getKey(),p.getValue()[0]);
		}
		
		return singles;
	}
	public static JSONObject invokeJSONProxyRequest( String uid,Map<String,String> params,Map<String,List<String>> rheaders ) throws Exception {
		AuthType authz = AuthType.as( params.get("authz") );
		
		HttpMethods httpMethod = HttpMethods.as( params.get("httpMethod"));
		String url = params.get("url");
		ContentType contentType = ContentType.as( params.get("contentType"));
		Map<String,String> headers = extractHeadersParam( params.get("headers"));
		String postData = params.get("postData");
		
		Map<String,String> filterParams = new HashMap<String,String>();
		if( contentType.equals( ContentType.FEED )) {
			filterParams.put("gs",params.get("getSummaries"));
			filterParams.put("ne",params.get("numEntries"));
		}
		
		JSONObject json = new JSONObject();
		
		JSONObject urlJson = new JSONObject();
		json.put( url,urlJson );
		
		String uidParamName = params.get("IS_AUTH_UID_PARAM_NAME");
		if( uidParamName != null && !"".equals( uidParamName ))
			headers.put( Authenticator.UID_PARAM_NAME,uidParamName );
		
		ProxyRequest proxy = new ProxyRequest(url,contentType.filterType );
		proxy.setPortalUid( uid );
		String oauthServiceName = null;
		String gadgetUrl = null;
		switch( authz ) {
		case OAUTH:
			headers.put("authType","oauth");
			oauthServiceName = params.get("OAUTH_SERVICE_NAME");
			gadgetUrl = params.get("gadgetUrl");
			
			ProxyRequest.OAuthConfig oauthConfig = proxy.new OAuthConfig(oauthServiceName);
			oauthConfig.setRequestTokenURL(params.get("requestTokenURL"));
			oauthConfig.setRequestTokenMethod(params.get("requestTokenMethod"));
			oauthConfig.setUserAuthorizationURL(params.get("userAuthorizationURL"));
			oauthConfig.setAccessTokenURL(params.get("accessTokenURL"));
			oauthConfig.setAccessTokenMethod(params.get("accessTokenMethod"));
			oauthConfig.setGadgetUrl(gadgetUrl);
			oauthConfig.setHostPrefix(params.get("hostPrefix"));
			
			OAuthToken token = OAuthTokenDAO.newInstance().getAccessToken(uid,
					gadgetUrl, oauthServiceName);
			if(token != null){
				oauthConfig.setAccessToken(token.getAccessToken());
				oauthConfig.setTokenSecret(token.getTokenSecret());
			}
			
			proxy.setOauthConfig(oauthConfig);
			break;
		case OAUTH2:
			headers.put("authType","oauth2");
			oauthServiceName = params.get("OAUTH_SERVICE_NAME");
			gadgetUrl = params.get("gadgetUrl");
			
			ProxyRequest.OAuth2Config oauth2Config = proxy.new OAuth2Config(oauthServiceName);
			oauth2Config.setUserAuthorizationURL(params.get("userAuthorizationURL"));
			oauth2Config.setAccessTokenURL(params.get("accessTokenURL"));
			oauth2Config.setScope(params.get("OAUTH2_SCOPE"));
			oauth2Config.setGadgetUrl(gadgetUrl);
			oauth2Config.setHostPrefix(params.get("hostPrefix"));
			
			OAuth2Token token2 = OAuth2TokenDAO.newInstance().getAccessToken(uid,
					gadgetUrl, oauthServiceName);
			if(token2 != null){
				oauth2Config.setCode(token2.getAuthCode());
				oauth2Config.setTokenType(token2.getTokenType());
				oauth2Config.setAccessToken(token2.getAccessToken());
				oauth2Config.setRefreshToken(token2.getRefreshToken());

				Long validityPeriodUTC = params.get(OAUTH2_FORCE_REFRESH) != null ?
						new Long(0) : token2.getValidityPeriodUTC();
						
				oauth2Config.setValidityPeriodUTC(validityPeriodUTC);
			}
			
			proxy.setOauth2Config(oauth2Config);
			break;
		case SIGNED:
			headers.put("authType", "signed");
			headers.put("gadgetUrl", params.get("gadgetUrl"));
			headers.put("moduleId", params.get("moduleId"));
			break;
		case POST_PORTAL_UID:
			if( uid == null ) break;
			
			//FIXME The postBody is overwritten by a certification information.
			headers.put("authType","g_postPortalCredential");
			httpMethod = HttpMethods.POST;
			
			break;
		case SEND_PORTAL_UID_HEADER:
			if( uid == null ) break;
			
			headers.put("authType","g_sendPortalCredentialHeader");
			
			break;
		case BASIC:
			String username = params.get("IS_AUTH_BASIC_USERNAME");
			String password = params.get("IS_AUTH_BASIC_PASSWORD");
			
			headers.put("authType","g_basic");
			headers.put("authuserid",username );
			headers.put("authpassword",password );
		}
		
		if (!HttpMethods.GET.equals(httpMethod) && postData != null)
			proxy.setReqeustBody( new ByteArrayInputStream( postData.getBytes("UTF-8")));
		
		for( Map.Entry<String,String> header : headers.entrySet() )
			proxy.putRequestHeader( header.getKey(),header.getValue() );
		
		for( Map.Entry<String,List<String>> header : rheaders.entrySet() ) {
			String name = header.getKey();
			if( headers.containsKey( name ) )
				continue;
			
			for( String value : header.getValue() ) {
				if( HttpMethods.GET.equals( httpMethod ) && "content-length".equalsIgnoreCase( name ))
					continue;
				
				proxy.putRequestHeader( name,value );
			}
		}
		
		for( Map.Entry<String,String> filterParam : filterParams.entrySet() )
			proxy.setFilterParameter( filterParam.getKey(),filterParam.getValue() );

		int status = httpMethod.invokeProxyRequest( proxy );
		
		String bodyStr = getResponseBodyAsStringWithAutoDetect( proxy );

		urlJson.put("body",bodyStr);

		Map<String,List<String>> responseHeaders = proxy.getResponseHeaders();
		JSONObject jsonHeaders = new JSONObject();
		for( String name : responseHeaders.keySet() ) {
			if( !jsonHeaders.has( name ))
				jsonHeaders.put( name,new JSONArray());
			if( "oauthApprovalUrl".equalsIgnoreCase(name)){
				urlJson.put("oauthApprovalUrl", proxy.getResponseHeader(name));
				status = 200;
			}else{
				JSONArray array = jsonHeaders.getJSONArray( name );
				List<String> values = responseHeaders.get( name );
				for( String value : values )
					array.put( value );
			}
		}
		urlJson.put("headers",jsonHeaders );
		urlJson.put("rc",status );

		if(status == 401 && (AuthType.OAUTH == authz || AuthType.OAUTH2 == authz)){
			if(AuthType.OAUTH == authz){
				OAuthService.getHandle().deleteOAuthToken(uid, gadgetUrl,
						oauthServiceName);				
			}else if(AuthType.OAUTH2 == authz){
				ProxyRequest.OAuth2Config oauth2Config = proxy.getOauth2Config();
				
				// Retry if config has refreshToken only.
				if(params.get(OAUTH2_FORCE_REFRESH) == null && oauth2Config.hasRefreshToken() && !oauth2Config.hasValidityPeriodUTC()){
					params.put(OAUTH2_FORCE_REFRESH, new String());
					return invokeJSONProxyRequest(uid, params, rheaders);
				}
				
				OAuthService.getHandle().deleteOAuth2Token(uid, gadgetUrl,
						oauthServiceName);
			}
			log.error("OAuth request is failed:\n" + bodyStr);
			urlJson.put("oauthError", "OAuth request is failed");
		}
		return json;
	}

	public static String getResponseBodyAsStringWithAutoDetect( ProxyRequest proxy ) throws Exception {
		byte[] body = ProxyRequest.stream2Bytes( proxy.getResponseBody());
		
		String contentType = null;
		List<String> contentTypes = proxy.getResponseHeaders("Content-Type");
		if( contentTypes.size() > 0 )
			contentType = contentTypes.get( contentTypes.size() -1 );
		
		String encoding = DetectTypeFilter.getContentTypeCharset( contentType );
//		if( encoding == null )
//			encoding = DetectTypeFilter.findEncoding( new ByteArrayInputStream( body ) );
		if( encoding == null )
			encoding = "UTF-8";
		
		return new String( body,encoding );
	}
	public static Map<String,String> extractHeadersParam( String headerData ) throws UnsupportedEncodingException {
		Map<String,String> headers = new HashMap<String,String>();
	    if( headerData != null && headerData.length() > 0 ) {
			String[] pairs = headerData.split("&");
			for( String pair : pairs ) {
				String[] header = pair.split("=");
				if( header.length != 2 )
					continue;
				
				headers.put( URLDecoder.decode( header[0],"UTF-8").toLowerCase(),
						URLDecoder.decode( header[1],"UTF-8") );
			}
	    }
	    
	    return headers;
	}
}
