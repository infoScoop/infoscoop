package org.infoscoop.dao.model;

import org.infoscoop.dao.model.base.BaseAccesslog;



public class Accesslog extends BaseAccesslog {
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public Accesslog () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public Accesslog (java.lang.Long id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public Accesslog (
		java.lang.Long id,
		java.lang.String uid,
		java.lang.String date) {

		super (
			id,
			uid,
			date);
	}

/*[CONSTRUCTOR MARKER END]*/


}