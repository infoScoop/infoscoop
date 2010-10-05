package org.infoscoop.dao.model;

import java.util.ArrayList;
import java.util.List;

import org.infoscoop.account.DomainManager;
import org.infoscoop.dao.model.base.BaseRole;



public class Role extends BaseRole {
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public Role () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public Role (java.lang.Integer id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public Role (
		java.lang.Integer id,
		java.lang.String name) {

		super (
			id,
			name);
	}

/*[CONSTRUCTOR MARKER END]*/

	@Override
	protected void initialize() {
		super.initialize();
		this.setFkDomainId(DomainManager.getContextDomainId());
	}

	public String getSize(){
		if(this.getRolePrincipals() == null)return "0";
		return Integer.toString(this.getRolePrincipals().size());
	}
	
	private List<String> deletePrincipalIdList = new ArrayList<String>();
	
	public List<String> getDeletePrincipalIdList(){
		return this.deletePrincipalIdList;
	}
	
}