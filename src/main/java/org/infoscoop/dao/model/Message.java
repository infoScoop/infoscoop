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

package org.infoscoop.dao.model;

import org.infoscoop.dao.model.base.BaseMessage;
import org.infoscoop.util.HtmlUtil;
import org.infoscoop.util.StringUtil;



public class Message extends BaseMessage {
	private static final long serialVersionUID = 1L;

	public static final String MESSAGE_PUBLIC = "MSGPUBLIC";
	public static final String MESSAGE_FROM = "MSGFROM";
	public static final String MESSAGE_TO = "MSGTO";
	public static final String MESSAGE_BROADCAST = "MSGBC";
	public static final String FYI_PUBLIC = "FYIPUBLIC";
	public static final String FYI_FROM = "FYIFROM";
	public static final String FYI_TO = "FYITO";
	public static final String SYSTEM = "SYS";

/*[CONSTRUCTOR MARKER BEGIN]*/
	public Message () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public Message (java.lang.Long id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public Message (
		java.lang.Long id,
		java.lang.String from,
		java.util.Date postedTime,
		java.lang.String type) {

		super (
			id,
			from,
			postedTime,
			type);
	}

/*[CONSTRUCTOR MARKER END]*/

	public String getHtmlBody() {
		String body = getBody();
		body = HtmlUtil.resolveHtmlEntities(body);
		body = body.replaceAll(
				"(http:\\/\\/[-_.!~*'()a-zA-Z0-9;/?:@&=+$,%#]+)",
				"<a href='$1' target='_blank'>$1</a>");
		body = body.replaceAll("\n", "<br>");
		return body;
	}

}
