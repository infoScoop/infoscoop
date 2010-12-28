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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.infoscoop.account.DomainManager;
import org.infoscoop.dao.model.MenuTree;
import org.infoscoop.util.SpringUtil;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class MenuTreeDAO extends HibernateDaoSupport {

	private static Log log = LogFactory.getLog(MenuTreeDAO.class);

	public static MenuTreeDAO newInstance() {
		return (MenuTreeDAO) SpringUtil.getContext().getBean("menuTreeDAO");
	}

	@SuppressWarnings("unchecked")
	public MenuTree get(int id) {
		List<MenuTree> menus = super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(MenuTree.class).add(
						Expression.eq(MenuTree.PROP_ID, id)).add(
						Expression.eq(MenuTree.PROP_FK_DOMAIN_ID, DomainManager.getContextDomainId())));
		if (menus.size() == 1)
			return menus.get(0);
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<MenuTree> getTopMenus() {
		return super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(MenuTree.class).add(
						Expression.eq(MenuTree.PROP_FK_DOMAIN_ID, DomainManager
								.getContextDomainId())).add(
						Expression.eq(MenuTree.PROP_TOP, 1)).addOrder(
						Order.asc(MenuTree.PROP_ORDER_INDEX)));
	}
	
	@SuppressWarnings("unchecked")
	public List<MenuTree> getSideMenus() {
		return super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(MenuTree.class).add(
						Expression.eq(MenuTree.PROP_FK_DOMAIN_ID, DomainManager
								.getContextDomainId())).add(
						Expression.eq(MenuTree.PROP_SIDE, 1)).addOrder(
						Order.asc(MenuTree.PROP_ORDER_INDEX)));
	}

	@SuppressWarnings("unchecked")
	public List<MenuTree> all() {
		List<MenuTree> menus = super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(MenuTree.class).add(
						Expression.eq(MenuTree.PROP_FK_DOMAIN_ID, DomainManager
								.getContextDomainId())).addOrder(
						Order.asc(MenuTree.PROP_ORDER_INDEX)));
		return menus;
	}
	
	@SuppressWarnings("unchecked")
	public int getMaxOrderIndex() {
		String queryString = "select max(OrderIndex) from MenuTree where fk_domain_id = ?";
		List<Integer> result = super.getHibernateTemplate().find(queryString,
				DomainManager.getContextDomainId());
		if (result.get(0) == null)
			return -1;
		else
			return result.get(0).intValue();
	}
	
	public void save(MenuTree menu) {
		super.getHibernateTemplate().saveOrUpdate(menu);
	}

	public void delete(int id) {
		MenuTree item = get(id);
		if (item != null)
			super.getHibernateTemplate().delete(item);
	}
}
