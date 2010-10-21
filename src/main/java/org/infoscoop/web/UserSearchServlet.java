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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.infoscoop.account.IAccountManager;
import org.infoscoop.account.IAccount;
import org.infoscoop.util.SpringUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class UserSearchServlet extends HttpServlet {

	private static final long serialVersionUID = "org.infoscoop.web.UserSearchServlet"
			.hashCode();

	private static Log log = LogFactory.getLog(UserSearchServlet.class);
	
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request,HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/plain; charset=UTF-8");
		
		IAccountManager service;
		try {
			String moduleName = request.getParameter("module");
			
			service = ( IAccountManager )SpringUtil.getBean( moduleName + "AccountManager");
		} catch( Exception ex ) {
			throw new ServletException("invalid module parameter");
		}

		Collection<IAccount> users = new ArrayList();
		try {
			// 2007.07.28 add koike
			String uid = request.getParameter("uid");
			
			if( uid == null || "".equals( uid )) {
				String conditionsStr = request.getParameter("conditions");
				if( conditionsStr == null || "".equals( conditionsStr ))
					throw new ServletException("conditions parameter required.");
				
				JSONObject conditions = new JSONObject( conditionsStr );
				
				Map<String,String> conditionMap = new HashMap<String, String>();
				for( Iterator keys=conditions.keys();keys.hasNext();) {
					String key = ( String )keys.next();
					String name = key;
					if("name".equals( key )) {
						name = "user_name";
					} else if("mail".equals( key )) {
						name = "user_email";
					} else if("belong".equals( key )) {
						name = "org_name";
					}
					
					conditionMap.put( name,conditions.getString( key ));
				}
				
				users = service.searchUser( conditionMap );
			} else {
				// 2007.07.28 add koike
				// add a search by uid
				
				users.add( service.getUser( uid, null ) );//TODO:
			}
		} catch( Exception e ) {
			log.error("",e);
			response.sendError(500, e.getMessage());
			e.printStackTrace();
		}
		
		if(log.isInfoEnabled())
			log.info(users);
		
		JSONArray results = new JSONArray();
		for( IAccount user : users ) {
			JSONObject userJson = new JSONObject();
			
			try {
				userJson.put("uid",user.getUid());
				userJson.put("name",( user.getName() == null )? "" : user.getName() );
				userJson.put("mail",( user.getMail() == null )? "" : user.getMail() );
				
				//FIXME It was non-correspondence in the plural groups
				userJson.put("belong",( user.getGroupName() == null )? "" : user.getGroupName() );
			} catch( JSONException ex ) {
				log.error("",ex );
			}
			results.put( userJson );
		}
		
		Writer writer = response.getWriter();
		writer.write( results.toString());
		writer.flush();
	}
}
