package org.infoscoop.dao.model;

import org.infoscoop.dao.model.base.BaseDomain;



public class Domain extends BaseDomain {
	private static final long serialVersionUID = 1L;
	
/*[CONSTRUCTOR MARKER BEGIN]*/
	public Domain () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public Domain (java.lang.Integer id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public Domain (
		java.lang.Integer id,
		java.lang.String name) {

		super (
			id,
			name);
	}

/*[CONSTRUCTOR MARKER END]*/


}