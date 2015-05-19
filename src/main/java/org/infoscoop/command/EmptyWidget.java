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
import org.infoscoop.dao.WidgetDAO;
import org.infoscoop.dao.model.Widget;

/**
 * The command class executed when a widget is deleted.
 * 
 * @author a-kimura
 * 
 */
public class EmptyWidget extends XMLCommandProcessor {

	private Log log = LogFactory.getLog(this.getClass());

	public EmptyWidget() {

	}

	public void execute() throws Exception {
		String commandId = super.commandXml.getAttribute("id").trim();
		String tabId = super.commandXml.getAttribute("tabId").trim();
		String widgetId = super.commandXml.getAttribute("widgetId").trim();
		String deleteDateStr = super.commandXml.getAttribute("deleteDate").trim();
		String squareid = UserContext.instance().getUserInfo().getCurrentSquareId();
		long deleteDate = 0;
		try {
			deleteDate = Long.parseLong(deleteDateStr);
		} catch (NumberFormatException e) {
		}

		if (log.isInfoEnabled()) {
			String logMsg = "uid:[" + uid + "]: processXML: tabId:[" + tabId
					+ "], widgetId:[" + widgetId + "], deleteDate:["
					+ deleteDate + "]";
			log.info(logMsg);
		}
		if (widgetId == null || widgetId == "") {
			String reason = "It's an unjust widgetId．widgetId:[" + widgetId + "]";
			this.result = XMLCommandUtil.createResultElement(uid, "processXML",
					log, commandId, false, reason);
			return;
		}
		try {
			int result = 0;

			TabDAO tabDAO = TabDAO.newInstance();
			WidgetDAO widgetDAO = WidgetDAO.newInstance();
			
        	Widget widget = widgetDAO.getWidget( uid,tabId,widgetId,squareid );
        	
			//TODO:check whether a widget is null or not;
			Widget nextSibling = tabDAO.getWidgetBySibling( uid,tabId,widgetId,squareid );
			if(nextSibling != null){
				nextSibling.setSiblingid(widget.getSiblingid());
			}
			
			if (tabId != null && tabId.length() > 0) {
				result = widgetDAO.emptyWidget(uid, widgetId,tabId, deleteDate, squareid);
			} else {
				result = widgetDAO.emptyWidget(uid, widgetId,deleteDate, squareid);
			}
			if (result == 0) {
				this.result = XMLCommandUtil.createResultElement(uid,
						"processXML", log, commandId, false,
						"Failed to delete the widget. Not found the widget that is a target to delete.");
// FIX 2478 Failed to save when the synchronized Multh is broght to other tabs.
//				return;
			}
		} catch (Exception e) {
			String reason = "Failed to execute the command of EmptyWidget";
			log.error(reason, e);
			this.result = XMLCommandUtil.createResultElement(uid, "processXML",
					log, commandId, false, reason);
			throw e;
		}
		this.result = XMLCommandUtil.createResultElement(uid, "processXML",
				log, commandId, true, null);
	}

}
