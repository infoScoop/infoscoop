package org.infoscoop.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.infoscoop.dao.model.Group;
import org.infoscoop.dao.model.User;
import org.infoscoop.util.SpringUtil;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class GroupDAO extends HibernateDaoSupport {

	public static GroupDAO newInstance() {
		return (GroupDAO) SpringUtil.getContext().getBean("groupDAO");
	}

	public List<Group> all() {
		return super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(Group.class));
	}

	public static void main(String args[]){
		SpringUtil.initContext(new String[]{"datasource.xml", "dataaccess.xml"});
		List group = GroupDAO.newInstance().all();
		System.out.println(group);
	}

	public Group get(String groupId) {
		return super.getHibernateTemplate().get(Group.class, new Integer(groupId));
	}

	public List<Group> getJson(String query) {
		return super.getHibernateTemplate().find("from Group where name like ?", query +"%");
	}

	public void save(Group item){
		super.getHibernateTemplate().saveOrUpdate(item);
	}

}
