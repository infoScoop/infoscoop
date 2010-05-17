package org.infoscoop.account;

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

	// 2007.11.29 koike
	// add the interface of search by uiduid
	/**
	 * @param uid
	 * @return
	 * @throws Exception
	 */
	public IAccount getUser(String uid) throws Exception;
}
