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
import org.infoscoop.context.UserContext;
import org.infoscoop.dao.TabDAO;
import org.infoscoop.dao.model.Tab;

public class RemoveTab extends XMLCommandProcessor {

    private Log log = LogFactory.getLog(this.getClass());
    
	public void execute() {
		String commandId = super.commandXml.getAttribute("id").trim();
        String tabId = super.commandXml.getAttribute("tabId").trim();
		String squareid = UserContext.instance().getUserInfo().getCurrentSquareId();
        
        if(log.isInfoEnabled()){
        	log.info("uid:[" + uid + "]: processXML: tabId:[" + tabId + "]");
        }
		
        TabDAO dao = TabDAO.newInstance();
        Tab tab = dao.getTab( uid, tabId, squareid );
        dao.deleteTab( tab );
        
        this.result = XMLCommandUtil.createResultElement(uid, "processXML",
                log, commandId, true, null);
        
	}

}
