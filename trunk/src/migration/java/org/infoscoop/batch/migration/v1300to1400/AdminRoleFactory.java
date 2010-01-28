package org.infoscoop.batch.migration.v1300to1400;

import org.infoscoop.dao.model.Adminrole;


public class AdminRoleFactory implements CSVBeanFactory {
	public Adminrole newBean( CSVField[] values ) {
		Adminrole adminrole = new Adminrole();
		adminrole.setRoleid( values[1].toString() );
		adminrole.setName(values[2].toString());
		adminrole.setPermission( values[3].toString() );
		adminrole.setAllowdelete( values[4].toInt());

		return adminrole;
	}
}
