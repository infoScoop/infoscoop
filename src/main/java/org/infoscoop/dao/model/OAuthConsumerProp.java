package org.infoscoop.dao.model;

import org.infoscoop.dao.model.base.BaseOAuthConsumerProp;



public class OAuthConsumerProp extends BaseOAuthConsumerProp {
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public OAuthConsumerProp () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public OAuthConsumerProp (java.lang.String id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public OAuthConsumerProp (
		java.lang.String id,
		java.lang.String serviceName,
		java.lang.Integer isUpload) {

		super (
			id,
			serviceName,
			isUpload);
	}

/*[CONSTRUCTOR MARKER END]*/
}