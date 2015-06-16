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

import java.util.Date;

import org.infoscoop.dao.model.base.BaseRestrictionKey;

public class RestrictionKey extends BaseRestrictionKey{
	
	public RestrictionKey() {
		// TODO Auto-generated constructor stub
	}
	
	public RestrictionKey(String id, Date expired, String uid) {
		super(id, expired, uid);
	}
	
	public boolean isExpired(){
		Date now = new Date();
		return now.after(this.getExpired());
	}
}
