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
import java.util.Map;

import org.apache.commons.collections.SequencedHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.acl.ISPrincipal;
import org.infoscoop.acl.SecurityController;
import org.infoscoop.dao.AdminRoleDAO;
import org.infoscoop.dao.PortalAdminsDAO;
import org.infoscoop.dao.model.Adminrole;
import org.infoscoop.dao.model.Portaladmins;
import org.infoscoop.util.SpringUtil;
import org.json.JSONArray;
import org.json.JSONObject;

public class PortalAdminsService {

	private static Log log = LogFactory.getLog(PortalAdminsService.class);

	private PortalAdminsDAO portalAdminsDAO;
	private AdminRoleDAO adminRoleDAO;
	
	public static final String ADMINROLE_DEFAULTPANEL = "defaultPanel";
	public static final String ADMINROLE_TAB_ADMIN = "tabAdmin";
	public static final String ADMINROLE_MENU = "menu";
	public static final String ADMINROLE_MENU_TREE = "menu_tree";
	
	/**
	 * Constructor
	 */
	public PortalAdminsService() {
	}

	/**
	 * @return
	 */
	public static PortalAdminsService getHandle() {
		return (PortalAdminsService) SpringUtil.getBean("PortalAdminsService");
	}

	/**
	 * @param portalAdminsDAO
	 */
	public void setPortalAdminsDAO(PortalAdminsDAO portalAdminsDAO) {
		this.portalAdminsDAO = portalAdminsDAO;
	}

	/**
	 * @param adminRoleDAO
	 */
	public void setAdminRoleDAO(AdminRoleDAO adminRoleDAO) {
		this.adminRoleDAO = adminRoleDAO;
	}

	/**
	 * @param adminsList
	 * @throws Exception 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public synchronized void updatePortalAdmins(Map adminsMap) throws Exception {
		if (adminsMap == null)
			return;
		
		boolean myIdExists = true;
		ISPrincipal p = SecurityController.getPrincipalByType("UIDPrincipal");

		List getNotAllowDeleteRoleIds = adminRoleDAO.getNotAllowDeleteRoleIds();
		
		adminRoleDAO.delete();
		
		List rolesList = (List)adminsMap.get("roles");
		List roleIdList = new ArrayList();
		
		// insert roles
		for(Iterator ite=rolesList.iterator();ite.hasNext();){
			Map map = (Map)ite.next();
			String roleId = (String)map.get("id");
			String name = (String)map.get("name");
			String permission = (String)map.get("permission");
			
			adminRoleDAO.insert(roleId, name, permission, !getNotAllowDeleteRoleIds.contains(roleId));
			roleIdList.add(roleId);
		}

		Map adminsData = new SequencedHashMap();
		List adminsList = (List)adminsMap.get("admins");
		String myRoleId = "";
		for (Iterator ite=adminsList.iterator();ite.hasNext();) {
			Map map = (Map) ite.next();
			String uid = (String)map.get("uid");
			String roleId = (String)map.get("roleId");
			
			if(p.getName().equals(uid)){
				myRoleId = roleId;
				myIdExists = true;
			}
			
			if (uid != null && roleId != null) {
				adminsData.put(uid, roleId);
			}
		}
		
		if(!myIdExists)
			throw new Exception("Same ID as oneself cannot be deleted.");
		
		portalAdminsDAO.delete();

		// insert admins
		for(Iterator ite=adminsData.keySet().iterator();ite.hasNext();){
			String uid = (String)ite.next();
			String roleId = (String)adminsData.get(uid);
			roleId = (roleIdList.contains(roleId))? roleId : null;
			
			portalAdminsDAO.insert(uid, roleId);
		}
		
		if(!roleIdList.contains(myRoleId) || !roleIdList.containsAll(getNotAllowDeleteRoleIds)){
			throw new Exception("The roll that cannot be deleted is contained.");
		}

	}
	
	/**
	 * @return
	 * @throws Exception
	 */
	public String getPortalAdminsJson() throws Exception {
		JSONObject adminsObj = new JSONObject();
		JSONObject jobj;
		
		List adminsList = getPortalAdmins();
		JSONArray adminsJson = new JSONArray();
		for (Iterator it = adminsList.iterator(); it.hasNext();) {
			Portaladmins portalAdmin = (Portaladmins) it.next();//Key become capital if Map is passed to constructor of JSONObject without change.
			jobj = new JSONObject();
			
			jobj.put("uid", portalAdmin.getUid());
			jobj.put("roleId", portalAdmin.getRoleid());
			adminsJson.put( jobj );
		}
		
		List roleList = adminRoleDAO.select();
		JSONArray rolesJson = new JSONArray();
		for (Iterator it = roleList.iterator(); it.hasNext();) {
			Adminrole adminRole = (Adminrole) it.next();
			jobj = new JSONObject();
			
			jobj.put("id", adminRole.getRoleid());
			jobj.put("name", adminRole.getName());
			jobj.put("permission", adminRole.getPermission());
			jobj.put("isAllowDelete", adminRole.isAllowDelete());
			rolesJson.put( jobj );
		}
		
		adminsObj.put("admins", adminsJson);
		adminsObj.put("roles", rolesJson);
		
		return adminsObj.toString();
	}

	/**
	 * @return
	 * @throws Exception
	 */
	public List<Portaladmins> getPortalAdmins() throws Exception {
		return portalAdminsDAO.select();
	}
	
	/**
	 * @return
	 * @throws Exception
	 */
	public Portaladmins getPortalAdmin(String userId) throws Exception {
		return portalAdminsDAO.selectById(userId);
	}
	
	/**
	 * 
	 * @param authorityid
	 * @return
	 */
	public boolean isPermitted(String authorityid){
		return getMyPermissionList().contains(authorityid);
	}
	
	public List getMyPermissionList(){
		List permissionList = new ArrayList();
		ISPrincipal p = SecurityController.getPrincipalByType("UIDPrincipal");
		Portaladmins pa = portalAdminsDAO.selectById(p.getName());
		
		try {
			JSONArray jArray = new JSONArray(pa.getAdminrole().getPermission());
			for(int i=0;i<jArray.length();i++){
				permissionList.add(jArray.getString(i));
			}
		} catch (Exception e) {
			log.error("", e);
		}
		return permissionList;
	}
	
	public boolean isMenuTreeRoleUser(){
		return !isPermitted(PortalAdminsService.ADMINROLE_MENU) && isPermitted(PortalAdminsService.ADMINROLE_MENU_TREE);
	}
}
