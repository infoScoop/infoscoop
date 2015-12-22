/* infoScoop OpenSource
 * Copyright (C) 2010 Beacon IT Inc.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0-standalone.html>.
 */

package org.infoscoop.dao.model;

import org.infoscoop.dao.model.base.BasePortallayout;



public class Portallayout extends BasePortallayout {
	private static final long serialVersionUID = 1L;
	
	public static final String LAYOUT_TYPE_CSS = "css";
	public static final String LAYOUT_TYPE_CSS_MOBILE = "css_mobile";
	public static final String LAYOUT_TYPE_JS = "javascript";
	public static final String LAYOUT_TYPE_CUSTOMTHEME = "customTheme";
	
/*[CONSTRUCTOR MARKER BEGIN]*/
	public Portallayout () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public Portallayout (PortallayoutPK id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public Portallayout (
		PortallayoutPK id,
		java.lang.String layout) {

		super (
			id,
			layout);
	}

/*[CONSTRUCTOR MARKER END]*/


}
