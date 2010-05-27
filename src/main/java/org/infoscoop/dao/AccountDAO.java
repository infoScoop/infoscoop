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

package org.infoscoop.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.MatchMode;
import org.infoscoop.dao.model.Account;
import org.infoscoop.util.SpringUtil;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class AccountDAO extends HibernateDaoSupport {

	public static AccountDAO newInstance() {
		return (AccountDAO) SpringUtil.getBean("accountDAO");
	}

	public void insert(Account account){
		super.getHibernateTemplate().save(account);

	}

	public Account get(String uid){
		return (Account)super.getHibernateTemplate().get(Account.class, uid);

	}

	public void update(Account account){
		super.getHibernateTemplate().update(account);

	}

	public List<Account> selectByName(String name){
		return super.getHibernateTemplate().findByCriteria(DetachedCriteria.forClass(Account.class).add(Expression.like("name", name, MatchMode.START)));
	}
}
