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


import java.util.Calendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.context.UserContext;
import org.infoscoop.dao.PreferenceDAO;
import org.infoscoop.dao.model.Preference;
import org.infoscoop.dao.model.PreferencePK;
import org.infoscoop.util.SpringUtil;
import org.infoscoop.util.Xml2Json;
import org.json.JSONObject;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class PreferenceService{

	private static Log log = LogFactory.getLog(PreferenceService.class);
	private static final String formatFullDate = "yyyy/MM/dd HH:mm:ss 'GMT'Z";
	private static final String formatW3C = "yyyy-MM-dd'T'HH:mm:ssZ";

	private PreferenceDAO preferenceDAO;
	
	public PreferenceService() {
	}

	public static PreferenceService getHandle() {
		return (PreferenceService)SpringUtil.getBean("PreferenceService");
	}
	
	public void setPreferenceDAO(PreferenceDAO preferenceDAO) {
		this.preferenceDAO = preferenceDAO;
	}
	
	public Preference getPreferenceEntity(String uid) throws Exception{
		String squareid = UserContext.instance().getUserInfo().getCurrentSquareId();
		Preference entity = preferenceDAO.select(uid, squareid);
		if(entity == null){
			entity = new Preference();
			entity.setId(new PreferencePK(uid, squareid));
			entity.setElement(Preference.newElement(uid));
		}
		
		return entity;
	}
	
	/**
	 * We return an entity of Preference related to uid. <BR>
	 * When there is not it, we return an empty entity. <BR>
	 * 
	 * @param uid
	 * @return
	 * @throws Exception
	 */
	public String getPreferenceJSON(String uid) throws Exception{
		Preference entity = getPreferenceEntity(uid);
		Node node = entity.getElement();
		
		JSONObject prefObj;
		if(node != null){
			Xml2Json x2j = new Xml2Json();
			String rootPath = "/preference";
			x2j.addSkipRule(rootPath);
			x2j.addPathRule(rootPath + "/property", "name", true, true);
			String prefJsonStr = x2j.xml2json((Element)node);
			prefObj = new JSONObject(prefJsonStr);

			// convert the logoffDateTime to the format for javascript.
			if(prefObj.has("property")){
				JSONObject prefPropObj = prefObj.getJSONObject("property");
				if(prefPropObj.has("logoffDateTime")){
					String logoffDateTime = prefPropObj.getString("logoffDateTime");
					prefPropObj.put("logoffDateTime",logoffDateTime );
				}
			}
			
			// remove failed flag
			boolean isChanged = PreferenceService.updateProperty((Element)node, "failed", "false");
			if(isChanged && uid !=null){
				entity.setElement((Element)node);
				PreferenceService.getHandle().update(entity);
			}
			
		}else{
			prefObj = new JSONObject();
		}
		return prefObj.toString();
	}
	
	
	/** 
	 * set access time
	 */
	public void setAccessTime(String uid) throws Exception {
		Preference entity = getPreferenceEntity(uid);
		Node node = entity.getElement();
		
		JSONObject prefObj;
		if(node != null){
			Xml2Json x2j = new Xml2Json();
			String rootPath = "/preference";
			x2j.addSkipRule(rootPath);
			x2j.addPathRule(rootPath + "/property", "name", true, true);
			String prefJsonStr = x2j.xml2json((Element)node);
			prefObj = new JSONObject(prefJsonStr);

			// create/update lastAccessDate and accessDate.
			if(prefObj.has("property")){
				JSONObject prefPropObj = prefObj.getJSONObject("property");
				if(prefPropObj.has("accessTime")){
					String accessTime = prefPropObj.getString("accessTime");
					PreferenceService.updateProperty((Element)node, "lastAccessTime", accessTime);
				}
			}

			Calendar calendar = Calendar.getInstance();
			long millis = calendar.getTimeInMillis();
			boolean changeAccessTime = PreferenceService.updateProperty((Element)node, "accessTime", String.valueOf(millis));
			if(changeAccessTime && uid != null){
				entity.setElement((Element)node);
				PreferenceService.getHandle().update(entity);
			}
		}else{
			prefObj = new JSONObject();
		}
	}
	
	/**
	 * get the property of preference.
	 */
	public String getProperty(String uid, String field) throws Exception {
		String squareid = UserContext.instance().getUserInfo().getCurrentSquareId();
		Preference entity = preferenceDAO.select(uid, squareid);
		String propValue = new String();
		if(entity != null){
			Node node = entity.getElement();
			JSONObject prefObj;
			if(node != null){
				Xml2Json x2j = new Xml2Json();
				String rootPath = "/preference";
				x2j.addSkipRule(rootPath);
				x2j.addPathRule(rootPath + "/property", "name", true, true);
				String prefJsonStr = x2j.xml2json((Element)node);
				prefObj = new JSONObject(prefJsonStr);
				if(prefObj.has("property")){
					JSONObject prefPropObj = prefObj.getJSONObject("property");
					if(prefPropObj.has(field)){
						propValue = prefPropObj.getString(field);
					}
				}
			}
		}

		return propValue;
	}
	
	/**
	 * add and update the property of preference.
	 * When a changed value does not change, we return false.
	 * 
	 * @param node
	 * @param field
	 * @param value
	 * @return boolean 
	 */
	public static boolean updateProperty(Element node, String field, String value){
		NodeList propList = node.getElementsByTagName("property");
		
		Element property;
		boolean isModified = false;
		for(int i=0;i<propList.getLength();i++){
			property = (Element)propList.item(i);
			if(property.getAttribute("name").equals(field)){
            	// If there is an existing property, we update a value.
        		NodeList textNodeList = property.getChildNodes();
        		while(textNodeList.getLength() > 0){
        			property.removeChild(textNodeList.item(0));
        		}
        		Text textNode = node.getOwnerDocument().createTextNode(value);
        		property.appendChild( textNode );
        		
				isModified = true;
				break;
			}
		}
		
		if(!isModified){
			Element propEl = node.getOwnerDocument().createElement("property");
			propEl.setAttribute("name", field);
			propEl.appendChild(node.getOwnerDocument().createTextNode(value));
			node.appendChild(propEl);
		}
		
		return true;
	}
	
	/**
	 * Helper of remove property
	 * @param node
	 * @param field
	 * @return
	 */
	public static boolean removeProperty(Element node, String field){
		NodeList propList = node.getElementsByTagName("property");
		
		Element property = null;
		
		for(int i=0;i<propList.getLength();i++){
			property = (Element)propList.item(i);
			if(property.getAttribute("name").equals(field)){
				node.removeChild(property);
				return true;
			}
		}
		return false;
	}
	public void update(Preference preference){
		this.preferenceDAO.update(preference);
	}
}
