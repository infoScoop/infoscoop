package org.infoscoop.dao.model;

import org.infoscoop.dao.model.base.BaseCommandBar;



public class CommandBar extends BaseCommandBar {
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public CommandBar () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public CommandBar (java.lang.Integer id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public CommandBar (
		java.lang.Integer id,
		java.lang.Integer displayOrder,
		java.lang.String accessLevel) {

		super (
			id,
			displayOrder,
			accessLevel);
	}

/*[CONSTRUCTOR MARKER END]*/


}