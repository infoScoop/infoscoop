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
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.context.UserContext;
import org.infoscoop.dao.LogDAO;
import org.infoscoop.dao.LogDAO.RssAccessStats;
import org.infoscoop.dao.LogDAO.RssAccessStatsEntry;
import org.json.JSONArray;
import org.json.JSONObject;

public class AccessStatsListServlet extends HttpServlet {

	private static final long serialVersionUID = "org.infoscoop.web.AccessStatsServlet"
			.hashCode();

	private Log log = LogFactory.getLog(this.getClass());

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		doPost(request, response);

	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/plain; charset=UTF-8");
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");
		String rssUrl = request.getParameter("rssUrl");
		int start = Integer.parseInt(request.getParameter("start"));
		int limit = Integer.parseInt(request.getParameter("limit"));
		LogDAO dao = LogDAO.newInstance();
		RssAccessStats stats = dao.getRssAccessStats(rssUrl, start, limit, UserContext.instance().getUserInfo().getCurrentSquareId());
		try {
			JSONObject json = new JSONObject();
			json.put("results", stats.getCount());
			JSONArray data = new JSONArray();
			json.put("data", data);
			for (Iterator it = stats.getEntries().iterator(); it.hasNext();) {
				RssAccessStatsEntry entry = (RssAccessStatsEntry) it.next();
				data.put(entry.toJSONObject());
			}
			PrintWriter out = response.getWriter();
			out.print(json.toString());
		} catch (Exception e) {
			log.error("An exception occurred.", e);
			response.sendError(500, e.getMessage());
		}
	}
}
