package org.infoscoop.command;

import java.text.SimpleDateFormat;
import java.util.Date;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.command.util.XMLCommandUtil;
import org.infoscoop.service.LogService;

/**
 * The command class executed when a log is added and updated.
 * 
 * @author nishiumi
 * 
 */
public class AddLog extends XMLCommandProcessor {

    private Log log = LogFactory.getLog(this.getClass());

    /**
     * create a new object of UpdateLog.
     * 
     */
    public AddLog() {

    }

    /**
     * add the information of log.
     * 
     * @param uid
     *            an userId that is target of retrieval.
     * @param el
     *             The element of request command. Attributes of "widgetId", "targetColumn", and "sibling" are necessary for the Element, 
	 *            <BR>
	 *            and the structure of widget's XML that adds under command-Element is also necessary. <BR>
	 *            example of input element：<BR>
     * 
     * <pre>
     *       &lt;command type=&quot;UpdateLog&quot; id=&quot;UpdateLog_http://www.google.co.jp&quot; logType=&quot;0&quot; url=&quot;http://www.google.co.jp&quot;
     *        rssUrl=&quot;http://www.google.co.jp/rss.xml&quot;
     *       &lt;/command&gt;
     * </pre>
     * @throws Exception 
     */

	public void execute() throws Exception {
		
        boolean isOK = false;
        String reason = null;

        String commandId = super.commandXml.getAttribute("id").trim();
        String logType = super.commandXml.getAttribute("logType").trim();
        String url = super.commandXml.getAttribute("url").trim();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH");
        String date = sdf.format(new Date());
        String rssUrl = super.commandXml.getAttribute("rssUrl").trim();

        if(log.isInfoEnabled())
        	log.info("uid:[" + uid + "]: processXML: logType:[" + logType
                + "], url:[" + url + "], date:[" + date + "]");

        if (!XMLCommandUtil.isNumberValue(logType)) {
            reason = "It's an unjust logType．logType:[" + logType + "]";
            this.result =  XMLCommandUtil.createResultElement(uid, "processXML",
                    log, commandId, isOK, reason);
            return;
        }

        if (url == null || url == "") {
            reason = "It's an unjust url．url:[" + url + "]";
            this.result = XMLCommandUtil.createResultElement(uid, "processXML",
                    log, commandId, isOK, reason);
            return;
        }

        if (date == null || date == "") {
            reason = "It's the unjust date．url:[" + date + "]";
            this.result = XMLCommandUtil.createResultElement(uid, "processXML",
                    log, commandId, isOK, reason);
            return;
        }
        
    	try{
            // register
            isOK = updateDB(uid, logType, url, rssUrl, date);
            
            if (!isOK) {
                reason = "Failed to register the information of log．uid:[" + uid + "]";
                this.result = XMLCommandUtil.createResultElement(uid, "processXML",
                        log, commandId, isOK, reason);
                return;
            }
    	}catch(Exception e){
    		log.error("",e);
    		throw e;
    	}

        
        this.result = XMLCommandUtil.createResultElement(uid, "processXML", log,
                commandId, isOK, reason);
    }

    /**
     * register xml into the database.
     * 
     * @param widgetDAO
     *            an instance of WidgetDAO
     * @param uid
     *            userID
     * @param logNode
     *            a log node that is target of retrieval.
     * @return When Renewal of database is success, the return value is "true", the other is "false".
     * @throws Exception 
     */
    private boolean updateDB(String uid, String logType,
            String url, String rssUrl, String date) throws Exception {

        boolean result = false;

    	LogService.getHandle().insertLog(uid, logType, url, rssUrl, date);
        result = true;
        
        return result;
    }

}
