package org.infoscoop.dao.model;

import org.infoscoop.dao.model.base.BaseGadgetInstanceUserprefPK;

public class GadgetInstanceUserprefPK extends BaseGadgetInstanceUserprefPK {
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public GadgetInstanceUserprefPK () {}
	
	public GadgetInstanceUserprefPK (
		org.infoscoop.dao.model.GadgetInstance fkGadgetInstance,
		java.lang.String name) {

		super (
			fkGadgetInstance,
			name);
	}
/*[CONSTRUCTOR MARKER END]*/


}