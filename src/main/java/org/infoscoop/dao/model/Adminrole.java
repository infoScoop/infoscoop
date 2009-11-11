package org.infoscoop.dao.model;

import org.infoscoop.dao.model.base.BaseAdminrole;



public class Adminrole extends BaseAdminrole {
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public Adminrole () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public Adminrole (java.lang.String id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public Adminrole (
		java.lang.String id,
		java.lang.String roleid,
		java.lang.String name,
		java.lang.String permission) {

		super (
			id,
			roleid,
			name,
			permission);
	}

/*[CONSTRUCTOR MARKER END]*/

	public boolean isAllowDelete(){
		return super.getAllowdelete() > 0;
	}
}