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

import java.util.ArrayList;
import java.util.Collection;

import org.infoscoop.dao.model.base.BaseSystemMessage;



public class SystemMessage extends BaseSystemMessage {
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public SystemMessage () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public SystemMessage (java.lang.Long id) {
		super(id);
	}

	public SystemMessage(String to, String resourceId, String replaceValues, String squareid) {
		super(to, resourceId, replaceValues, squareid);
	}

	public Collection<String> getReplaceValueCollection() {
		String[] values = super.getReplaceValues().split(",");
		Collection<String> msgs = new ArrayList<String>();
		for(int i = 0; i < values.length; i++){
			msgs.add(values[i].trim());
		}
		return msgs;
	}

/*[CONSTRUCTOR MARKER END]*/


}
