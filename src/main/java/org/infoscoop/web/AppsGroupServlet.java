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
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.dao.GroupDAO;
import org.infoscoop.dao.model.User;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AppsGroupServlet extends HttpServlet {
	private static final long serialVersionUID = "org.infoscoop.web.AppsGroupServlet"
		.hashCode();
	
	private static Log log = LogFactory.getLog(AppsGroupServlet.class);
	
	@SuppressWarnings("unchecked")
	protected void doGet( HttpServletRequest req, HttpServletResponse resp )
			throws IOException, ServletException {
		List<String> groupIds = GroupDAO.newInstance().getGroupIds();
		JSONArray results = new JSONArray();
		for ( String groupId: groupIds){
			JSONObject group = new JSONObject();
			JSONArray UserEmails = new JSONArray();
			String groupName = GroupDAO.newInstance().get(groupId).getName();
			try {
				group.put("name", groupName);
				Set<User> Users = GroupDAO.newInstance().get(groupId).getUsers();
				for(User user: Users){
					UserEmails.put(user.getEmail());
				}
				group.put("members", UserEmails);
			}catch (JSONException ex) {
				log.error("",ex);
				resp.sendError(500, ex.getMessage());
				ex.printStackTrace();
				return;
			}
			results.put(group);
		}
		resp.setContentType("application/json; charset=UTF-8");
		Writer writer = resp.getWriter();
		writer.write( results.toString());
		writer.flush();
	}
}
