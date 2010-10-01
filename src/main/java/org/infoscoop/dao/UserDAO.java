package org.infoscoop.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.infoscoop.dao.model.Gadget;
import org.infoscoop.dao.model.Group;
import org.infoscoop.dao.model.User;
import org.infoscoop.util.SpringUtil;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class UserDAO extends HibernateDaoSupport {

	public static UserDAO newInstance() {
		return (UserDAO) SpringUtil.getContext().getBean("userDAO");
	}

	public List<User> all() {
		return super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(User.class));
	}

	public static void main(String args[]){
		SpringUtil.initContext(new String[]{"datasource.xml", "dataaccess.xml"});
		List user = UserDAO.newInstance().all();
		System.out.println(user);
	}

	public User get(String id) {
		return super.getHibernateTemplate().get(User.class, new Integer(id));
	}
	
	public List<User> getByName(String name){
		return super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(User.class).add(Expression.eq( User.PROP_NAME,name )));
	}

	public List<User> selectByName(String name) {
		return super.getHibernateTemplate().find("from User where name like ?", name +"%");
	}

	public void save(User item){
		super.getHibernateTemplate().saveOrUpdate(item);
	}

}
