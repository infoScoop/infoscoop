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

import java.util.List;

import org.infoscoop.account.IAccount;
import org.infoscoop.account.IGroup;


public class LDAPAccount implements IAccount{
	private String dn;
	private String uid;
	private String mail;
	private String name;
	private String groupName;
	// add List of group
	private IGroup[] groups;
	private List<String> mails;
	
	public LDAPAccount(String dn, String uid, String mail, String displayName, String groupName) {
		super();
		this.dn = dn;
		this.uid = uid;
		this.mail = mail;
		this.name = displayName;
		this.groupName = groupName;
	}
	
	// add the constructor that added groups of List to argument. 
	public LDAPAccount(String dn, String uid, String mail, String displayName, String groupName, IGroup[] groups, List<String> mails) {
		super();
		this.dn = dn;
		this.uid = uid;
		this.mail = mail;
		this.name = displayName;
		this.groupName = groupName;
		this.groups = groups;
		this.mails = mails;
	}
	
	public String getName() {
		return name;
	}

	public String getDn() {
		return dn;
	}

	public String getUid() {
		return uid;
	}
	
	public String getMail() {
		return mail;
	}
	
	public String getGroupName(){
		if(groupName == null && this.groups != null){
			StringBuffer groupNameList = new StringBuffer();
			for(int i = 0; i < this.groups.length; i++){
				groupNameList.append(this.groups[i].getName());
				if( i +1 < this.groups.length )
					groupNameList.append(",");
			}
			return groupNameList.toString();
		}else{	
			return groupName;
		}
	}
	
	public IGroup[] getGroups(){
		return groups;
	}
	
	public boolean equals(Object obj){
		if(obj instanceof LDAPAccount){
			LDAPAccount user = (LDAPAccount)obj;
			return this.dn.equals(user.getDn());
		}
		return false;
	}

	public int hashCode() {
		return this.dn.hashCode();
	}
	
	public String toString(){
//		return this.dn + ", " + this.uid + ", " + this.name + ", " + this.mail + ", " + groupName;
		return this.dn + ", " + this.uid + ", " + this.name + ", " + this.mail + ", " + this.groupName + this.groups;
		
	}

	public void setGroups(IGroup[] igroup) {
		this.groups = igroup;
		
	}
	
	public void setMails(List<String> mails) {
		this.mails = mails;
	}
	
	public List<String> getMails() {
		return mails;
	}
}
