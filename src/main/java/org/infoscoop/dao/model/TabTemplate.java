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
	public TabTemplate (java.lang.String id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public TabTemplate (
		java.lang.String id,
		java.lang.String tabName,
		java.lang.Integer published,
		java.lang.Integer accessLevel) {

		super (
			id,
			tabName,
			published,
			accessLevel);
	}

/*[CONSTRUCTOR MARKER END]*/


}