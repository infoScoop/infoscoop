package org.infoscoop.command;

import java.text.SimpleDateFormat;
import java.util.Date;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.command.util.XMLCommandUtil;
import org.infoscoop.dao.PreferenceDAO;
import org.infoscoop.dao.SessionDAO;
import org.infoscoop.dao.model.Preference;
import org.infoscoop.service.PreferenceService;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class ExecLogoffProcess extends XMLCommandProcessor {
	private Log logger = LogFactory.getLog(this.getClass());
	private static final String FORMAT_W3C = "yyyy-MM-dd'T'HH:mm:ssZ";

	/**
	 * 
	 * update the date of logOff and delete a session.
	 * 
	 * @param uid
	 *            an userId that is target of operation.
	 * @param el
	 *            The element of request command. Attributes of "widgetId", "field", and "value" are necessary for the Element.<BR>
	 *            <BR>
	 *            example of input element：<BR>
	 * 
	 * <pre>
	 *  &lt;command type=&quot;ExecLogoffProcess&quot; id=&quot;ExecLogoffProcess&quot;/&gt;
	 * </pre>
	 */

	public void execute() {

        String commandId = super.commandXml.getAttribute("id").trim();
		String field = super.commandXml.getAttribute("field").trim();
		String value = new SimpleDateFormat( FORMAT_W3C ).format(new Date());
		
		if (logger.isInfoEnabled()) {
			logger.info("uid:[" + uid + "]: processXML: field:[" + field
					+ "], value:[" + value + "]");
		}

		SessionDAO.newInstance().deleteSessionId(uid);
		
		Preference entity = PreferenceDAO.newInstance().select(uid);
		Element prefEl;
		try {
			prefEl = entity.getElement();
		} catch (SAXException e) {
			 this.result = XMLCommandUtil.createResultElement(uid, "processXML",
		                logger, commandId, false, e.getMessage());
			 return;
		}
        if(prefEl != null){
       		PreferenceService.updateProperty(prefEl, field, value);
       		entity.setElement(prefEl);
       		PreferenceService.getHandle().update(entity);
       	}
        this.result = XMLCommandUtil.createResultElement(uid, "processXML",
                logger, commandId, true, null);
	}
	
}
