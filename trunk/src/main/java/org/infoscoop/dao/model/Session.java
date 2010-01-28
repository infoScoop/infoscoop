package org.infoscoop.dao.model;

import org.infoscoop.dao.model.base.BaseSession;



public class Session extends BaseSession {
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public Session () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public Session (java.lang.String uid) {
		super(uid);
	}

	/**
	 * Constructor for required fields
	 */
	public Session (
		java.lang.String uid,
		java.lang.String sessionid) {

		super (
			uid,
			sessionid);
	}

/*[CONSTRUCTOR MARKER END]*/


}