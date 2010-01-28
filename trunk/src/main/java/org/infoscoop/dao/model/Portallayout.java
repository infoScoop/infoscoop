package org.infoscoop.dao.model;

import org.infoscoop.dao.model.base.BasePortallayout;



public class Portallayout extends BasePortallayout {
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public Portallayout () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public Portallayout (java.lang.String id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public Portallayout (
		java.lang.String id,
		java.lang.String layout) {

		super (
			id,
			layout);
	}

/*[CONSTRUCTOR MARKER END]*/


}