package org.infoscoop.api.dao.model;

import org.infoscoop.api.dao.model.base.BaseOAuth2ProviderClientDetail;

public class OAuth2ProviderClientDetail extends BaseOAuth2ProviderClientDetail {
	private static final long serialVersionUID = 1L;

	public OAuth2ProviderClientDetail() {
		super();
	}
	
	public OAuth2ProviderClientDetail (String id) {
		super(id);
	}
	
	public OAuth2ProviderClientDetail (String id, String title, String secret) {
		super(id,title,secret);
	}
}
