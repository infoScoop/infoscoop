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
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

import javax.servlet.ServletException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;


import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.infoscoop.request.ProxyRequest;
import org.infoscoop.service.GadgetService;
import org.infoscoop.util.NoOpEntityResolver;
import org.infoscoop.util.XmlUtil;
import org.infoscoop.widgetconf.I18NConverter;
import org.infoscoop.widgetconf.WidgetConfUtil;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class GadgetFilter extends ProxyFilter {
	private static final Log log = LogFactory.getLog(GadgetFilter.class);
	
	private static final String PARAM_MODULE_ID = "__MODULE_ID__";
	private static final String PARAM_STATIC_CONTENT_URL = "__STATIC_CONTENT_URL__";
	private static final String PARAM_HOST_PREFIX = "__HOST_PREFIX__";
	private static final String PARAM_TAB_ID = "__TAB_ID__";
	
	private DocumentBuilderFactory factory;
	
	public GadgetFilter() {
		factory = DocumentBuilderFactory.newInstance();
		factory.setValidating( false );
		
	}
	
	private Document gadget2dom( InputStream responseBody )
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilder builder = factory.newDocumentBuilder();
		builder.setEntityResolver(NoOpEntityResolver.getInstance());
		Document doc = builder.parse(responseBody);
		return doc;
	}

	public static byte[] gadget2html( String baseUrl,Document doc,
			Map<String,String> urlParameters,I18NConverter i18n ) throws Exception {
		XPathFactory xpathFactory = XPathFactory.newInstance();
		XPath xpath = xpathFactory.newXPath();
		
		VelocityContext context = new VelocityContext();
		context.put("baseUrl",baseUrl );
		context.put("content",replaceContentStr( doc,i18n,urlParameters ) );
		
		context.put("widgetId",urlParameters.get( PARAM_MODULE_ID));
		context.put("staticContentURL",urlParameters.get( PARAM_STATIC_CONTENT_URL ));
		context.put("hostPrefix",urlParameters.get( PARAM_HOST_PREFIX ));
		context.put("tabId",urlParameters.get( PARAM_TAB_ID ));
		context.put("gadgetUrl",urlParameters.get( "url" ));
		
		// ModulePrefs
		context.put("requires", getRequires( xpath,doc ));
		context.put("oauthServicesJson", getOAuthServicesJson( xpath,doc ));
		context.put("oauth2ServicesJson", getOAuth2ServicesJson( xpath,doc ));
		
		context.put("i18nMsgs", new JSONObject( i18n.getMsgs()));
		context.put("userPrefs",getUserPrefs( urlParameters ));
		context.put("dir",i18n.getDirection());
		
		StringWriter writer = new StringWriter();
		Template template = Velocity.getTemplate("gadget-html.vm", "UTF-8");
		template.merge( context,writer );
		
		return writer.toString().getBytes("UTF-8");
	}

	private static Map<String,String> getUrlParameters( Map<String,String> filterParameters ) {
		Map<String,String> parameters = new HashMap<String,String>();
		for( String key : filterParameters.keySet()) {
			String value = filterParameters.get( key );
			
			if( key.startsWith("up_") && key.length() > 3 )
				key = "__UP_"+key.substring(3)+"__";
			
			parameters.put( key,value );
		}
		
		return parameters;
	}
	
	private static String replaceContentStr( Document doc,I18NConverter i18n,
			Map<String,String> urlParameters ) throws Exception {
		NodeList contentNodeList = doc.getElementsByTagName("Content");
		if( contentNodeList.getLength() == 0 )
			return "";
		
		Node content = contentNodeList.item(0);
		String contentStr = XmlUtil.getChildText(content);
		
		contentStr = i18n.replace(contentStr);
		
		for( Map.Entry<String,String> param : urlParameters.entrySet() ) {
			String key = param.getKey();
			if( !PARAM_MODULE_ID.equals( key ) &&
				!( key.startsWith("__UP_") && key.endsWith("__")))
				continue;
			
			contentStr = contentStr.replaceAll( key,Matcher.quoteReplacement( param.getValue() ));
		}
		
		return contentStr;
	}
	
	private static JSONObject getRequires( XPath xpath,Document doc ) throws XPathExpressionException,JSONException {
		JSONObject requires = new JSONObject();
		requires.put("core",new JSONObject());
		requires.put("core.io",new JSONObject());
		requires.put("rpc",new JSONObject());
		
		NodeList requireNodes = ( NodeList )xpath.evaluate(
				"/Module/ModulePrefs/Require|/Module/ModulePrefs/Optional",doc,XPathConstants.NODESET );
		for (int i = 0; i < requireNodes.getLength(); i++) {
			Element require = ( Element )requireNodes.item(i);
			
			requires.put( require.getAttribute("feature").toLowerCase(),getRequireParams( xpath,require ));
		}
		
		requires.put("infoscoop",new JSONObject());
		
		return requires;
	}
	
	private static JSONObject getRequireParams( XPath xpath,Element require ) throws XPathExpressionException,JSONException {
		JSONObject params = new JSONObject();

		NodeList paramNodes = ( NodeList )xpath.evaluate("Param[@name]",require,XPathConstants.NODESET );
		for( int j=0;j<paramNodes.getLength();j++ ) {
			Element param = ( Element )paramNodes.item( j );
			
			params.put( param.getAttribute("name").toLowerCase(),param.getTextContent() );
		}
		
		return params;
	}
		
	private static String getOAuthServicesJson(XPath xpath, Document doc) throws XPathExpressionException, JSONException {
		JSONObject services = new JSONObject();
		NodeList serviceNodes = ( NodeList )xpath.evaluate(
				"/Module/ModulePrefs/OAuth/Service",doc,XPathConstants.NODESET );
		for( int j=0;j<serviceNodes.getLength();j++ ) {
			Element serviceEl = ( Element )serviceNodes.item( j );
			JSONObject service = new JSONObject();
			NodeList nodeList = serviceEl.getElementsByTagName("Request");
			if(nodeList.getLength() > 0){
				Element requestEl = (Element)nodeList.item(0);
				service.put("requestTokenURL", requestEl.getAttribute("url"));
				String method = requestEl.getAttribute("method");
				if(method != null)
					service.put("requestTokenMethod", requestEl.getAttribute("method"));
			}
			nodeList = serviceEl.getElementsByTagName("Authorization");
			if(nodeList.getLength() > 0){
				Element requestEl = (Element)nodeList.item(0);
				service.put("userAuthorizationURL", requestEl.getAttribute("url"));
			}
			nodeList = serviceEl.getElementsByTagName("Access");
			if(nodeList.getLength() > 0){
				Element requestEl = (Element)nodeList.item(0);
				service.put("accessTokenURL", requestEl.getAttribute("url"));
				String method = requestEl.getAttribute("method");
				if(method != null)
					service.put("accessTokenMethod", requestEl.getAttribute("method"));
			}
			services.put(serviceEl.getAttribute("name"), service);
		}
		return services.toString();
	}
	
	private static String getOAuth2ServicesJson(XPath xpath, Document doc) throws XPathExpressionException, JSONException {
		JSONObject services = new JSONObject();
		NodeList serviceNodes = ( NodeList )xpath.evaluate(
				"/Module/ModulePrefs/OAuth2/Service",doc,XPathConstants.NODESET );
		for( int j=0;j<serviceNodes.getLength();j++ ) {
			Element serviceEl = ( Element )serviceNodes.item( j );
			JSONObject service = new JSONObject();
			NodeList nodeList = serviceEl.getElementsByTagName("Authorization");
			if(nodeList.getLength() > 0){
				Element requestEl = (Element)nodeList.item(0);
				service.put("userAuthorizationURL", requestEl.getAttribute("url"));
			}
			nodeList = serviceEl.getElementsByTagName("Token");
			if(nodeList.getLength() > 0){
				Element requestEl = (Element)nodeList.item(0);
				service.put("accessTokenURL", requestEl.getAttribute("url"));
			}
			service.put("scope", serviceEl.getAttribute("scope"));
			services.put(serviceEl.getAttribute("name"), service);
		}
		return services.toString();
	}
	
	private static JSONObject getUserPrefs( Map<String,String> filterParameters ) {
		JSONObject userPrefs = new JSONObject();
		for( String key : filterParameters.keySet() ) {
			if( key.startsWith("__UP_") ) {
				try {
					userPrefs.put( key.substring(5,key.length() -2 ),filterParameters.get( key ));
				} catch( JSONException ex ) {
					throw new RuntimeException( ex );
				}
			}
		}
		
		return userPrefs;
	}
	
	protected int preProcess(HttpClient client, HttpMethod method, ProxyRequest request) {
//		String uploadType = request.getFilterParameter("uploadType");
		String uploadType = null;
		String url = request.getOriginalURL();
		if( url.startsWith("upload__"))
			uploadType = url.substring( 8,url.lastIndexOf("/"));
		
		if( uploadType != null && !"".equals( uploadType )) {
			Map<String,String> urlParameters = getUrlParameters( request.getFilterParameters() );
			WidgetConfUtil.GadgetContext context = new WidgetConfUtil.GadgetContext().setUrl(url);
			try {
				InputStream data = context.getUploadGadget(urlParameters.get( PARAM_HOST_PREFIX ));
				if( data == null )
					return 404;


				request.setResponseBody(data);

				request.setResponseBody( postProcess( request, request.getResponseBody() ));

				return 200;
			} catch( IOException ex ) {
				throw new RuntimeException( ex );
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				return 500;
			}
		}
		
		return 0;
	}

	protected InputStream postProcess(
			ProxyRequest request, InputStream responseStream ) throws IOException {
		request.putResponseHeader("Cache-Control", "no-cache");
		
		byte[] responseBytes = null;
		try {
			int timeout = 0;
			try{
				String timeoutHeader = request.getRequestHeader("MSDPortal-Timeout");
				timeout = Integer.parseInt( timeoutHeader ) - 1000;
			} catch(NumberFormatException e){ }
			
			Document doc = gadget2dom( responseStream );

			Map<String,String> urlParameters = getUrlParameters( request.getFilterParameters() );
			
			WidgetConfUtil.GadgetContext context = new WidgetConfUtil.GadgetContext()
				.setTimeout( timeout )
				.setUrl( request.getOriginalURL() )
				.setHostPrefix( urlParameters.get( PARAM_HOST_PREFIX ));
			
			responseBytes = gadget2html( context.getBaseUrl(),doc,urlParameters,
					context.getI18NConveter( request.getLocale(),doc ));
		} catch ( Exception ex ) {
			log.error("unexpected error ocurred.", ex );
			
			responseBytes = "unexpected error ocurred.".getBytes("UTF-8");
		}
		//request.setResponseBody(new ByteArrayInputStream(responseBytes));
		
		request.putResponseHeader("Content-Length",Integer.toString( responseBytes.length ));
		request.putResponseHeader("Content-Type","text/html; charset=\"utf-8\"");
		
		return new ByteArrayInputStream(responseBytes);
	}
}
