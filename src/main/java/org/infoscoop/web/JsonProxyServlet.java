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
import org.infoscoop.request.Authenticator;
import org.infoscoop.request.ProxyRequest;
import org.infoscoop.request.filter.DetectTypeFilter;
import org.json.JSONArray;
import org.json.JSONObject;

public class JsonProxyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static Log log = LogFactory.getLog(JsonProxyServlet.class);
	
	public static enum HttpMethods {
		GET,POST,PUT,DELETE;
		
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
			default: return proxy.executeGet();
			}
		}
		public HttpMethod makeMethod( String url ) {
			switch( this ) {
			case POST: return new PostMethod( url );
			case PUT: return new PutMethod( url );
			case DELETE: return new DeleteMethod( url );
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
		SEND_PORTAL_UID_HEADER,
		POST_PORTAL_UID;
		
		public static AuthType as( String authType ) {
			for( AuthType value : AuthType.values()) {
				if( value.name().equalsIgnoreCase( authType ))
					return value;
			}
			
			return NONE;
		}
	}
	
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
				
//				System.out.println(name+":\t"+value );
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
			if( p.getValue().length > 0 )
				singles.put( p.getKey(),p.getValue()[0]);
		}
		
		return singles;
	}
	private JSONObject invokeJSONProxyRequest( String uid,Map<String,String> params,Map<String,List<String>> rheaders ) throws Exception {
		AuthType authz = AuthType.as( params.get("authz") );
//		String container = params.get("container");
//		String gadget = params.get("gadget");
		
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
		
		switch( authz ) {
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
		}
		
		ProxyRequest proxy = new ProxyRequest(url,contentType.filterType );
		proxy.setPortalUid( uid );
		
		if( !HttpMethods.GET.equals( httpMethod ) )
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

		urlJson.put("body",getResponseBodyAsStringWithAutoDetect( proxy ));
		
		Map<String,List<String>> responseHeaders = proxy.getResponseHeaders();
		JSONObject jsonHeaders = new JSONObject();
		for( String name : responseHeaders.keySet() ) {
			if( !jsonHeaders.has( name ))
				jsonHeaders.put( name,new JSONArray());
			
			JSONArray array = jsonHeaders.getJSONArray( name );
			List<String> values = responseHeaders.get( name );
			for( String value : values )
				array.put( value );
		}
		
		urlJson.put("headers",jsonHeaders );
		urlJson.put("rc",status );
		
		return json;
	}

	private String getResponseBodyAsStringWithAutoDetect( ProxyRequest proxy ) throws Exception {
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
	private Map<String,String> extractHeadersParam( String headerData ) throws UnsupportedEncodingException {
		Map<String,String> headers = new HashMap<String,String>();
	    if( headerData.length() > 0 ) {
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
