package org.infoscoop.command;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.command.util.XMLCommandUtil;
import org.infoscoop.dao.TabDAO;
import org.infoscoop.dao.model.Tab;
public class UpdateTabPreference extends XMLCommandProcessor {

    private Log log = LogFactory.getLog(this.getClass());
    
	public void execute() {
        String commandId = super.commandXml.getAttribute("id").trim();
        String tabId = super.commandXml.getAttribute("tabId").trim();
        String field = super.commandXml.getAttribute("field").trim();
        String value = super.commandXml.getAttribute("value").trim();
         
        if(log.isInfoEnabled()){
        	log.info("uid:[" + uid + "]: processXML: tabId:[" + tabId + 
        			"], field:[" + field + "], value:[" + value + "]");
        }
        
        Tab tab = TabDAO.newInstance().getTab(uid, tabId);
        if("tabName".equals(field)){
        	tab.setName(value);
        }else if("tabNumber".equals(field)){
        	tab.setOrder(Integer.valueOf(value));
        }else if("tabType".equals(field)){
        	tab.setType(value);
        }else{
        	tab.setProperty(field, value);
        }
        
        this.result = XMLCommandUtil.createResultElement(uid, "processXML",
                log, commandId, true, null);
	}

}
