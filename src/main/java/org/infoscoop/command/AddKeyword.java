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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.command.util.XMLCommandUtil;
import org.infoscoop.dao.KeywordLogDAO;

/**
 * The command class that executed when adding and renewing a keyword at retrieving.
 * 
 * @author koumoto
 * 
 */
public class AddKeyword extends XMLCommandProcessor {

	private Log logger = LogFactory.getLog(this.getClass());

	/**
	 * create a new object of UpdateKeyword.
	 * 
	 */
	public AddKeyword() {

	}

	/**
	 * add the information of keyword.
	 * 
	 * @param uid
	 *            an userId that is target of retrieval.
	 * @param el
	 *            The element of request command. Attributes of "widgetId", "targetColumn", and "sibling" are necessary for the Element, 
	 *            <BR>
	 *            and the structure of widget's XML that adds under command-Element is also necessary. <BR>
	 *            example of input element：<BR>
	 * 
	 * <pre>
	 *   &lt;command type=&quot;UpdateLog&quot; id=&quot;UpdateLog_http://www.google.co.jp&quot; logType=&quot;0&quot; url=&quot;http://www.google.co.jp&quot;
	 *    rssUrl=&quot;http://www.google.co.jp/rss.xml&quot;
	 *   &lt;/command&gt;
	 * </pre>
	 */

	public void execute() {
	
		boolean isOK = false;
		String reason = null;

		String commandId = super.commandXml.getAttribute("id").trim();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		//String keyword = XmlUtil.escapeXmlEntities(el.getAttributeValue("keyword").trim());
		String keyword = super.commandXml.getAttribute("keyword").trim();
		String[] keywords = keyword.split(" ");
		String date = sdf.format(new Date());

		//String logMsg = "uid:[" + uid + "]: processXML: date:[" + date + "], keyword:[" + keyword + "], type:[" + type + "]";
		if(logger.isInfoEnabled())
			logger.info("uid:[" + uid + "]: processXML: date:[" + date + "], keyword:[" + keyword + "]");
		
		if (date == null || date == "") {
			reason = "It's the unjust date．url:[" + date + "]";
			this.result = XMLCommandUtil.createResultElement(uid, "processXML",
					logger, commandId, isOK, reason);
			return;
		}

		if (keyword == null || keyword == "") {
			reason = "It's an unjust keyword．keyword:[" + keyword + "]";
			this.result = XMLCommandUtil.createResultElement(uid, "processXML",
					logger, commandId, isOK, reason);
			return;
		}
				
		// register
		// register all of the content of input.
		boolean key = true;
		KeywordLogDAO keywordLogDAO = KeywordLogDAO.newInstance();
		key = keywordLogDAO.getKeyword(uid, date, keyword, "1");
		
		if (key) {
			isOK = updateDB(keywordLogDAO, uid, date, keyword, "1");
			if (!isOK) {
				reason = "Can't register the keyword. keyword:[" + keyword + "]";
			}
		} else {
			isOK = true;
			reason = "Can't register the keyword because it has already registered． keyword:[" + keyword + "]";
		}
		//register each word
		for (int i=0; i<keywords.length; i++) {
			key = keywordLogDAO.getKeyword(uid, date, keywords[i], "0");
			if (key) {
				isOK = updateDB(keywordLogDAO, uid, date, keywords[i], "0");
				if (!isOK) {
					reason = "Can't register the keyword. keywords[i]:[" + keywords[i] + "]";
				}
			}
		}

		this.result = XMLCommandUtil.createResultElement(uid, "processXML", logger,
				commandId, isOK, reason);
	}

	/**
	 * register xml into the database.
	 * 
	 * @param widgetDAO
	 *            an instance of WidgetDAO
	 * @param uid
	 *            userID
	 * @param keywordLogNode
	 *            a keywordLog node of target for renewal
	 * @return When Renewal of database is success, the return value is "true", the other is "false".
	 */
	private boolean updateDB(KeywordLogDAO keywordLogDAO, String uid, String date,
			String keyword, String keywordLogType) {

		boolean result = false;

		keywordLogDAO.insertLog(uid, date, keyword, keywordLogType);
		result = true;
		
		return result;
	}

}
