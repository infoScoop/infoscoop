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

package org.infoscoop.dao.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


import org.infoscoop.dao.WidgetDAO;
import org.infoscoop.dao.model.base.BaseWidget;
import org.infoscoop.util.StringUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class Widget extends BaseWidget {
	private static final long serialVersionUID = 1L;
	
	private Map<String,UserPref> userPrefs;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public Widget () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public Widget (java.lang.String id) {
		super(id);
	}
	
	public Widget(String tabId, Long deleteDate, String widgetId, String uid){
		super.setTabid(tabId);
		super.setDeletedate(deleteDate);
		super.setWidgetid(widgetId);
		super.setUid(uid);
	}

/*[CONSTRUCTOR MARKER END]*/

	
	public JSONObject toJSONObject() throws JSONException{
		JSONObject json = new JSONObject();
		json.put("id", this.getWidgetid());
		json.put("column", ( super.getColumn() == null ? "":super.getColumn().toString()));
		json.put("tabId", this.getTabid());
		json.put("href", super.getHref());
		json.put("title", super.getTitle());
		json.put("siblingId", super.getSiblingid());
		json.put("parentId", super.getParentid());
		json.put("menuId", super.getMenuid());
		json.put("type", super.getType());

		JSONObject userPrefsJson = new JSONObject();
		JSONArray longUserPrefsJson = new JSONArray();
		Map<String,UserPref> userPrefs = getUserPrefs();
		for( String key : userPrefs.keySet() ) {
			UserPref userPref = userPrefs.get( key );
			if( userPref == null )
				continue;
			
			try {
				if( !userPref.hasLongValue() ) {
					userPrefsJson.put( key,userPrefs.get( key ).getShortValue());
				} else {
					// longValue not returned
					userPrefsJson.put( key,false );
					longUserPrefsJson.put( key );
				}
			} catch( JSONException ex ) {
				throw new RuntimeException( ex );
			}
		}
		json.put("property",userPrefsJson );
		json.put("longProperty",longUserPrefsJson );

		json.put("createDate", this.getCreatedate());
		json.put("deleteDate", this.getDeletedate());
		json.put("ignoreHeader", this.isIgnoreHeader());
		json.put("noBorder", this.isNoBorder());
		
		return json;
	}

	public boolean isIgnoreHeader(){
		if(super.getIgnoreheader() == null){
			return false;
		}else{
			return super.getIgnoreheader().intValue() == 1;
		}
	}
	
	public void setIgnoreHeader(boolean b) {
		super.setIgnoreheader(new Integer((b ? 1 : 0 )));		
	}

	public boolean isNoBorder(){
		if(super.getNoborder() == null){
			return false;
		}else{
			return super.getNoborder().intValue() == 1;
		}
	}
	
	public void setNoBorder(boolean b) {
		super.setNoborder(new Integer((b ? 1 : 0 )));		
	}
	
	public Map<String,UserPref> getUserPrefs() {
		if( userPrefs == null ) {
			userPrefs = new HashMap<String,UserPref>();
			userPrefs.putAll( WidgetDAO.newInstance().getUserPrefs( getId() ) );
		}
		
		return userPrefs;
	}
	
	public void setUserPref( String name,String value ) {
		Map<String,UserPref> userPrefs = getUserPrefs();
		if( value == null )
			value = "";
		
		UserPref userPref;
		if( userPrefs.containsKey( name )) {
			userPref = userPrefs.get( name );
		} else {
			userPref = new UserPref( new USERPREFPK( getId(),name ));
			userPrefs.put( name,userPref );
		}
		userPref.setValue( value );
	}
	
	public void removeUserPref( String name ) {
		Map<String,UserPref> userPrefs = getUserPrefs();
		userPrefs.remove(name);
	}
	
	public void setUserPrefsJSON( JSONObject userPrefsJson ) {
		Map<String,UserPref> userPrefs = getUserPrefs();
		for( Iterator<String> keys=userPrefsJson.keys();keys.hasNext(); ) {
			String key = keys.next();
			try {
				String value = userPrefsJson.getString( key );
				if( value == null )
					value = "";
				
				UserPref userPref = userPrefs.get( key );
				if( userPref == null ) {
					userPref = new UserPref( new USERPREFPK( getId(),key ));
					userPrefs.put( key,userPref );
				}
				
				userPref.setValue( value );
			} catch( JSONException ex ) {
				throw new RuntimeException( ex );
			}
		}
	}

	public String getMenuid() {
		return StringUtil.getNullSafe( super.getMenuid() );
	}
	
	@Override
	public void setTitle(String title) {
		if (title.length() > 80)
			title = title.substring(0, 80);
		super.setTitle(title);
	}
}
