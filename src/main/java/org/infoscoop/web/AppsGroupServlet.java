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
import org.infoscoop.dao.model.Group;
import org.infoscoop.dao.model.User;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AppsGroupServlet extends HttpServlet {
	private static final long serialVersionUID = "org.infoscoop.web.AppsGroupServlet"
		.hashCode();
	
	private static Log log = LogFactory.getLog(AppsGroupServlet.class);
	
	protected void doGet( HttpServletRequest req, HttpServletResponse resp )
			throws ServletException, IOException  {
		doPost(req, resp);
	}

	protected void doPost( HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		GroupDAO groupDAO = GroupDAO.newInstance();
		JSONArray results = new JSONArray();
		String groupEmail = req.getParameter("groupEmail");
		if (groupEmail == null){
			List<Group> groups = groupDAO.all();
			for ( Group group: groups ){
				JSONObject jsonObj = new JSONObject();
				try {
					jsonObj.put("name", group.getName());
					jsonObj.put("email", group.getEmail());
				} catch (JSONException e) {
					log.error("",e);
					resp.sendError(500, e.getMessage());
					return;
				}
				results.put(jsonObj);
			}
		}else{
			Group group = groupDAO.getByEmail(groupEmail);
			Set<User> users = group.getUsers();
			for(User user: users)
				results.put(user.getEmail());
		}
		resp.setContentType("application/json; charset=UTF-8");
		Writer writer = resp.getWriter();
		writer.write( results.toString());
		writer.flush();
	}
}
