package org.infoscoop.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
		for (Iterator it = propsMap.keySet().iterator(); it.hasNext();) {
			String id = (String) it.next();
			String value = (String) propsMap.get(id);
			if (id != null) {
				propertiesDAO.update(id, value);
			}
		}
	}
		
	/**
	 * @return String
	 * @throws Exception
	 */
	public String getPropertiesJson(Locale locale) throws Exception {
		JSONObject propertiesJson = new JSONObject();
		List propList = propertiesDAO.findAllProperties();
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
			propertiesJson.put( prop.getId(), propJson);
		}
		String json = propertiesJson.toString();
		return I18NUtil.resolve(I18NUtil.TYPE_PROPERTY, json, locale, true);
	}
	
	public String getProperty( String name ) throws Exception {
		Properties property = propertiesDAO.findProperty( name );
		if( property == null )
			return null;
		
		return property.getValue();
	}
	
	public Map getPropertiesMap() throws Exception {
		List properties = propertiesDAO.findAllProperties();
		
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