package org.infoscoop.dao.model;

import org.infoscoop.dao.model.base.BaseRolePrincipal;



public class RolePrincipal extends BaseRolePrincipal {
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public RolePrincipal () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public RolePrincipal (java.lang.Integer id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public RolePrincipal (
		java.lang.Integer id,
		org.infoscoop.dao.model.Role fkRole,
		java.lang.String type,
		java.lang.String name) {

		super (
			id,
			fkRole,
			type,
			name);
	}

/*[CONSTRUCTOR MARKER END]*/


}