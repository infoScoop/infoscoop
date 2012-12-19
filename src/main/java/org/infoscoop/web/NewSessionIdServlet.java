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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.infoscoop.service.PreferenceService;
import org.infoscoop.service.SessionService;
import org.w3c.util.UUID;

public class NewSessionIdServlet extends HttpServlet{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3877908316420660798L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setHeader("Pragma","no-cache");
		resp.setHeader("Cache-Control", "no-cache");
		String isPreviewStr = req.getParameter("isPreview");
		Boolean isPreview = null;
		if( isPreviewStr != null && !"null".equals( isPreviewStr ))
			isPreview = Boolean.valueOf( isPreviewStr );

		String uid = ( String )req.getSession().getAttribute("Uid");

		try {
			String sessionId;
			if( uid == null || isPreview != null && isPreview.booleanValue()){
				sessionId = new UUID().toString();
			}else{
				sessionId = SessionService.getHandle().newSessionId( uid );
				
				//set lastAccessDate
				PreferenceService.getHandle().setAccessTime(uid);
			}
			resp.getWriter().println(
					"is_sessionId = \"" + sessionId.replace("\\", "\\\\") +  "\"");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
