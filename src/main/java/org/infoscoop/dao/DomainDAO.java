package org.infoscoop.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.MatchMode;
import org.infoscoop.dao.model.Account;
import org.infoscoop.dao.model.Domain;
import org.infoscoop.util.SpringUtil;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class DomainDAO extends HibernateDaoSupport {
	public static DomainDAO newInstance() {
		return (DomainDAO) SpringUtil.getBean("domainDAO");
	}

	public Domain get(Integer domainId) {
		List<Domain> results = super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(Domain.class).add(
						Expression.eq(Domain.PROP_ID, domainId)));
		if (results.isEmpty())
			return null;
		else
			return results.get(0);
	}

	public Domain getByName(String domainName) {
		List<Domain> results = super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(Domain.class).add(
						Expression.eq(Domain.PROP_NAME, domainName)));
		
		if(results.isEmpty())
			return null;
		else
			return results.get(0);
	}

	public void save(Domain domain) {
		super.getHibernateTemplate().save(domain);
	}
	
	public static void main(String args[]){
		SpringUtil.initContext(new String[]{"datasource.xml","dataaccess.xml"});
		System.out.println(DomainDAO.newInstance().getByName("infoscoop.org"));
		
	}
}
