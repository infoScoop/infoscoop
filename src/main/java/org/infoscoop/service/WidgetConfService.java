package org.infoscoop.service;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.dao.WidgetConfDAO;
import org.infoscoop.dao.model.WidgetConf;
import org.infoscoop.util.I18NUtil;
import org.infoscoop.util.SpringUtil;
import org.infoscoop.util.XmlUtil;
import org.infoscoop.widgetconf.WidgetConfUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class WidgetConfService {
	//private static WidgetConfService m_service = new WidgetConfService();
	private static Log log = LogFactory.getLog(WidgetConfService.class);

	private WidgetConfDAO widgetConfDAO;

	private int CACHE_LIFE_TIME = 360;//2hours as default

	public static WidgetConfService getHandle() {
		return (WidgetConfService) SpringUtil.getBean("WidgetConfService");
		//return m_service;
	}

	public void setWidgetConfDAO(WidgetConfDAO widgetConfDAO) {
		this.widgetConfDAO = widgetConfDAO;
	}

	public String getWidgetConfsJson( Locale locale, boolean useClient ) throws Exception{
		try {
			List<WidgetConf> widgetConfs = widgetConfDAO.selectAll();

			JSONObject json = new JSONObject();
			for (WidgetConf widgetConf : widgetConfs) {
				String type = widgetConf.getType();

				json.put(type, WidgetConfUtil.widgetConf2JSONObject(widgetConf
						.getElement(), null, useClient));
			}

			return I18NUtil.resolve(I18NUtil.TYPE_WIDGET, json.toString(1),
					locale, true);
		} catch (Exception e) {
			log.error("Unexpected error occurred.", e);
			throw e;
		}
	}

	/**
	 * @param request
	 * @return String
	 * @throws Exception
	 */
	public String getWidgetConfsJson(Locale locale) throws Exception {
		return getWidgetConfsJson(locale, false);
	}

	/**
	 * @param request
	 * @return String
	 * @throws Exception
	 */
	public Element getWidgetConfByType(String type)
			throws Exception {
		try {
			return widgetConfDAO.getElement(type);
		} catch (Exception e) {
			log.error("Unexpected error occurred.", e);
			throw e;
		}
	}

	public String getWidgetConfJsonByType(String type, Locale locale,
			boolean useClient) throws Exception {
		try {
			Element widgetConf = widgetConfDAO.getElement(type);
			if (widgetConf == null)
				return "{}";

			JSONObject json = WidgetConfUtil.widgetConf2JSONObject(widgetConf,
					null, useClient);

			return I18NUtil.resolve(I18NUtil.TYPE_WIDGET, json.toString(1),
					locale, true);
		} catch (Exception e) {
			log.error("Unexpected error occurred.", e);
			throw e;
		}
	}

	/**
	 * @param request
	 * @return String
	 * @throws Exception
	 */
	public String getWidgetConfJsonByType( String type,Locale locale )
			throws Exception {

		try {
			Element widgetConf = widgetConfDAO.getElement(type);
			if (widgetConf == null)
				return "{}";

			JSONObject json = WidgetConfUtil.widgetConf2JSONObject( widgetConf,null );

			return I18NUtil.resolve(I18NUtil.TYPE_WIDGET, json.toString(1),locale,true );
		} catch (Exception e) {
			log.error("Unexpected error occurred.", e);
			throw e;
		}
	}

	/**
	 * @param request
	 * @return String
	 * @throws Exception
	 */
	/*public String getWidgetConfJsonByTypes( String[] types,String url,Locale locale,int timeout )
			throws Exception {
		try {
			Element[] widgetConf = widgetConfDAO.getElements( types );
			if( widgetConf == null )
				return null;

			List widgetConfs = Arrays.asList( widgetConf );
			for( int i=0; i<widgetConf.length;i++ )
				widgetConfs.add( widgetConf[i] );

			Xml2Json x2j = WidgetConfUtil.createWidgetConf2Json();

			//FIXME 呼ばれていないだけで多分 ClassCastException
			return WidgetConfUtil.widgetConf2JSON(x2j, widgetConfs, url,
					locale,timeout, false);
		} catch (Exception e) {
			log.error("予期しない例外が発生しました.", e);
			throw e;
		}
	}*/

	/**
	 * @param type
	 * @param widgetConfJSON JSON形式のwidgetConf
	 * @throws Exception
	 */
	public void updateWidgetConf(String type, String widgetConfJSON) throws Exception {
		try {
			WidgetConf conf = widgetConfDAO.get(type);
			Element confEl = conf.getElement();
			JSONObject json = new JSONObject(widgetConfJSON);
			if(json.has("autoRefresh")){
				confEl.setAttribute("autoRefresh", json.getString("autoRefresh"));
			}
			if(json.has("backgroundColor")){
				confEl.setAttribute("backgroundColor", json.getString("backgroundColor"));
			}

			if(json.has("WidgetPref")){
				String widgetPrefStr = json.getString("WidgetPref");
				// update widgetPref
				if (widgetPrefStr != null && 0 < widgetPrefStr.length()) {
					JSONObject widgetPrefList = new JSONObject(widgetPrefStr);

					updateWidgetPrefNode(confEl.getOwnerDocument(), confEl,
							widgetPrefList);
				}
			}
			if(json.has("UserPref")){
				String userPrefStr = json.getString("UserPref");
				// update userPref
				if (userPrefStr != null && 0 < userPrefStr.length()) {
					JSONObject userPrefList = new JSONObject(userPrefStr);

					updateUserPrefNode(confEl.getOwnerDocument(), confEl,
							userPrefList);
				}
			}
			conf.setElement(confEl);
			widgetConfDAO.update(conf);
		} catch (Exception e) {
			log.error("update of widet configuration \"" + type + "\" failed.",
					e);
			throw e;
		}
	}

	/**
	 * @param doc
	 * @param widgetConfNode
	 * @param updatePrefList
	 * @throws JSONException
	 */
	public static void updateWidgetPrefNode(Document doc, Element widgetConfNode,
			JSONObject updatePrefList) throws JSONException {

		NodeList prefList = widgetConfNode.getElementsByTagName("WidgetPref");

		Iterator keys = updatePrefList.keys();
		while (keys.hasNext()) {
			String id = (String) keys.next();
			JSONObject prefJson = updatePrefList.getJSONObject(id);
			if( !prefJson.has("name"))
				continue;
			
			String name = prefJson.getString("name");

			String datatype = "";
			if( prefJson.has("datatype"))
				datatype = prefJson.getString("datatype");

			String value = prefJson.getString("value");
			
			int prefLength = prefList.getLength();
			boolean update = false;
			for (int i = 0; i < prefLength; i++) {
				Element pref = (Element) prefList.item(i);
				if ( !name.equals(pref.getAttribute("name")))
					continue;
				
				if("xml".equals( datatype )||"json".equals( datatype )) {
					while( pref.getFirstChild() != null )
						pref.removeChild( pref.getFirstChild());
					
					pref.appendChild( doc.createTextNode( value ));
				} else {
					pref.setAttribute("value", value);
				}
				
				update = true;
			}
			
			// is this code require ?
			if (!update) {
				Element newPref = doc.createElement("WidgetPref");
				newPref.setAttribute("name", name);
				
				if ("xml".equals(datatype) || "json".equals(datatype)) {
					newPref.appendChild(doc.createTextNode( value ));
				} else {
					newPref.setAttribute("value", value);
				}
				int lastPrefIndex = prefList.getLength() - 1;
				Element lastPref = (Element) prefList.item(lastPrefIndex);
				Element nextPrefNode = (Element) lastPref.getNextSibling();
				if (nextPrefNode != null) {
					widgetConfNode.insertBefore(newPref, nextPrefNode);
				} else {
					widgetConfNode.appendChild(newPref);
				}
			}
		}
	}

	/**
	 * @param doc
	 * @param widgetConfNode
	 * @param updatePrefList
	 * @throws JSONException
	 */
	private void updateUserPrefNode(Document doc, Element widgetConfNode,
			JSONObject updatePrefList) throws JSONException {

		NodeList prefList = widgetConfNode.getElementsByTagName("UserPref");

		Iterator keys = updatePrefList.keys();
		while (keys.hasNext()) {
			String id = (String) keys.next();
			JSONObject prefJson = updatePrefList.getJSONObject(id);

			if(!prefJson.has("name"))
				continue;
			
			String name = prefJson.getString("name");

			if(!prefJson.has("default_value"))continue;
			String value = prefJson.getString("default_value");
			
			String datatype = null;
			if( prefJson.has("datatype"))
				datatype = prefJson.getString("datatype");
			
			int prefLength = prefList.getLength();
			boolean update = false;
			for (int i = 0; i < prefLength; i++) {
				Element pref = (Element) prefList.item(i);
				if(!name.equals(pref.getAttribute("name"))) continue;
				
				if("xml".equals( datatype )||"json".equals( datatype )) {
					while( pref.getFirstChild() != null )
						pref.removeChild( pref.getFirstChild());
					
					pref.appendChild( doc.createTextNode( value ));
				} else {
					pref.setAttribute("default_value",value );
				}
				
				update = true;
			}

			// is this code require ?
			// and this code is obsolete.  what is "inputType" attribute ?
			if (!update) {
				Element newPref = doc.createElement("UserPref");
				newPref.setAttribute("name", name);
				
				if(("xml".equals( datatype ) || "json".equals( datatype )) ) {
					newPref.appendChild( doc.createTextNode( value ));
				} else {
					newPref.setAttribute("default_value", value);
				}
				
				String inputType = prefJson.getString("inputType");
				if (inputType != null) {
					newPref.setAttribute("inputType", inputType);
				}
				String displayName = prefJson.getString("display_name");
				if ( prefJson.has("display_name")) {
					newPref.setAttribute("display_name", displayName);
				}
				if (prefJson.has("EnumValue")) {
					JSONArray options = prefJson.getJSONArray("EnumValue");
					for (int j = 0; j < options.length(); j++) {
						JSONObject option = options.getJSONObject(j);
						Element optionNode = doc.createElement("EnumValue");
						optionNode.setAttribute("display_value", option
								.getString("display_value"));
						optionNode.setAttribute("value", option
								.getString("value"));
						newPref.appendChild(optionNode);
					}
				}

				int lastPrefIndex = prefList.getLength() - 1;
				Element lastPref = (Element) prefList.item(lastPrefIndex);
				Element nextPrefNode = (Element) lastPref.getNextSibling();
				if (nextPrefNode != null) {
					widgetConfNode.insertBefore(newPref, nextPrefNode);
				} else {
					widgetConfNode.appendChild(newPref);
				}
			}
		}
	}

	/**
	 * Update displayFlag of specified widgetConf
	 * @param type Type of updating widget
	 * @param displayFlag Value to be updated
	 * @throws Exception
	 */
	public void updateWidgetDisplay(String type, String displayFlag)
			throws Exception {
		try {
			WidgetConf conf = widgetConfDAO.get(type);
			Element confEl = conf.getElement();

			if ("true".equals(displayFlag.toLowerCase())) {
				confEl.setAttribute("displayFlag", "true");
			} else {
				confEl.setAttribute("displayFlag", "false");
			}

			conf.setElement(confEl);
			widgetConfDAO.update(conf);
		} catch (Exception e) {
			log.error("update of widet configuration \"" + type + "\" failed.",
					e);
			throw e;
		}
	}

	/**
	 * Update title of specified widgetConf
	 * @param type Type of updating widget 
	 * @param title Value to be updated
	 * @throws Exception
	 */
	public void updateWidgetTitle(String type, String title) throws Exception {
		try {
			WidgetConf conf = widgetConfDAO.get( type );
			Element confEl = conf.getElement();
			confEl.setAttribute("title", title);

			conf.setElement( confEl );
			widgetConfDAO.update( conf );
		} catch (Exception e) {
			log.error("update of widet configuration \"" + type + "\" failed.",
					e);
			throw e;
		}
	}

	/**
	 * Committing Gadget to DB
	 * @param type Type(id) of creating widget
	 * @param xml Gadget file
	 * @throws Exception
	 */
	public void insertGadgetConf(String type, String xml) throws Exception {
		try {
			WidgetConf conf = new WidgetConf();
			conf.setType(type);
			conf.setData(xml);
			widgetConfDAO.insert(conf);
		} catch (Exception e) {
			log.error(
					"insert of widget configuration \"" + type + "\" failed.",
					e);
			throw e;
		}
	}

	/**
	 * Committing Gadget to DB
	 * @param type
	 * @param node
	 */
	public void insertGadgetConf(String type, Node node){
		WidgetConf conf = new WidgetConf();
		conf.setType(type);
		conf.setData(XmlUtil.dom2String(node));
		widgetConfDAO.insert(conf);
	}

}