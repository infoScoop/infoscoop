package org.infoscoop.dao.model;

import org.infoscoop.dao.model.base.BaseGadgetIcon;
import org.infoscoop.util.StringUtil;



public class GadgetIcon extends BaseGadgetIcon {
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public GadgetIcon () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public GadgetIcon (java.lang.String type) {
		super(type);
	}

	/**
	 * Constructor for required fields
	 */
	public GadgetIcon (
		java.lang.String type,
		java.lang.String url) {

		super (
			type,
			url);
	}

/*[CONSTRUCTOR MARKER END]*/

	@Override
	public String getUrl() {
		return StringUtil.getNullSafe( super.getUrl() );
	}
}