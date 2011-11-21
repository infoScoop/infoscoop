package org.infoscoop.dao.model;

import org.infoscoop.dao.model.base.BaseOAuth2Token;

public class OAuth2Token extends BaseOAuth2Token {
	private static final long serialVersionUID = 1L;

	/*[CONSTRUCTOR MARKER BEGIN]*/
		public OAuth2Token () {
			super();
		}

		/**
		 * Constructor for primary key
		 */
		public OAuth2Token (org.infoscoop.dao.model.OAUTH2_TOKEN_PK id) {
			super(id);
		}

	/*[CONSTRUCTOR MARKER END]*/
}