package org.infoscoop.dao.model;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;
import org.infoscoop.account.IAccount;
import org.infoscoop.account.IGroup;

public class Account implements IAccount {
	
	private String uid;
	private String name;
	private String password;
	private String mail;
	private String groupName;

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

	public IGroup[] getGroups() { return null; }


}
