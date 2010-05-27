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

package org.infoscoop.admin.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.infoscoop.admin.exception.MenusIllegalEditException;
import org.infoscoop.admin.exception.MenusTimeoutException;
import org.infoscoop.service.SiteAggregationMenuService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SiteAggregationServiceCommand extends ServiceCommand {
	public CommandResponse execute(String commandName, HttpServletRequest req,
			HttpServletResponse resp) throws Exception {
		
		try{
			if("commitMenu".equals(commandName)) {
				String menuType = req.getParameter("menuType");
				String forceUpdateMapStr = req.getParameter("forceUpdateMap");
				String editSitetopIdListStr = req.getParameter("editSitetopIdList");
				
				JSONObject forceUpdateMapJson = new JSONObject(forceUpdateMapStr);
				Map<String, List<SiteAggregationMenuService.ForceUpdateUserPref>> forceUpdateMap = new HashMap<String, List<SiteAggregationMenuService.ForceUpdateUserPref>>();
				for(Iterator it = forceUpdateMapJson.keys(); it.hasNext();){
					String menuId = (String)it.next();
					JSONObject upJson = forceUpdateMapJson.getJSONObject(menuId);
					List<SiteAggregationMenuService.ForceUpdateUserPref> upList = new ArrayList<SiteAggregationMenuService.ForceUpdateUserPref>(); 
					
					for(Iterator upNames = upJson.keys();upNames.hasNext();){
						String upName = (String) upNames.next();
						SiteAggregationMenuService.ForceUpdateUserPref pref = (( SiteAggregationMenuService )service ).new ForceUpdateUserPref(upName);
						JSONObject upObj = upJson.getJSONObject(upName);
						pref.setImplied(upObj.getBoolean("implied"));
						upList.add(pref);
					}
					forceUpdateMap.put(menuId, upList);
				}
				
				JSONArray jsonArray = new JSONArray(editSitetopIdListStr);
				List<String> editSitetopIdList = new ArrayList<String>();
				for( int i=0;i<jsonArray.length();i++ )
					editSitetopIdList.add(jsonArray.getString(i));
				
				(( SiteAggregationMenuService )service ).commitMenu(menuType, forceUpdateMap, editSitetopIdList);
				return new CommandResponse(true, "");
			}
			
			return super.execute(commandName, req, resp);
		}catch(MenusTimeoutException e){
			return new CommandResponse(false, createErrorJSON("menusTimeout", null));
		}catch(MenusIllegalEditException e){
			return new CommandResponse(false, createErrorJSON("menusIllegalEdit", e.getErrorSitetopIdList()));
		}
	}

	private static String createErrorJSON(String errorType, List<String> errorSitetopIdList) throws JSONException{
		JSONObject json = new JSONObject();
		json.put("errorType", errorType);
		
		if(errorSitetopIdList != null)
			json.put("errorIdList", new JSONArray(errorSitetopIdList));
		
		return json.toString();
	}
}
