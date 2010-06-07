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

package org.infoscoop.widgetconf;



import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.request.ProxyRequest;
import org.infoscoop.service.GadgetService;
import org.infoscoop.util.NoOpEntityResolver;
import org.infoscoop.util.Xml2Json;
import org.infoscoop.web.ProxyServlet;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * 
 * @author nishiumi
 *
 */
public class WidgetConfUtil {
	public static Xml2Json createWidgetConf2Json() {
		Xml2Json x2j = new Xml2Json();
		x2j.addNamespaceResolver("dax",
				"http://www.beacon-it.co.jp/schema/dax100.xsd");
		String rootPath = "/widgetConfiguration";
		x2j.addPathRule(rootPath, "type", true, false);

		x2j.addPathRule(rootPath + "/WidgetPref", "name", true, false);
		x2j.addPathRule(rootPath + "/WidgetPref/EnumValue", null, true, false);
		x2j.addPathRule(rootPath + "/UserPref", "name", true, false);
		x2j.addPathRule(rootPath + "/UserPref/EnumValue", null, true, false);
		x2j.addPathRule(rootPath + "/Header/menu", null, true, false);
		x2j.addPathRule(rootPath + "/Header/icon", null, true, false);
		x2j.addPathRule(rootPath + "/Maximize/Header/icon", null,
				true, false);
		return x2j;
	}

	public static Xml2Json createGadget2Json() {
		Xml2Json x2j = new Xml2Json();
		String rootPath = "/Module";
		x2j.addPathRule(rootPath + "/ModulePrefs/Require", "feature", true, false);
		x2j.addSkipRule(rootPath + "/ModulePrefs/Locale");
		
		x2j.addPathRule(rootPath + "/ModulePrefs", null, false, false);
		
		x2j.addPathRule(rootPath + "/UserPref", "name", true, false);
		x2j.addPathRule(rootPath + "/UserPref/EnumValue", null, true, false);
		
		x2j.addPathRule(rootPath + "/WidgetPref","name", true, false);
		x2j.addPathRule(rootPath + "/WidgetPref/EnumValue", null, true, false);
		
		x2j.addPathRule(rootPath + "/Header/menu", null, true, false);
		x2j.addPathRule(rootPath + "/Header/icon", null, true, false);
		x2j.addPathRule(rootPath + "/Content", "view", true, false);
		
		x2j.addPathRule(rootPath + "/Maximize/Header/menu", null, true, false);
		x2j.addPathRule(rootPath + "/Maximize/Header/icon", null, true, false);
		
		return x2j;
	}
	
	public static JSONObject widgetConf2JSONObject( Element widgetConf,I18NConverter i18n, boolean useClient ) throws Exception {
		if ( widgetConf == null )
			return new JSONObject();
		
		Xml2Json xml2json = WidgetConfUtil.createWidgetConf2Json();
		if( i18n != null )
			xml2json.setListner( new I18NListener( i18n ));
		
		JSONObject json = xml2json.xml2jsonObj(widgetConf);
		if (useClient) {
			removeServerOnlyPref(json);
		}
		
		if( json.has("WidgetPref"))
			widgetPrefContentToValue( json.getJSONObject("WidgetPref"));

		if( json.has("UserPref"))
			userPrefContentToValue( json.getJSONObject("UserPref"));
		
		return json;
	}
	
	public static JSONObject widgetConf2JSONObject( Element widgetConf,I18NConverter i18n ) throws Exception {
		return widgetConf2JSONObject(widgetConf, i18n, false);
	}
	
	public static JSONObject gadget2JSONObject(Element widgetConf,
			I18NConverter i18n, boolean useClient) throws Exception {
		if ( widgetConf == null )
			return new JSONObject();
		
		Xml2Json xml2json = WidgetConfUtil.createGadget2Json();
		if( i18n != null )
			xml2json.setListner( new I18NListener( i18n ));
		
		JSONObject json = xml2json.xml2jsonObj( widgetConf );
		
		if( json.has("WidgetPref"))
			widgetPrefContentToValue( json.getJSONObject("WidgetPref"));

		if( json.has("UserPref"))
			userPrefContentToValue( json.getJSONObject("UserPref"));
		
		if (useClient) {
			removeServerOnlyPref(json);
		}
		
		return json;
	}
	
	public static JSONObject gadget2JSONObject(Element widgetConf,
			I18NConverter i18n) throws Exception {
		return gadget2JSONObject(widgetConf, i18n, false);
	}
	
	private static void removeServerOnlyPref(JSONObject widgetConf)
			throws JSONException {
		JSONObject widPrefs = widgetConf.optJSONObject("WidgetPref");
		if (widPrefs != null) {
			List<String> removeKeys = new ArrayList<String>();
			for (Iterator<String> keys = widPrefs.keys(); keys.hasNext();) {
				String key = keys.next();
				JSONObject widPref = widPrefs.getJSONObject(key);
				if (widPref.has("useServerOnly")
						&& widPref.getString("useServerOnly").equalsIgnoreCase(
								"true")) {
					removeKeys.add(key);
				}
			}
			for (Iterator<String> keys = removeKeys.iterator(); keys.hasNext();) {
				widPrefs.remove(keys.next());
			}
		}
	}
	
	public static JSONObject gadgetJSONtoPortalGadgetJSON( JSONObject jsonObj ) throws JSONException {
		if(!jsonObj.has("Header"))
			jsonObj.put("Header", new JSONObject());
		if(jsonObj.has("ModulePrefs")){
			JSONObject modulePrefs = jsonObj.getJSONObject("ModulePrefs");
			if(!modulePrefs.has("height")){
				jsonObj.put("height", "200px");
			}
		}

		if( !jsonObj.has("UserPref"))
			jsonObj.put("UserPref",new JSONObject() );

		if(jsonObj.has("Content")){
			String firstView = null;
			JSONObject homeContent = null;
			JSONObject noneViewContent = null;
			JSONObject canvasContent = null;
			
			JSONObject contents = jsonObj.getJSONObject("Content");
			for(java.util.Iterator it = contents.keys();it.hasNext();){
				String view = (String)it.next();
				if(firstView == null)firstView = view;
				if(view.indexOf("home") >= 0){
					homeContent = contents.getJSONObject(view);
				}else if("".equals(view.trim()))
					noneViewContent = contents.getJSONObject(view);	

				if(view.indexOf("canvas") >= 0){
					canvasContent = contents.getJSONObject(view);

				}

			}

			if(homeContent != null){
				jsonObj.put("Content", homeContent);	
			}else if (noneViewContent != null){
				jsonObj.put("Content", noneViewContent);
			}else{
				jsonObj.put("Content", contents.getJSONObject(firstView));
			}
			
			JSONObject content = jsonObj.getJSONObject("Content");
			if( content.has("type") && content.getString("type").equalsIgnoreCase("url") && content.has("href") ){
				content.put("href",ProxyRequest.escapeURL( content.getString("href")) );
			} else if( !content.has("type")) {
				content.put("type","html");
			}
			
			if(canvasContent != null){
				JSONObject maximize = new JSONObject();
				jsonObj.put("Maximize", maximize);
				
				if( canvasContent.has("type") && canvasContent.getString("type").equalsIgnoreCase("url") && canvasContent.has("href")){
					canvasContent.put("href",ProxyRequest.escapeURL( canvasContent.getString("href") ));
				}
				maximize.put("Content",canvasContent );
			}
		} else {
			JSONObject content = new JSONObject();
			content.put("type","html");
			jsonObj.put("Content",content );
		}
		
		boolean isJavascript = jsonObj.has("Content")
				&& jsonObj.getJSONObject("Content").has("type")
				&& jsonObj.getJSONObject("Content").getString("type").equals(
						"javascript");
		
		if (jsonObj.has("WidgetPref") && !isJavascript) {
			JSONObject widgetPrefs = jsonObj.getJSONObject("WidgetPref");
			
			JSONObject userPrefs;
			if( jsonObj.has("UserPref")) {
				userPrefs = jsonObj.getJSONObject("UserPref");
			} else {
				userPrefs = new JSONObject();
			}
			
			for( Iterator keys=widgetPrefs.keys();keys.hasNext(); ) {
				String key = ( String )keys.next();
				if( userPrefs.has( key ))
					continue;

				JSONObject widgetPref = widgetPrefs.getJSONObject( key );
				
				JSONObject userPref = new JSONObject();
				userPref.put("datatype","hidden");
				if( !widgetPref.has("name"))
					continue;
				
				userPref.put("name",widgetPref.getString("name"));
				
				String datatype;
				if( widgetPref.has("datatype")) {
					datatype = widgetPref.getString("datatype");
				} else {
					datatype = "string";
				}
				
				String value = null;
				if(("xml".equals( datatype ) || "json".equals( datatype ))&& widgetPref.has("content")) {
					value = widgetPref.getString("content");
				} else if( widgetPref.has("value")) {
					value = widgetPref.getString("value");
				}
				
				if( value != null ) {
					userPref.put("default_value",value );
					userPrefs.put( key,userPref );
				}
			}
			
			jsonObj.remove("WidgetPref");
		}
		
		if (jsonObj.has("WidgetPref") && isJavascript)
			widgetPrefContentToValue( jsonObj.getJSONObject("WidgetPref"));
		
		if( jsonObj.has("UserPref"))
			userPrefContentToValue( jsonObj.getJSONObject("UserPref"));
		
		return jsonObj;
	}
	public static void widgetPrefContentToValue( JSONObject prefs ) throws JSONException {
		prefContentToValue( prefs,"value");
	}
	public static void userPrefContentToValue( JSONObject prefs ) throws JSONException {
		prefContentToValue( prefs,"default_value");
	}
	public static void prefContentToValue( JSONObject prefs,String valueAttrName ) throws JSONException {
		for( Iterator keys=prefs.keys();keys.hasNext();) {
			String key = ( String )keys.next();
			JSONObject pref = prefs.getJSONObject( key );
			if( !pref.has("name"))
				continue;
			
			if( !pref.has("datatype"))
				continue;
			
			String datatype = pref.getString("datatype");
			if(("xml".equals( datatype ) || "json".equals( datatype ))&& pref.has("content")) {
				pref.put( valueAttrName,pref.getString("content"));
				pref.remove("content");
			}
		}
	}

	public static class GadgetContext {
		private static Log log = LogFactory.getLog(GadgetContext.class);

		private int timeout;
		private String url;
		private String hostPrefix;
		
		private String uploadType;

		public int getTimeout() {
			return timeout;
		}

		public GadgetContext setTimeout(int timeout) {
			this.timeout = timeout;
			return this;
		}

		public String getUrl() {
			return url;
		}

		public GadgetContext setUrl(String url) {
			this.url = url;
			if (url.startsWith("upload__")) {
				uploadType = url.substring(8);
				if( uploadType.indexOf("/") >= 0 )
					uploadType = uploadType.substring(0, uploadType.indexOf("/"));
			}

			return this;
		}

		public GadgetContext setHostPrefix(String hostPrefix) {
			this.hostPrefix = hostPrefix;
			return this;
		}

		public boolean isUploadGadget() {
			return (uploadType != null);
		}

		public String getUploadType() {
			return uploadType;
		}

		public String getBaseUrl() {
			if (uploadType != null)
				return hostPrefix + "/gadget/" + uploadType ;

			return url;
		}

		public I18NConverter getI18NConveter(Locale locale, Document gadgetDoc) {
			MessageBundle.Factory bundleFactory;
			if (uploadType != null) {
				bundleFactory = new MessageBundle.Factory.Upload(timeout,
						uploadType);
			} else {
				bundleFactory = new MessageBundle.Factory.URL(timeout, url);
			}

			return new I18NConverter(locale, bundleFactory.createBundles(gadgetDoc));
		}

		public Document getDocument(HttpServletRequest request) throws Exception {
			InputStream in;
			if (isUploadGadget()) {
				String hostPrefix = request.getParameter("hostPrefix");
				in = getUploadGadget(hostPrefix);
				if(in == null){
					log.error("Gadget[" + uploadType + "]does not exist.");
					throw new Exception("Gadget[" + uploadType + "]does not exist.");
				}
			} else {
				in = getGadget(request);
			}

			DocumentBuilderFactory builderFactory = DocumentBuilderFactory
					.newInstance();
			builderFactory.setValidating(false);
			DocumentBuilder builder = builderFactory.newDocumentBuilder();
			builder.setEntityResolver(NoOpEntityResolver.getInstance());
			// TODO:Encoding

			return builder.parse(in);
		}

		private InputStream getGadget(HttpServletRequest request) throws Exception {
			InputStream is = null;
			ProxyRequest proxyRequest = null;
			try {
				proxyRequest = new ProxyRequest(url, "XML");
				proxyRequest.setLocales(request.getLocales());
				proxyRequest.setPortalUid((String) request.getSession()
						.getAttribute("Uid"));
				proxyRequest.setTimeout((timeout > 0) ? timeout
						: ProxyServlet.DEFAULT_TIMEOUT);

				for (Enumeration headerNames = request.getHeaderNames(); headerNames
						.hasMoreElements();) {
					String name = (String) headerNames.nextElement();
					String value = (String) request.getHeader(name);
					proxyRequest.putRequestHeader(name, value);
				}
				proxyRequest.addIgnoreHeader("user-agent");
				proxyRequest.addIgnoreHeader("accept-encoding");

				int statusCode = proxyRequest.executeGet();

				if (statusCode != 200)
					throw new Exception("gadget url="
							+ proxyRequest.getProxy().getUrl() + ", statucCode="
							+ statusCode);

				if (log.isInfoEnabled())
					log.info("gadget url : " + proxyRequest.getProxy().getUrl());

				is = proxyRequest.getResponseBody();
				is = new BufferedInputStream(is);
			} finally {
				if (proxyRequest != null)
					proxyRequest.close();
			}

			return is;
		}

		public InputStream getUploadGadget(String hostPrefix) throws Exception {
			byte[] gadget = GadgetService.getHandle().selectGadget(uploadType);
			if (log.isDebugEnabled())
				log.debug("gadget : " + gadget);

			if (gadget == null)
				return null;
					
			String gadgetStr = new String(gadget,"UTF-8");
			Pattern pattern = Pattern.compile( "__IS_GADGET_BASE_URL__" );

			Matcher matcher = pattern.matcher(gadgetStr);
			if(matcher.find())
				gadgetStr = matcher.replaceAll(hostPrefix + "/gadget/" + uploadType);
			gadget = gadgetStr.getBytes("UTF-8");

			return new ByteArrayInputStream(gadget);
		}
	}
}
