package org.infoscoop.dao.model;

import org.infoscoop.dao.model.base.BaseGadgetInstance;



public class GadgetInstance extends BaseGadgetInstance {
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public GadgetInstance () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public GadgetInstance (java.lang.Integer id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public GadgetInstance (
		java.lang.Integer id,
		java.lang.String type,
		java.lang.String title,
		java.lang.String href) {

		super (
			id,
			type,
			title,
			href);
	}

/*[CONSTRUCTOR MARKER END]*/


}