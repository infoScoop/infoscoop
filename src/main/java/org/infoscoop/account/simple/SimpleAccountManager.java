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
import java.util.*;

import javax.security.auth.Subject;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.account.AuthenticationException;
import org.infoscoop.account.IAccount;
import org.infoscoop.account.IAccountManager;
import org.infoscoop.account.PrincipalDef;
import org.infoscoop.account.helper.AccountHelper;
import org.infoscoop.acl.ISPrincipal;
import org.infoscoop.dao.AccountDAO;
import org.infoscoop.dao.model.Account;
import org.infoscoop.dao.model.AccountAttr;
import org.infoscoop.dao.model.AccountSquare;
import org.json.JSONObject;

/**
 * @author hr-endoh
 *
 */
public class SimpleAccountManager implements IAccountManager{
	private static Log log = LogFactory.getLog(SimpleAccountManager.class);
	private AccountDAO dao;

	private static final String DISPLAY_NAME_PARAM = "displayname";
	private static final String FIRST_NAME_PARAM = "firstname";
	private static final String GIVEN_NAME_PARAM = "givenname";
	private static final String EMAIL_PARAM = "email";

	/**
	 * Account manager form.
	 */
	private String accountManagerFormDef = null;
	public void setAccountDAO(AccountDAO dao){
		this.dao = dao;
	}

	public IAccount getUser(String uid) throws Exception {
		return dao.get(uid);
	}

	@Override
	public void updateUser(Map<String, Object> user) throws Exception {
		String uid = (String)user.get("uid");
		Account account = (Account)getUser(uid);

		// password
		String password = (String) user.get("password");
		if(password != null)
			account.setPasswordPlainText(password);

		// email
		String email = (String) user.get("email");
		if(email != null)
			account.setMail(email);

		// given name
		String givenName = (String) user.get("givenName");
		if(givenName != null && givenName.trim().length() > 0)
			account.setGivenName(givenName);

		// family name
		String familyName = (String) user.get("familyName");
		if(familyName != null && familyName.trim().length() > 0)
			account.setFamilyName(familyName);

		// name
		String name = (String) user.get("name");
		if(name != null)
			account.setName(name);

		// default square
		String defaultSquareId = (String)user.get("defaultSquareId");
		if(defaultSquareId != null) {
			if(defaultSquareId.trim().length() > 0) {
				account.setDefaultSquareId(defaultSquareId);
			} else {
				account.setDefaultSquareId(account.getMySquareId());
			}
		}

		dao.update(account);
	}

	/* (non-Javadoc)
	 * @see org.infoscoop.searchuid.ISearchModule#search(java.util.Map)
	 */
	public List searchUser(Map searchConditionMap) throws Exception {
		return this.dao.selectByMap(searchConditionMap);
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
		ISPrincipal p = new ISPrincipal(ISPrincipal.UID_PRINCIPAL, account.getUid(), null);
		p.setDisplayName(account.getName());
		loginUser.getPrincipals().add(p);
		
		Set<AccountAttr> accountAttrs = account.getAccountAttrs();
		for(Iterator<AccountAttr> ite = accountAttrs.iterator();ite.hasNext();){
			AccountAttr attr =  ite.next();
			ISPrincipal attrPrincipal = new ISPrincipal(attr.getName(), attr.getValue(), attr.getSquareId());
			loginUser.getPrincipals().add(attrPrincipal);
		}
		
		return loginUser;
	}

	private void checkCredentials(String password, String digest) throws AuthenticationException {
		password = getCryptoHash(password);
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
		if(!AccountHelper.isValidPassword(password)) {
			log.error("invalid password.");
			throw new IllegalArgumentException();
		}

		Account account = dao.get(userid);
		account.setPasswordPlainText(password);
		dao.update(account);
	}

	public Collection<PrincipalDef> getPrincipalDefs() {
		return new ArrayList<PrincipalDef>();
	}

	@Override
	public void addSquareId(String userid, String squareId) {
		dao.saveAccountSquare(userid, squareId);
	}

	@Override
	public void updateDefaultSquare(String userid, String defaultSquareId) {
		if(StringUtils.isBlank(defaultSquareId)) {
			log.error("blank Square ID.");
			throw new IllegalArgumentException();
		}
		
		Account account = dao.get(userid);
		account.setDefaultSquareId(defaultSquareId);
		dao.update(account);
	}

	@Override
	public JSONObject getAccountManagerForm(String userId) throws Exception {
		Account account = (Account)this.getUser(userId);

		String formDef = this.accountManagerFormDef;
		JSONObject object = new JSONObject(formDef);

		// profile
		JSONObject profileObj = object.getJSONObject("profile");
		JSONObject firstNameObj = profileObj.getJSONObject(FIRST_NAME_PARAM);
		firstNameObj.put("title", "%{lb_ee_firstName}");
		firstNameObj.put("value", account.getGivenName());

		JSONObject givenNameObj = profileObj.getJSONObject(GIVEN_NAME_PARAM);
		givenNameObj.put("title", "%{lb_ee_familyName}");
		givenNameObj.put("value", account.getFamilyName());

		JSONObject displayNameObj = profileObj.getJSONObject(DISPLAY_NAME_PARAM);
		displayNameObj.put("title", "%{lb_ee_displayName}");
		displayNameObj.put("value", account.getName());

		JSONObject emailObj = profileObj.getJSONObject(EMAIL_PARAM);
		emailObj.put("title", "%{lb_email_address}");
		emailObj.put("value", account.getMail());

		return object;
	}

	@Override
	public String updateUserProfile(String userId, Map<String, String[]> map) {
		String displayName = map.get(DISPLAY_NAME_PARAM)[0];
		String firstName = map.get(FIRST_NAME_PARAM)[0];
		String familyName = map.get(GIVEN_NAME_PARAM)[0];
		String email = map.get(EMAIL_PARAM)[0];

		if(StringUtils.isBlank(displayName)
				|| AccountHelper.isNotValidFirstName(firstName)
				|| AccountHelper.isNotValidGivenName(familyName)
				|| AccountHelper.isNotValidEmail(email)) {
			log.error("invalid profiles.");
			throw new IllegalArgumentException();
		}

		Account account = dao.get(userId);
		account.setName(displayName);
		account.setFamilyName(familyName);
		account.setGivenName(firstName);
		account.setMail(email);
		dao.update(account);
		
		return displayName;
	}

	@Override
	public void deleteUser(String userId) throws Exception {
		dao.delete(userId);
	}

	@Override
	public IAccount registUser(String userid, String password, String firstName,
			String familyName, String defaultSquareId, String email, String ownedSquareNum, String updatePermission) throws Exception {
		String displayName = firstName + " " + familyName;
		Account account = new Account(userid, displayName, password);
		account.setFamilyName(familyName);
		account.setGivenName(firstName);
		account.setDefaultSquareId(defaultSquareId);
		account.setMySquareId(defaultSquareId);
		account.setMail(email);
		
		AccountSquare accountSquare = new AccountSquare(userid, defaultSquareId);
		AccountAttr ownedNum = new AccountAttr(userid, AccountAttributeName.OWNED_SQUARE_NUMBER, ownedSquareNum, true, null);
		AccountAttr permission = new AccountAttr(userid, AccountAttributeName.UPDATE_PERMISSION,  updatePermission, true, null);
		dao.insert(account);
		dao.insertAccountSquare(accountSquare);
		dao.insertAccountAttr(ownedNum);
		dao.insertAccountAttr(permission);

		return account;
	}

	@Override
	public void removeSquareId(String userid, String squareId) throws Exception {
		AccountSquare accountSquare = dao.getAccountSquare(userid, squareId);
		dao.deleteAccountSquare(accountSquare);
	}
	
	private String getCryptoHash(String password) throws AuthenticationException{
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
		return password;
	}
	
	public void setAccountManagerFormDef(String accountManagerFormDef) {
		this.accountManagerFormDef = accountManagerFormDef;
	}

	@Override
	public void setAccountAttribute(String userid, String name, String value, Boolean system, String squareId) {
		dao.saveAccountAttr(userid, name, value, system, squareId);
	}

	@Override
	public String getAccountAttributeValue(String userid, String name) throws Exception {
		return getAccountAttributeValue(userid, name, null);
	}

	public String getAccountAttributeValue(String userid, String name, String squareId) throws Exception {
		Map<String, Object> entity = getAccountAttribute(userid, name, squareId);
		return (String)entity.get("value");
	}

	/* all square and same key */
	@Override
	public List<Map<String, Object>> getAccountAttribute(String userid, String name) throws Exception {
		List<AccountAttr> attrList = dao.getAccountAttr(userid, name);
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();

		for(AccountAttr attr : attrList) {
			Map<String, Object> map = new HashMap<>();

			if(attr != null){
				map.put("name", attr.getName());
				map.put("value", attr.getValue());
				map.put("system", attr.getSystem());
				map.put("account", attr.getAccountId());
				map.put("squareId", attr.getSquareId());
			}
		}
		return resultList;
	}

	/* specific square and specific key */
	public Map<String, Object> getAccountAttribute(String userid, String name, String squareId) throws Exception {
		AccountAttr attr = dao.getAccountAttr(userid, name, squareId);
		Map<String, Object> map = new HashMap<>();

		if(attr != null){
			map.put("name", attr.getName());
			map.put("value", attr.getValue());
			map.put("system", attr.getSystem());
			map.put("account", attr.getAccountId());
			map.put("squareId", attr.getSquareId());
		}

		return map;
	}

	@Override
	public void deleteAccountAttribute(String userid, String squareId) throws Exception {
		List<AccountAttr> attrs = dao.getAccountAttrBySquareId(userid, squareId);

		if(attrs != null && attrs.size() > 0) {
			for(AccountAttr attr : attrs) {
				dao.deleteAccountAttr(attr);
			}
		}
	}

	@Override
	public void setAccountOwner(String userid, String value) throws Exception {
		setAccountAttribute(userid, AccountAttributeName.REGISTERED_SQUARE, value, true, null);
	}
}
