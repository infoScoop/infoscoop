package org.infoscoop.dao.model;

import org.infoscoop.dao.model.base.BaseCommandBarStaticGadget;



public class CommandBarStaticGadget extends BaseCommandBarStaticGadget implements StaticGadget{
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public CommandBarStaticGadget () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public CommandBarStaticGadget (java.lang.Integer id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public CommandBarStaticGadget (
		java.lang.Integer id,
		org.infoscoop.dao.model.GadgetInstance gadgetInstance,
		org.infoscoop.dao.model.CommandBar fkCommandBar,
		java.lang.String containerId) {

		super (
			id,
			gadgetInstance,
			fkCommandBar,
			containerId);
	}

/*[CONSTRUCTOR MARKER END]*/


	public boolean isIgnoreHeaderBool() {
		return false;
	}
	public boolean isNoBorderBool() {
		return false;
	}
}