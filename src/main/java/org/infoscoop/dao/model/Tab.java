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

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.dao.model.base.BaseTab;
import org.infoscoop.util.I18NUtil;
import org.json.JSONException;
import org.json.JSONObject;



public class Tab extends BaseTab {
	private static Log log = LogFactory.getLog(Tab.class);
	
	private static final long serialVersionUID = 1L;
	
	private boolean isTrashDynamicPanelWidgets;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public Tab () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public Tab (org.infoscoop.dao.model.TABPK id) {
		super(id);
	}

/*[CONSTRUCTOR MARKER END]*/

	public JSONObject getProperties(){
		try {
			return new JSONObject(super.getData());
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}
	
	public String getUid() {
		return super.getId().getUid();
	}
	public String getTabId(){
		return super.getId().getId();
	}
	public void setProperty(String field, String value) {
		try {
			String data = super.getData();
			if( data == null )
				data = "{}";
			
			JSONObject json = new JSONObject( data );
			json.put(field, value);
			super.setData(json.toString());
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}
	

	public JSONObject toJSONObject( Collection dynamicWidgets,Collection staticWidgets ) throws JSONException {
		return toJSONObject( dynamicWidgets,staticWidgets,new HashMap() );
	}
	public JSONObject toJSONObject( 
			Collection dynamicWidgets,Collection staticWidgets,Map resMap ) throws JSONException{
		
		JSONObject json = new JSONObject();
		json.put("uid", getUid());
		json.put("defaultUid", super.getDefaultuid());
		json.put("tabId", getTabId());
		json.put("tabName",I18NUtil.replace( super.getName(), resMap ) );
//		json.put("tabNumber", this.tabNumber);
		// Usual logic is affected if the type of variable is number
		json.put("tabNumber", super.getOrder() != null ?
				String.valueOf(super.getOrder()) : "");
		json.put("tabType", super.getType());
		json.put("widgetLastModified", super.getWidgetlastmodified());
		json.put("property", getProperties());

		JSONObject staticPanel = new JSONObject();
		for(Iterator it = staticWidgets.iterator(); it.hasNext(); ){
			Widget widget = (Widget)it.next();
			staticPanel.put(widget.getWidgetid(), widget.toJSONObject());
		}
		json.put("staticPanel", staticPanel);
		
		if (this.isDisabledDynamicPanel()) {
			json.put("disabledDynamicPanel", true);
			if (this.isTrashDynamicPanelWidgets()) {
				json.put("isTrashDynamicPanelWidgets", true);
			}
		}
		JSONObject dynamicPanel = new JSONObject();
		for (Iterator it = dynamicWidgets.iterator(); it.hasNext();) {
			Widget widget = (Widget) it.next();
			dynamicPanel.put(widget.getWidgetid(), widget.toJSONObject());
		}
		json.put("dynamicPanel", dynamicPanel);
		
		return json;
	}

	public boolean isDisabledDynamicPanel() {
		Integer disableddynamicpanel = super.getDisableddynamicpanel();
		return disableddynamicpanel != null && disableddynamicpanel == 1;
	}

	public void setDisabledDynamicPanelBool(boolean disableddynamicpanel) {
		super.setDisableddynamicpanel(disableddynamicpanel ? 1 : 0);
	}

	public boolean isTrashDynamicPanelWidgets() {
		return isTrashDynamicPanelWidgets;
	}

	public void setTrashDynamicPanelWidgets(boolean isTrashDynamicPanelWidgets) {
		this.isTrashDynamicPanelWidgets = isTrashDynamicPanelWidgets;
	}
}
