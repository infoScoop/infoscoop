/* infoScoop OpenSource
 * Copyright (C) 2015 UNIRITA Inc.
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

package org.infoscoop.account.saas;

import org.infoscoop.account.IAccount;
import org.infoscoop.account.IGroup;

import java.util.List;


public class SaaSAccount implements IAccount{
	private String dn;
	private String uid;
	private String mail;
	private String name;
	private String defaultSquareId;
	private List<String> belongSquareId;
	private List<String> mails;
	
	public SaaSAccount(String dn, String uid, String mail, String displayName, String defaultSquareId) {
		super();
		this.dn = dn;
		this.uid = uid;
		this.mail = mail;
		this.name = displayName;
		this.defaultSquareId = defaultSquareId;
	}
	
	// add the constructor that added groups of List to argument. 
	public SaaSAccount(String dn, String uid, String mail, String displayName, String defaultSquareId, List<String> belongSquareId, List<String> mails) {
		super();
		this.dn = dn;
		this.uid = uid;
		this.mail = mail;
		this.name = displayName;
		this.defaultSquareId = defaultSquareId;
		this.belongSquareId = belongSquareId;
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
	
	public String getDefaultSquareId(){
		return defaultSquareId;
	}

	public boolean equals(Object obj){
		if(obj instanceof SaaSAccount){
			SaaSAccount user = (SaaSAccount)obj;
			return this.dn.equals(user.getDn());
		}
		return false;
	}

	public int hashCode() {
		return this.dn.hashCode();
	}
	
	public String toString(){
//		return this.dn + ", " + this.uid + ", " + this.name + ", " + this.mail + ", " + groupName;
		return this.dn + ", " + this.uid + ", " + this.name + ", " + this.mail + ", " + this.defaultSquareId;
		
	}

	public void setBelongSquareId(List<String> belongSquareId) {
		this.belongSquareId = belongSquareId;
	}

	public List<String> getBelongSquareId() {
		return belongSquareId;
	}

	public void setMails(List<String> mails) {
		this.mails = mails;
	}
	
	public List<String> getMails() {
		return mails;
	}

	public String getGroupName(){return null;}
	public IGroup[] getGroups(){return null;}
}
