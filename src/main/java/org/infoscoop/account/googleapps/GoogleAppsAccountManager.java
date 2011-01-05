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

package org.infoscoop.account.googleapps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.security.auth.Subject;

import org.infoscoop.account.AuthenticationException;
import org.infoscoop.account.IAccount;
import org.infoscoop.account.IAccountManager;
import org.infoscoop.account.PrincipalDef;
import org.infoscoop.acl.ISPrincipal;
import org.infoscoop.dao.DomainDAO;
import org.infoscoop.dao.UserDAO;
import org.infoscoop.dao.model.Account;
import org.infoscoop.dao.model.Domain;
import org.infoscoop.dao.model.User;

/**
 * @author a-kimura
 *
 */
public class GoogleAppsAccountManager implements IAccountManager{

	private UserDAO dao;
	private DomainDAO domainDao;
	
	public void setUserDAO(UserDAO dao){
		this.dao = dao;
	}

	public void setDomainDAO(DomainDAO domainDao){
		this.domainDao = domainDao;
	}
	
	public IAccount getUser(String uid, String domainName) throws Exception {
		Domain domain = this.domainDao.getByName(domainName);
		return dao.getByEmail(uid, domain.getId());
	}

	/* (non-Javadoc)
	 * @see org.infoscoop.searchuid.ISearchModule#search(java.util.Map)
	 */
	public List searchUser(Map searchConditionMap) throws Exception {
		String name = (String)searchConditionMap.get("user_name");
		return this.dao.selectByName(name);
	}

	public void login(String userid, String password, String domain) throws AuthenticationException {
	}
	
	public Subject getSubject(String userid, String domainName) throws Exception {
		IAccount account = (IAccount)this.getUser(userid, domainName);
		if(account == null){
			throw new AuthenticationException(userid + " is not found.");
		}
		Subject loginUser = new Subject();
		ISPrincipal p = new ISPrincipal(ISPrincipal.UID_PRINCIPAL, account.getUid());
		p.setDisplayName(account.getName());
		loginUser.getPrincipals().add(p);
		ISPrincipal domain = new ISPrincipal(ISPrincipal.DOMAIN_PRINCIPAL,((User)account).getFkDomainId().toString());
		domain.setDisplayName(domainName);
		loginUser.getPrincipals().add(domain);
		return loginUser;
	}

	public boolean enableChangePassword(){
		return true;
	}

	public void changePassword(String userid, String password,
			String oldPassword, String domain) throws AuthenticationException {
	}

	public Collection<PrincipalDef> getPrincipalDefs() {
		return new ArrayList<PrincipalDef>();
	}

	public void changePassword(String userid, String password,
			String oldPassword) throws AuthenticationException {
		// TODO Auto-generated method stub
		
	}

	public void login(String userid, String password)
			throws AuthenticationException {
		// TODO Auto-generated method stub
		
	}
}
