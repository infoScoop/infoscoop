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

import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.infoscoop.dao.model.Account;
import org.infoscoop.dao.model.AccountAttr;
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
		super.getHibernateTemplate().flush();
	}

	public List<Account> selectByName(String name){
		return super.getHibernateTemplate().findByCriteria(DetachedCriteria.forClass(Account.class).add(Expression.like("name", name, MatchMode.START)));
	}

	public List<AccountSquare> getAccountSquares(String uid){
		List<AccountSquare> result = super.getHibernateTemplate().findByCriteria(DetachedCriteria.forClass(AccountSquare.class)
				.add(Expression.eq("accountId", uid))
				.addOrder(Order.asc(AccountSquare.PROP_CREATED)));

		if( result == null || result.size() == 0 )
			return null;

		return result;
	}

	public AccountSquare getAccountSquare(String uid, String squareId){
		List<AccountSquare> result = super.getHibernateTemplate().findByCriteria(DetachedCriteria.forClass(AccountSquare.class)
				.add(Expression.eq("accountId", uid))
				.add(Expression.eq("squareId", squareId)));
		
		if( result == null || result.size() == 0 )
			return null;
		
		return ( AccountSquare )result.get(0);
	}
	
	public void insertAccountSquare(AccountSquare accountSquare){
		super.getHibernateTemplate().saveOrUpdate(accountSquare);
	}

	public void saveAccountSquare(String uid, String squareId) {
		AccountSquare square = getAccountSquare(uid, squareId);
		if(square == null)
			square = new AccountSquare(uid, squareId);
		super.getHibernateTemplate().saveOrUpdate(square);
		super.getHibernateTemplate().flush();
	}

	public void deleteAccountSquare(AccountSquare accountSquare){
		super.getHibernateTemplate().delete(accountSquare);
	}

	public List<AccountAttr> getAccountAttrList(String uid) {
		List<AccountAttr> result = super.getHibernateTemplate().findByCriteria(DetachedCriteria.forClass(AccountAttr.class)
				.add(Expression.eq(AccountAttr.PROP_UID, uid)));

		if(result == null || result.size() == 0)
			return null;

		return result;
	}

	public List<AccountAttr> getAccountAttr(String uid, String key) {
		List<AccountAttr> result = super.getHibernateTemplate().findByCriteria(DetachedCriteria.forClass(AccountAttr.class)
				.add(Expression.eq(AccountAttr.PROP_UID, uid))
				.add(Expression.eq(AccountAttr.PROP_NAME, key)));

		if(result == null || result.size() == 0)
			return null;

		return result;
	}

	public List<AccountAttr> getAccountAttrListBySquareId(String squareId) {
		DetachedCriteria detachedCriteria = DetachedCriteria.forClass(AccountAttr.class);
		if (squareId != null) {
			detachedCriteria.add(Expression.eq(AccountAttr.PROP_SQUARE_ID, squareId));
		} else {
			detachedCriteria.add(Expression.isNull(AccountAttr.PROP_SQUARE_ID));
		}

		List<AccountAttr> result = super.getHibernateTemplate().findByCriteria(detachedCriteria);
		if(result == null || result.size() == 0)
			return null;

		return result;
	}

	public List<AccountAttr> getAccountAttrBySquareId(String uid, String squareId) {
		DetachedCriteria detachedCriteria = DetachedCriteria.forClass(AccountAttr.class)
				.add(Expression.eq(AccountAttr.PROP_UID, uid));

		if (squareId != null) {
			detachedCriteria.add(Expression.eq(AccountAttr.PROP_SQUARE_ID, squareId));
		} else {
			detachedCriteria.add(Expression.isNull(AccountAttr.PROP_SQUARE_ID));
		}
		List<AccountAttr> result = super.getHibernateTemplate().findByCriteria(detachedCriteria);

		if(result == null || result.size() == 0)
			return null;

		return result;
	}

	public AccountAttr getAccountAttr(String uid, String key, String squareId, Boolean system) {
		DetachedCriteria detachedCriteria = DetachedCriteria.forClass(AccountAttr.class)
				.add(Expression.eq(AccountAttr.PROP_UID, uid))
				.add(Expression.eq(AccountAttr.PROP_NAME, key))
				.add(Expression.eq(AccountAttr.PROP_SYSTEM, system));

		if (squareId != null) {
			detachedCriteria.add(Expression.eq(AccountAttr.PROP_SQUARE_ID, squareId));
		} else {
			detachedCriteria.add(Expression.isNull(AccountAttr.PROP_SQUARE_ID));
		}
		List<AccountAttr> result = super.getHibernateTemplate().findByCriteria(detachedCriteria);

		if(result == null || result.size() == 0)
			return null;

		return result.get(0);
	}

	public void insertAccountAttr(AccountAttr accountAttr) {
		super.getHibernateTemplate().saveOrUpdate(accountAttr);
	}

	public void saveAccountAttr(String uid, String name, String value, Boolean system, String squareId) {
		AccountAttr attr = getAccountAttr(uid, name, squareId, system);
		if(attr == null)
			attr = new AccountAttr(uid, name, value, system, squareId);
		attr.setValue(value);
		super.getHibernateTemplate().saveOrUpdate(attr);
		super.getHibernateTemplate().flush();
	}

	public void deleteAccountAttr(AccountAttr accountAttr) {
		super.getHibernateTemplate().delete(accountAttr);
	}

	public void delete(String uid){
		Account entity = get(uid);
		super.getHibernateTemplate().delete(entity);
	}
	
	public List<Account> selectByMap(Map condition) {
		return selectByMap(condition, null, null);
	}
	
	public int selectCountByMap(Map condition) {
		DetachedCriteria criteria = createSearchCriteria(condition);
		
		List list = super.getHibernateTemplate().findByCriteria(criteria);
		return (int)list.get(0);
	}
	
	public List<Account> selectByMapWithAttr(Map condition) {
		DetachedCriteria criteria = createSearchCriteria(condition);
		criteria.createAlias("AccountAttrs", "attrs", CriteriaSpecification.LEFT_JOIN);
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		return super.getHibernateTemplate().findByCriteria(criteria);
	}
	
	public List<Account> selectByMap(Map condition, Integer pageSize, Integer pageNum) {
		/*
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
			criteria.add(Expression.like("name", "%" + name + "%"));

		if(defaultSquareId != null)
			criteria.add(Expression.eq("defaultSquareId", defaultSquareId));

		if(givenName != null)
			criteria.add(Expression.eq("givenName", givenName));

		if(familyName != null)
			criteria.add(Expression.eq("familyName", familyName));

		if(email != null)
			criteria.add(Expression.like("mail", "%" + email + "%"));

		if(squareId != null)
			criteria.add(Expression.eq("as.squareId", squareId));
		*/
		DetachedCriteria criteria = createSearchCriteria(condition);
		
		List<Account> result;
		if(pageSize != null && pageNum != null){
			result = super.getHibernateTemplate().findByCriteria(criteria, pageNum*pageSize, pageSize);
		}else{
			result = super.getHibernateTemplate().findByCriteria(criteria);
		}
		return result;
	}

	private DetachedCriteria createSearchCriteria(Map condition){
		String uid = (String)condition.get("user_id");
		String uidLike = (String)condition.get("user_id_like");
		String name = (String)condition.get("user_name");
		String defaultSquareId = (String)condition.get("user_default_square_id");
		String givenName = (String)condition.get("user_given_name");
		String familyName = (String)condition.get("user_family_name");
		String email = (String)condition.get("user_email");
		String squareId = (String)condition.get("user_belong_square");

		DetachedCriteria criteria = DetachedCriteria.forClass(Account.class).createAlias("AccountSquares", "as", CriteriaSpecification.LEFT_JOIN);
		
		if(uid != null)
			criteria.add(Expression.eq("Uid", uid));
		
		if(uidLike != null)
			criteria.add(Expression.like("Uid", "%" + uidLike + "%"));

		if(name != null)
			criteria.add(Expression.like("name", "%" + name + "%"));

		if(defaultSquareId != null)
			criteria.add(Expression.eq("defaultSquareId", defaultSquareId));

		if(givenName != null)
			criteria.add(Expression.eq("givenName", givenName));

		if(familyName != null)
			criteria.add(Expression.eq("familyName", familyName));

		if(email != null)
			criteria.add(Expression.like("mail", "%" + email + "%"));

		if(squareId != null)
			criteria.add(Expression.eq("as.squareId", squareId));

		return criteria;
	}
}
