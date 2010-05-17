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
