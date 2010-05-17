
package org.infoscoop.account.ldap;

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
	
	public LDAPAccount(String dn, String uid, String mail, String displayName, String groupName) {
		super();
		this.dn = dn;
		this.uid = uid;
		this.mail = mail;
		this.name = displayName;
		this.groupName = groupName;
	}
	
	// add the constructor that added groups of List to argument. 
	public LDAPAccount(String dn, String uid, String mail, String displayName, String groupName, IGroup[] groups) {
		super();
		this.dn = dn;
		this.uid = uid;
		this.mail = mail;
		this.name = displayName;
		this.groupName = groupName;
		this.groups = groups;
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
}
