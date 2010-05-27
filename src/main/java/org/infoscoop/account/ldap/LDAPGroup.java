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

package org.infoscoop.account.ldap;

import java.util.Collection;

import org.infoscoop.account.IGroup;


public class LDAPGroup implements IGroup{
	
	private String groupId;
	private String name;
	private Collection members;
	public LDAPGroup(String dn, String displayName, Collection members) {
		super();
		this.groupId = dn;
		this.name = displayName;
		this.members = members;
	}
	public String getName() {
		return name;
	}
	public String getGroupId() {
		return groupId;
	}
	public Collection getMembers() {
		return members;
	}

	public String toString(){
//		return members.toString();
		return this.groupId + ":" + this.name;
	}
}
