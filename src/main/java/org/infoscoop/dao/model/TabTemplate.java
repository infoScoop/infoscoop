package org.infoscoop.dao.model;

import org.infoscoop.dao.model.base.BaseTabTemplate;



public class TabTemplate extends BaseTabTemplate {
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public TabTemplate () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public TabTemplate (java.lang.Integer id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public TabTemplate (
		java.lang.Integer id,
		java.lang.String name,
		java.lang.Integer published) {

		super (
			id,
			name,
			published);
	}

/*[CONSTRUCTOR MARKER END]*/


}