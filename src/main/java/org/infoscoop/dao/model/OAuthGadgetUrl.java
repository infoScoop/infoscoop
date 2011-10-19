package org.infoscoop.dao.model;

import org.infoscoop.dao.model.base.BaseOAuthGadgetUrl;
import org.infoscoop.util.Crypt;

public class OAuthGadgetUrl extends BaseOAuthGadgetUrl{
	
	/*[CONSTRUCTOR MARKER BEGIN]*/
	public OAuthGadgetUrl () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public OAuthGadgetUrl (java.lang.Long id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public OAuthGadgetUrl (
		java.lang.Long id,
		java.lang.String gadgetUrl) {

		super (
			id,
			gadgetUrl);
	}

/*[CONSTRUCTOR MARKER END]*/
	@Override
	public void setGadgetUrl(String gadgetUrl) {
		super.setGadgetUrl(gadgetUrl);
		super.setGadgetUrlKey(Crypt.getHash(gadgetUrl));
	}
}
