package org.infoscoop.dao.model;

import org.infoscoop.account.DomainManager;
import org.infoscoop.dao.model.base.BaseUser;



public class User extends BaseUser {
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public User () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public User (java.lang.String id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public User (
		java.lang.String id,
		java.lang.String name,
		java.lang.String email) {

		super (
			id,
			name,
			email);
	}

/*[CONSTRUCTOR MARKER END]*/

	@Override
	protected void initialize() {
		super.initialize();
		super.setFkDomainId(DomainManager.getContextDomainId());
	}


}