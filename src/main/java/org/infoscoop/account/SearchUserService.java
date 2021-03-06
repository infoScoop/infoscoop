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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SearchUserService {
	private IAccountManager module;
	private static boolean isAvailable = true;

	public void setAccountManager(IAccountManager manager){
		this.module = manager;
	}

	/**
	 * Search User by condition startsWith
	 * @param searchConditionMap
	 * @return
	 * @throws Exception
	 */
	public List search(Map searchConditionMap) throws Exception {
		return this.module.searchUser(searchConditionMap);
	}

	// add the interface of search by uid
	public IAccount getUser(String uid) throws Exception {
		return this.module.getUser(uid);
	}

	/**
	 * return whether SearchUserService is possible or not.
	 * @return
	 */
	public static boolean isAvailable() {
		return isAvailable;
	}

	/**
	 * mark that SearchUserService is possible.
	 */
	public static void setNotAvailable() {
		isAvailable = false;
	}

	public static void main(String args[]) throws Exception{
		ApplicationContext contxt =  new ClassPathXmlApplicationContext("services.xml");

		SearchUserService service = (SearchUserService)contxt.getBean("searchUserService");
		Map map = new HashMap();
		map.put("name", "B");
		map.put("mail", null);
		map.put("org", null);
		Collection users = service.search(map);

		for(Iterator it = users.iterator(); it.hasNext();){
			System.out.println(it.next());
		}

	}

}
