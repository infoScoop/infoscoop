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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.dao.WidgetDAO;
import org.infoscoop.dao.model.Tab;
import org.infoscoop.dao.model.Widget;
import org.infoscoop.service.TabService;
import org.infoscoop.util.I18NUtil;
import org.json.JSONArray;
import org.json.JSONObject;

public class WidgetServlet extends HttpServlet {

	private static final long serialVersionUID = "org.infoscoop.web.WidgetServlet"
			.hashCode();
	
	private static String defaultUid = "default";

	private static Log log = LogFactory.getLog(WidgetServlet.class);
	
	public static String getDefaultUid() {
		return defaultUid;
	}

	public void init(ServletConfig config) throws ServletException {
		defaultUid = config.getInitParameter("defaultUid");
		if(defaultUid == null) defaultUid = "default";
		
		super.init(config);
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		doPost(request, response);

	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		long start = System.currentTimeMillis();
		
		String uid = (String) request.getSession().getAttribute("Uid");
		
		String tabOrderStr = request.getParameter("tabOrder");

		String resetStr = request.getParameter("reset");
		String tabIdParam = request.getParameter("tabId");
		if("true".equalsIgnoreCase( resetStr )) {
			try{
				Integer tabId = null;
				if(tabIdParam != null)
					tabId = Integer.valueOf(tabIdParam.trim().replace("tab", ""));
					
				TabService.getHandle().clearConfigurations( uid, tabId );
			}catch (Exception e) {
				log.error("An exception occeurred.", e);
				response.sendError(500, e.getMessage());
			} 
			return;
		}
		
		if(log.isInfoEnabled()){
			log.info("uid:[" + uid + "]: doPost");
		}		
		
		response.setContentType("text/plain; charset=UTF-8");
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");
		
		Writer writer = response.getWriter();
		JSONArray responseAray = new JSONArray();
		
		try {
			
			JSONObject bvObj = new JSONObject();
			bvObj.append("buildVersion", getServletContext().getAttribute("buildTimestamp"));
			responseAray.put(bvObj);
			
			Collection widgetsList;
			if(tabOrderStr == null)
				widgetsList = getDisplayContents(uid, request);
			else{
				int tabOrder = Integer.parseInt(tabOrderStr);
				widgetsList = getDisplayContents(uid, tabOrder, request);
			}
			
			if (widgetsList == null || widgetsList.isEmpty()) {
				if(log.isInfoEnabled())
					log.info("widget not found.");
				response.sendError(500, "widget not found.");
				return;
			}
			
			Map resMap = I18NUtil.getResourceMap( I18NUtil.TYPE_LAYOUT,request.getLocale() );
			Set dynamicPanelWidgetIds = new HashSet();
			for(Iterator it = widgetsList.iterator(); it.hasNext();){
				Object[] t = ( Object[] )it.next();
				
				Tab tab = (Tab)t[0];
				String layout = (String)t[1];
				Collection<Widget> dynamicWidgets = ( Collection )t[2];
				Collection<Widget> staticWidgets = ( Collection )t[3];
				
				//Because there is the possibility that the widgetID repeats depending on the setting situation of the dynamic panel of the initial screen setting, we remove it.
				List removeWidgetList = new ArrayList();
				for(Widget wid : dynamicWidgets){
					if(dynamicPanelWidgetIds.contains(wid.getWidgetid())){
						removeWidgetList.add(wid);
					}
					if(!"MultiRssReader".equals(wid.getType())){
						dynamicPanelWidgetIds.add(wid.getWidgetid());
					}
				}
				
				//FIXME dirty
				WidgetDAO.newInstance().getHibernateTemplate().deleteAll( removeWidgetList );
				
				responseAray.put( tab.toJSONObject( layout, dynamicWidgets,staticWidgets,resMap ));
			}
			
			String jsonStr = responseAray.toString();
			writer.write(jsonStr);
			//writer.write(responseAray.toString());
			
		
		}catch (Exception e) {
			log.error("An exception occeurred.", e);
			response.sendError(500, e.getMessage());
		} 
		if(log.isTraceEnabled()){
			long end = System.currentTimeMillis();
			log.trace("--- WidgetServlet doPost: " + (end - start));
		}
		long end = System.currentTimeMillis();
	}
	
	protected Collection getDisplayContents(String uid, HttpServletRequest request) throws Exception{
		return TabService.getHandle().getWidgetsNode(uid, defaultUid);
	}
	
	protected Collection getDisplayContents(String uid, int tabOrder,
			HttpServletRequest req) throws Exception {
		
		Object tabObj = TabService.getHandle().getWidgetsNodeByTabOrder(uid, getDefaultUid(), tabOrder);
		if(tabObj == null)
			return null;
		
		List result = new ArrayList();
		result.add(tabObj);
		return (Collection)result;
	}

	
}
