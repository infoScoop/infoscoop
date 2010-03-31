package org.infoscoop.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.dao.PropertiesDAO;
import org.infoscoop.dao.model.Property;
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
			Property prop = (Property)propIt.next();//Key become capital if Map is passed to constructor of JSONObject without change.
			JSONObject propJson = new JSONObject();
			propJson.put("id", prop.getName());
			propJson.put("category", prop.getCategory());
			if (prop.getAdvanced() == 1)
				propJson.put("advanced", true);
			
			if("hidden".equals( prop.getDatatype().toLowerCase()))
				continue;
			
			propJson.put("datatype", prop.getDatatype());
			propJson.put("value", prop.getValue());
			if (prop.getEnumValue() != null && prop.getEnumValue().length() > 0)
				propJson.put("enumValue", prop.getEnumValue());
			if (prop.getRequired() == 1)
				propJson.put("required", true);
			if (prop.getRegex() != null && prop.getRegex().length() > 0)
				propJson.put("regex", prop.getRegex());
			if (prop.getRegexMsg() != null && prop.getRegexMsg().length() > 0)
				propJson.put("regexMsg", prop.getRegexMsg() );
			propertiesJson.put( prop.getName(), propJson);
		}
		String json = propertiesJson.toString();
		return I18NUtil.resolve(I18NUtil.TYPE_PROPERTY, json, locale, true);
	}
	
	public String getProperty( String name ) throws Exception {
		Property property = propertiesDAO.findProperty( name );
		if( property == null )
			return null;
		
		return property.getValue();
	}
	
	public Map getPropertiesMap() throws Exception {
		List properties = propertiesDAO.findAllProperties();
		
		Map result = new HashMap();
		for( int i=0;i<properties.size();i++ ) {
			Property property = ( Property )properties.get(i);
			if("hidden".equals( property.getDatatype().toLowerCase()))
				continue;
			
			result.put( property.getName(),property.getValue() );
		}
		
		return result;
	}
	

}