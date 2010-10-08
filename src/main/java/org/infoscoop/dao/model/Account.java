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

import org.apache.commons.codec.binary.Base64;
import org.infoscoop.account.IAccount;
import org.infoscoop.account.IGroup;
import org.infoscoop.dao.model.base.BaseAccount;

public class Account extends BaseAccount implements IAccount{
	
	private String mail;
	private String groupName;
	public Account(String uid, String name, Integer fkDomainId, String plainTextPassword){
		super();
		super.setId(new ACCOUNTPK(fkDomainId, uid));
		super.setName( name );
		this.setPasswordPlainText( plainTextPassword);
	}

	public Account(){
		super();
	}

	public void setPasswordPlainText(String plainTextPassword) { 
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA");
			super.setPassword( new String(Base64.encodeBase64(digest.digest( plainTextPassword.getBytes("iso-8859-1")))));
		} catch (NoSuchAlgorithmException e) {	e.printStackTrace(); 
		} catch (UnsupportedEncodingException e) {	e.printStackTrace(); }
	}

	public String getMail() { return this.mail; }
	public void setMail(String mail) { this.mail = mail; }

	public String getGroupName() { return this.groupName; }
	public void setGroupName(String groupName) { this.groupName = groupName; }

	public IGroup[] getGroups() { return null; }

	public String getUid() {
		return super.getId().getUid();
	}

}
