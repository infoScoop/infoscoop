package org.infoscoop.dao.model;

import org.infoscoop.dao.model.base.BaseCommandBar;



public class CommandBar extends BaseCommandBar {
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public CommandBar () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public CommandBar (java.lang.Integer id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public CommandBar (
		java.lang.Integer id,
		java.lang.Integer displayOrder,
		java.lang.String accessLevel) {

		super (
			id,
			displayOrder,
			accessLevel);
	}

	public Object getLayout() {
		StringBuffer html = new StringBuffer();
		html.append("<table cellpadding='0' cellspacing='3' width='100%'>\n  <tr>");
		for(CommandBarStaticGadget gadget: super.getCommandBarStaticGadgets()){
			if("Ticker".equals(gadget.getGadgetInstance().getType()))
				html.append("  <td width='100%'><div id='").append(gadget.getContainerId()).append("'></div></td>\n");	
			else
				html.append("  <td><div id='").append(gadget.getContainerId()).append("'></div></td>\n");			
		}
		html.append("  <td><div id='portal-go-home'></div></td>\n");
		html.append("  <td><div id='portal-preference'></div></td>\n");
		html.append("  <td><div id='portal-trash'></div></td>\n");
		html.append("  <td><div id='portal-admin-link'></div></td>\n");
		html.append("  </tr>\n</table>");
		return html.toString();
	}

/*[CONSTRUCTOR MARKER END]*/


}