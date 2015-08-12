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

package org.infoscoop.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.infoscoop.dao.model.TabLayout;
import org.infoscoop.service.StaticTabService;
import org.infoscoop.service.TabLayoutService;
import org.json.JSONObject;

public class DesignServlet extends HttpServlet {
	private static final long serialVersionUID = "org.infoscoop.web.DesignServlet"
			.hashCode();

	private static Log log = LogFactory.getLog(DesignServlet.class);

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		String tabId = request.getParameter("tabId");
		PrintWriter out = null;
		try {
			TabLayoutService service = TabLayoutService.getHandle();
			Integer currentRoleOrder = getCurrentOrder(tabId);
			String json = service.getDefaultPanelJson(tabId, currentRoleOrder);
			
			response.setHeader("Cache-Control","no-cache");
			response.setContentType("text/plain;charset=UTF-8");
			response.setContentLength(json.getBytes("utf-8").length);
			out = new PrintWriter(response.getWriter());
			out.print(json);
		} catch (Exception e) {
			log.error("Failed to get TabLayout data.", e);
			throw new ServletException(e);
		} finally {
			if(out != null)
				out.close();
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
				
		// save role layout.
		String layoutParamsStr = request.getParameter("data");
		
		PrintWriter out = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			Map layoutParams = mapper.readValue(layoutParamsStr, HashMap.class);
			String tabId = (String)layoutParams.get("tabId");
			
			boolean isTabAdmin =  StaticTabService.getHandle().isTabAdmin(tabId);
			if(!isTabAdmin){
				response.sendError(HttpStatus.SC_FORBIDDEN, "You are not administrator.");
				return;
			}
			
			// adjust tabLayoutService process
			JSONObject staticPanelJson = new JSONObject((Map)layoutParams.get("staticPanel"));
			layoutParams.put("staticPanel", staticPanelJson.toString());
			JSONObject dynamicPanelJson = new JSONObject((Map)layoutParams.get("dynamicPanel"));
			layoutParams.put("dynamicPanel", dynamicPanelJson.toString());
			
			TabLayoutService service = TabLayoutService.getHandle();
			Integer currentRoleOrder = getCurrentOrder(tabId);
			
			service.updateRoleLayout(tabId, currentRoleOrder, layoutParams);
			
			response.setHeader("Cache-Control","no-cache");
			response.setContentType("text/plain;charset=UTF-8");
			response.setContentLength(tabId.getBytes("utf-8").length);
			out = new PrintWriter(response.getWriter());
			out.print(tabId);
		} catch (Exception e) {
			log.error("Failed to get TabLayout data.", e);
			throw new ServletException(e);
		} finally {
			if(out != null)
				out.close();
		}
	}
	
	private Integer getCurrentOrder(String tabId) throws ClassNotFoundException, Exception{
		TabLayoutService service = TabLayoutService.getHandle();
		Map<String, TabLayout> myTabLayouts = service.getMyTabLayoutHTML();
		
		TabLayout layout = myTabLayouts.get(tabId);
		return  layout.getId().getRoleorder();
	}
}
