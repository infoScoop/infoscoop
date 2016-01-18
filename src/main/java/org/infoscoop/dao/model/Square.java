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

import org.infoscoop.dao.model.base.BaseSquare;

public class Square extends BaseSquare {
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public Square () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public Square (java.lang.String id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public Square (
		java.lang.String id,
		java.lang.String name,
		java.lang.String description,
		java.util.Date lastmodified,
		java.lang.String owner,
		java.lang.Integer maxUserNum,
		java.lang.String parentSquareId) {

		super (
			id,
			name,
			description,
			lastmodified,
			owner,
			maxUserNum,
			parentSquareId);
	}

/*[CONSTRUCTOR MARKER END]*/

}
