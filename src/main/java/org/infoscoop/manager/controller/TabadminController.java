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

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.infoscoop.dao.StaticTabDAO;
import org.infoscoop.dao.model.StaticTab;
import org.infoscoop.service.PortalAdminsService;
import org.infoscoop.service.StaticTabService;
import org.infoscoop.util.spring.TextView;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/tabadmin")
public class TabadminController extends DefaultpanelController implements ControllerInterface{
	public String getRoleName() {
		return PortalAdminsService.ADMINROLE_TAB_ADMIN;
	}
	
	@Override
	public String index(HttpServletRequest request) throws Exception {
		request.setAttribute("isDefaultPanelAdmin", false);
		request.setAttribute("tabAdminsJSON", "[]");
		request.setAttribute("tabListJSON", StaticTabService.getHandle().getTabIdListJson());
		return "defaultpanel/index";
	}
	
	@Override
	public String addTab(HttpServletRequest request,
			HttpServletResponse response, String addTabJson) throws Exception {
		return res403(response);
	}
	
	@Override
	public String editTab(HttpServletRequest request,
			HttpServletResponse response, String tabId) throws Exception {

		StaticTab tab = StaticTabDAO.newInstance().getTab(tabId);
		if(tab == null){
			request.setAttribute("errorMessage", "alb_error_tabnotfound");
			index(request);
			return "defaultpanel/index";
		}

		if(!StaticTabService.getHandle().isTabAdmin(tabId))
			return res403(response);
		
		return super.editTab(request, response, tabId);
	}
	
	@Override
	public String commandbar(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return res403(response);
	}
	
	@Override
	public TextView commitTab(HttpServletRequest request,
			HttpServletResponse response,
			@RequestBody String updateDataJson) throws Exception {
		
		TextView view = new TextView();
		view.setContentType("application/json; charset=UTF-8");
		res403(response);
		return view;
	}
	
	private String res403(HttpServletResponse response) throws IOException{
		response.sendError(403, "You are not administrator.");
		return null;
	}
}
