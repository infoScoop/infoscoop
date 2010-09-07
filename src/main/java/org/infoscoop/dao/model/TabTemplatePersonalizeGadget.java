package org.infoscoop.dao.model;

import org.infoscoop.dao.model.base.BaseTabTemplatePersonalizeGadget;



public class TabTemplatePersonalizeGadget extends BaseTabTemplatePersonalizeGadget {
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public TabTemplatePersonalizeGadget () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public TabTemplatePersonalizeGadget (java.lang.Integer id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public TabTemplatePersonalizeGadget (
		java.lang.Integer id,
		org.infoscoop.dao.model.GadgetInstance fkGadgetInstance,
		org.infoscoop.dao.model.TabTemplate fkTabTemplate,
		java.lang.String widgetId,
		java.lang.Integer columnNum) {

		super (
			id,
			fkGadgetInstance,
			fkTabTemplate,
			widgetId,
			columnNum);
	}

/*[CONSTRUCTOR MARKER END]*/


}