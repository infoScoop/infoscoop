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
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;



import org.apache.commons.collections.MultiHashMap;
import org.apache.commons.collections.MultiMap;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.request.ProxyRequest;
import org.infoscoop.util.I18NUtil;
import org.infoscoop.util.StringUtil;
import org.json.JSONObject;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class MakeMenuFilter extends ProxyFilter {
	private static Log log = LogFactory.getLog(MakeMenuFilter.class);
	private static String MENUTYPE_SIDEMENU = "sidemenu";
	private static String MENUTYPE_GUIDANCE = "guidance";
	
	private static SAXParserFactory factory;
	static{
		try{
			factory = SAXParserFactory.newInstance();
		}catch(Exception e){
			log.error("Loading XML Parser library failed." + e);
		}
	}
	
	protected int preProcess(HttpClient client, HttpMethod method, ProxyRequest request) {
		return 0;
	}
	
	protected InputStream postProcess(ProxyRequest request, InputStream responseStream) throws IOException {
		String menuType = request.getRequestHeader("menuType");
		if( menuType == null || menuType.equals("") )
			menuType = request.getTargetURL().substring( request.getTargetURL().lastIndexOf("/") +1 );
		
		byte[] responseBytes = process(responseStream, menuType, request.getOriginalURL(), request.getLocale(), true);
		//request.setResponseBody(new ByteArrayInputStream(responseBytes));
		
		request.putResponseHeader("Content-Length", Integer.toString(responseBytes.length));
		request.putResponseHeader("Content-Type", "text/plain; charset=\"utf-8\"");
		
		return new ByteArrayInputStream(responseBytes);
	}

	/**
	 * TODO:isExternalService is not required because menuUrl is fixed
	 * @param responseStream
	 * @param menuType
	 * @param menuUrl
	 * @param locale
	 * @param isExternalService
	 * @return
	 * @throws IOException
	 */
	public static byte[] process(InputStream responseStream, String menuType, String menuUrl, Locale locale, boolean isExternalService) throws IOException {
		SAXParser parser = null;
		try {
			parser = factory.newSAXParser();
		} catch (ParserConfigurationException e) {
			log.error("", e);
		} catch (SAXException e) {
			log.error("", e);
		}
		
		Map resMap;
		try {
			resMap = I18NUtil.getResourceMap( I18NUtil.TYPE_MENU, locale );
		} catch( Exception ex ) {
			log.error("Faild to processing of internationalizing menu.", ex );
			
			resMap = new HashMap();
		}
		
		MakeMenuHandler handler = new MakeMenuHandler( menuUrl, resMap );
		
		try {
			parser.parse(responseStream, handler);
		} catch (SAXException e) {
			log.error("Failed to parse menu", e);
		} catch (IOException e) {
			log.error("Failed to parse menu", e);
		}
		try {
			String json = null;
			String functionName;
			if(!isExternalService){
				functionName = menuType.equals( MENUTYPE_SIDEMENU )?
						"IS_SidePanel.setMenu" : menuType.equals( MENUTYPE_GUIDANCE )?
								"IS_Guidance.setMenu" : "IS_SiteAggregationMenu.setMenu";
				json = handler.getJSONPString(functionName);
			}else{
				functionName = menuType.equals( MENUTYPE_SIDEMENU )?
						"IS_SidePanel.setServiceMenu" : menuType.equals( MENUTYPE_GUIDANCE )?
								"IS_Guidance.setServiceMenu" : "IS_SiteAggregationMenu.setServiceMenu";
				json = handler.getSiteTopJSONPString( functionName );
			}
			return json.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			log.error("", e);
		}
		return null;
	}

	private static class MakeMenuHandler extends DefaultHandler{
		private CharArrayWriter buf = new CharArrayWriter();
		private StringBuffer menuItemArray = new StringBuffer();
		private StringBuffer siteTopArray = new StringBuffer();
		private MultiMap treeMap = new MultiHashMap();
		private Map resMap;
		private String menuUrl;
		
		boolean firstProperty = true;
		boolean firstItem = true;
		boolean firstSiteTop = true;
		boolean close = false;
		boolean emptySiteTop = false;
		Stack idStack = new Stack(); 
		
		long start = System.currentTimeMillis();
		
		public MakeMenuHandler( String url, Map resMap ) {
			this.menuUrl = url;
			this.resMap = resMap;
		}
		
		public void startDocument() throws SAXException {
			menuItemArray.append("{");
			siteTopArray.append("[");
		}
		
		public String getJSONPString(String functionName){
			return functionName +"(" + JSONObject.quote(menuUrl) + ","+ menuItemArray.toString() + "," +  siteTopArray + "," + makeTreeMapJSON() + ");";
			//return functionName +"("+ menuItemArray.toString() + "," +  siteTopArray + "," + makeTreeMapJSON() + ");";
		}
		
		public String getSiteTopJSONPString(String functionName){
			return functionName+"(" + JSONObject.quote(menuUrl) + "," + menuItemArray.toString() + "," +  makeTreeMapJSON() + "," + siteTopArray + ");";	
		}
		
		public void endDocument() throws SAXException {
			menuItemArray.append("}");
			siteTopArray.append("]");
		}
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			buf.reset();
			endSiteElement = false;
			if(qName.equals("site")||qName.equals("site-top")){
				//String menuId = (qName.equals("site") || this.siteTopId == null) ? attributes.getValue("id") : this.siteTopId;
				String menuId = attributes.getValue("id");
				String title = attributes.getValue("title");
				
				if(qName.equals("site-top")){
					if (menuId == null || title == null){
						emptySiteTop = true;
						return;
					}
					emptySiteTop = false;
					if(!firstSiteTop){
						siteTopArray.append(",");
					}
					firstSiteTop = false;
					siteTopArray.append(JSONObject.quote(menuId));
				}
				
				if(!firstItem){
					if(!close){
						menuItemArray.append("}");
					}
					menuItemArray.append(",");
				}
				firstItem = false;
				close = false;
				String parentId = null;
				if(!idStack.isEmpty()){
					parentId = (String) idStack.peek();
					treeMap.put(parentId, menuId);
					
				}
				idStack.push(menuId);
				
				menuItemArray.append(menuId).append(":");
				menuItemArray.append("{");
				menuItemArray.append("id:").append( JSONObject.quote(menuId) );
				title = I18NUtil.replace(title, resMap);
				if( title.length() > 80 )
					title = title.substring(0,80);
				
				menuItemArray.append(",title:").append(JSONObject.quote( title ));
				
				String href = attributes.getValue("href");
				if(href != null){
					href = I18NUtil.replace( href,resMap );
					try {
						if( href.getBytes("UTF-8").length > 1024 )
							href = StringUtil.getTruncatedString( href,1024,"UTF-8");
					} catch( UnsupportedEncodingException ex ) {
						//ignore
					}
					
					menuItemArray.append(",href:").append(JSONObject.quote( href ));
				}
				
				String target = attributes.getValue("display");
				if(target != null){
					menuItemArray.append(",display:").append(JSONObject.quote(target));
				}
				
				String linkDisabled = attributes.getValue("link_disabled");
				if(linkDisabled == null) linkDisabled = "false";
				menuItemArray.append(",linkDisabled:").append(Boolean.parseBoolean(linkDisabled));
				
				String serviceURL = attributes.getValue("serviceURL");
				if(serviceURL != null){
					menuItemArray.append(",serviceURL:").append(JSONObject.quote(
							I18NUtil.replace( serviceURL,resMap ) ));
				}
				String serviceAuthType = attributes.getValue("serviceAuthType");
				if (serviceAuthType != null) {
					menuItemArray.append(",serviceAuthType:").append(
							JSONObject.quote(serviceAuthType));
				}
				String type = attributes.getValue("type");
				if(type != null){
					try {
						if(!( menuId.getBytes("UTF-8").length <= 254 )) {
							log.warn("ID of menu is too long.[ id:"+menuId+" ]");
						} else if( !( type.getBytes("UTF-8").length <= 1024 ) ) {
							log.warn("ID of menu is too long.[ id:"+menuId+",type: "+type+" ]");
						} else {
							menuItemArray.append(",type:").append(JSONObject.quote(type));
						}
					} catch( UnsupportedEncodingException ex ) {
						//ignore
					}
				}
				String alert = attributes.getValue("alert");
				if(alert != null){
					menuItemArray.append(",alert:").append(alert);
				}
				if(parentId != null){
					menuItemArray.append(",parentId:").append(JSONObject.quote(parentId));
				}
				String directoryTitle = attributes.getValue("directory_title");
				if(directoryTitle != null){
					directoryTitle = I18NUtil.replace(directoryTitle, resMap);
					menuItemArray.append(",directoryTitle:").append(JSONObject.quote(directoryTitle));
				}
				
				String multiString = attributes.getValue("multi");
				if(multiString!=null ){
					menuItemArray.append(",multi:").append("true".equalsIgnoreCase(multiString)?"true":"false");
				}
				
				String refreshInterval = attributes.getValue("refreshInterval");
				if (refreshInterval != null){
					try{
						int refreshIntervalInt = Integer.parseInt(refreshInterval); 
						menuItemArray.append(",refreshInterval:").append(refreshIntervalInt);
					}catch(NumberFormatException e){
						menuItemArray.append(",refreshInterval:").append("null");
					}
				}
				
				String thumbnail = attributes.getValue("thumbnail");
				if(thumbnail!=null ){
					menuItemArray.append(",thumbnail:").append(JSONObject.quote(thumbnail));
				}
			}else if(qName.equals("properties")){
				firstProperty = true;
				menuItemArray.append(",properties:{");
				
			}else if(qName.equals("property")){
				if(!firstProperty){
					menuItemArray.append(",");
				}
				menuItemArray.append(JSONObject.quote(attributes.getValue("name"))).append(":");
				firstProperty = false;
			}
		}
		public void characters(char[] ch, int start, int length) throws SAXException {
			buf.write(ch, start, length);
		}
		
		boolean endSiteElement = false;
		public void endElement(String uri, String localName, String qName) throws SAXException {
			if (qName.equals("site")
					|| (qName.equals("site-top") && !emptySiteTop)) {
//				if(endSiteElement && !close){
				if(!close){
					menuItemArray.append("}");
					close = true;
				}
				idStack.pop();
				endSiteElement = true;
			}else if(qName.equals("properties")){
				menuItemArray.append("}");
			}else if(qName.equals("property")){
				menuItemArray.append(JSONObject.quote(buf.toString().trim()));
			}
			buf.reset();
		}
		
		private String makeTreeMapJSON(){
			StringBuffer treeMapJSON = new StringBuffer();
			treeMapJSON.append("{");
			for(Iterator it = treeMap.keySet().iterator(); it.hasNext();){
				String parentId = (String)it.next();
				treeMapJSON.append(parentId).append(":[");
				Collection children = (Collection) treeMap.get(parentId);
				for(Iterator childIt = children.iterator(); childIt.hasNext();){
					String childId = (String)childIt.next();
					treeMapJSON.append(JSONObject.quote(childId));
					if(childIt.hasNext()){
						treeMapJSON.append(",");
					}
				}
				treeMapJSON.append("]");
				if(it.hasNext()){
					treeMapJSON.append(",");
				}
			}
			treeMapJSON.append("}");
			return treeMapJSON.toString();
		}
	}

}
