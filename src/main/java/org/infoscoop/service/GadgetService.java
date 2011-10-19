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

package org.infoscoop.service;

import java.io.*;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xpath.XPathAPI;
import org.infoscoop.dao.GadgetDAO;
import org.infoscoop.dao.GadgetIconDAO;
import org.infoscoop.dao.OAuthConsumerDAO;
import org.infoscoop.dao.model.Gadget;
import org.infoscoop.util.I18NUtil;
import org.infoscoop.util.NoOpEntityResolver;
import org.infoscoop.util.SpringUtil;
import org.infoscoop.util.XmlUtil;
import org.infoscoop.widgetconf.I18NConverter;
import org.infoscoop.widgetconf.MessageBundle;
import org.infoscoop.widgetconf.WidgetConfUtil;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class GadgetService {
	private static Log log = LogFactory.getLog(GadgetService.class);
	
	private GadgetDAO gadgetDAO;
	private GadgetIconDAO gadgetIconDAO;
	private OAuthConsumerDAO oauthConsumerDAO;
	
	public static GadgetService getHandle() {
		return (GadgetService) SpringUtil.getBean("GadgetService");
		//return m_service;
	}
	
	public GadgetService(){
	}
	
	public void setGadgetDAO(GadgetDAO gadgetDAO) {
		this.gadgetDAO = gadgetDAO;
	}
	
	public void setGadgetIconDAO(GadgetIconDAO gadgetIconDAO) {
		this.gadgetIconDAO = gadgetIconDAO;
	}
	
	public byte[] selectGadget( String type ) {
		Gadget gadget = gadgetDAO.select( type );
		if( gadget == null )
			return null;
		
		return gadget.getData();
	}

	/**
	 * Return the list of the gadget whose form is JSON for the management of widget. It doesn't include a resource.
	 * @return
	 * @throws Exception 
	 */
	public String getGadgetJson( Locale locale,int timeout ) throws Exception {
		
		List<Gadget> gadgetList = gadgetDAO.selectGadgetXMLs();
		
		String json = gadget2JSON( gadgetList, false, locale, timeout, true);
		return json;
	}
	
	public JSONObject getGadgetJson(String type, Locale locale) throws Exception{
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		builderFactory.setValidating(false);

		DocumentBuilder builder = builderFactory.newDocumentBuilder();
		builder.setEntityResolver(NoOpEntityResolver.getInstance());
		
		Gadget gadget = gadgetDAO.select(type);
		Document gadgetDoc = builder.parse(new ByteArrayInputStream(gadget.getData()));
		Element gadgetEl = gadgetDoc.getDocumentElement();
		JSONObject confJson = WidgetConfUtil.gadget2JSONObject( gadgetEl,null );
		return confJson;
	}
	
	private static String gadget2JSON(List<Gadget> gadgetList, boolean isUpdate, Locale locale,
			int timeout, boolean enableI18N) throws Exception {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		builderFactory.setValidating(false);

		DocumentBuilder builder = builderFactory.newDocumentBuilder();
		builder.setEntityResolver(NoOpEntityResolver.getInstance());
		
		JSONObject json = new JSONObject();
		for (int i = 0; i < gadgetList.size(); i++) {
			Gadget gadget = gadgetList.get(i);
			try {
				//TODO:enocoding
				Document gadgetDoc = ( Document )XmlUtil.string2DomWithBomCode( new String( gadget.getData(),"UTF-8") );
				Element gadgetEl = gadgetDoc.getDocumentElement();
				String type = gadget.getType();
				
				I18NConverter i18n = new I18NConverter( locale,
						new MessageBundle.Factory.Upload( timeout,type ).createBundles( gadgetDoc ) );
				
				JSONObject confJson = WidgetConfUtil.gadget2JSONObject( gadgetEl,i18n );
				if( !type.startsWith("upload__"))
					type = "upload__"+type;
				
				json.put( type, confJson);
			} catch( Exception ex ) {
				log.error("UploadGadget Parse failed: ["+gadget.getType()+"]",ex );
			}
		}
		String jsonStr = json.toString(1);
		if( enableI18N ) {
			jsonStr = I18NUtil.resolve(I18NUtil.TYPE_WIDGET, jsonStr,locale );
		}
		return jsonStr;
	}

	
	public void deleteGadget(String type){
		if( type.startsWith("upload__"))
			type = type.substring(8);
		
		gadgetDAO.deleteType(type);
		gadgetIconDAO.deleteByType(type);
	}
	/**
	 * @param type
	 * @param widgetConfJSON a widgetConf whose form is JSON.
	 * @throws Exception
	 */
	public void updateGadget(String type, String gadgetJSON, String authServiceList) throws Exception {
		if( type.startsWith("upload__"))
			type = type.substring(8);
		
		if(log.isInfoEnabled())
			log.info("uploadGadget type=" + type);
		try {

			Gadget gadget = gadgetDAO.select(type );
			JSONObject json = new JSONObject(gadgetJSON);
			
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			builderFactory.setValidating(false);
			
			DocumentBuilder builder = builderFactory.newDocumentBuilder();
			builder.setEntityResolver(NoOpEntityResolver.getInstance());
			Document gadgetDoc = builder.parse(new ByteArrayInputStream(gadget.getData()));
			Element gadgetEl = gadgetDoc.getDocumentElement();
			String resourceUrl = "";
			if(json.has("ModulePrefs")){
				JSONObject modulePrefs = json.getJSONObject("ModulePrefs");
				for(Iterator<String> prefNames = modulePrefs.keys();prefNames.hasNext();){
					String prefName = prefNames.next();
					if(
							!(
									"autoRefresh".equals(prefName) ||
									"title".equals(prefName) ||
									"directory_title".equals(prefName) ||
									"title_url".equals(prefName) ||
									"height".equals(prefName) ||
									"scrolling".equals(prefName) ||
									"singleton".equals( prefName ) ||
									"resource_url".equals( prefName )
							)
					)continue;

					String value =  modulePrefs.getString(prefName);
					if(log.isInfoEnabled())
						log.info("Modify Gadget's ModulePrefs@" + prefName + " to " + value + ".");
					Element modulePrefsEl = (Element)gadgetEl.getElementsByTagName("ModulePrefs").item(0);
					modulePrefsEl.setAttribute(prefName, value);
					
					if(prefName.equals("resource_url"))
						resourceUrl = value;
				}
			}
			Element iconElm = (Element) XPathAPI.selectSingleNode(
					gadgetDoc, "/Module/ModulePrefs/Icon");

			if (iconElm != null) {
				String iconUrl = resourceUrl + iconElm.getTextContent();
				GadgetIconDAO.newInstance().insertUpdate(type, iconUrl);
			} else {
				GadgetIconDAO.newInstance().insertUpdate(type, "");
			}
			
			updateUserPrefNodes( gadgetDoc,json );
			
			if( json.has("WidgetPref"))
				WidgetConfService.updateWidgetPrefNode( gadgetDoc,gadgetEl,json.getJSONObject("WidgetPref"));
			
			gadgetDAO.update(type,"/",type+".xml", XmlUtil.dom2String(gadgetDoc).getBytes("UTF-8"));
		} catch (Exception e) {
			log.error("update of widet configuration \"" + type + "\" failed.",
					e);
			throw e;
		}
	}
	
	private void updateUserPrefNodes( Document doc,JSONObject json ) throws JSONException {
		String prefType = "UserPref";
		if( !json.has( prefType ))
			return;
		
		JSONObject prefs = json.getJSONObject( prefType );
		
		Map<String,Element> prefNodes = new HashMap<String, Element>();
		NodeList prefNodeList = doc.getElementsByTagName( prefType );
		for(int i = 0; i < prefNodeList.getLength();i++){
			Element prefNode = (Element)prefNodeList.item(i);
			
			prefNodes.put( prefNode.getAttribute("name"),prefNode );
		}
		
		// update the userPref
		for(Iterator<String> prefNames = prefs.keys();prefNames.hasNext();){
			String prefName = prefNames.next();
			JSONObject pref = prefs.getJSONObject(prefName);
			if(!pref.has("default_value"))continue;
			
			String value = pref.getString("default_value");
			Element prefNode = prefNodes.get( prefName );
			if( prefNode == null )
				continue;
			
			if(log.isInfoEnabled())
				log.info("Modify Gadget's "+prefType+" name=" + prefName + " to " + value + ".");

			String datatype = "";
			if( pref.has("datatype"))
				datatype = pref.getString("datatype");
			
			if("xml".equals( datatype )||"json".equals( datatype )) {
				prefNode.removeAttribute("default_value");
				while( prefNode.getFirstChild() != null )
					prefNode.removeChild( prefNode.getFirstChild());
				
				prefNode.appendChild( doc.createTextNode( value ));
			} else {
				prefNode.setAttribute("default_value", value);
			}
		}
	}
}
