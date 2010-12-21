package org.infoscoop.dao.model;

import org.infoscoop.dao.model.base.BaseOAuth2LeggedConsumer;



public class OAuth2LeggedConsumer extends BaseOAuth2LeggedConsumer {
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public OAuth2LeggedConsumer () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public OAuth2LeggedConsumer (java.lang.Long id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public OAuth2LeggedConsumer (
		java.lang.Long id,
		java.lang.String serviceName) {

		super (
			id,
			serviceName);
	}

/*[CONSTRUCTOR MARKER END]*/


}