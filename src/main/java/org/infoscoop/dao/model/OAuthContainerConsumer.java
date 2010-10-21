package org.infoscoop.dao.model;

import org.infoscoop.dao.model.base.BaseOAuthContainerConsumer;



public class OAuthContainerConsumer extends BaseOAuthContainerConsumer {
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public OAuthContainerConsumer () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public OAuthContainerConsumer (java.lang.Long id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public OAuthContainerConsumer (
		java.lang.Long id,
		java.lang.String serviceName) {

		super (
			id,
			serviceName);
	}

/*[CONSTRUCTOR MARKER END]*/


}