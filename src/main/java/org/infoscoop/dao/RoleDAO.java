package org.infoscoop.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.infoscoop.dao.model.Role;
import org.infoscoop.dao.model.RolePrincipal;
import org.infoscoop.util.SpringUtil;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class RoleDAO extends HibernateDaoSupport {

	public static RoleDAO newInstance() {
		return (RoleDAO) SpringUtil.getContext().getBean("roleDAO");
	}

	public List<Role> all() {
		return super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(Role.class));
	}

	public void save(Role item) {
		super.getHibernateTemplate().saveOrUpdate(item);
	}

	public static void main(String args[]){
		SpringUtil.initContext(new String[]{"datasource.xml", "dataaccess.xml"});
		List roles = RoleDAO.newInstance().all();
		System.out.println(roles);
	}

	public Role get(String roleId) {
		return super.getHibernateTemplate().get(Role.class, new Integer(roleId));
	}

	public void delete(String roleId) {
		super.getHibernateTemplate().bulkUpdate("delete from Role where id = ?", new Object[]{new Integer(roleId)});
	}

	public RolePrincipal getRolePrincipal(String rolePrincipalId) {
		return super.getHibernateTemplate().get(RolePrincipal.class, new Integer(rolePrincipalId));
	}

	public void deleteRolePrincipal(RolePrincipal rolePrincipalId) {
		super.getHibernateTemplate().delete(rolePrincipalId);
	}

	public void updatePrindipcal(RolePrincipal rolePrincipal) {
		super.getHibernateTemplate().update(rolePrincipal);
	}


}
