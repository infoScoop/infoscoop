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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.security.auth.Subject;

import org.apache.commons.collections.MultiHashMap;
import org.apache.commons.collections.SequencedHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.acl.ISPrincipal;
import org.infoscoop.acl.SecurityController;
import org.infoscoop.dao.TabDAO;
import org.infoscoop.dao.TabLayoutDAO;
import org.infoscoop.dao.WidgetDAO;
import org.infoscoop.dao.model.TabLayout;
import org.infoscoop.util.RoleUtil;
import org.infoscoop.util.SpringUtil;
import org.infoscoop.util.XmlUtil;
import org.infoscoop.web.WidgetServlet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TabLayoutService {
	public static String DEFAULT_ROLE_NAME = "defaultRole";
	
	private TabLayoutDAO tabLayoutDAO;
	private WidgetDAO widgetDAO;
	private TabDAO tabDAO;
	
	private static Log log = LogFactory.getLog(TabLayoutService.class);
	
	private static final String COMMANDBAR_TAB_ID = "commandbar";

	public TabLayoutService() {
	}
	
	public void setTabLayoutDAO(TabLayoutDAO tabLayoutDAO) {
		this.tabLayoutDAO = tabLayoutDAO;
	}

	public void setWidgetDAO(WidgetDAO widgetDAO) {
		this.widgetDAO = widgetDAO;
	}

	public void setTabDAO(TabDAO tabDAO) {
		this.tabDAO = tabDAO;
	}
	
	public static TabLayoutService getHandle() {
		return (TabLayoutService)SpringUtil.getBean("TabLayoutService");
	}
	
	/**
	 * Committing temporary data to actual data
	 * @throws IllegalAccessException 
	 */
	public synchronized void commitDefaultPanel() throws IllegalAccessException {
		String myUid = checkLoginUid();
		//tabLayoutDAO.deleteByTemp(TabLayout.TEMP_FALSE);
		//tabLayoutDAO.updateTempFlag(TabLayout.TEMP_TRUE);
		tabLayoutDAO.copy(myUid, false);
		log.info("Success to commit TabLayouts.");
	}
	
	public synchronized void deleteTemp() throws IllegalAccessException{
		checkLoginUid();
		tabLayoutDAO.deleteByTemp(TabLayout.TEMP_TRUE);
		log.info("Success to delete Tempolary TabLayouts.");
	}
	
	/**
	 * Return login user id if it can be edited 
	 * @return
	 * @throws IllegalAccessException
	 */
	private String checkLoginUid() throws IllegalAccessException {
		ISPrincipal p = SecurityController.getPrincipalByType("UIDPrincipal");
		String myUid = p.getName();
		String lockingUid = tabLayoutDAO.selectLockingUid();
		if (lockingUid == null) {
			throw new IllegalAccessException("Temp data is not found.");
		}
		if (myUid != null && lockingUid != null && !myUid.equals(lockingUid)) {
			throw new IllegalAccessException("The user \"" + lockingUid
					+ "\" is editing it.");
		}
		return myUid;
	}

	/**
	 * @param tabId
	 * @param tabNumber
	 * @param panelMap
	 * @return
	 * @throws Exception
	 */
	public synchronized String updateDefaultPanel(String tabId, String tabNumber,
			Map panelMap) throws Exception {
		String myUid = checkLoginUid();
		try {
			if (tabId == null || tabId.length() == 0) {
				Map MaxMap = tabLayoutDAO.selectMax();
				// 
				tabId = (String) MaxMap.get("tabId");
				if (tabId != null && tabId.length() != 0) {
					int newInt = Integer.valueOf(tabId).intValue();
					if (0 == newInt) {
						tabId = "10001";
					} else if (0 < newInt) {
						tabId = String.valueOf(newInt + 1).toString();
					} else {
						throw new Exception("Bad max tabId");
					}
				} else {
					throw new Exception("\"select max tabId\" not found");
				}
				// 
				tabNumber = MaxMap.get("tabNumber").toString();
				if (tabNumber != null && tabNumber.length() != 0) {
					int newInt = Integer.valueOf(tabNumber).intValue();
					tabNumber = String.valueOf(newInt + 1).toString();
				} else {
					throw new Exception("\"select max tabNumber\" not found");
				}
			}
			
			List oldTabList = tabLayoutDAO.selectByTabId(tabId,
					TabLayout.TEMP_TRUE);
			Map oldDynamicPanelMap = new HashMap();
			for(Iterator it = oldTabList.iterator(); it.hasNext();){
				TabLayout tab = (TabLayout)it.next();
				
				JSONObject json = tab.getDynamicPanelJson();

				oldDynamicPanelMap.put(tab.getId().getRoleorder(), json);
			}
			// Delete
			tabLayoutDAO.deleteByTabId(tabId);
			
			Map newDynamicPanelMap = new HashMap(); 
			// Insert
			for (Iterator it = panelMap.keySet().iterator(); it.hasNext();) {
				String id = (String) it.next();
				Map map = null;
				if (panelMap.get(id) instanceof Map) {
					map = (Map) panelMap.get(id);
					
					// Transfer to document
					StringBuffer xml = new StringBuffer();
					xml.append("<widgets");
					xml.append(" tabId=").append("\"");
					xml.append(tabId).append("\"");
					xml.append(" tabName=").append("\"");
					String tabTitle = (String) map.get("tabName");
					xml.append(XmlUtil.escapeXmlEntities(tabTitle)).append("\"");
					xml.append(" tabType=").append("\"");
					xml.append("static").append("\"");
					// columnsWidth attribute is not needed if it is commandbar
					if (!COMMANDBAR_TAB_ID.equals(tabId)) {
						xml.append(" columnsWidth=").append("\"").append(
								(String) map.get("columnsWidth")).append("\"");
						xml.append(" numCol=").append("\"").append(
								(String) map.get("numCol")).append("\"");
					}
					xml.append(">");
					xml.append("\n");
					xml.append("<panel type=\"StaticPanel\">");
					xml.append("\n");
					JSONObject staticJson = new JSONObject((String)map.get("staticPanel"));
					for (Iterator widgetsIt = staticJson.keys(); widgetsIt.hasNext();) {
						String widgetId = (String) widgetsIt.next();
						JSONObject widgetJSON = staticJson.getJSONObject(widgetId);
					
						xml.append( widgetJSONtoString( widgetJSON ));
					}
					xml.append("</panel>");
					xml.append("\n");
					// DynamicPanel tab is not needed if it is commandbar
					if (!COMMANDBAR_TAB_ID.equals(tabId)) {
						xml.append("<panel type=\"DynamicPanel\"");
						Boolean disabledDynamicPanel = (Boolean) map
								.get("disabledDynamicPanel");
						if (disabledDynamicPanel != null
								&& disabledDynamicPanel)
							xml.append(" disabled=\"true\"");
						xml.append(">");
						JSONObject dynamicJson = new JSONObject((String) map
								.get("dynamicPanel"));
						newDynamicPanelMap.put(map.get("roleOrder"), dynamicJson);
						for (Iterator widgetsIt = dynamicJson.keys(); widgetsIt
								.hasNext();) {
							String widgetId = (String) widgetsIt.next();
							JSONObject widget = dynamicJson
									.getJSONObject(widgetId);
							if (widget.get("id") == null
									|| widget.get("id").equals(""))
								continue;
							
							xml.append("<widget");
							xml.append(" id=").append("\"");
							xml.append((String) widget.get("id")).append("\"");
							xml.append(" column=").append("\"");
							xml.append((String) widget.get("column")).append("\"");
							
							if( widget.has("properties")) {
								xml.append(">");
								JSONObject properties = ( JSONObject )widget.get("properties");
								xml.append("<data>");
								for( Iterator keys=properties.keys();keys.hasNext();) {
									String key = ( String )keys.next();
									
									xml.append("<property name=\"").append( key ).append("\">");
									xml.append( XmlUtil.escapeXmlEntities( properties.get( key ).toString()));
									xml.append("</property>");
								}
								xml.append("</data>");
								xml.append("</widget>");
							} else {
								xml.append("/>");
							}
						}
						xml.append("</panel>");
						xml.append("\n");
					}
					xml.append("</widgets>");
//					Document doc = AdminServiceUtil.stringToDocument(xml
//							.toString());
					
					// Setting again
//					map.put("widgets", doc);
					map.put("widgets", xml.toString());
					map.put("tabId", tabId);
					map.put("tabNumber", tabNumber != null
							&& tabNumber.length() > 0 ? tabNumber : null);
					
					String roleName = (String)map.get("roleName");
					if(DEFAULT_ROLE_NAME.equals(roleName) && 
							map.containsKey("disabledDefault") && Boolean.parseBoolean((String)map.get("disabledDefault"))){
						map.put("deleteFlag", "1");
					}else{
						map.put("deleteFlag", "0");
					}
					map.put("temp", "1");
					map.put("workinguid", myUid);
				}
				tabLayoutDAO.insert(map);
			}

			updateWidgets(oldDynamicPanelMap, newDynamicPanelMap);
			
			// Update last modified date of tab0 if it is commandbar
			if (COMMANDBAR_TAB_ID.equals(tabId)) {
				tabLayoutDAO.updateLastmodifiedByTabId("0");
			}

			return "[" + JSONObject.quote(tabId) + ", " + JSONObject.quote(tabNumber) + "]";
		} catch (Exception e) {
			log.error("Unexpected error occurred.", e);
			throw e;
		}
	}
	
	private static String widgetJSONtoString( JSONObject widget ) throws JSONException {
		StringBuffer xml = new StringBuffer();
		
		boolean disabled = false;
		if(widget.has("disabled"))
			disabled = widget.getBoolean("disabled");
		
		xml.append("<" + (disabled ? "!--" : "") + "widget");
		xml.append(" id=").append("\"").append( XmlUtil.escapeXmlEntities( widget.getString("id"))).append("\"");
		xml.append(" href=").append("\"");
		if(widget.has("href"))
			xml.append( XmlUtil.escapeXmlEntities((String) widget.get("href")));
		
		xml.append("\"");
		xml.append(" title=").append("\"").append(
				XmlUtil.escapeXmlEntities(widget.getString("title"))).append("\"");
		
		xml.append(" type=").append("\"").append(
				widget.getString("type")).append("\"");
		
		if ( widget.has("ignoreHeader") && widget.getBoolean("ignoreHeader"))
			xml.append(" ignoreHeader=\"true\"");
		if ( widget.has("noBorder") && widget.getBoolean("noBorder"))
			xml.append(" noBorder=\"true\"");
		
		xml.append(">").append("\n");
		
		xml.append("<data>").append("\n");
		JSONObject properties = widget.getJSONObject("properties");
		for (Iterator ite = properties.keys(); ite.hasNext();) {
			String propertyName = (String) ite.next();
			if(JSONObject.NULL == properties.get(propertyName)) continue;
			
			String propertyValue = properties.getString(propertyName);
			
			xml.append("<property name=").append("\"").append(
					XmlUtil.escapeXmlEntities(propertyName)).append("\"").append(">");
			xml.append(XmlUtil.escapeXmlEntities(propertyValue));
			xml.append("</property>\n");
		}
		xml.append("</data>").append("\n");
		
		xml.append("</widget" + (disabled ? "--" : "") + ">");
		
		return xml.toString();
	}
	
	private void updateWidgets(Map oldDynamicPanelMap, Map newDynamicPanelMap) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @param tabId
	 * @throws Exception
	 */
	public synchronized void removeDefaultPanel(String tabId) throws Exception {
		try {
			// Update
			tabLayoutDAO.updateDeleteFlag(tabId, "1");
		} catch (Exception e) {
			log.error("Unexpected error occurred.", e);
			throw e;
		}
	}

	/**
	 * for debug
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String args[]) throws Exception {
		//System.out.println(getHandle().getDefaultPanelJson(COMMANDBAR_TAB_ID));
		System.out.println(getHandle().getTabIdListJson());
		System.out.println(getHandle().getDefaultPanelJson(COMMANDBAR_TAB_ID));
	}
	
	/**
	 * Obtain user who is editing
	 * @return
	 */
	public String getLockingUid(){
		return tabLayoutDAO.selectLockingUid();
	}

	/**
	 * @param tabId
	 * @return
	 * @throws Exception
	 */
	public String getDefaultPanelJson(String tabId) throws Exception {
		List tabLayoutList = this.tabLayoutDAO.selectByTabId(tabId);
		JSONObject result = new JSONObject();
		JSONObject value = null;
		for(Iterator it = tabLayoutList.iterator(); it.hasNext();){
			TabLayout tablayout = (TabLayout)it.next();
			value = new JSONObject();
//			value.put("id", Tablayout.getId().getTabid() + "_" + Tablayout.getRole());	// tabId+role can not be unique
			value.put("id", tablayout.getId().getTabid() + "_" + tablayout.getId().getRoleorder() + "_" + tablayout.getRole());	// fix #174
			value.put("tabId", tablayout.getId().getTabid());
			value.put("tabName", tablayout.getTabName());
			value.put("columnsWidth", tablayout.getColumnsWidth());
			value.put("tabNumber", tablayout.getTabnumber());
			value.put("role", tablayout.getRole());
			value.put("principalType", tablayout.getPrincipaltype());
			value.put("roleOrder", tablayout.getId().getRoleorder().intValue() );
			value.put("roleName", tablayout.getRolename());
			value.put("defaultUid", tablayout.getDefaultuid());
			value.put("widgetsLastmodified", tablayout.getWidgetslastmodified());
			value.put("staticPanel", (tabId.equalsIgnoreCase("commandbar"))?
					tablayout.getStaticPanelJsonWithComment() : tablayout.getStaticPanelJson());
			value.put("layout", tablayout.getLayout());
			value.put("dynamicPanel", tablayout.getDynamicPanelJson());
			value.put("disabledDynamicPanel", tablayout.isDisabledDynamicPanel());
			
			result.put(value.getString("id"), value);
			
			if(DEFAULT_ROLE_NAME.equals(tablayout.getRolename()) && tablayout.getDeleteflag().intValue() == 1)
				value.put("disabledDefault", true);
		}
		
		// fix #174
		if(value != null){
			// At the end of roleOrde is default（This is only way to determine as regular expression is not unique because of handling ecah subject）
			value.put("isDefault", "true");
		}
		
		return result.toString();
	}

	/**
	 * @return
	 * @throws Exception
	 */
	public String getTabIdListJson() throws Exception {
		//Copy data to TEMP.
		ISPrincipal p = SecurityController.getPrincipalByType("UIDPrincipal");
		String myUid = p.getName();
		tabLayoutDAO.copy(myUid, true);
		
		List list = this.tabLayoutDAO.selectTabId();
		
		//[["commandbar","0"],{"commandbar":{id:""},"0":{id:"0"}}]

		JSONArray json = new JSONArray();
		// Generate JSON
		JSONArray tabIdList = new JSONArray();
		JSONObject tabNumberMap = new JSONObject();
		for(Iterator ite = list.iterator(); ite.hasNext();){
			Object[] obj = (Object[])ite.next();
			String tabId = (String) obj[0];
			Integer tabNum = (Integer)obj[1];
			
			tabIdList.put(tabId);
			JSONObject tabNumObj = new JSONObject();
			tabNumObj.put("id", tabNum == null ? null : tabNum.toString());//TODO: 
			tabNumberMap.put(tabId, tabNumObj);
		
		}
		json.put(tabIdList);
		json.put(tabNumberMap);
//		System.out.println(json.toString());
		return json.toString();
	}

	/**
	 * Return map of Customization information related to role information.
	 * Return default Customization information if role can not be found.
	 * 	
	 * @param resource
	 * @return Map
	 * <UL>
	 * 	<LI>key		: tabId</LI>
	 * 	<LI>value	: layout</LI>
	 * </UL>
	 * 		
	 * @throws DataResourceException
	 * @throws ClassNotFoundException
	 * @throws Exception
	 */
	public Map getMyTabLayoutHTML() throws ClassNotFoundException, Exception{
		Map map = getMyTabLayout();
		Map customizationMap = new SequencedHashMap();
		
		Iterator ite = map.keySet().iterator();
		
		while(ite.hasNext()){
			String tabId = (String)ite.next();
			TabLayout tabLayout = (TabLayout)map.get(tabId);
			
			customizationMap.put(tabId, tabLayout.getLayout());
		}
		
		return customizationMap;
	}
	
	/**
	 * Return map of Customization information related to role information.
	 * Return default Customization information if role can not be found.
	 * 	
	 * @param resource
	 * @return Map
	 * <UL>
	 * 	<LI>key		: tabId</LI>
	 * 	<LI>value	: XmlObject</LI>
	 * </UL>
	 * @throws DataResourceException
	 */
	public Map getMyTabLayout(String tabId) {
		Map resultMap = new HashMap();
		Subject loginUser = SecurityController.getContextSubject();
		if(loginUser == null){
			// Return default
			resultMap = getDefaultTabLayout(null);
		}else{
			long start = System.currentTimeMillis();
			MultiHashMap map = this.tabLayoutDAO.getTabLayout(tabId);
			Iterator ite = map.keySet().iterator();
			
			while(ite.hasNext()){
				boolean isEmpty = true;
				String key = (String)ite.next();
				List layoutList = (List)map.get(key);
				
				Iterator docIte = layoutList.iterator();
				while(docIte.hasNext()){
					TabLayout layout = (TabLayout)docIte.next();
					
					try {
						if(RoleUtil.isPermitted(layout.getPrincipaltype(), layout.getRole())){
							isEmpty = false;
							resultMap.put(key, layout);
							break;
						}
					} catch (ClassNotFoundException e) {
						log.error("", e);
					}
				}
				
				if(isEmpty){
					// Default of tab is obtained if tab information can not be found.
					putDefaultTabLayout(key, layoutList, resultMap);
				}
			}
		}
		Map map = sortMapBySortId(resultMap);
		return map;
	}
	
	public Map getMyTabLayout() {
		return getMyTabLayout(null);
	}
	
	/**
	 * Obtain default tablayout information.
	 * 
	 * @param resource
	 * @param layoutMap
	 * @return Map
	 * <UL>
	 * 	<LI>key		: tabId</LI>
	 * 	<LI>value	: XmlObject</LI>
	 * </UL>
	 * @throws DataResourceException
	 */
	public Map getDefaultTabLayout(MultiHashMap layoutMap) {
		Map resultMap = new HashMap();
		if(layoutMap == null){
			layoutMap = this.tabLayoutDAO.getTabLayout(null);
//			layoutMap = TabLayoutDAO.newInstance().getTabLayout(null);
		}
		
		Iterator ite = layoutMap.keySet().iterator();
		while(ite.hasNext()){
			String key = (String)ite.next();
			List tabList= (List)layoutMap.get(key);
			putDefaultTabLayout(key, tabList, resultMap);
		}
		return resultMap;
	}
	
	private void putDefaultTabLayout(String tabId, List layoutList, Map targetMap){
		Iterator layoutIte = layoutList.iterator();
		while(layoutIte.hasNext()){
			TabLayout layout = (TabLayout)layoutIte.next();
			String role = layout.getRole();
			if(WidgetServlet.getDefaultUid().equals(role)){
				targetMap.put(tabId, layout);
				break;
			}
		}
	}
	
	private Map sortMapBySortId(Map map){
		ArrayList entries = new ArrayList(map.entrySet());
		Collections.sort(entries,new Comparator(){
			public int compare(Object o1, Object o2){
				Map.Entry e1 =(Map.Entry)o1;
				Map.Entry e2 =(Map.Entry)o2;
				TabLayout x1 = (TabLayout)e1.getValue();
				TabLayout x2 = (TabLayout)e2.getValue();
				
				int i = 0;
				int j = 0;
				try{
					i = x1.getTabnumber() != null? x1.getTabnumber().intValue() : 0;
				}catch(NumberFormatException e){
					return 1;
				}
				try{
					j = x2.getTabnumber() != null? x2.getTabnumber().intValue() : 0;
				}catch(NumberFormatException e){
					return 0;
				}
				
				return (i > j)? 1 : 0;
			}
		});
		
		Iterator ite = entries.iterator();
		Map sortedMap = new SequencedHashMap();
		while(ite.hasNext()){
			Map.Entry e1 = (Map.Entry)ite.next();
			sortedMap.put(e1.getKey(), e1.getValue());
		}
		return sortedMap;
	}

	public Collection getDefaultTabLayout() throws Exception {
		Map map = sortMapBySortId( getDefaultTabLayout(null));
		return map.values();
	}

}
