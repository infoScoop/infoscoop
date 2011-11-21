package org.infoscoop.dao.model;

import org.infoscoop.dao.model.base.BaseOAUTH2_TOKEN_PK;

public class OAUTH2_TOKEN_PK  extends BaseOAUTH2_TOKEN_PK {
	private static final long serialVersionUID = 1L;

	/*[CONSTRUCTOR MARKER BEGIN]*/
		public OAUTH2_TOKEN_PK () {}
		
		public OAUTH2_TOKEN_PK (
			java.lang.String uid,
			java.lang.String fkOAuthId) {

			super (
				uid,
				fkOAuthId);
		}
	/*[CONSTRUCTOR MARKER END]*/
}
