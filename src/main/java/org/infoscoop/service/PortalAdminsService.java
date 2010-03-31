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
import org.infoscoop.dao.model.Portaladmin;
import org.infoscoop.util.SpringUtil;
import org.json.JSONArray;
import org.json.JSONObject;

public class PortalAdminsService {

	private static Log log = LogFactory.getLog(PortalAdminsService.class);

	private PortalAdminsDAO portalAdminsDAO;
	private AdminRoleDAO adminRoleDAO;
	
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
			Portaladmin portalAdmin = (Portaladmin) it.next();//Key become capital if Map is passed to constructor of JSONObject without change.
			jobj = new JSONObject();
			
			jobj.put("uid", portalAdmin.getUid());
			jobj.put("roleId", portalAdmin.getAdminrole().getRoleid());
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
	public List<Portaladmin> getPortalAdmins() throws Exception {
		return portalAdminsDAO.select();
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
		Portaladmin pa = portalAdminsDAO.selectById(p.getName());
		
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
		return !isPermitted("menu") && isPermitted("menu_tree");
	}
}