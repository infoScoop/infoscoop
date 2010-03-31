package org.infoscoop.command;

import javax.xml.parsers.FactoryConfigurationError;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.command.util.XMLCommandUtil;
import org.infoscoop.dao.TabDAO;
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
			Tab tab = new Tab(uid, tabId);
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
