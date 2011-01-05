package org.infoscoop.acl;

import java.io.Serializable;
import java.security.Principal;


public class ISAdminPrincipal implements Principal, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4156506506416185187L;

	@Override
	public String getName() {
		return ISPrincipal.ADMINISTRATOR_PRINCIPAL;
	}

}
