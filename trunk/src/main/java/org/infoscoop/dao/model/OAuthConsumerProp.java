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
	public OAuthConsumerProp (org.infoscoop.dao.model.OAUTH_CONSUMER_PK id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public OAuthConsumerProp (
		org.infoscoop.dao.model.OAUTH_CONSUMER_PK id,
		java.lang.String gadgetUrl) {

		super (
			id,
			gadgetUrl);
	}

/*[CONSTRUCTOR MARKER END]*/


}