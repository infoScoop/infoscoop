package org.infoscoop.command;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.command.util.XMLCommandUtil;
import org.infoscoop.dao.PreferenceDAO;
import org.infoscoop.dao.model.Preference;
import org.infoscoop.service.PreferenceService;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class UpdatePreference extends XMLCommandProcessor {

	private Log logger = LogFactory.getLog(this.getClass());
	
	/**
	 * update the whole setting.
	 * 
	 * @param uid
	 *            an userId that is a target of operaion.
	 * @param el
	 *            The element of request command. Attributes of "widgetId", "field", and "value" are necessary for the Element.<BR>
	 *            <BR>
	 *             example of input element：<BR>
	 * 
	 * <pre>
	 *  &lt;command type=&quot;UpdatePreference&quot; id=&quot;UpdatePreference_fontSize&quot; field=&quot;fontSize&quot; value=&quot;120%&quot;/&gt;
	 * </pre>
	 */
	public void execute() {
        String commandId = super.commandXml.getAttribute("id").trim();
		
		String field = super.commandXml.getAttribute("field").trim();
		String value = super.commandXml.getAttribute("value").trim();
		
		if (logger.isInfoEnabled()) {
			logger.info("uid:[" + uid + "]: processXML: field:[" + field
					+ "], value:[" + value + "]");
		}
		
		Preference entity = PreferenceDAO.newInstance().select(uid);
		Element prefEl = null;
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
