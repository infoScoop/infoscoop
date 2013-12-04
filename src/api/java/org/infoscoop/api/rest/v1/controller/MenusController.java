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

package org.infoscoop.api.rest.v1.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.api.rest.v1.response.MenusResponse;
import org.infoscoop.api.rest.v1.response.model.Menu;
import org.infoscoop.dao.model.Siteaggregationmenu;
import org.infoscoop.service.SiteAggregationMenuService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/v1/user/menus")
public class MenusController extends BaseController{
	private static Log log = LogFactory.getLog(MenusController.class);
	
	private static final String SERV_KEY_ITEMS = "items";
	private static final String SERV_KEY_SITETOPARRAY = "siteTopArray";
	private static final String SERV_KEY_MAPJSON = "mapJson";
	
	private static final String MENUTYPE_TOPMENU = "topmenu";
	private static final String MENUTYPE_SIDEMENU = "sidemenu";
	
	private static final String ITEM_ATTR_AUTHS = "auths";
	private static final String ITEM_ATTR_PROPERTIES = "properties";
	private static final String ITEM_ATTR_MENUTREEADMINS = "menuTreeAdmins";
	
	@Autowired
	private SiteAggregationMenuService siteAggregationMenuService;

	/**
	 *	Get menu
	 *	@param menuType (Default:all)
	 *	@return
	 */
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public MenusResponse getMenus(@RequestParam(value="menuType", required=false) String menuType) throws Exception{
		MenusResponse menuResponse = new MenusResponse();

		if(MENUTYPE_TOPMENU.equals(menuType)){
			Siteaggregationmenu siteMenu = siteAggregationMenuService.getMenuEntity(MENUTYPE_TOPMENU);
			Map menuTree = siteAggregationMenuService.getMenuTreeJson(siteMenu, null);
			Map<String, Map<String,Object>> items = deserializeItems((JSONObject)menuTree.get(SERV_KEY_ITEMS));
			List<String> siteTopList = deserializeJSONArrayToList((JSONArray)menuTree.get(SERV_KEY_SITETOPARRAY));
			Map<String, List<String>> keyMap = deserializeJSONObjectToMapList((JSONObject)menuTree.get(SERV_KEY_MAPJSON));

			menuResponse.setTopmenu(new Menu(items,siteTopList,keyMap));
		}else if(MENUTYPE_SIDEMENU.equals(menuType)){
			Siteaggregationmenu siteMenu = siteAggregationMenuService.getMenuEntity(MENUTYPE_SIDEMENU);
			Map menuTree = siteAggregationMenuService.getMenuTreeJson(siteMenu, null);
			Map<String, Map<String,Object>> items = deserializeItems((JSONObject)menuTree.get(SERV_KEY_ITEMS));
			List<String> siteTopList = deserializeJSONArrayToList((JSONArray)menuTree.get(SERV_KEY_SITETOPARRAY));
			Map<String, List<String>> keyMap = deserializeJSONObjectToMapList((JSONObject)menuTree.get(SERV_KEY_MAPJSON));

			menuResponse.setSidemenu(new Menu(items,siteTopList,keyMap));
		}else{
			Siteaggregationmenu topmenu = siteAggregationMenuService.getMenuEntity(MENUTYPE_TOPMENU);
			Siteaggregationmenu sidemenu = siteAggregationMenuService.getMenuEntity(MENUTYPE_SIDEMENU);
			Map topMenuTree = siteAggregationMenuService.getMenuTreeJson(topmenu, null);
			Map sideMenuTree = siteAggregationMenuService.getMenuTreeJson(sidemenu, null);

			// topmenu
			Map<String, Map<String,Object>> topMenuItems = deserializeItems((JSONObject)topMenuTree.get(SERV_KEY_ITEMS));
			List<String> topMenuSiteTopList = deserializeJSONArrayToList((JSONArray)topMenuTree.get(SERV_KEY_SITETOPARRAY));
			Map<String, List<String>> topMenuKeyMap = deserializeJSONObjectToMapList((JSONObject)topMenuTree.get(SERV_KEY_MAPJSON));

			// sidemenu
			Map<String, Map<String,Object>> sideMenuItems = deserializeItems((JSONObject)sideMenuTree.get(SERV_KEY_ITEMS));
			List<String> sideMenuSiteTopList = deserializeJSONArrayToList((JSONArray)sideMenuTree.get(SERV_KEY_SITETOPARRAY));
			Map<String, List<String>> sideMenuKeyMap = deserializeJSONObjectToMapList((JSONObject)sideMenuTree.get(SERV_KEY_MAPJSON));

			menuResponse.setTopmenu(new Menu(topMenuItems,topMenuSiteTopList,topMenuKeyMap));
			menuResponse.setSidemenu(new Menu(sideMenuItems,sideMenuSiteTopList,sideMenuKeyMap));
		}

		return menuResponse;
	}

	private Map<String, Map<String,Object>> deserializeItems(JSONObject jsonObject) throws JSONException{
		Map<String, Map<String,Object>> resultMap = new HashMap<String, Map<String,Object>>();
		for(Iterator<String> itr = jsonObject.keys();itr.hasNext();){
			Map<String,Object> itemMap = new HashMap<String,Object>();
			String key = itr.next();
			JSONObject obj = jsonObject.getJSONObject(key);
			
			for(Iterator<String> itr2 = obj.keys();itr2.hasNext();){
				String key2 = itr2.next();
				if(key2.equals(ITEM_ATTR_PROPERTIES)){
					itemMap.put(key2, deserializeJSONObjectToMapStr(obj.getJSONObject(key2)));
				}else if(key2.equals(ITEM_ATTR_MENUTREEADMINS) || key2.equals(ITEM_ATTR_AUTHS)){
					itemMap.put(key2, deserializeJSONArrayToList(obj.getJSONArray(key2)));
				}else{
					itemMap.put(key2, obj.get(key2).toString());					
				}	
			}

			resultMap.put(key, itemMap);
		}
		return resultMap;
	}
	
	private List<String> deserializeJSONArrayToList(JSONArray jsonArray) throws JSONException{
		List<String> list = new ArrayList<String>();

		for(int i = 0;i<jsonArray.length();i++){
			String str = jsonArray.get(i).toString();
			list.add(str);
		}
		
		return list;
	}
	
	private Map<String, List<String>> deserializeJSONObjectToMapList(JSONObject jsonObject) throws JSONException{
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		for(Iterator<String> itr = jsonObject.keys();itr.hasNext();){
			String key = itr.next();
			List<String> list = deserializeJSONArrayToList(jsonObject.getJSONArray(key));
			map.put(key, list);
		}
		return map;
	}
	
	private Map<String,String> deserializeJSONObjectToMapStr(JSONObject jsonObject) throws JSONException{
		Map<String,String> map = new HashMap<String,String>();
		for(Iterator<String> itr = jsonObject.keys();itr.hasNext();){
			String key = itr.next();
			map.put(key, jsonObject.getString(key));
		}
		return map;
	}
}
