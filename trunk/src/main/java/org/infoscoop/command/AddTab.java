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

import javax.xml.parsers.FactoryConfigurationError;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.command.util.XMLCommandUtil;
import org.infoscoop.dao.TabDAO;
import org.infoscoop.dao.model.TABPK;
import org.infoscoop.dao.model.Tab;

public class AddTab extends XMLCommandProcessor {

    private Log log = LogFactory.getLog(this.getClass());
    
	public void execute() {
	 	
        String commandId = super.commandXml.getAttribute("id").trim();
        String tabId = super.commandXml.getAttribute("tabId").trim();
        String tabName = super.commandXml.getAttribute("tabName").trim();
        String tabType = super.commandXml.getAttribute("tabType").trim();
        if(tabType == null) tabType = "static";
        String numCol = super.commandXml.getAttribute("numCol").trim();
        
        if(log.isInfoEnabled()){
        	log.info("uid:[" + uid + "]: processXML: tabId:[" + tabId + "], tabName:[" + tabName + 
                	"], tabType:[" + tabType + "], numCol:[" + numCol + "]");
        }
        
        try {
			Tab tab = new Tab( new TABPK( uid,tabId));
			tab.setName(tabName);
			tab.setType(tabType);
			tab.setProperty("numCol", numCol);
			//tab.setTabNumber(Integer.valueOf(tabNumber));
			TabDAO.newInstance().addTab(tab);
			
		} catch (FactoryConfigurationError e) {
            String reason = "An error occurred in the command for adding tab.tabId:[" + tabId + "]";
            this.result = XMLCommandUtil.createResultElement(uid, "processXML",
                    log, commandId, false, reason);
            
            throw e;
		}
        this.result = XMLCommandUtil.createResultElement(uid, "processXML",
                log, commandId, true, null);
	}

}
