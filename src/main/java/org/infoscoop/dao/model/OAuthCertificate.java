package org.infoscoop.dao.model;

import org.infoscoop.dao.model.base.BaseOAuthCertificate;



public class OAuthCertificate extends BaseOAuthCertificate {
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public OAuthCertificate () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public OAuthCertificate (java.lang.String consumerKey) {
		super(consumerKey);
	}

/*[CONSTRUCTOR MARKER END]*/


}