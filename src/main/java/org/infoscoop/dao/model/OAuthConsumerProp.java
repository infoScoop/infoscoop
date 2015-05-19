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
	public OAuthConsumerProp (OAuthConsumerPropPK id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public OAuthConsumerProp (
		OAuthConsumerPropPK id,
		java.lang.String serviceName) {

		super (
			id,
			serviceName);
	}

/*[CONSTRUCTOR MARKER END]*/
}