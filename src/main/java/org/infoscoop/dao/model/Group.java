package org.infoscoop.dao.model;

import org.infoscoop.dao.model.base.BaseGroup;



public class Group extends BaseGroup {
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public Group () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public Group (java.lang.String id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public Group (
		java.lang.String id,
		java.lang.String name) {

		super (
			id,
			name);
	}

/*[CONSTRUCTOR MARKER END]*/


}