package org.infoscoop.dao.model;

import org.infoscoop.dao.model.base.BaseMenuUserprefsPK;

public class MenuUserprefsPK extends BaseMenuUserprefsPK {
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public MenuUserprefsPK () {}
	
	public MenuUserprefsPK (
		org.infoscoop.dao.model.MenuItem fkMenuItem,
		java.lang.String name) {

		super (
			fkMenuItem,
			name);
	}
/*[CONSTRUCTOR MARKER END]*/


}