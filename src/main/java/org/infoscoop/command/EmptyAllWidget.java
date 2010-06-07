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
