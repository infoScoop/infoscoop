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

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.infoscoop.account.IAccount;
import org.infoscoop.account.IGroup;

public class Account implements IAccount {
	
	private String uid;
	private String name;
	private String password;
	private String groupName;
	private String defaultSquareId;
	private String mySquareId;
	private String givenName;
	private String familyName;
	private String mail;
	private boolean isAdmin;
	List<String> belongIds = new ArrayList<String>();
	
	private Set<org.infoscoop.dao.model.AccountAttr> accountAttrs;
	private Set<AccountSquare> accountSquares;

	public Account(String uid, String name, String plainTextPassword){
		this.uid = uid;
		this.name = name;
		this.setPasswordPlainText( plainTextPassword);
	}

	public Account(){}

	public String getUid() { return this.uid; }
	public void setUid(String uid) { this.uid = uid; }

	public String getName() { return this.name; }
	public void setName(String name) { this.name = name; }
	

	public String getPassword() { return password; }
	public void setPassword(String password) { this.password = password; }
	public void setPasswordPlainText(String plainTextPassword) { 
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA");
			this.password = new String(Base64.encodeBase64(digest.digest( plainTextPassword.getBytes("iso-8859-1"))));
		} catch (NoSuchAlgorithmException e) {	e.printStackTrace(); 
		} catch (UnsupportedEncodingException e) {	e.printStackTrace(); }
	}

	public String getMail() { return this.mail; }
	public void setMail(String mail) { this.mail = mail; }

	public String getGroupName() { return this.groupName; }
	public void setGroupName(String groupName) { this.groupName = groupName; }

	public String getDefaultSquareId() {
		return defaultSquareId;
	}

	public void setDefaultSquareId(String defaultSquareId) {
		this.defaultSquareId = defaultSquareId;
	}

	public String getGivenName() {
		return givenName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	public String getFamilyName() {
		return familyName;
	}

	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}

	public IGroup[] getGroups() { return null; }
	public List<String> getMails() {
		List<String> mails = new ArrayList<String>();
		if(this.mail!=null) mails.add(this.mail);
		return mails;
	}
	public List<String> getBelongids() {
		if(belongIds.size() == 0){
			Set<AccountSquare> accountSquares = getAccountSquares();
			for(AccountSquare accountSquare : accountSquares){
				belongIds.add(accountSquare.getSquareId());
			}
		}
		
//		if(this.belongid!=null) belongIds.add(this.belongid);
		return belongIds;
	}

	public Set<AccountAttr> getAccountAttrs() {
		return accountAttrs;
	}

	public void setAccountAttrs(Set<AccountAttr> accountAttrs) {
		this.accountAttrs = accountAttrs;
	}

	public Set<AccountSquare> getAccountSquares() {
		return accountSquares;
	}

	public void setAccountSquares(Set<AccountSquare> accountSquares) {
		this.accountSquares = accountSquares;
	}

	@Override
	public boolean isAdmin() {
		return isAdmin;
	}

	@Override
	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	@Override
	public String getMySquareId() {
		return this.mySquareId;
	}

	public void setMySquareId(String mySquareId) {
		this.mySquareId = mySquareId;
	}

}
