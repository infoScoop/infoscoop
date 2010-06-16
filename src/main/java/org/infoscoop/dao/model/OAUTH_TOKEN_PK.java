package org.infoscoop.dao.model;

import org.infoscoop.dao.model.base.BaseOAUTH_TOKEN_PK;

public class OAUTH_TOKEN_PK extends BaseOAUTH_TOKEN_PK {
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public OAUTH_TOKEN_PK () {}
	
	public OAUTH_TOKEN_PK (
		java.lang.String uid,
		java.lang.String gadgetUrl,
		java.lang.String serviceName) {

		super (
			uid,
			gadgetUrl,
			serviceName);
	}
/*[CONSTRUCTOR MARKER END]*/


}