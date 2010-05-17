package org.infoscoop.command;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.command.util.XMLCommandUtil;
import org.infoscoop.dao.WidgetDAO;
import org.infoscoop.dao.model.Widget;

/**
 * The command class to update a property of a widget.
 * 
 * @tabId a tabId that a widget that is target to delete belongs to. 
 * @widgetId a widgetId that is target to delete.
 * @field a name of property of a widget
 * @value new value
 * 
 * @transaction true
 * 
 * @author nakata
 * 
 */
public class UpdateWidgetPreference extends XMLCommandProcessor {

    private Log logger = LogFactory.getLog(this.getClass());
        
    public UpdateWidgetPreference() {
    }

    public void execute() {
        String commandId = super.commandXml.getAttribute("id").trim();
        String tabId = super.commandXml.getAttribute("tabId").trim();
        String widgetId = super.commandXml.getAttribute("widgetId").trim();
        String field = super.commandXml.getAttribute("field").trim();
        String value = super.commandXml.getAttribute("value").trim();

        if(logger.isInfoEnabled()){
        	String logMsg = "uid:[" + uid + "]: processXML: tabId:[" + tabId
					+ "], widgetId:[" + widgetId + "], field:[" + field
					+ "], value:[" + value + "]";
        	logger.info(logMsg);
        }
        if (widgetId == null || widgetId == "") {
            String reason = "It's an unjust widgetId．widgetId:[" + widgetId + "]";
           	this.result = XMLCommandUtil.createResultElement(uid, "processXML",
                    logger, commandId, false, reason);
            return;
        }
		
        Widget widget = WidgetDAO.newInstance().getWidget(uid, tabId, widgetId);
        if("title".equals(field)){
        	widget.setTitle(value);
        }
        else if("href".equals(field)){
        	widget.setHref(value);
        }
        WidgetDAO.newInstance().updateWidget(widget);
        this.result = XMLCommandUtil.createResultElement(uid, "processXML",
                logger, commandId, true, null);
    }

}
