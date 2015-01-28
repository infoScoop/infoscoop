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
import java.io.Writer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.dao.model.Portallayout;
import org.infoscoop.service.PortalLayoutService;
import org.infoscoop.service.PropertiesService;
import org.infoscoop.util.SpringUtil;

public class PortalLayoutServlet extends HttpServlet {
	private static final long serialVersionUID = "org.infoscoop.web.PortalLayoutServlet"
			.hashCode();
	
	private static Log logger = LogFactory.getLog(PortalLayoutServlet.class);

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		doPost(request, response);
	}
	
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String uid = (String) request.getSession().getAttribute("Uid");
		
		if (logger.isInfoEnabled()) {
			logger.info("uid:[" + uid + "]: doPost");
		}
		
		String type = request.getParameter("type");
		if (type == null) {
			logger.error("It is an unjust summons. Type is not appointed.");
			response.sendError(500);
		}
		
		if(Portallayout.LAYOUT_TYPE_CSS.equalsIgnoreCase(type)){
			response.setContentType("text/css; charset=UTF-8");
		}
		else if(Portallayout.LAYOUT_TYPE_JS.equalsIgnoreCase(type)){
			response.setContentType("text/javascript; charset=UTF-8");
		}
		else if(Portallayout.LAYOUT_TYPE_CUSTOMTHEME.equalsIgnoreCase(type)){
			try {
				PropertiesService propertiesService = (PropertiesService)SpringUtil.getBean("PropertiesService");
				String staticContentURL = propertiesService.getProperty("staticContentURL");
				
				PortalLayoutService service = (PortalLayoutService)SpringUtil.getBean("PortalLayoutService");
				String customTheme = service.getPortalLayout(type);
				
				request.setAttribute("staticContentURL", staticContentURL);
				request.setAttribute("customTheme", customTheme);
				request.getRequestDispatcher("/WEB-INF/jsp/theme/customTheme.jsp")
					.forward(request, response);
				
			} catch (Exception e) {
				logger.error("--- unexpected error occurred.", e);
				response.sendError(500);
			}
			return;
		}
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");

		Writer out = response.getWriter();
		
		try {
			
			PortalLayoutService service = (PortalLayoutService)SpringUtil.getBean("PortalLayoutService");
			String js = service.getPortalLayout(type);
			if(js != null)
				out.write(js);
		} catch (Exception e){
			logger.error("--- unexpected error occurred.", e);
			response.sendError(500);
		}
		out.flush();
		out.close();
		
	}
	
}
