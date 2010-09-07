package org.infoscoop.dao.model;

import org.infoscoop.dao.model.base.BaseMenuPosition;



public class MenuPosition extends BaseMenuPosition {
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public MenuPosition () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public MenuPosition (java.lang.String id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public MenuPosition (
		java.lang.String id,
		org.infoscoop.dao.model.MenuTree fkMenuTree) {

		super (
			id,
			fkMenuTree);
	}

/*[CONSTRUCTOR MARKER END]*/


}