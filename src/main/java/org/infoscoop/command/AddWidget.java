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
import org.infoscoop.command.util.XMLCommandUtil;
import org.infoscoop.dao.MenuItemDAO;
import org.infoscoop.dao.TabDAO;
import org.infoscoop.dao.WidgetDAO;
import org.infoscoop.dao.model.MenuItem;
import org.infoscoop.dao.model.Widget;
import org.json.JSONObject;

/**
 * The command class executed when a Widget is added.
 * 
 * @author nakata
 * @author hr-endoh
 */
public class AddWidget extends XMLCommandProcessor{

    private Log log = LogFactory.getLog(this.getClass());

    /**
     * create a new object of AddWidget.
     * 
     */
    public AddWidget(){
    }
    
	public void execute() throws Exception {
	 	
        String commandId = super.commandXml.getAttribute("id").trim();
        String widgetId = super.commandXml.getAttribute("widgetId").trim();
        String tabId = super.commandXml.getAttribute("tabId").trim();
        String targetColumn = super.commandXml.getAttribute("targetColumn").trim();
        String parent = super.commandXml.getAttribute("parent").trim();
        String sibling = super.commandXml.getAttribute("sibling").trim();
        String menuid = super.commandXml.getAttribute("menuId").trim();

        if(log.isInfoEnabled()){
        	log.info("uid:[" + uid + "]: processXML: widgetId:[" + widgetId
                	+ "], tabId:[" + tabId + "], targetColumn:[" + targetColumn + 
                	"], parent:[" + parent + "], sibling:[" + sibling + "]");
        }

        if (widgetId == null || widgetId == "") {
            String reason = "It's an unjust widgetId．widgetId:[" + widgetId + "]";
            log.error("Failed to execute the command of AddWidget： " + reason);
            this.result = XMLCommandUtil.createResultElement(uid, "processXML",
                    log, commandId, false, reason);
            return;
        }

        if (targetColumn != null && !"".equals(targetColumn) && !XMLCommandUtil.isNumberValue(targetColumn)) {
        	String reason = "It's an unjust value of column．targetColumn:[" + targetColumn + "]";
            log.error("Failed to execute the command of AddWidget： " + reason);
            this.result = XMLCommandUtil.createResultElement(uid, "processXML",
                    log, commandId, false, reason);
            return;
        }
        
        
        // convert the JSON to XML.
        String confJSONStr = super.commandXml.getAttribute("widgetConf");
        JSONObject confJson = null;
        try {
    		confJson = new JSONObject(confJSONStr);
    	} catch (Exception e) {
    		log.error("", e);
            String reason = "The information of widget is unjust.";
            log.error("Failed to execute the command of AddWidget： " + reason);
            this.result = XMLCommandUtil.createResultElement(uid, "processXML",
                    log, commandId, false, reason);
            throw e;
    	}

    	TabDAO tabDAO = TabDAO.newInstance();

    	Widget newNextSibling;
    	if( parent != null && !"".equals( parent )) {
    		//newNextSibling = tabDAO.getSubWidgetBySibling( uid,tabId,sibling,parent,widgetId );
    		newNextSibling = null;
    	} else {
    		log.info("Find sibling: "+sibling+" of "+targetColumn );
    		newNextSibling = tabDAO.getColumnWidgetBySibling( uid,tabId,sibling,Integer.valueOf( targetColumn ),widgetId );
    	}

    	if(newNextSibling != null){
    		newNextSibling.setSiblingid( widgetId );
    		log.info("Replace siblingId of [" + newNextSibling.getWidgetid() + "] to " + widgetId );
    		//       		WidgetDAO.newInstance().updateWidget(uid, tabId, newNextSibling);
    	}

    	Widget widget = new Widget();
		widget.setTabid(tabId);
		widget.setWidgetid(widgetId);
		widget.setUid(uid);
		
    	if(targetColumn != null && !"".equals(targetColumn)){
    		widget.setColumn(Integer.valueOf(targetColumn));
    	}
    	widget.setSiblingid(sibling);
    	widget.setParentid(parent);

		if (menuid != null && menuid.length() > 0) {
			MenuItem menuItem = MenuItemDAO.newInstance().get(
					Integer.valueOf(menuid));
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

    	widget.setIsstatic(Integer.valueOf(0));

    	WidgetDAO.newInstance().addWidget( widget );

    	//    		dao.updateTab( tab );

    	this.result = XMLCommandUtil.createResultElement(uid, "processXML",
    			log, commandId, true, null);
	}
}
