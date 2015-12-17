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
import java.util.Map;

import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.MatchMode;
import org.infoscoop.dao.model.Account;
import org.infoscoop.dao.model.AccountSquare;
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
	
	public AccountSquare getAccountSquare(String uid, String squareId){
		List<AccountSquare> result = super.getHibernateTemplate().findByCriteria(DetachedCriteria.forClass(AccountSquare.class)
				.add(Expression.eq("Uid", uid))
				.add(Expression.eq("squareId", squareId)));
		
		if( result == null || result.size() == 0 )
			return null;
		
		return ( AccountSquare )result.get(0);
	}
	
	public void insertAccountSquare(AccountSquare accountSquare){
		super.getHibernateTemplate().save(accountSquare);
	}
	
	public void deleteAccountSquare(AccountSquare accountSquare){
		super.getHibernateTemplate().delete(accountSquare);
	}
	
	public void delete(String uid){
		Account entity = get(uid);
		super.getHibernateTemplate().delete(entity);
	}
	
	public List<Account> selectByMap(Map condition) {
		String uid = (String)condition.get("user_id");
		String name = (String)condition.get("user_name");
		String defaultSquareId = (String)condition.get("user_default_square_id");
		String givenName = (String)condition.get("user_given_name");
		String familyName = (String)condition.get("user_family_name");
		String email = (String)condition.get("user_email");
		String squareId = (String)condition.get("user_belong_square");

		DetachedCriteria criteria = DetachedCriteria.forClass(Account.class).createAlias("AccountSquares", "as", CriteriaSpecification.LEFT_JOIN);

		if(uid != null)
			criteria.add(Expression.eq("Uid", uid));

		if(name != null)
			criteria.add(Expression.eq("name", name));

		if(defaultSquareId != null)
			criteria.add(Expression.eq("defaultSquareId", defaultSquareId));

		if(givenName != null)
			criteria.add(Expression.eq("givenName", givenName));

		if(familyName != null)
			criteria.add(Expression.eq("familyName", familyName));

		if(email != null)
			criteria.add(Expression.eq("mail", email));

		if(squareId != null)
			criteria.add(Expression.eq("as.squareId", squareId));

		return super.getHibernateTemplate().findByCriteria(criteria);
	}

}
