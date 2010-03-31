package org.infoscoop.command;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.command.util.XMLCommandUtil;
import org.infoscoop.dao.TabDAO;
import org.infoscoop.dao.model.Tab;

public class RemoveTab extends XMLCommandProcessor {

    private Log log = LogFactory.getLog(this.getClass());
    
	public void execute() {
		String commandId = super.commandXml.getAttribute("id").trim();
        String tabId = super.commandXml.getAttribute("tabId").trim();
        
        if(log.isInfoEnabled()){
        	log.info("uid:[" + uid + "]: processXML: tabId:[" + tabId + "]");
        }
		
        TabDAO dao = TabDAO.newInstance();
        Tab tab = dao.getTab( uid, tabId );
        dao.deleteTab( tab );
        
        this.result = XMLCommandUtil.createResultElement(uid, "processXML",
                log, commandId, true, null);
        
	}

}
