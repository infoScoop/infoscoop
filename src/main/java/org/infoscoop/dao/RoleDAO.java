package org.infoscoop.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.infoscoop.account.DomainManager;
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
				DetachedCriteria.forClass(Role.class).add(
						Expression.eq(Role.PROP_FK_DOMAIN_ID, DomainManager.getContextDomainId())));
	}

	public void save(Role item) {
		super.getHibernateTemplate().saveOrUpdate(item);
	}

	public Role get(String roleId) {
		return super.getHibernateTemplate().get(Role.class, Integer.valueOf(roleId));
	}

	public void delete(String roleId) {
		super.getHibernateTemplate().bulkUpdate("delete from Role where id = ?", new Object[]{Integer.valueOf(roleId)});
	}

	public RolePrincipal getRolePrincipal(String rolePrincipalId) {
		return super.getHibernateTemplate().get(RolePrincipal.class, Integer.valueOf(rolePrincipalId));
	}

	public void deleteRolePrincipal(RolePrincipal rolePrincipal) {
		super.getHibernateTemplate().delete(rolePrincipal);
	}

	public void updatePrindipcal(RolePrincipal rolePrincipal) {
		super.getHibernateTemplate().update(rolePrincipal);
	}


}
