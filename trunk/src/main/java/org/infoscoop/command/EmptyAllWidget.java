package org.infoscoop.command;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.command.util.XMLCommandUtil;
import org.infoscoop.dao.WidgetDAO;

/**
 * The command class executed when a widget is deleted.
 * 
 * @author a-kimura
 * 
 */
public class EmptyAllWidget extends XMLCommandProcessor {

	private Log log = LogFactory.getLog(this.getClass());

	public EmptyAllWidget() {

	}

	public void execute() throws Exception {
		String commandId = super.commandXml.getAttribute("id").trim();
		if (log.isInfoEnabled()) {
			String logMsg = "uid:[" + uid + "]";
			log.info(logMsg);
		}
		try {
			WidgetDAO.newInstance().emptyDeletedWidgets(uid);
		} catch (Exception e) {
			String reason = "failed to execute the command of EmptyAllWidget";
			log.error(reason, e);
			this.result = XMLCommandUtil.createResultElement(uid, "processXML",
					log, commandId, false, reason);
			throw e;
		}
		this.result = XMLCommandUtil.createResultElement(uid, "processXML",
				log, commandId, true, null);
	}

}
