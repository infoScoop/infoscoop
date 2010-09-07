package org.infoscoop.dao.model;

import org.infoscoop.dao.model.base.BaseMenuTree;



public class MenuTree extends BaseMenuTree {
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public MenuTree () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public MenuTree (java.lang.Integer id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public MenuTree (
		java.lang.Integer id,
		java.lang.String title) {

		super (
			id,
			title);
	}

/*[CONSTRUCTOR MARKER END]*/


}