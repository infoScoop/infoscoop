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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.context.UserContext;
import org.infoscoop.dao.PropertiesDAO;
import org.infoscoop.dao.model.Properties;
import org.infoscoop.util.I18NUtil;
import org.infoscoop.util.SpringUtil;
import org.json.JSONObject;

public class PropertiesService{

	private static Log log = LogFactory.getLog(PropertiesService.class);

	private PropertiesDAO propertiesDAO;
	
	public PropertiesService() {
	}

	public static PropertiesService getHandle() {
		return (PropertiesService)SpringUtil.getBean("PropertiesService");
	}
	
	public void setPropertiesDAO(PropertiesDAO propertiesDAO) {
		this.propertiesDAO = propertiesDAO;
	}
	
	/**
	 * @param propsMap
	 * @throws Exception
	 */
	public synchronized void updateProperties(Map propsMap) throws Exception {
		String squareid = UserContext.instance().getUserInfo().getCurrentSquareId();
		for (Iterator it = propsMap.keySet().iterator(); it.hasNext();) {
			String id = (String) it.next();
			String value = (String) propsMap.get(id);
			if (id != null) {
				propertiesDAO.update(id, value, squareid);
			}
		}
	}
		
	/**
	 * @return String
	 * @throws Exception
	 */
	public String getPropertiesJson(Locale locale) throws Exception {
		JSONObject propertiesJson = new JSONObject();
		String squareid = UserContext.instance().getUserInfo().getCurrentSquareId();
		List propList = propertiesDAO.findAllProperties(squareid);
		for(Iterator propIt = propList.iterator(); propIt.hasNext();){
			Properties prop = (Properties)propIt.next();//Key become capital if Map is passed to constructor of JSONObject without change.
			JSONObject propJson = new JSONObject();
			propJson.put("id", prop.getId());
			propJson.put("category", prop.getCategory());
			if (prop.getAdvanced() != null && prop.getAdvanced() == 1)
				propJson.put("advanced", true);
			
			if("hidden".equals( prop.getDatatype().toLowerCase()))
				continue;
			
			propJson.put("datatype", prop.getDatatype());
			propJson.put("value", prop.getValue());
			propJson.put("description", prop.getDescription());
			if (prop.getEnumvalue() != null && prop.getEnumvalue().length() > 0)
				propJson.put("enumValue", prop.getEnumvalue());
			if (prop.getRequired() != null && prop.getRequired() == 1)
				propJson.put("required", true);
			if (prop.getRegex() != null && prop.getRegex().length() > 0)
				propJson.put("regex", prop.getRegex());
			if (prop.getRegexmsg() != null && prop.getRegexmsg().length() > 0)
				propJson.put("regexMsg", prop.getRegexmsg() );
			propertiesJson.put( prop.getId().getId(), propJson);
		}
		String json = propertiesJson.toString();
		return I18NUtil.resolve(I18NUtil.TYPE_PROPERTY, json, locale, true);
	}
	
	public String getProperty( String name ) throws Exception {
		String squareid = UserContext.instance().getUserInfo().getCurrentSquareId();
		Properties property = propertiesDAO.findProperty( name, squareid );
		if( property == null )
			return null;
		
		return property.getValue();
	}
	
	public Map getPropertiesMap() throws Exception {
		String squareid = UserContext.instance().getUserInfo().getCurrentSquareId();
		List properties = propertiesDAO.findAllProperties(squareid);
		
		Map result = new HashMap();
		for( int i=0;i<properties.size();i++ ) {
			Properties property = ( Properties )properties.get(i);
			if("hidden".equals( property.getDatatype().toLowerCase()))
				continue;
			
			result.put( property.getId(),property.getValue() );
		}
		
		return result;
	}
	

}
