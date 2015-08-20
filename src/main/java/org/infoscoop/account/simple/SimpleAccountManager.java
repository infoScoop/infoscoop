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

package org.infoscoop.account.simple;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.security.auth.Subject;

import org.apache.commons.codec.binary.Base64;
import org.infoscoop.account.AuthenticationException;
import org.infoscoop.account.IAccount;
import org.infoscoop.account.IAccountManager;
import org.infoscoop.account.PrincipalDef;
import org.infoscoop.acl.ISPrincipal;
import org.infoscoop.dao.AccountDAO;
import org.infoscoop.dao.model.Account;
import org.json.JSONObject;

/**
 * @author hr-endoh
 *
 */
public class SimpleAccountManager implements IAccountManager{

	private AccountDAO dao;

	public void setAccountDAO(AccountDAO dao){
		this.dao = dao;
	}

	public IAccount getUser(String uid) throws Exception {
		return dao.get(uid);
	}

	/* (non-Javadoc)
	 * @see org.infoscoop.searchuid.ISearchModule#search(java.util.Map)
	 */
	public List searchUser(Map searchConditionMap) throws Exception {
		String name = (String)searchConditionMap.get("user_name");
		return this.dao.selectByName(name);
	}

	public void login(String userid, String password) throws AuthenticationException {

		Account account;
		try {
			account = (Account)this.getUser(userid);
			if(account == null){
				throw new AuthenticationException(userid + " is not found.");
			}
		} catch (Exception e) {
			throw new AuthenticationException(e);
		}

		String _password = account.getPassword();
		checkCredentials(password, _password);

		
	}
	
	public Subject getSubject(String userid) throws Exception {
		Account account = (Account)this.getUser(userid);
		if(account == null) return null;
		
		Subject loginUser = new Subject();
		ISPrincipal p = new ISPrincipal(ISPrincipal.UID_PRINCIPAL, account.getUid());
		p.setDisplayName(account.getName());
		loginUser.getPrincipals().add(p);
		return loginUser;
	}

	private void checkCredentials(String password, String digest) throws AuthenticationException {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA");
		} catch (NoSuchAlgorithmException e) {
			throw new AuthenticationException(e);
		}
		try {
			password = new String(Base64.encodeBase64(md.digest(password.getBytes("iso-8859-1"))));
		} catch (UnsupportedEncodingException e) {
			throw new AuthenticationException(e);
		}
		if(!digest.equals(password)){
			throw new AuthenticationException("invalid password.");
		}
	}

	public boolean enableChangePassword(){
		return true;
	}

	public void changePassword(String userid, String password,
			String oldPassword) throws AuthenticationException {
		Account account;
		try {
			account = (Account)this.getUser(userid);
			if(account == null){
				throw new AuthenticationException(userid + " is not found.");
			}
		} catch (Exception e) {
			throw new AuthenticationException(e);
		}

		String _password = account.getPassword();
		checkCredentials(oldPassword, _password);

		account.setPasswordPlainText(password);
		dao.update(account);

	}

	@Override
	public void updatePassword(String userid, String password) throws AuthenticationException {
		throw new UnsupportedOperationException();
	}

	public Collection<PrincipalDef> getPrincipalDefs() {
		return new ArrayList<PrincipalDef>();
	}

	@Override
	public void addSquareId(String userid, String squareId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateDefaultSquare(String userid, String defaultSquareId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public JSONObject getAccountManagerForm(String userId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String updateUserProfile(String userId, Map<String, String[]> map) {
		throw new UnsupportedOperationException();
	}
}
