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

import javax.servlet.http.HttpServletRequest;

import org.infoscoop.acl.SecurityController;
import org.infoscoop.dao.model.TabLayout;
import org.infoscoop.service.TabLayoutService;
import org.infoscoop.util.spring.TextView;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DefaultpanelController implements ControllerInterface{
	@RequestMapping(method=RequestMethod.GET)
	public void index() throws Exception {
	}

	@RequestMapping(method=RequestMethod.GET)
	public void editRole() throws Exception {
	}

	public String getRoleName() {
		return "defaultPanel";
	}

//	@Transactional
	@RequestMapping
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
