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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.dao.model.TabLayout;
import org.infoscoop.service.TabLayoutService;

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

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		// save role layout.
		String tabId = request.getParameter("tabId");
		String layoutParamsStr = request.getParameter("layoutParams");
		System.out.println(layoutParamsStr);
		
		PrintWriter out = null;
		try {
			Map layoutParams = new HashMap();
			
			TabLayoutService service = TabLayoutService.getHandle();
			Integer currentRoleOrder = getCurrentOrder(tabId);
			
//			service.updateRoleLayout(tabId, currentRoleOrder, layoutParams);
			
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
