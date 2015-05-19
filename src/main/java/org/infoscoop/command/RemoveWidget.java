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

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.command.util.XMLCommandUtil;
import org.infoscoop.context.UserContext;
import org.infoscoop.dao.TabDAO;
import org.infoscoop.dao.WidgetDAO;
import org.infoscoop.dao.model.UserPref;
import org.infoscoop.dao.model.Widget;
import org.infoscoop.service.AuthCredentialService;

/**
 * The command class executed when a widget is deleted.
 *
 * @tabId A tabID to which a widget for deletion belongs.
 * @parent not used
 * @widgetId a widgetId for deletion
 * @deleteDate the date of deletion. If it's empty, it's given by the servlet.
 * 
 * @author nakata
 * @author hr-endoh
 * 
 */
public class RemoveWidget extends XMLCommandProcessor {

    private Log log = LogFactory.getLog(this.getClass());

    public RemoveWidget() {
    }
    
    public void execute() throws Exception{
	 	
    	String commandId = super.commandXml.getAttribute("id").trim();
        String tabId = super.commandXml.getAttribute("tabId").trim();
        String widgetId = super.commandXml.getAttribute("widgetId").trim();
        String parent = super.commandXml.getAttribute("parent").trim();
        String deleteDate = super.commandXml.getAttribute("deleteDate").trim();
		String squareid = UserContext.instance().getUserInfo().getCurrentSquareId();
        
        if(log.isInfoEnabled()){
        	String logMsg = "uid:[" + uid + "]: processXML: tabId:[" + tabId + "], widgetId:[" + widgetId + "], parent:[" + parent + "], deleteDate:[" + deleteDate + "]";
        	log.info(logMsg);
        }
        if (widgetId == null || widgetId == "") {
        	String reason = "It's an unjust widgetId．widgetId:[" + widgetId + "]";
            this.result =  XMLCommandUtil.createResultElement(uid, "processXML",
                    log, commandId, false, reason);
            return;
        }
        
        try{
        	TabDAO tabDAO = TabDAO.newInstance();
        	WidgetDAO widgetDAO = WidgetDAO.newInstance();
        	
        	Widget widget = widgetDAO.getWidget( uid,tabId,widgetId,squareid );
        	
        	if(widget == null || deleteDate == null || "".equals(deleteDate)){
                this.result = XMLCommandUtil.createResultElement(uid, "processXML",
                        log, commandId, false, "Failed to delete the widget. Not found the widget to delete.");
                return;
        	}
        	
        	//TODO:check whether the widget is null or not;
        	Widget nextSibling = tabDAO.getWidgetBySibling( uid,tabId,widgetId,squareid );
        	if(nextSibling != null){
        		nextSibling.setSiblingid(widget.getSiblingid());
//        		WidgetDAO.newInstance().updateWidget(nextSibling);
        	}
        	
        	widget.setDeletedate( Long.valueOf( deleteDate ));
        	widget.setTabid("-1");
        	
        	WidgetDAO.newInstance().updateWidget( widget );
        	
        	Map<String,UserPref> userPrefs = widget.getUserPrefs();
        	if( userPrefs.containsKey("authCredentialId")){
        		String authCredentialId = userPrefs.get("authCredentialId").getValue();
        		AuthCredentialService.getHandle().removeCredential(widget.getUid(), authCredentialId);
        	}
        } catch (Exception e) {			
            String reason = "Failed to delete the widget.";
            log.error("Failed to execute the command of RemoveWidget", e);
            this.result = XMLCommandUtil.createResultElement(uid, "processXML",
                    log, commandId, false, reason);
            
            throw e;
		}
        this.result = XMLCommandUtil.createResultElement(uid, "processXML",
                log, commandId, true, null);
    }
    
}
