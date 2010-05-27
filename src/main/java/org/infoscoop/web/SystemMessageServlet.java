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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.service.SystemMessageService;

public class SystemMessageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static Log log = LogFactory.getLog(SystemMessageServlet.class);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String uid = (String) req.getSession().getAttribute("Uid");

		if (uid == null) {
			resp.sendError(403);
			return;
		}

		try {
			resp.setContentType("text/json;charset=utf-8");
			resp.getWriter().write(SystemMessageService.getHandle().getNonReadMessagesJson(uid));
			resp.setHeader("Pragma", "no-cache");
			resp.setHeader("Cache-Control", "no-cache");
		} catch (Exception e) {
			log.error("Failed getting System message", e);
			resp.sendError(500, e.getMessage());
		}

	}
}
