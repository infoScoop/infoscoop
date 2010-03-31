package org.infoscoop.dao.model;

import org.infoscoop.dao.model.base.BasePortaladmins;



public class Portaladmins extends BasePortaladmins {
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public Portaladmins () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public Portaladmins (java.lang.String id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public Portaladmins (
		java.lang.String id,
		java.lang.String uid) {

		super (
			id,
			uid);
	}

/*[CONSTRUCTOR MARKER END]*/


}