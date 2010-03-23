
package org.infoscoop.service;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.dao.PreferenceDAO;
import org.infoscoop.dao.model.Preference;
import org.infoscoop.util.SpringUtil;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class PreferenceService{

	private static Log log = LogFactory.getLog(PreferenceService.class);

	private PreferenceDAO preferenceDAO;
	
	public PreferenceService() {
	}

	public static PreferenceService getHandle() {
		return (PreferenceService)SpringUtil.getBean("PreferenceService");
	}
	
	public void setPreferenceDAO(PreferenceDAO preferenceDAO) {
		this.preferenceDAO = preferenceDAO;
	}
	
	/**
	 * We return an entity of Preference related to uid. <BR>
	 * When there is not it, we return an empty entity. <BR>
	 * 
	 * @param uid
	 * @return
	 * @throws Exception
	 */
	public Preference getPrefEntity(String uid) throws Exception{
		Preference entity = preferenceDAO.select(uid);
		if(entity == null){
			entity = new Preference();
			entity.setUid(uid);
			entity.setElement(Preference.newElement(uid));
		}
		return entity;
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
				break;
			}
		}
		if(property == null) return false;
		node.removeChild(property);
		return true;
	}
	public void update(Preference preference){
		this.preferenceDAO.update(preference);
	}
}