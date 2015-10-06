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

import org.infoscoop.dao.model.base.BaseGlobalPreference;
import org.infoscoop.util.StringUtil;




public class GlobalPreference extends BaseGlobalPreference {
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	/**
	 * Constructor for primary key
	 */
	public GlobalPreference () {
		super();
	}

	/**
	 * Constructor for required fields
	 */
	public GlobalPreference (
		java.lang.String uid,
		java.lang.String name,
		java.lang.String value) {

		super(uid, name, value);
	}

/*[CONSTRUCTOR MARKER END]*/
	@Override
	public String getValue() {
		// TODO Auto-generated method stub
		return StringUtil.getNullSafe(super.getValue());
	}
}
