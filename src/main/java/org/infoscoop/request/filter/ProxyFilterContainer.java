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

package org.infoscoop.request.filter;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.dao.model.Cache;
import org.infoscoop.properties.InfoScoopProperties;
import org.infoscoop.request.AuthenticatorUtil;
import org.infoscoop.request.ProxyRequest;
import org.infoscoop.request.proxy.Proxy;
import org.infoscoop.service.CacheService;
import org.infoscoop.util.RequestUtil;

public class ProxyFilterContainer {
	private static final Log log = LogFactory
			.getLog(ProxyFilterContainer.class);

	private List<ProxyFilter> filterChain = new ArrayList<ProxyFilter>(3);
	final static int EXECUTE_POST_STATUS = 201;
	
	private static final String excludePattern;
	static{
		excludePattern = InfoScoopProperties.getInstance().getProperty("proxy.exclude.url.pattern");
	}
	
	public void addFilter(ProxyFilter filter){
		filterChain.add(filter);
	}

	public void setFilter(List<ProxyFilter> filters){
		this.filterChain = filters;
	}

	public final int prepareInvoke(HttpClient client, HttpMethod method, ProxyRequest request)throws Exception {
		// check exclude pattern
		String domain = method.getURI().getHost();
		if(excludePattern != null && domain != null && domain.matches(excludePattern))
			return HttpStatus.SC_BAD_REQUEST;
		
		// filer pre processing
		for(int i = 0; i < filterChain.size(); i++){
			ProxyFilter filter = (ProxyFilter)filterChain.get(i);
			try{
				int statusCode = filter.preProcess(client, method, request);
				Header[] headers = method.getResponseHeaders();
				for (int j = 0; j < headers.length; j++) {
					request.putResponseHeader(headers[j].getName(), headers[j].getValue());
				}
				if(statusCode > 0){
					return statusCode;
				}
			}catch(Exception e){
				log.error("Throw exception from preProcess method.", e);
				return 500;
			}
		}
		return 0;
	}
	public final int getCache(HttpClient client, HttpMethod method, ProxyRequest request)throws Exception {
		// check public cache
		if(method instanceof GetMethod){

			request.addIgnoreHeader("content-type");

			if(request.allowUserPublicCache()){
				// TODO: Is the improvement of the performance necessary?
				Cache cache = CacheService.getHandle().getCacheByUrl(request.getOriginalURL());
				if(cache != null ){
					int cacheLifeTime = request.getProxy().getCacheLifeTime();
					if(cache.getTimestamp().getTime() + cacheLifeTime * 60 * 1000 > System.currentTimeMillis()){
						for(Header header : cache.getHeaderList()) {
							if("X-IS-REDIRECTED-FROM".equalsIgnoreCase( header.getName())) {
								request.setRedirectURL( header.getValue());
							} else {
								request.putResponseHeader(header.getName(), header.getValue());
							}
						}
						if(log.isInfoEnabled())
							log.info(request.getOriginalURL() + " get from public cache");
						
						String ifModifiedSince = request.getRequestHeader("if-modified-since");
						if(ifModifiedSince == null || "Thu, 01 Jun 1970 00:00:00 GMT".equals(request.getRequestHeader("if-modified-since"))){
							doFilterChain(request, new ByteArrayInputStream(cache.getBodyBytes()));
							return HttpStatus.SC_OK;
						}else{
							return HttpStatus.SC_NOT_MODIFIED;
						}
					}else{
						if(log.isInfoEnabled())
							log.info(request.getOriginalURL() + " delete from public cache by timeout");
						CacheService.getHandle().deleteCacheByUrl(request.getOriginalURL());
					}
				}
			}
		}
		
		return 0;
	}
	public final int invoke(HttpClient client, HttpMethod method, ProxyRequest request)throws Exception {
		int preStatus = prepareInvoke( client,method,request );
		switch(preStatus){
			case 0:
				break;
			case EXECUTE_POST_STATUS:
				doFilterChain(request, request.getResponseBody());
			default:
				return preStatus;
		}
		// copy headers sent target server
		List ignoreHeaderNames = request.getIgnoreHeaders();
		List allowedHeaderNames = request.getAllowedHeaders();
		boolean allowAllHeader = false;

		Proxy proxy = request.getProxy();
		if( proxy != null ) {
			allowAllHeader = proxy.isAllowAllHeader();
			if( !allowAllHeader )
				allowedHeaderNames.addAll( proxy.getAllowedHeaders() );
		}
		
		AuthenticatorUtil.doAuthentication( client,method,request );
		
		StringBuffer headersSb = new StringBuffer();
		for (String name : request.getRequestHeaders().keySet()) {

			String value = request.getRequestHeader(name);
			String lowname = name.toLowerCase();

			if (!allowAllHeader && !allowedHeaderNames.contains(lowname))
				continue;

			if (ignoreHeaderNames.contains(lowname))
				continue;

			if("cookie".equalsIgnoreCase(name)){
				if (proxy.getSendingCookies() != null) {
					value = RequestUtil
					.removeCookieParam(value, proxy.getSendingCookies());
				}
			}

			if("if-modified-since".equalsIgnoreCase(name) && "Thu, 01 Jun 1970 00:00:00 GMT".equals(value))
				continue;

			method.addRequestHeader(new Header(name, value));
			headersSb.append(name + "=" + value + ",  ");
		}
		
		int cacheStatus = getCache( client,method,request );
		if( cacheStatus != 0 )
			return cacheStatus;
		
		if (log.isInfoEnabled())
			log.info("RequestHeader: " + headersSb);

		
		// execute http method and process redirect
		method.setFollowRedirects(false);

		client.executeMethod(method);

		int statusCode = method.getStatusCode();

		for (int i = 0;
					   statusCode == HttpStatus.SC_MOVED_TEMPORARILY
					|| statusCode == HttpStatus.SC_MOVED_PERMANENTLY
					|| statusCode == HttpStatus.SC_SEE_OTHER
					|| statusCode == HttpStatus.SC_TEMPORARY_REDIRECT
			;i++) {

			// connection release
			method.releaseConnection();
			
			if (i == 5) {
				log.error("The circular redirect is limited by five times.");
				return 500;
			}

			Header location = method.getResponseHeader("Location");
			String redirectUrl = location.getValue();

			// According to 2,068 1.1 rfc http spec, we cannot appoint the relative URL,
			// but microsoft.com gives back the relative URL.
			if( redirectUrl.startsWith("/")) {
				URI baseURI = method.getURI();
				baseURI.setPath( redirectUrl );

				redirectUrl = baseURI.toString();
			}

			//method.setURI(new URI(redirectUrl, false));
			Header[] headers = method.getRequestHeaders();
			method = new GetMethod(redirectUrl);
			for (int j = 0; j < headers.length; j++) {
				String headerName = headers[j].getName();
				if (!headerName.equalsIgnoreCase("content-length")
						&& !headerName.equalsIgnoreCase("authorization"))
					method.setRequestHeader(headers[j]);
			}
			AuthenticatorUtil.doAuthentication( client,method,request );
			method.setRequestHeader("authorization", request
					.getRequestHeader("Authorization"));
			method.setFollowRedirects(false);
			client.executeMethod(method);
			statusCode = method.getStatusCode();
			request.setRedirectURL( redirectUrl );
			
			if(log.isInfoEnabled())
				log.info("Redirect " +  request.getTargetURL() + " to " + location + ".");
		}

		// copy response headers to proxyReqeust
		Header[] headers = method.getResponseHeaders();
		for (int i = 0; i < headers.length; i++) {
			request.putResponseHeader(headers[i].getName(), headers[i]
					.getValue());
		}

		if(log.isInfoEnabled())
			log.info("Original Status:" +  statusCode);

		// check response code
		if( statusCode == HttpStatus.SC_PROXY_AUTHENTICATION_REQUIRED){
			log.error("Proxy Authentication Required. Confirm ajax proxy setting.");
			throw new Exception("Http Status 407, Proxy Authentication Required. Please contuct System Administrator.");
		}
		if (statusCode == HttpStatus.SC_NOT_MODIFIED
				|| statusCode == HttpStatus.SC_RESET_CONTENT){
			return statusCode;
		}else if (statusCode < 200 || statusCode >= 300) {
			request.setResponseBody(method.getResponseBodyAsStream());
			return statusCode;
		}

		// process response body
		InputStream responseStream = null;
		if(statusCode != HttpStatus.SC_NO_CONTENT){
			if(request.allowUserPublicCache()){
				byte[] responseBody = method.getResponseBody();
				
				Map<String,List<String>> responseHeaders = request.getResponseHeaders();
				if( request.getRedirectURL() != null )
					responseHeaders.put("X-IS-REDIRECTED-FROM",Arrays.asList( new String[]{ request.getRedirectURL() }));
				if(method instanceof GetMethod){
					putCache(request.getOriginalURL(),new ByteArrayInputStream(responseBody), responseHeaders );					
				}

				responseStream = new ByteArrayInputStream(responseBody);
			}else{
				responseStream = method.getResponseBodyAsStream();
			}
		}
		doFilterChain(request, responseStream);

		return statusCode != HttpStatus.SC_NO_CONTENT ? method.getStatusCode() : 200;
	}

	private void doFilterChain(ProxyRequest request, InputStream responseStream) throws Exception{
		for(int i = 0; i < filterChain.size(); i++){
			ProxyFilter filter = (ProxyFilter)filterChain.get(i);
			if(responseStream != null || filter.allow204()){
				try{
					responseStream = filter.postProcess(request, responseStream );
					request.setResponseBody( responseStream );
				}catch(Exception e){
					log.error("Error has occured while post processing.");
					throw new Exception("Error has occured while post processing.", e);
				}
			}
		}

	}

	private void putCache(String url, InputStream responseStream, Map headersMap){
		try {
			String cacheID = CacheService.getHandle().insertCache(
					url,
					responseStream,
					headersMap);
			if(log.isInfoEnabled())
				log.info(url + " save as public cache : id = " + cacheID);
		} catch (Exception e){
			log.error("",e);
			//response.sendError(500, e.getMessage());
		}
	}
}
