package org.infoscoop.api.dao.model;

import org.infoscoop.api.dao.model.base.BaseOAuth2ProviderAccessToken;

public class OAuth2ProviderAccessToken extends BaseOAuth2ProviderAccessToken {
	private static final long serialVersionUID = 1L;

	public OAuth2ProviderAccessToken() {
		super();
	}

	public OAuth2ProviderAccessToken(String id) {
		super(id);
	}
}
