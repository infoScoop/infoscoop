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

package org.infoscoop.account;

import org.json.JSONObject;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.security.auth.Subject;

/**
 * @author hr-endoh
 *
 */
public interface IAccountManager {

	/**
	 *
	 */
	public Collection<PrincipalDef> getPrincipalDefs();

	/**
	 * @param userid
	 * @param password
	 * @throws AuthenticationException
	 */
	public void login(String userid, String password) throws AuthenticationException;

	/**
	 * @return
	 * @throws Exception 
	 */
	public Subject getSubject(String userid) throws Exception;
	
	/**
	 * @return Return true if changePassword method  is implemented.
	 */
	public boolean enableChangePassword();

	/**
	 * @param userid
	 * @param password
	 * @param oldPassword
	 */
	public void changePassword(String userid, String password, String oldPassword) throws AuthenticationException;

	/**
	 * @param searchConditionMap<String,String> : Retrieval key name(name,email,org,etc...), value
	 * @return
	 * @throws Exception
	 */
	public List<IAccount> searchUser(Map<String, String> searchConditionMap) throws Exception;

	public List<IAccount> searchUser(Map<String, String> searchConditionMap, Integer pageSize, Integer pageNum) throws Exception;

	// 2007.11.29 koike
	// add the interface of search by uiduid
	/**
	 * @param uid
	 * @return
	 * @throws Exception
	 */
	public IAccount getUser(String uid) throws Exception;

	public void updateUser(Map<String, Object> user) throws Exception;

	/**
	 * @param userid
	 * @param password
	 */
	public void updatePassword(String userid, String password) throws Exception;

	public void addSquareId(String userid, String squareId) throws Exception;

	public void updateDefaultSquare(String userid, String defaultSquareId) throws Exception;

	public void updateMySquareId(String userid, String mySquareId) throws Exception;

	public JSONObject getAccountManagerForm(String userId) throws Exception;

	public String updateUserProfile(String userId, Map<String, String[]> map) throws Exception;
	
	public void deleteUser(String userId) throws Exception;
	
	public IAccount registUser(String userid, String password, String firstName, String familyName, String defaultSquareId, String email, String ownedSquareNum, String updatePermission, Boolean requirePasswordReset) throws Exception;
	public IAccount registUser(String userid, String password, String firstName, String familyName, String displayName, String defaultSquareId, String email, String ownedSquareNum, String updatePermission, Boolean requirePasswordReset) throws Exception;

	public void removeSquareId(String userid, String squareId) throws Exception;

	public void setAccountAttribute(String userid, String name, String value, Boolean system, String squareId) throws Exception;

	public void updateAccountAttribute(String userid, Map<String, String[]> map) throws Exception;

	public String getAccountAttributeValue(String userid, String name) throws Exception;

	public List<Map<String, Object>> getAccountAttribute(String userid, String name) throws Exception;

	public void deleteAccountAttribute(String userid, String squareId) throws Exception;

	public void deleteAccountAttributeBySquareId(String squareId) throws Exception;

	public void setAccountOwner(String userid, String value) throws Exception;
}
