package org.infoscoop.dao.model;

import org.infoscoop.dao.model.base.BaseTabTemplatePersonalizeGadget;



public class TabTemplatePersonalizeGadget extends BaseTabTemplatePersonalizeGadget 
		implements Cloneable{
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


	public TabTemplatePersonalizeGadget createTemp (TabTemplate tabTemplate)throws CloneNotSupportedException{
		TabTemplatePersonalizeGadget personalizeGadgetClone = new TabTemplatePersonalizeGadget();
		personalizeGadgetClone.setColumnNum(this.getColumnNum());
		personalizeGadgetClone.setFkGadgetInstance(this.getFkGadgetInstance());
		personalizeGadgetClone.setFkTabTemplate(this.getFkTabTemplate());
		personalizeGadgetClone.setSibling(this.getSibling());
		personalizeGadgetClone.setWidgetId(this.getWidgetId());
		personalizeGadgetClone.setFkTabTemplate(tabTemplate);
		return personalizeGadgetClone;
	}
		
}