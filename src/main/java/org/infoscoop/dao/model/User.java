package org.infoscoop.dao.model;

import org.infoscoop.account.DomainManager;
import org.infoscoop.account.IAccount;
import org.infoscoop.dao.model.base.BaseUser;



public class User extends BaseUser implements IAccount {
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
		java.lang.String email,
		java.lang.Integer admin) {

		super (
			id,
			name,
			email,
			admin);
	}

/*[CONSTRUCTOR MARKER END]*/

	@Override
	protected void initialize() {
		super.initialize();
		super.setFkDomainId(DomainManager.getContextDomainId());
	}

	public String getGroupName() {
//		if (getGroups() != null && getGroups().iterator().hasNext()) {
//			return getGroups().iterator().next().getName();
//		}
		return null;
	}

	public String getMail() {
		return getEmail();
	}

	public String getUid() {
		return getEmail();
	}

	public boolean isAdministrator() {
		return super.getAdmin() == 1;
	}
}