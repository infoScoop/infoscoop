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
