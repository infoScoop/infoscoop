package org.infoscoop.dao.model;

import org.infoscoop.dao.model.base.BaseOAuthToken;



public class OAuthToken extends BaseOAuthToken {
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public OAuthToken () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public OAuthToken (org.infoscoop.dao.model.OAUTH_TOKEN_PK id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public OAuthToken (
		org.infoscoop.dao.model.OAUTH_TOKEN_PK id,
		java.lang.String gadgetUrl,
		java.lang.String tokenSecret) {

		super (
			id,
			gadgetUrl,
			tokenSecret);
	}

/*[CONSTRUCTOR MARKER END]*/


}