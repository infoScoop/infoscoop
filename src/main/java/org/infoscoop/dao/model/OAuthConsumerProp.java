package org.infoscoop.dao.model;

import org.infoscoop.dao.model.base.BaseOAuthConsumerProp;
import org.infoscoop.util.Crypt;



public class OAuthConsumerProp extends BaseOAuthConsumerProp {
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public OAuthConsumerProp () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public OAuthConsumerProp (java.lang.Long id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public OAuthConsumerProp (
		java.lang.Long id,
		java.lang.String gadgetUrl,
		java.lang.String gadgetUrlKey,
		java.lang.String serviceName,
		java.lang.Integer isUpload) {

		super (
			id,
			gadgetUrl,
			gadgetUrlKey,
			serviceName,
			isUpload);
	}

/*[CONSTRUCTOR MARKER END]*/

	@Override
	public void setGadgetUrl(String gadgetUrl) {
		super.setGadgetUrl(gadgetUrl);
		super.setGadgetUrlKey(Crypt.getHash(gadgetUrl));
	}
}