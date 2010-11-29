package org.infoscoop.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.infoscoop.account.DomainManager;
import org.infoscoop.dao.model.Group;
import org.infoscoop.util.SpringUtil;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class GroupDAO extends HibernateDaoSupport {

	public static GroupDAO newInstance() {
		return (GroupDAO) SpringUtil.getContext().getBean("groupDAO");
	}

	@SuppressWarnings("unchecked")
	public List<Group> all() {
		return super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(Group.class).add(
						Expression.eq(Group.PROP_FK_DOMAIN_ID, DomainManager.getContextDomainId())));
	}

	public Group get(String groupId) {
		return super.getHibernateTemplate().get(Group.class, groupId);
	}

	@SuppressWarnings("unchecked")
	public List<Group> selectByName(String name) {
		return super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(Group.class).add(
						Expression.like(Group.PROP_NAME,name)).add(
								Expression.eq(Group.PROP_FK_DOMAIN_ID,DomainManager.getContextDomainId())));
	}

	public void save(Group item){
		super.getHibernateTemplate().saveOrUpdate(item);
	}

	@SuppressWarnings("unchecked")
	public Group getByEmail(String email) {
		List<Group> groupList = super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(Group.class).add(Expression.eq( Group.PROP_EMAIL,email )).add(
						Expression.eq(Group.PROP_FK_DOMAIN_ID, DomainManager.getContextDomainId())));
		if(groupList.isEmpty())
			return null;
		else
			return groupList.get(0);
	}

	@SuppressWarnings("unchecked")
	public List<String> getGroupIds() {
		String queryString = "select distinct g.Id from Group g";
		return super.getHibernateTemplate().find(queryString);
	}
	
}
