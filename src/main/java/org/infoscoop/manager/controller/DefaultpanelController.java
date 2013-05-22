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

package org.infoscoop.manager.controller;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.infoscoop.acl.ISPrincipal;
import org.infoscoop.acl.SecurityController;
import org.infoscoop.dao.StaticTabDAO;
import org.infoscoop.dao.model.StaticTab;
import org.infoscoop.dao.model.TabLayout;
import org.infoscoop.service.PortalAdminsService;
import org.infoscoop.service.StaticTabService;
import org.infoscoop.service.TabLayoutService;
import org.infoscoop.util.spring.TextView;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DefaultpanelController implements ControllerInterface{
	@RequestMapping(method=RequestMethod.GET)
	public String index(HttpServletRequest request) throws Exception {
		request.setAttribute("isDefaultPanelAdmin", true);
		request.setAttribute("tabAdminsJSON", StaticTabService.getHandle().getTabAdminsJSON());
		request.setAttribute("tabListJSON", StaticTabService.getHandle().getTabIdListJson());
		return "defaultpanel/index";
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(method=RequestMethod.POST)
	public String addTab(HttpServletRequest request, 
			HttpServletResponse response, 
			@RequestParam String addTabJson) throws Exception {
		String defaultPanelJson = "{}";

		Map emptyDataMap = new HashMap();
		Map emptyDataMapWrapper = new HashMap();
		JSONObject json = new JSONObject(addTabJson);
		String key;
		for(Iterator keys = json.keys();keys.hasNext();){
			key = (String)keys.next();
			emptyDataMap.put(key, json.getString(key));
		}
		emptyDataMap.put("roleOrder", 0);
		emptyDataMapWrapper.put("emptyDataMap", emptyDataMap);
		
		String tabId = TabLayoutService.getHandle().getNewTabId();
		TabLayoutService.getHandle().updateDefaultPanel(tabId, emptyDataMapWrapper, true);
		request.setAttribute("tabId", tabId);
		request.setAttribute("isNew", true);
		
		defaultPanelJson = TabLayoutService.getHandle().getDefaultPanelJson(tabId);
		request.setAttribute("defaultPanelJson", defaultPanelJson);
		
		return "defaultpanel/editTab";
	}

	@RequestMapping(method=RequestMethod.GET)
	public String editTab(HttpServletRequest request, 
			HttpServletResponse response, 
			@RequestParam("tabId") String tabId) throws Exception {
		String defaultPanelJson = "{}";
		int displayTabOrder = -1;
		
		StaticTab tab = StaticTabDAO.newInstance().getTab(tabId);
		if(tab == null){
			request.setAttribute("errorMessage", "alb_error_tabnotfound");
			index(request);
			return "defaultpanel/index";
		}
		
		request.setAttribute("tabId", tabId);
		request.setAttribute("tabNumber", tab.getTabnumber());
		request.setAttribute("tabDesc", tab.getTabdesc()!=null? tab.getTabdesc() : "");
		
		displayTabOrder = StaticTabService.getHandle().getDisplayTabOrder(tabId);
		request.setAttribute("displayTabOrder", displayTabOrder);
		
		// forceEdit flag
		String forceEdit = (String)request.getAttribute("forceEdit");
		boolean isForceEdit = (forceEdit != null);
		
		// checkTimeout
		if(TabLayoutService.getHandle().checkTimeout(tabId))
			isForceEdit = true;
		
		if(!isForceEdit){
			// Check locking by another user.
			String lockingUid = TabLayoutService.getHandle().getLockingUid(tabId);
			ISPrincipal p = SecurityController.getPrincipalByType("UIDPrincipal");
			if(lockingUid != null && !p.getName().equals(lockingUid)){
				// If a record is locking, go to conflict page.
				request.setAttribute("lockingUid", lockingUid);
				return "defaultpanel/conflict";
			}
		}
		
		// lock
		TabLayoutService.getHandle().copyToTemp(tabId);

		defaultPanelJson = TabLayoutService.getHandle().getDefaultPanelJson(tabId);
		request.setAttribute("defaultPanelJson", defaultPanelJson);
		
		return "defaultpanel/editTab";
	}
	
	@RequestMapping(method=RequestMethod.GET)
	public String commandbar(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String result =  editTab(request, response, TabLayoutService.COMMANDBAR_TAB_ID);
		request.setAttribute("commandbarView", true);
		return result;
	}	

	@RequestMapping(method=RequestMethod.GET)
	public String forceEdit(HttpServletRequest request, 
			HttpServletResponse response,
			@RequestParam("tabId") String tabId) throws Exception {
		request.setAttribute("forceEdit", "true");
		
		if(TabLayoutService.COMMANDBAR_TAB_ID.equals(tabId))
			return commandbar(request, response);
		
		return editTab(request, response, tabId);
	}
	
	@RequestMapping(method=RequestMethod.POST)
	public TextView commitTab(HttpServletRequest request,
			HttpServletResponse response,
			@RequestBody String updateDataJson) throws Exception {
		StaticTabService.getHandle().updateStaticTab(updateDataJson);
		
		TextView view = new TextView();
		view.setResponseBody("update is succeed.");
		view.setContentType("application/json; charset=UTF-8");
		return view;
//		return "redirect:index";
	}
	
	@RequestMapping(method=RequestMethod.GET)
	public void iframe() throws Exception {
	}

	@RequestMapping(method=RequestMethod.GET)
	public void blank() throws Exception {
	}
	
	@RequestMapping(method=RequestMethod.GET)
	public String editRole() throws Exception {
		return "defaultpanel/editRole";
	}
	
	public String getRoleName() {
		return PortalAdminsService.ADMINROLE_DEFAULTPANEL;
	}
	
//	@Transactional
	@RequestMapping(method=RequestMethod.GET)
	public TextView widsrv(HttpServletRequest request,
			@RequestParam("tabId") String tabId,
			@RequestParam("roleOrder") Integer roleOrder, Model model)
			throws Exception {
		TabLayoutService service = TabLayoutService.getHandle();
		String uid = SecurityController.getPrincipalByType("UIDPrincipal").getName();

		JSONArray tabsJson = new JSONArray();

		JSONObject bvObj = new JSONObject();
		bvObj.append("buildVersion", "");

		JSONObject tabJson = service.getTabJson(uid, tabId, roleOrder, TabLayout.TEMP_TRUE);

		tabsJson.put(bvObj);
		tabsJson.put(tabJson);

		TextView view = new TextView();
		view.setResponseBody(tabsJson.toString());
		view.setContentType("application/json; charset=UTF-8");
		return view;
	}
}
