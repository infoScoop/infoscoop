package org.infoscoop.dao.model;

import org.infoscoop.dao.model.base.BaseTabTemplateParsonalizeGadget;



public class TabTemplateParsonalizeGadget extends BaseTabTemplateParsonalizeGadget {
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public TabTemplateParsonalizeGadget () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public TabTemplateParsonalizeGadget (java.lang.Integer id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public TabTemplateParsonalizeGadget (
		java.lang.Integer id,
		org.infoscoop.dao.model.GadgetInstance fkGadgetInstance,
		org.infoscoop.dao.model.TabTemplate fkTabtemplate,
		java.lang.Integer columnNum) {

		super (
			id,
			fkGadgetInstance,
			fkTabtemplate,
			columnNum);
	}

/*[CONSTRUCTOR MARKER END]*/


}