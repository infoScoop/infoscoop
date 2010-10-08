package org.infoscoop.dao.model;

import org.infoscoop.dao.model.base.BaseACCOUNTPK;

public class ACCOUNTPK extends BaseACCOUNTPK {
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public ACCOUNTPK () {}
	
	public ACCOUNTPK (
		java.lang.Integer fkDomainId,
		java.lang.String uid) {

		super (
			fkDomainId,
			uid);
	}
/*[CONSTRUCTOR MARKER END]*/


}