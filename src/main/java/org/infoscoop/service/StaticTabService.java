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

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.transform.TransformerException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.Project;
import org.apache.xerces.parsers.DOMParser;
import org.apache.xpath.XPathAPI;
import org.cyberneko.html.HTMLConfiguration;
import org.infoscoop.acl.ISPrincipal;
import org.infoscoop.acl.SecurityController;
import org.infoscoop.dao.StaticTabDAO;
import org.infoscoop.dao.TabAdminDAO;
import org.infoscoop.dao.TabLayoutDAO;
import org.infoscoop.dao.model.Adminrole;
import org.infoscoop.dao.model.Portaladmins;
import org.infoscoop.dao.model.StaticTab;
import org.infoscoop.dao.model.TabAdmin;
import org.infoscoop.dao.model.TabAdminPK;
import org.infoscoop.dao.model.TabLayout;
import org.infoscoop.util.HtmlUtil;
import org.infoscoop.util.SpringUtil;
import org.infoscoop.util.XmlUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.traversal.NodeIterator;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class StaticTabService {
	private static Object lock = new Object();

	private StaticTabDAO staticTabDAO;
	private TabAdminDAO tabAdminDAO;
	private static List<String> undeletableTabIdList
		= Arrays.asList(StaticTab.COMMANDBAR_TAB_ID, StaticTab.PORTALHEADER_TAB_ID, StaticTab.TABID_HOME);
	
	private static Log log = LogFactory.getLog(StaticTabService.class);

	public StaticTabService() {
	}

	public void setStaticTabDAO(StaticTabDAO staticTabDAO) {
		this.staticTabDAO = staticTabDAO;
	}
	
	public void setTabAdminDAO(TabAdminDAO tabAdminDAO) {
		this.tabAdminDAO = tabAdminDAO;
	}

	public static StaticTabService getHandle() {
		return (StaticTabService) SpringUtil.getBean("StaticTabService");
	}

	/**
	 * tabIdList for DefaultPanel administration.
	 * 
	 * @return jsonString
	 * @throws Exception
	 */
	public String getTabIdListJson() throws Exception {
		ISPrincipal p = SecurityController.getPrincipalByType("UIDPrincipal");
		String uid = p.getName();
		
		PortalAdminsService service = PortalAdminsService.getHandle();
		
		List<StaticTab> result = staticTabDAO.getStaticTabList();
		boolean isTabAdmin = service.isPermitted(PortalAdminsService.ADMINROLE_TAB_ADMIN)
				&& !service.isPermitted(PortalAdminsService.ADMINROLE_DEFAULTPANEL);

		JSONArray jsonArray = new JSONArray();
		List<String> tabIdList = new ArrayList<String>();

		JSONObject json;
		JSONArray admins;
		
		int rowNo = 0;
		for (Iterator<StaticTab> ite = result.iterator(); ite.hasNext();) {
			json = new JSONObject();
			admins = new JSONArray();

			StaticTab st = (StaticTab) ite.next();
			String tabId = st.getTabid();

			if (tabIdList.contains(tabId))
				continue;
			
			List tabAdminUidList = st.getTabAdminUidList();
			json.put("rowNo", rowNo++);
			tabIdList.add(tabId);
			
			if((isTabAdmin && !tabAdminUidList.contains(uid)))
				continue;
			
			json.put("id", st.getTabid());
			json.put("tabDesc", st.getTabdesc());

			for (Iterator<String> ite2=tabAdminUidList.iterator();ite2.hasNext();) {
				admins.put(ite2.next());
			}
			json.put("adminUidList", admins);
			jsonArray.put(json);
		}
		return jsonArray.toString();
	}

	public StaticTab getStaticTab(String tabId){
		return staticTabDAO.getTab(tabId);
	}
	
	public List<StaticTab> getStaticTabList() {
		List<StaticTab> result = staticTabDAO.getAllStaicLayoutList();
		
		List<String> staticIdList = new ArrayList<String>();
		List<StaticTab> staticTabList = new ArrayList<StaticTab>();
		for(StaticTab staticTab : result){
			if(!staticIdList.contains(staticTab.getTabid()))
				staticTabList.add(staticTab);
			
			staticIdList.add(staticTab.getTabid());
		}
		return staticTabList;
	}
	

	public int getDisplayTabOrder(String tabId) throws Exception {
		List<String> tabIdList = staticTabDAO.getTabIdList();
		return tabIdList.indexOf(tabId);
	}

	public void deleteTabs(List<String> tabIdList) throws Exception {
		String tabId;
		try {
			for (Iterator<String> ite = tabIdList.iterator(); ite.hasNext();) {
				tabId = ite.next();
				if(undeletableTabIdList.contains(tabId))
					throw new RuntimeException("tabId=" + tabId + " cannot be deleted.");
				
				StaticTab staticTab = staticTabDAO.getTab(tabId);
				tabAdminDAO.delete(staticTab.getTabAdmin());
				staticTabDAO.updateDeleteFlag(staticTab, StaticTab.DELETEFLAG_TRUE);
			}
		} catch (Exception e) {
			log.error("Unexpected error occurred.", e);
			throw e;
		}
	}
	
	public void updateStaticTab(String updateDataJson) throws Exception{
		JSONObject json = new JSONObject(updateDataJson);
		List<String> deleteIdList = new ArrayList<String>();
		
		if(json.has("deleteIdList")){
			JSONArray array = json.getJSONArray("deleteIdList");
			
			for(int i=0;i<array.length();i++){
				deleteIdList.add(array.getString(i));
			}
			deleteTabs(deleteIdList);
		}
		if(json.has("adminUidJson")){
			JSONObject adminUidJson = json.getJSONObject("adminUidJson");
			for(Iterator<String> ite = adminUidJson.keys();ite.hasNext();){
				String tabId = ite.next();
				List adminUidList = JsonArray2List(adminUidJson.getJSONArray(tabId));
				replaceAdminUidList(tabId, adminUidList);
			}
		}		
	}

	private List JsonArray2List(JSONArray jArray) throws JSONException{
		List list = new ArrayList();
		for(int i=0;i<jArray.length();i++)
			list.add(jArray.get(i));
		
		return list;
	}
	
	public void replaceAdminUidList(String tabId, List adminUidList) throws Exception{
		tabAdminDAO.deleteByTabId(tabId);
		for(Iterator<String> ite = adminUidList.iterator();ite.hasNext();){
			String userId = ite.next();
			Portaladmins admin = PortalAdminsService.getHandle().getPortalAdmin(userId);
			if(admin != null)
				tabAdminDAO.insert(tabId, userId);
		}
	}
	
	public String getTabAdminsJSON() throws Exception{
		List<Portaladmins> admins = PortalAdminsService.getHandle().getPortalAdmins();
		JSONArray treeAdminArray = new JSONArray();
		
		for(Portaladmins admin : admins){
			Adminrole adminRole = admin.getAdminrole();
			if(adminRole == null)
				continue;
			
			String permissionStr = adminRole.getPermission();
			JSONArray jArray = new JSONArray(permissionStr);
			for(int i=0;i<jArray.length();i++){
				if(PortalAdminsService.ADMINROLE_TAB_ADMIN.equals(jArray.getString(i)))
					treeAdminArray.put(admin.getUid());
			}
		}
		
		return treeAdminArray.toString();
	}

	public boolean isTabAdmin(String tabId){
		if(PortalAdminsService.getHandle().isPermitted(PortalAdminsService.ADMINROLE_DEFAULTPANEL))
			return true;
		
		ISPrincipal p = SecurityController.getPrincipalByType("UIDPrincipal");
		String uid = p.getName();
		
		StaticTab staticTab = staticTabDAO.getTab(tabId);
		if(staticTab == null)
			return false;
		
		Set<TabAdmin> tabAdminSet = staticTab.getTabAdmin();
		for(Iterator<TabAdmin> ite=tabAdminSet.iterator();ite.hasNext();){
			TabAdmin tabAdmin = ite.next();
			if(uid.equals(tabAdmin.getId().getUid()))
				return true;
		}
		return false;
	}
	
	public void replaceStaticTab(String currentStaticTabId, StaticTab newStaticTab) throws SAXException, IOException, TransformerException{
		StaticTab currentStaticTab = getStaticTab(currentStaticTabId);
		if(currentStaticTab != null){
			replaceStaticTab(currentStaticTab, newStaticTab);
		}else{
			saveStaticTab(currentStaticTabId, newStaticTab);
		}
	}
	
	/**
	 * 
	 * @param currentStaticTab
	 * @param newStaticTab
	 * @throws SAXException
	 * @throws IOException
	 * @throws TransformerException
	 */
	public void replaceStaticTab(StaticTab currentStaticTab, StaticTab newStaticTab) throws SAXException, IOException, TransformerException{
		String targetTabId = currentStaticTab.getTabid();
		
		// set target tabid to statictab
		newStaticTab.setTabid(targetTabId);
		newStaticTab.setTabnumber(currentStaticTab.getTabnumber());

		if(targetTabId.equals(StaticTab.TABID_HOME)){
			// disableDefault is disabled on HOME tab.
			newStaticTab.setDisabledefault(StaticTab.DISABLE_DEFAULT_FALSE);
		}
		
		TabLayoutDAO tabLayoutDAO = TabLayoutDAO.newInstance();
		TabAdminDAO tadAdminDAO = TabAdminDAO.newInstance();
		
		// clean tablayouts data
		tabLayoutDAO.delete(currentStaticTab.getTabLayout());
		tabLayoutDAO.deleteTempByTabId(targetTabId);
		
		// clean tabadmin data
		tadAdminDAO.delete(currentStaticTab.getTabAdmin());
		
		staticTabDAO.deleteTab(currentStaticTab);
		saveStaticTab(targetTabId, newStaticTab);
	}
	
	/**
	 * Preservation of a staticTab and renewal of gadget ID (in xml, layoutHtml) are performed.</br> 
	 * @param tabId
	 * @param newStaticTab
	 * @throws SAXException
	 * @throws IOException
	 * @throws TransformerException
	 */
	@SuppressWarnings("unchecked")
	public void saveStaticTab(String tabId, StaticTab newStaticTab) throws SAXException, IOException, TransformerException{
		TabLayoutDAO tabLayoutDAO = TabLayoutDAO.newInstance();
		TabAdminDAO tadAdminDAO = TabAdminDAO.newInstance();
		
		Set<TabLayout> tabLayouts = newStaticTab.getTabLayout();
		for(Iterator<TabLayout> ite = tabLayouts.iterator();ite.hasNext();){
			// set target tabid to tablayout
			TabLayout tabLayout = ite.next();
			tabLayout.getId().setTabid(tabId);
			
			// update gadgetid. commandbar gadgetid is not overlap, it is not changed. 
			if(!tabId.equals(StaticTab.COMMANDBAR_TAB_ID))
				updateGadgetId(tabLayout);

			tabLayoutDAO.insert(tabLayout);
		}
		
		staticTabDAO.saveTab(newStaticTab);

		Set<TabAdmin> tabAdmins = newStaticTab.getTabAdmin();
		for(Iterator<TabAdmin> ite = tabAdmins.iterator();ite.hasNext();){
			TabAdmin tabAdmin = ite.next();
			tabAdmin.getId().setTabid(tabId);
			tadAdminDAO.insert(tabAdmin);
		}
}
	
	/**
	 * replace all static tabs. (include commandbar, header)</br>
	 * The tab layout which cannot be deleted is replaced.
	 * @param staticTabs
	 * @throws Exception
	 */
	public void replaceAllTabs(List<StaticTab> staticTabs) throws Exception{
		// delete tabLayouts
		List<StaticTab> currentStaticTabList = getStaticTabList();
		TabLayoutDAO tabLayoutDAO = TabLayoutDAO.newInstance();
		List<String> currentTabIdList = new ArrayList<String>();
		for(Iterator<StaticTab> ite=currentStaticTabList.iterator();ite.hasNext();){
			StaticTab currentStaticTab = ite.next();
			
			String currentTabId = currentStaticTab.getTabid();
			
			tabLayoutDAO.delete(currentStaticTab.getTabLayout());
			tabLayoutDAO.deleteTempByTabId(currentTabId);
			
			if(!undeletableTabIdList.contains(currentStaticTab.getTabid()))
				currentTabIdList.add(currentStaticTab.getTabid());
		}
		// delete static tabs without undeletableTabs.
		StaticTabService.getHandle().deleteTabs(currentTabIdList);
		
		// insert tabs
		for(Iterator<StaticTab> ite=staticTabs.iterator();ite.hasNext();){
			StaticTab staticTab = ite.next();
			String tabId = staticTab.getTabid();
			
			if(undeletableTabIdList.contains(tabId)){
				replaceStaticTab(tabId, staticTab);
			}else{
				tabId = getNewTabId();
				staticTab.setTabid(tabId);
				saveStaticTab(tabId, staticTab);
			}
		}
	}
	
	/**
	 * Update gadgetId from gadgetXML, layoutHTML
	 * @param tabLayout
	 * @throws SAXException 
	 * @throws IOException 
	 * @throws TransformerException 
	 */
	private void updateGadgetId(TabLayout tabLayout) throws SAXException, IOException, TransformerException{
		Element gadgets = tabLayout.getElement();
		String layoutHTML = tabLayout.getLayout();
		
		Element layout = HtmlUtil.html2Dom(layoutHTML);
		if(layout == null) return;
		
		NodeIterator widgetIte = XPathAPI.selectNodeIterator(gadgets, "//widget");
		Element widget;
		int count = 0;
		while((widget = (Element)widgetIte.nextNode()) != null){
			String newId = "p_" + new Date().getTime() + "_" + tabLayout.getId().getTabid() + "_" + count++;
			String oldId = widget.getAttribute("id");
			Element gadgetDiv = (Element)XPathAPI.selectSingleNode(layout, "//*[@id='" + oldId + "']");
			
			widget.setAttribute("id", newId);
			if(gadgetDiv != null){
				gadgetDiv.setAttribute("id", newId);
			}
		}
		
		tabLayout.setElement(gadgets);
		tabLayout.setLayout(XmlUtil.dom2HtmlString(layout, "UTF-8"));
	}
	
	public Integer getNextTabNumber() throws Exception{
		synchronized(lock){
			Map maxMap = staticTabDAO.selectMax();
			String tabNumber = maxMap.get("tabNumber").toString();
			if (tabNumber != null && tabNumber.length() != 0) {
				int newInt = Integer.valueOf(tabNumber).intValue();
				tabNumber = String.valueOf(newInt + 1).toString();
			} else {
				throw new Exception("\"select max tabNumber\" not found");
			}
			return new Integer(tabNumber);
		}
	}
	
	public String getNewTabId() throws Exception{
		synchronized(lock){
			String tabId = staticTabDAO.selectMaxTabId();
			
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
			return tabId;
		}
	}
	
}
