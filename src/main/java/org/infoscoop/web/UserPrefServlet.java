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
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.infoscoop.dao.WidgetDAO;
import org.infoscoop.dao.model.UserPref;
import org.infoscoop.dao.model.Widget;
import org.json.JSONException;
import org.json.JSONObject;


public class UserPrefServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String widgetId = req.getParameter("widgetId");
		String tabId = req.getParameter("tabId");
		String[] prefNames = req.getParameterValues("up");
		
		String uid = ( String )req.getSession().getAttribute("Uid");
		
		JSONObject json = new JSONObject();
		
		Widget widget = WidgetDAO.newInstance().getWidget( uid,tabId,widgetId );
		if( widget != null ) {
			Map<String,UserPref> prefs = widget.getUserPrefs();
			for( String prefName : prefNames ) {
				if( !prefs.containsKey( prefName ))
					continue;
				
				UserPref pref = prefs.get( prefName );
				try {
					json.put( prefName,pref.getValue());
				} catch( JSONException ex ) {
					throw new RuntimeException( ex );
				}
			}
		}
		
		resp.setContentType("application/json");
		resp.getOutputStream().write( json.toString().getBytes("UTF-8"));
	}
}
