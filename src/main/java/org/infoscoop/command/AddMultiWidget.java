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

package org.infoscoop.command;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.account.DomainManager;
import org.infoscoop.command.util.XMLCommandUtil;
import org.infoscoop.dao.MenuItemDAO;
import org.infoscoop.dao.TabDAO;
import org.infoscoop.dao.model.MenuItem;
import org.infoscoop.dao.model.Widget;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * The command class of MultiWidget executed when a MultiWidget is added.
 * 
 * @author nishiumi
 */
public class AddMultiWidget extends XMLCommandProcessor{

    private Log log = LogFactory.getLog(this.getClass());
    
    /**
     * create a new object of AddMultiWidget.
     * 
     */
    public AddMultiWidget(){
    }
    
	public void execute() throws Exception {
		
        String commandId = super.commandXml.getAttribute("id").trim();
        String parentWidgetId = super.commandXml.getAttribute("widgetId").trim();
        String tabId = super.commandXml.getAttribute("tabId").trim();
        String targetColumn = super.commandXml.getAttribute("targetColumn").trim();
        String parent = super.commandXml.getAttribute("parent").trim();
        String sibling = super.commandXml.getAttribute("sibling").trim();
        String menuId = super.commandXml.getAttribute("menuId").trim();

        if (log.isInfoEnabled()) {
			log.info("uid:[" + uid + "]: processXML: widgetId:["
					+ parentWidgetId + "], tabId:[" + tabId
					+ "], targetColumn:[" + targetColumn + "], parent:["
					+ parent + "], sibling:[" + sibling + "], menuId:["
					+ menuId + "]");
        }

        if (parentWidgetId == null || parentWidgetId == "") {
            String reason = "It's an unjust widgetId．widgetId:[" + parentWidgetId + "]";
            log.error("Failed to exexute the command of AddMultiWidget： " + reason);
            this.result = XMLCommandUtil.createResultElement(uid, "processXML",
                    log, commandId, false, reason);
            return;
        }

        if (targetColumn != null && !"".equals(targetColumn) && !XMLCommandUtil.isNumberValue(targetColumn)) {
        	String reason = "Value of column is unjust．targetColumn:[" + targetColumn + "]";
            log.error("Failed to execute the command of AddMultiWidget： " + reason);
            this.result = XMLCommandUtil.createResultElement(uid, "processXML",
                    log, commandId, false, reason);
            return;
        }
        
        // convert JSON to XML
        String parentConfJSONStr = super.commandXml.getAttribute("widgetConf");
        String subWidgetConfJSONStr = super.commandXml.getAttribute("subWidgetConfList");
        
        JSONObject parentConfJSON = null;
        JSONArray subWidgetConfJsonAry = null;
        try {
        	parentConfJSON = new JSONObject(parentConfJSONStr);
    		subWidgetConfJsonAry = new JSONArray(subWidgetConfJSONStr);
    	} catch (Exception e) {
            String reason = "The infomation of Widget is unjust.";
            log.error("Failed to execute the command of AddMultiWidget： " + reason, e);
            this.result = XMLCommandUtil.createResultElement(uid, "processXML",
                    log, commandId, false, reason);
            throw e;
		}
    	
    	try{
    		TabDAO tabDAO = TabDAO.newInstance();
    		
    		Widget newNextSibling = tabDAO.getColumnWidgetBySibling( uid,tabId,sibling,Integer.valueOf( targetColumn ),parentWidgetId );
        	if(newNextSibling != null){
        		newNextSibling.setSiblingid( parentWidgetId );
        		log.info("Replace siblingId of [" + newNextSibling.getWidgetid() + "] to " + parentWidgetId );
 //       		WidgetDAO.newInstance().updateWidget(uid, tabId, newNextSibling);
        	}
    		
    		// insert parent
    		Widget widget = 
    			createWidget(tabId, parent, parentWidgetId, targetColumn, sibling, menuId, parentConfJSON);
    		tabDAO.addDynamicWidget( uid,"defaultUid",tabId, widget);
    		
    		// insert subWidgets
			String subCategorySibling = "";
			String subWidgetId, subWidgetMenuId;
			for (int i = 0; i < subWidgetConfJsonAry.length(); i++) {
				JSONObject confJson = new JSONObject(subWidgetConfJsonAry
						.get(i).toString());
				subWidgetId = confJson.getString("id");
				subWidgetMenuId = confJson.getString("menuId");

				Widget subWidget = createWidget(tabId, parentWidgetId,
						subWidgetId, targetColumn, subCategorySibling,
						subWidgetMenuId, confJson);
				tabDAO.addDynamicWidget(uid, "defaultUid", tabId, subWidget);
    			
    			subCategorySibling = subWidgetId;
			}
        	
//    		WidgetDAO.newInstance().updateTab( tab );
    	} catch (Exception e) {			
            String reason = "Failed to save the infomaton of widget.";
			log.error("Failed to execute the command of AddMultiWidget： " + reason, e);
            this.result = XMLCommandUtil.createResultElement(uid, "processXML",
                    log, commandId, false, reason);
            
            throw e;
		}
        this.result = XMLCommandUtil.createResultElement(uid, "processXML",
                log, commandId, true, null);
	}
	
	private Widget createWidget(String tabId, String parentId, String widgetId,
			String targetColumn, String sibling, String menuId,
			JSONObject confJson) throws JSONException {
		Widget widget = new Widget();
		widget.setTabid(tabId);
		widget.setFkDomainId(DomainManager.getContextDomainId());
		widget.setWidgetid(widgetId);
		widget.setUid(uid);
		if(targetColumn != null && !"".equals(targetColumn)){
			widget.setColumn(new Integer(targetColumn));
		}
		widget.setSiblingid(sibling);
		widget.setParentid(parentId);

		if (menuId != null && menuId.length() > 0) {
			MenuItem menuItem = MenuItemDAO.newInstance().get(
					Integer.parseInt(menuId));
			widget.setMenuItem(menuItem);
		}
		
		if(confJson.has("title"))
			widget.setTitle(confJson.getString("title"));
		if(confJson.has("href"))
			widget.setHref(confJson.getString("href"));
		if(confJson.has("type"))
			widget.setType(confJson.getString("type"));
		if(confJson.has("property"))
			widget.setUserPrefsJSON(confJson.getJSONObject("property"));
		if (confJson.has("ignoreHeader"))
			widget.setIgnoreHeader(confJson.getBoolean("ignoreHeader"));
		if (confJson.has("noBorder"))
			widget.setIgnoreHeader(confJson.getBoolean("noBorder"));

		widget.setIsstatic(new Integer(0));
		return widget;
	}
}
