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

import java.util.Collection;

import javax.security.auth.Subject;

import org.infoscoop.util.SpringUtil;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

public class AuthenticationService {

	private IAccountManager manager;
	
	public static AuthenticationService getInstance() {
		try {
			return (AuthenticationService) SpringUtil
					.getBean("authenticationService");
		} catch (NoSuchBeanDefinitionException e) {
			return null;
		}
	}

	public void setAccountManager(IAccountManager manager){
		this.manager = manager;
	}

	public void login(String userid, String password) throws AuthenticationException{
		this.manager.login(userid, password);
	}
	
	public Subject getSubject(String userid) throws Exception{
		return this.manager.getSubject(userid);
	}

	public boolean enableChangePassword(){
		return this.manager.enableChangePassword();
	}

	public void changePassword(String userid, String password, String oldPassword) throws AuthenticationException{
		this.manager.changePassword(userid, password, oldPassword);
	}

	public Collection<PrincipalDef> getPrincipalDefs(){
		return this.manager.getPrincipalDefs();
	}

	public IAccountManager getAccountManager() {
		return this.manager;
	}
}
