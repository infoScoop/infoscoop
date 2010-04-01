
package org.infoscoop.command;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.command.util.XMLCommandUtil;
import org.infoscoop.dao.WidgetDAO;
import org.infoscoop.dao.model.Widget;
import org.json.JSONObject;

/**
 * The command class called when copying a property of a widget from item of menu at initial login.
 * 
 * @author hr-endoh
 */
public class UpdateWidget extends XMLCommandProcessor{

    private Log log = LogFactory.getLog(this.getClass());

    /**
     * create a new Object of AddWidget.
     * 
     */
    public UpdateWidget(){
    }
    
	public void execute() throws Exception {
	 	
        String commandId = super.commandXml.getAttribute("id").trim();
        String widgetId = super.commandXml.getAttribute("widgetId").trim();
        String tabId = super.commandXml.getAttribute("tabId").trim();
        String targetColumn = super.commandXml.getAttribute("targetColumn").trim();
        String parent = super.commandXml.getAttribute("parent").trim();
        String sibling = super.commandXml.getAttribute("sibling").trim();

        if(log.isInfoEnabled()){
        	log.info("uid:[" + uid + "]: processXML: widgetId:[" + widgetId
                	+ "], tabId:[" + tabId + "], targetColumn:[" + targetColumn + 
                	"], parent:[" + parent + "], sibling:[" + sibling + "]");
        }

        if (widgetId == null || "".equals(widgetId)) {
            String reason = "It's an unjust widgetId．widgetId:[" + widgetId + "]";
            log.error("Failed to execute the command of AddWidget： " + reason);
            this.result = XMLCommandUtil.createResultElement(uid, "processXML",
                    log, commandId, false, reason);
            return;
        }

        if (targetColumn != null && !"".equals(targetColumn) && !XMLCommandUtil.isNumberValue(targetColumn)) {
        	String reason = "Value of column is unjust．targetColumn:[" + targetColumn + "]";
            log.error("Failed to execute the command of AddWidget： " + reason);
            this.result = XMLCommandUtil.createResultElement(uid, "processXML",
                    log, commandId, false, reason);
            return;
        }
       
        // convert JSON to XML
        String confJSONStr = super.commandXml.getAttribute("widgetConf");
        JSONObject confJson = null;
        try {
    		confJson = new JSONObject(confJSONStr);
    	} catch (Exception e) {
    		log.error("", e);
            String reason = "The infomation of the widget is unjust.";
            log.error("Failed to execute the command of UpdateWidget： " + reason);
            this.result = XMLCommandUtil.createResultElement(uid, "processXML",
                    log, commandId, false, reason);
            throw e;
		}
    	
    	try{
    		Widget widget = WidgetDAO.newInstance().getWidget( uid,tabId,widgetId );
			if( widget == null ) {
				log.error("Failed to execute the command of UpdateWidget： " + widgetId);
				this.result = XMLCommandUtil.createResultElement(uid, "processXML",
						log, commandId, false, widgetId);
				return;
			}
			
    		if(targetColumn != null && !"".equals(targetColumn)){
    			widget.setColumn(new Integer(targetColumn));
    		}
    		widget.setSiblingid(sibling);
    		widget.setParentid(parent);
    		if(confJson.has("title"))
    			widget.setTitle(confJson.getString("title"));
    		if(confJson.has("href"))
    			widget.setHref(confJson.getString("href"));
    		if(confJson.has("type"))
    			widget.setType(confJson.getString("type"));
    		if(confJson.has("siblingId"))
    			widget.setSiblingid(confJson.getString("siblingId"));
    		if(confJson.has("property"))
    			widget.setUserPrefsJSON(confJson.getJSONObject("property") );
    		if (confJson.has("ignoreHeader"))
    			widget.setIgnoreHeader(confJson.getBoolean("ignoreHeader"));
    		if (confJson.has("noBorder"))
    			widget.setIgnoreHeader(confJson.getBoolean("noBorder"));
    		
    		WidgetDAO.newInstance().updateWidget( widget );
    	} catch (Exception e) {
    		log.error("", e);
            String reason = "Failed to save the widget.";
            log.error("Failed to execute the command of UpdateWidget： " + reason);
            this.result = XMLCommandUtil.createResultElement(uid, "processXML",
                    log, commandId, false, reason);
            throw e;
		}
    	 


        this.result = XMLCommandUtil.createResultElement(uid, "processXML",
                log, commandId, true, null);
	}

}
