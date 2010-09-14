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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.infoscoop.dao.model.MenuItem;
import org.infoscoop.dao.model.MenuTree;
import org.infoscoop.util.SpringUtil;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class MenuItemDAO extends HibernateDaoSupport {

	private static Log log = LogFactory.getLog(MenuItemDAO.class);

	public static MenuItemDAO newInstance() {
		return (MenuItemDAO) SpringUtil.getContext().getBean("menuItemDAO");
	}

	@SuppressWarnings("unchecked")
	public MenuItem get(String id) {
		List<MenuItem> items = super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(MenuItem.class).add(
						Expression.eq(MenuItem.PROP_ID, id)));
		if (items.size() == 1)
			return items.get(0);
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<MenuItem> getByParentId(String parentId) {
		if (parentId == null)
			return null;
		MenuItem parent = get(parentId);
		if (parent == null)
			return null;
		return super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(MenuItem.class).add(
						Expression.eq(MenuItem.PROP_FK_PARENT, parent))
						.addOrder(Order.asc(MenuItem.PROP_MENU_ORDER)));
	}

	@SuppressWarnings("unchecked")
	@Deprecated
	public List<MenuItem> getTree() {
		List<MenuItem> flatItems = super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(MenuItem.class).addOrder(
						Order.asc(MenuItem.PROP_MENU_ORDER)));
		return createMenuTree(flatItems, null);
	}

	@SuppressWarnings("unchecked")
	public List<MenuItem> getTree(Integer menuTreeId) {
		if (menuTreeId == null)
			return null;
		List<MenuTree> menus = super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(MenuTree.class).add(
						Expression.eq(MenuTree.PROP_ID, menuTreeId)));
		if (menus.size() == 0)
			return null;
		List<MenuItem> flatItems = super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(MenuItem.class)
						.add(
								Expression.eq(MenuItem.PROP_FK_MENU_TREE, menus
										.get(0))).addOrder(
								Order.asc(MenuItem.PROP_MENU_ORDER)));
		return createMenuTree(flatItems, null);
	}

	protected static List<MenuItem> createMenuTree(
			Collection<MenuItem> flatItems, String parentId) {
		if (flatItems == null)
			return null;
		List<MenuItem> items = new ArrayList<MenuItem>();
		for (MenuItem item : flatItems) {
			if (parentId == null && item.getFkParent() == null)
				items.add(item);
			else if (item.getFkParent() != null
					&& item.getFkParent().getId().equals(parentId))
				items.add(item);
		}
		for (MenuItem item : items) {
			item.setChildItems(createMenuTree(flatItems, item.getId()));
		}
		return items;
	}

	public void save(MenuItem item) {
		super.getHibernateTemplate().saveOrUpdate(item);
	}

	public void delete(String id) {
		MenuItem item = get(id);
		if (item != null)
			super.getHibernateTemplate().delete(item);
	}
}
