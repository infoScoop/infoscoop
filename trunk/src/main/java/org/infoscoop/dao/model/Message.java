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

	@Override
	public String getFrom() {
		return StringUtil.getNullSafe( super.getFrom() );
	}
	@Override
	public String getType() {
		return StringUtil.getNullSafe( super.getType() );
	}
}