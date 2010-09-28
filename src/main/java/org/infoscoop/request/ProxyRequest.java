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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.MultiHashMap;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpVersion;
import org.apache.commons.httpclient.NTCredentials;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.OptionsMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.request.filter.ProxyFilterContainer;
import org.infoscoop.request.proxy.Proxy;
import org.infoscoop.request.proxy.ProxyConfig;
import org.infoscoop.util.SpringUtil;

public class ProxyRequest{
	
	private static Log log = LogFactory.getLog(ProxyRequest.class);
	
	public static final String ALLOW_CIRCULAR_REDIRECT = "X-IS-ALLOWCIRCULARREDIRECT";
	public static final String DISABLE_CACHE = "X-IS-DISABLE-CACHE";
	
	/**
	 * Socket Connection Timeout 
	 * Defult value is 14000 mill second.
	 */
	public static final String SOCKET_CONNECTION_TIMEOUT_PROPERTY = "infoscoop.proxy.socket.connection.timeout";
	private static int SOCKET_CONNECTION_TIMEOUT = 14000;
	
	//private static Map filterMap = new HashMap();
	
	private static Pattern pathsPattern = Pattern.compile("([\\w]+://[^/.]+(?:\\.[^/.]+)*)((?:/[^/?#]+)*)(.*)");
	
	static{
		String socketConnectionTimeout = System.getProperty(SOCKET_CONNECTION_TIMEOUT_PROPERTY);
		if(socketConnectionTimeout != null){
			SOCKET_CONNECTION_TIMEOUT = Integer.parseInt(socketConnectionTimeout);			
		}
	}
	
	HttpMethod executedMethod;

    /**
     * The url that sent from the client.
     */
    private String originalURL;
    
	/**
	 * The url that resolved the Japanese domain.
	 */
	private String escapedOriginalURL;
	
	/**
	 * The url to exexute really.
	 */
	private String targetURL;
    private String filterType;

    private int timeout;
    private Proxy proxy;
    
    private Map<String, List<String>> requestHeaders = new HeadersMap();
    private InputStream requestBody;

    private Map<String, List<String>> responseHeaders = new HeadersMap();
    private InputStream responseBody;
   // private ProxyFilter filter;

	private String portalUid;
    
    private Enumeration locales;
    
    private List ignoreHeaderNames = new ArrayList();
    private List allowedHeaderNames = new ArrayList();
    
    private String filterEncoding;
    
    private Map filterParameters = new HashMap();
    
    private String redirectURL;
    
    private OAuthConfig oauthConfig;
    
    private String targetJSessionId;
    
    public String getTargetJSessionId() {
		return targetJSessionId;
	}

    public ProxyRequest( String url, String filterType){
        this.filterType = filterType;
        this.originalURL = url;
		this.escapedOriginalURL = escapeURL( this.originalURL );
        if( log.isInfoEnabled() )
               log.info("Request url: " + this.originalURL);
        
        ProxyConfig proxyConfig = ProxyConfig.getInstance();
        if(proxyConfig != null){
        	this.proxy = proxyConfig.resolve( this.originalURL );
        	
        	this.targetURL = escapeURL( proxy.getUrl() );
        }else{
        	this.targetURL = escapeURL( this.escapedOriginalURL );
        }
		if (log.isInfoEnabled())
			log.info("Escaped url=" + this.escapedOriginalURL + ", filter="
					+ this.filterType);
    	
    	ignoreHeaderNames.add("accept-encoding");
    	ignoreHeaderNames.add("content-length");
    	ignoreHeaderNames.add("authtype");
    	ignoreHeaderNames.add("authcredentialid");
    	ignoreHeaderNames.add(Authenticator.UID_PARAM_NAME.toLowerCase());
    	ignoreHeaderNames.add(Authenticator.PASSWD_PARAM_NAME.toLowerCase());
    	
    	this.putResponseHeader("X-IS-INTRANET", Boolean.toString(this.proxy.isIntranet()));
	}
    
	public void setTimeout(int timeout){
    	if(log.isInfoEnabled())
    		log.info("Request timeout: " + timeout);
		this.timeout = timeout;
	}
	   
	public boolean allowUserPublicCache(){		
		return (
				(proxy.getCacheLifeTime()>0)&& 
				this.getRequestHeader(DISABLE_CACHE) == null && 
				this.getRequestHeaders("authCredentialId") == null &&
				this.getRequestHeaders("authType") == null
		);
	}
	
    /**
     * The header that peculiar to ProxyRequest.
     * 1) ALLOW_CIRCULAR_REDIRECT : true / false
     * @param name
     * @param value
     */
    public void putRequestHeader(String name, String value){
		List<String> headers = this.getRequestHeaders(name);
		if(headers == null){
			headers = new ArrayList<String>();
		}
		headers.add(value);// Cannot put headers directly 
		this.requestHeaders.put(name, headers);
    }
    
	public void setRequestHeader(String name, String value) {
		List<String> headers = this.getRequestHeaders(name);
		if (headers == null) {
			headers = new ArrayList<String>();
		} else {
			headers.clear();
		}
		headers.add(value);// Cannot put headers directly
		this.requestHeaders.put(name, headers);
	}
    
    public Map<String, List<String>> getRequestHeaders(){
		return this.requestHeaders;
    }

    public List<String> getRequestHeaders(String name) {
		return this.requestHeaders.get(name);
	}
    
    public String getRequestHeader(String name) {
		List<String> headers = getRequestHeaders(name);
		if(headers == null)
			return null;
		else if(headers.isEmpty()){
			return null;
		}else 
			return headers.get(0);
	}
    
    public String getTargetURL(){
        return this.targetURL;
    }
	
	public static String getAbsoluteURL( String baseUrl,String url ) {
		if (url.indexOf("://") > 0)
			return url;
		if (url.startsWith("/")) {
			int index = baseUrl.indexOf("://");
			String prefix = baseUrl.substring(0, baseUrl.indexOf("/", index + 3));
			
			return prefix + url;
		}
		String prefix = baseUrl.substring(0, baseUrl.lastIndexOf("/"));
		
		return prefix + "/" + url;
	}
    
    public static String escapeURL( String urlString ) {
    	urlString = urlString.trim().replaceAll(" ", "%20");//If there is not this process, " " becomes "+".
    	urlString = escapeHostname( urlString );
    	urlString = escapePath( urlString );
    	urlString = escapeQuery( urlString );
    	
    	return urlString;
    }
    private static String escapeHostname( String urlString ) {
    	if( Float.parseFloat( System.getProperty("java.specification.version")) < 1.5f ) {
    		return urlString;
    	}

    	int domainStart = urlString.indexOf("://");
    	if( domainStart < 0 )
    		return urlString;
    	
    	domainStart += 3;
    	int portStart = urlString.indexOf(":",domainStart );
    	int domainEnd = urlString.indexOf("/",domainStart );
    	if( domainEnd < 0 )
    		domainEnd = urlString.length();
    	
    	if( !( portStart < 0 ) && portStart < domainEnd )
    		domainEnd = portStart;
    	
    	String alphadigit = "[a-zA-Z0-9\\-]+"; // http hostname
		String hostname = urlString.substring( domainStart,domainEnd );
		if( hostname.matches( alphadigit+"(\\."+alphadigit+")*"))
			return urlString;
		
		String[] domainNames = hostname.split("\\.");
		StringBuffer buf = new StringBuffer();
		
		buf.append( urlString.substring(0,domainStart) );
		for( int i=0;i<domainNames.length;i++ ) {
			String domainName = domainNames[i];
			if( !domainName.matches( alphadigit ) ) {
				try {
					Class clazz = Class.forName("gnu.inet.encoding.Punycode");
					Method method = clazz.getMethod("encode",new Class[]{ String.class });
					
					domainName = "xn--"+method.invoke( clazz,new Object[]{ domainName } );
				} catch( Exception ex ) {
					log.error("Escape URL String Failed: "+urlString );
					log.error("", ex );
					
					return urlString;
				}
				
			}
			
			buf.append( domainName );
			if( i < domainNames.length -1 )
				buf.append(".");
		}
		
		if( domainEnd < urlString.length() )
			buf.append( urlString.substring(domainEnd) );
		
		return buf.toString();
	}
    private static String escapePath( String urlString ) {
		Matcher matcher = pathsPattern.matcher( urlString );
		if( !matcher.matches() )
	    	return urlString;
		
		StringBuffer buf = new StringBuffer();
		buf.append( matcher.group(1));
		
		String[] paths = matcher.group(2).split("/");
		for( int j=0;j<paths.length;j++ ) {
			if( paths[j].length() > 0 ) {
				buf.append("/").append(urlEncode(paths[j]));				
			}
		}
		
		if( matcher.groupCount() == 3 )
			buf.append( matcher.group(3));
		
		return buf.toString();
    }
    
    private static String escapeQuery(String urlString){
    	String[] paths = urlString.split("\\?");
    	if(paths.length > 1){
    		StringBuffer buf = new StringBuffer();
    		buf.append(paths[0]).append("?");
    		
    		String[] querys = paths[1].split("&");
    		for(int i = 0; i < querys.length; i++){
        		String[] paramValue = querys[i].split("=");
				buf.append(urlEncode(paramValue[0]));
				if( querys[i].indexOf("=") >= 0 )
					buf.append("=");
				
				if(paramValue.length > 1){
					String value = paramValue[1];
					
					buf.append(urlEncode(value));
				}
				if( i < querys.length -1 )
					buf.append("&");
    		}
    		return buf.toString();
    	}else{
    		return urlString;
    	}
		
    }
    
    private static String urlEncode(String value){
    	StringBuffer buf = new StringBuffer();
    	for( int j=0;j<value.length();j++ ) {
			char c = value.charAt(j);
			
			if( ( c == '%' )&&( value.length() > j +2 )&&
					isHexChar( value.charAt( j+1 ) ) &&
					isHexChar( value.charAt( j+2 ) )) {
				buf.append( c ).append( value.charAt( j+1 )).append( value.charAt( j+2 ));
				j += 2;
			} else {
				try {
					buf.append( URLEncoder.encode( String.valueOf( value.charAt(j)),"UTF-8"));
				} catch( UnsupportedEncodingException ex ) {
					//ignore
				}
			}
		}
    	return buf.toString();
    }
    
    private static boolean isHexChar( char c ) {
    	c = Character.toLowerCase( c );
    	if( Character.isDigit( c ) ||(( 'a' <= c )&&( c <= 'f') ) )
    		return true;
    	
    	return false;
    }
    
    public void setReqeustBody(InputStream body){
        this.requestBody = body;
    }
    
    public InputStream getRequestBody(){
    	return this.requestBody;
    }
    
    //public ProxyFilter getProxyFilter(){
    //	return filter;
    //}

	private HttpClient newHttpClient() {
		HttpConnectionManagerParams params = new HttpConnectionManagerParams();
		if(this.timeout > 0){
			params.setConnectionTimeout(this.timeout);	
		}else{
			params.setConnectionTimeout(SOCKET_CONNECTION_TIMEOUT);
		}
		HttpConnectionManager manager = new SimpleHttpConnectionManager();
		manager.setParams(params);
		HttpClient client = new HttpClient(manager);
		client.getParams().setVersion( new HttpVersion( 1,1 ));
		
		if (proxy != null && proxy.isUseProxy()) {
			if (log.isInfoEnabled())
				log.info("Proxy=" + proxy.getHost() + ":"
						+ proxy.getPort() + ", authentication=" + proxy.needsProxyAuth());
			
			client.getHostConfiguration().setProxy(proxy.getHost(),
					proxy.getPort());
			
			if(proxy.needsProxyAuth()){
				client.getParams().setAuthenticationPreemptive(true);
				client.getState().setProxyCredentials(
						new AuthScope(proxy.getHost(), proxy.getPort()),
						proxy.getProxyCredentials()
				);
			}
			
		}
		client.getParams().setParameter("http.socket.timeout",
		        new Integer(this.timeout));
		
		String allowCircularRedirect = 
			this.getRequestHeader(ALLOW_CIRCULAR_REDIRECT) == null ? "false" : this.getRequestHeader(ALLOW_CIRCULAR_REDIRECT);
		
		if(Boolean.valueOf(allowCircularRedirect).booleanValue()){
			if(log.isInfoEnabled())log.info("Circular redirect on");
			client.getParams().setParameter(HttpClientParams.ALLOW_CIRCULAR_REDIRECTS, Boolean.TRUE);
		}
		return client;
	}
	
	public int executeGet() throws Exception{
		GetMethod method = null;
		try{
			HttpClient client = this.newHttpClient(); 
			method = new GetMethod(this.getTargetURL());
			method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
					new DefaultHttpMethodRetryHandler(1, false));
			ProxyFilterContainer filterContainer = (ProxyFilterContainer)SpringUtil.getBean(filterType);
			return filterContainer.invoke(client, method, this);
		} catch (ProxyAuthenticationException e) {
			if (e.isTraceOn()) {
				log.error(this.getTargetURL());
				log.error("", e);
			} else {
				log.warn(this.getTargetURL() + " : " + e.getMessage());
			}
			return 401;
		}
	}
	
	public int executePost() throws Exception{
		PostMethod method = null;
		try{
			HttpClient client = this.newHttpClient(); 
			method = new PostMethod(this.getTargetURL());
			method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
					new DefaultHttpMethodRetryHandler(1, false));
			if(this.getRequestBody() != null)
				method.setRequestEntity(new InputStreamRequestEntity(this.getRequestBody()));
			ProxyFilterContainer filterContainer = (ProxyFilterContainer)SpringUtil.getBean(filterType);
			return filterContainer.invoke(client, method, this);
		} catch (ProxyAuthenticationException e) {
			if (e.isTraceOn()) {
				log.error(this.getTargetURL());
				log.error("", e);
			} else {
				log.warn(this.getTargetURL() + " : " + e.getMessage());
			}
			return 401;
		}
	}
	
	public int executePut() throws Exception{
		PutMethod method = null;
		try{
			HttpClient client = this.newHttpClient(); 
			method = new PutMethod(this.getTargetURL());
			method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
					new DefaultHttpMethodRetryHandler(1, false));
			if(this.getRequestBody() != null)
				method.setRequestEntity(new InputStreamRequestEntity(this.getRequestBody()));
			ProxyFilterContainer filterContainer = (ProxyFilterContainer)SpringUtil.getBean(filterType);
			return filterContainer.invoke(client, method, this);
		} catch (ProxyAuthenticationException e) {
			if (e.isTraceOn()) {
				log.error(this.getTargetURL());
				log.error("", e);
			} else {
				log.warn(this.getTargetURL() + " : " + e.getMessage());
			}
			return 401;
		}
	}
	
	private static class ReportMethod extends PostMethod{
		public ReportMethod(String url){
			super(url);
		}
		public String getName(){
			return "REPORT";
		}
	}
	
	private static class PropFindMethod extends PostMethod {
		public PropFindMethod (String url) {
			super(url);
		}
		public String getName() {
			return "PROPFIND";
		}
	}
	public int executeReport() throws Exception {
		ReportMethod method = null;
		try{
			HttpClient client = this.newHttpClient(); 
			method = new ReportMethod(this.getTargetURL());
			method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
					new DefaultHttpMethodRetryHandler(1, false));
			if(this.getRequestBody() != null)
				method.setRequestEntity(new InputStreamRequestEntity(this.getRequestBody()));
			ProxyFilterContainer filterContainer = (ProxyFilterContainer)SpringUtil.getBean(filterType);
			return filterContainer.invoke(client, method, this);
		} catch (ProxyAuthenticationException e) {
			if (e.isTraceOn()) {
				log.error(this.getTargetURL());
				log.error("", e);
			} else {
				log.warn(this.getTargetURL() + " : " + e.getMessage());
			}
			return 401;
		}
	}
		
	public int executeDelete() throws Exception{
		DeleteMethod method = null;
		try{
			HttpClient client = this.newHttpClient(); 
			method = new DeleteMethod(this.getTargetURL());
			method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
					new DefaultHttpMethodRetryHandler(1, false));
			ProxyFilterContainer filterContainer = (ProxyFilterContainer)SpringUtil.getBean(filterType);
			return filterContainer.invoke(client, method, this);
		} catch (ProxyAuthenticationException e) {
			if (e.isTraceOn()) {
				log.error(this.getTargetURL());
				log.error("", e);
			} else {
				log.warn(this.getTargetURL() + " : " + e.getMessage());
			}
			return 401;
		}
	}
	
	public int executePropFind() throws Exception {
		PropFindMethod method = null;
		try{
			HttpClient client = this.newHttpClient(); 
			method = new PropFindMethod(this.getTargetURL());
			method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
					new DefaultHttpMethodRetryHandler(1, false));
			if(this.getRequestBody() != null)
				method.setRequestEntity(new InputStreamRequestEntity(this.getRequestBody()));
			ProxyFilterContainer filterContainer = (ProxyFilterContainer)SpringUtil.getBean(filterType);
			return filterContainer.invoke(client, method, this);
		} catch (ProxyAuthenticationException e) {
			if (e.isTraceOn()) {
				log.error(this.getTargetURL());
				log.error("", e);
			} else {
				log.warn(this.getTargetURL() + " : " + e.getMessage());
			}
			return 401;
		}
	}

	public int executeOptions() throws Exception {
		OptionsMethod method = null;
		try{
			HttpClient client = this.newHttpClient(); 
			method = new OptionsMethod(this.getTargetURL());
			method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
					new DefaultHttpMethodRetryHandler(1, false));
			ProxyFilterContainer filterContainer = (ProxyFilterContainer)SpringUtil.getBean(filterType);
			return filterContainer.invoke(client, method, this);
		} catch (ProxyAuthenticationException e) {
			if (e.isTraceOn()) {
				log.error(this.getTargetURL());
				log.error("", e);
			} else {
				log.warn(this.getTargetURL() + " : " + e.getMessage());
			}
			return 401;
		}
	}
	
	public void setResponseBody(InputStream body){
		this.responseBody = body;
	}
	public InputStream getResponseBody(){
		return this.responseBody;
	}
	
	public String getResponseBodyAsString( String charset ) throws IOException {
		if( responseBody == null )
			return null;
		
		BufferedReader reader = new BufferedReader( new InputStreamReader( responseBody,charset ));
		
		StringBuffer buf = new StringBuffer();
		int c;
		while(( c = reader.read()) > 0 )
			buf.append( ( char )c );
		
		return buf.toString();
	}
	
	public void putResponseHeader(String name, String value){
		String updateHeaderName = name;
		for(String headerName : this.responseHeaders.keySet()){
			if(headerName.equalsIgnoreCase(name)){
				updateHeaderName = headerName;
			}
		}
		if("set-cookie".equalsIgnoreCase(updateHeaderName)){
			String[] cookies =  value.split(";");
			for(int i = 0; i < cookies.length; i++){
				String[] nameValue = cookies[i].split("=");
				if(nameValue.length != 2)continue;
				if("JSESSIONID".equals(nameValue[0].trim())){
					this.targetJSessionId = nameValue[1].trim();
				}
			}
		}

		if("www-authenticate".equalsIgnoreCase(updateHeaderName)){
			if(this.responseHeaders.containsKey(updateHeaderName.toLowerCase()))
				return;
		}
		this.getResponseHeaders(name).add(value);
	}
	
	public List<String> getResponseHeaders(String name) {
		List<String> headers = this.responseHeaders.get(name);
		if(headers == null){
			this.responseHeaders.put(name, new ArrayList<String>());
			return this.responseHeaders.get(name);
		}else 
			return headers;
	}

	public String getResponseHeader(String name) {
		List<String> headers = this.getResponseHeaders(name);
		if(headers.isEmpty())
			return null;
		else 
			return headers.get(0);
	}
	
    public Map<String, List<String>> getResponseHeaders(){
		return this.responseHeaders;
    }
    
	public String getEscapedOriginalURL() {
		return this.escapedOriginalURL;
	}
	
	public String getOriginalURL(){
		return this.originalURL;
	}
	
	public String getRedirectURL() {
		return redirectURL;
	}
	public void setRedirectURL( String redirectURL ) {
		this.redirectURL = redirectURL;
	}
	
	public void close(){
		if(this.executedMethod != null)
			this.executedMethod.releaseConnection();
	}

	public void setPortalUid(String portalUid) {
		this.portalUid = portalUid;
	}
	
	public String getPortalUid() {
		return this.portalUid;
	}

	public void setFilterEncoding(String filterEncoding) {
		this.filterEncoding = filterEncoding;
	}
	
	public String getFilterEncoding() {
		return this.filterEncoding;
	}	
	public void setLocales(Enumeration locales) {
		this.locales = locales;
	}

	public Enumeration getLocales() {
		return this.locales;
	}
	
	public Locale getLocale() {
		return (Locale) this.locales.nextElement();
	}

	public void addIgnoreHeader(String name) {
		this.ignoreHeaderNames.add(name.toLowerCase());
	}

	public void removeIgnoreHeader(String name) {
		this.ignoreHeaderNames.remove(name.toLowerCase());
	}

	public List getIgnoreHeaders() {
		return this.ignoreHeaderNames;
	}
	
	public List getAllowedHeaders() {
		return this.allowedHeaderNames;
	}
	
	//TODO: default access
	public Proxy getProxy() {
		return proxy;
	}
	
	public String getFilterParameter( String key ) {
		return ( String )filterParameters.get( key );
	}
	public void setFilterParameter( String key,String value ) {
		filterParameters.put ( key,value );
	}
	public Map<String,String> getFilterParameters() {
		return filterParameters;
	}

	public static void main(String args[]) throws MalformedURLException{
		HttpConnectionManagerParams params = new HttpConnectionManagerParams();
		params.setConnectionTimeout(3000);
		HttpConnectionManager manager = new SimpleHttpConnectionManager();
		manager.setParams(params);
		HttpClient c = new HttpClient(manager);
		c.getParams().setParameter("http.socket.timeout",
		        new Integer(5000));
		
		Credentials credentials = new NTCredentials("test", "test", "", "");//192.168.233.2INFOSCOOP
		//Credentials credentials = new UsernamePasswordCredentials("INFOSCOOP\test", "test");
		// the scope of the certification.
		URL urlObj = new URL("http://192.168.233.2/index.html");
		AuthScope scope1 = new AuthScope(urlObj.getHost(), urlObj.getPort(), null);
		// set a pair of a scope and an information of the certification.
		c.getState().setCredentials(scope1, credentials);
		
		//GetMethod g = new GetMethod("http://inicio/syanai/rss.do?category=3bb34-f8f19ded28-038fb3114a5cd639e1963371842f83c0&u=bQRF9JnX%2Bm7H0n4iwJJ3ZA%3D%3D");//"http://172.22.113.111");
		//GetMethod g = new GetMethod("http://172.22.113.111");
		GetMethod g = new GetMethod("http://192.168.233.2/index.html");
		g.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler(0, false));
		try {
			c.executeMethod(g);
			System.out.println(g.getStatusCode());
			System.out.println(g.getResponseBodyAsString());
		} catch (HttpException e) {
			log.error("", e);
		} catch (IOException e) {
			log.error("", e);
		}
	}
	
	public static byte[] stream2Bytes(InputStream is){
		if (is == null)
			return new byte[0];
		BufferedInputStream inputStream = new BufferedInputStream(is);
		ByteArrayOutputStream returnStream = new ByteArrayOutputStream();
		int temp;
		try {
			while( (temp = inputStream.read() ) != -1 ){
				returnStream.write(temp);
			}
		} catch (IOException e) {
			log.error("", e);
		}
		return returnStream.toByteArray();
	}
	
    public OAuthConfig getOauthConfig() {
		return oauthConfig;
	}

	public void setOauthConfig(OAuthConfig oauthConfig) {
		this.oauthConfig = oauthConfig;
	}

	public class OAuthConfig {
		String serviceName;
		String requestTokenURL;
		String requestTokenMethod;
		String userAuthorizationURL;
		String accessTokenURL;
		String accessTokenMethod;
		
		String requestToken;
		String accessToken;
		String tokenSecret;
		
		String gadgetUrl;
		String hostPrefix;

		public String getGadgetUrl() {
			return gadgetUrl;
		}

		public void setGadgetUrl(String gadgetUrl) {
			this.gadgetUrl = gadgetUrl;
		}

		public String getHostPrefix() {
			return hostPrefix;
		}

		public void setHostPrefix(String hostPrefix) {
			this.hostPrefix = hostPrefix;
		}

		public OAuthConfig(String serviceName){
			this.serviceName = serviceName;
		}
		
		public void setRequestToken(String requestToken) {
			this.requestToken = requestToken;
		}
		public void setAccessToken(String accessToken) {
			this.accessToken = accessToken;
		}
		public void setTokenSecret(String tokenSecret) {
			this.tokenSecret = tokenSecret;
		}
		
		public void setRequestTokenURL(String requestTokenURL) {
			this.requestTokenURL = requestTokenURL;
		}
		public void setRequestTokenMethod(String requestTokenMethod) {
			this.requestTokenMethod = requestTokenMethod;
		}
		public void setUserAuthorizationURL(String userAuthorizationURL) {
			this.userAuthorizationURL = userAuthorizationURL;
		}
		public void setAccessTokenURL(String accessTokenURL) {
			this.accessTokenURL = accessTokenURL;
		}
		public void setAccessTokenMethod(String accessTokenMethod) {
			this.accessTokenMethod = accessTokenMethod;
		}
	}

}

class HeadersMap implements Map<String, List<String>>{
	private static final long serialVersionUID = 
		"org.infoscoop.request.RequestHeadersMap".hashCode();

	private Map headers = new MultiHashMap();
			

	public void clear() {
		throw new UnsupportedOperationException("no implements");
	}

	public boolean containsKey(Object key) {
		return this.headers.containsKey(((String) key).toLowerCase());
	}

	public boolean containsValue(Object value) {
		throw new UnsupportedOperationException("no implements");
	}

	public Set<Entry<String, List<String>>> entrySet() {
		return headers.entrySet();
	}

	public List<String> get(Object key) {
		return (List<String>) this.headers.get(((String) key).toLowerCase());
	}

	public boolean isEmpty() {
		return this.headers.isEmpty();
	}

	public Set<String> keySet() {
		return this.headers.keySet();
	}

	public List<String> put(String key, List<String> values) {
		List<String> newValues = new ArrayList<String>();
		for(String value: values){
			try {
				newValues.add(new String(value.getBytes("iso-8859-1"), "utf-8"));
			} catch (UnsupportedEncodingException e) {
			}
		}
		return (List<String>) this.headers.put(key.toLowerCase(), newValues);
	}

	public void putAll(Map<? extends String, ? extends List<String>> t) {
		
		for(Entry<? extends String, ? extends List<String>> headers : t.entrySet()){
			this.put(headers.getKey(), headers.getValue());			
		}
		
	}

	public List<String> remove(Object key) {
		return (List<String>) this.headers.remove(((String) key).toLowerCase());
	}

	public int size() {
		return this.headers.size();
	}

	public Collection<List<String>> values() {
		throw new UnsupportedOperationException("no implements");
	}

}
