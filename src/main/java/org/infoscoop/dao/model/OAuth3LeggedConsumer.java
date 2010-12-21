package org.infoscoop.dao.model;

import org.infoscoop.dao.model.base.BaseOAuth3LeggedConsumer;
import org.infoscoop.util.Crypt;



public class OAuth3LeggedConsumer extends BaseOAuth3LeggedConsumer {
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public OAuth3LeggedConsumer () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public OAuth3LeggedConsumer (java.lang.Long id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public OAuth3LeggedConsumer (
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