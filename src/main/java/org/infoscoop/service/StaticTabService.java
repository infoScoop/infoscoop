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
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.acl.ISPrincipal;
import org.infoscoop.acl.SecurityController;
import org.infoscoop.dao.StaticTabDAO;
import org.infoscoop.dao.TabAdminDAO;
import org.infoscoop.dao.model.Adminrole;
import org.infoscoop.dao.model.Portaladmins;
import org.infoscoop.dao.model.StaticTab;
import org.infoscoop.dao.model.TabAdmin;
import org.infoscoop.util.SpringUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class StaticTabService {

	private StaticTabDAO staticTabDAO;
	private TabAdminDAO tabAdminDAO;

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

	public int getDisplayTabOrder(String tabId) throws Exception {
		List<String> tabIdList = staticTabDAO.getTabIdList();
		return tabIdList.indexOf(tabId);
	}

	public void deleteTabs(List<String> tabIdList) throws Exception {
		String tabId;
		try {
			for (Iterator<String> ite = tabIdList.iterator(); ite.hasNext();) {
				tabId = ite.next();
				staticTabDAO.updateDeleteFlag(tabId, StaticTab.DELETEFLAG_TRUE);
			}
		} catch (Exception e) {
			log.error("Unexpected error occurred.", e);
			throw e;
		}
	}
	
	public void updateStaticTab(String updateDataJson) throws Exception{
		JSONObject json = new JSONObject(updateDataJson);
		
		if(json.has("deleteIdList")){
			JSONArray array = json.getJSONArray("deleteIdList");
			
			List<String> deleteIdList = new ArrayList<String>();
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
}
