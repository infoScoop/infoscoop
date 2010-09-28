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
import org.infoscoop.dao.model.MenuItem;
import org.infoscoop.dao.model.MenuPosition;
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
						Expression.eq(MenuTree.PROP_ID, id)));
		if (menus.size() == 1)
			return menus.get(0);
		return null;
	}

	@SuppressWarnings("unchecked")
	private MenuPosition getPosition(String position) {
		List<MenuPosition> positions = super.getHibernateTemplate()
				.findByCriteria(
						DetachedCriteria.forClass(MenuPosition.class).add(
								Expression.eq(MenuPosition.PROP_ID, position)));
		if (positions.size() == 1)
			return positions.get(0);
		return null;
	}
	
	public MenuTree getByPosition(String position) {
		MenuPosition pos = getPosition(position);
		if (pos != null)
			return pos.getFkMenuTree();
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<MenuTree> all() {
		List<MenuTree> menus = super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(MenuTree.class));
		// join
		List<MenuPosition> poss = super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(MenuPosition.class));
		for (MenuTree menu : menus) {
			for (MenuPosition pos : poss) {
				if (menu.equals(pos.getFkMenuTree()))
					menu.addPosition(pos.getId());
			}
		}
		return menus;
	}
	
	public void updatePosition(MenuTree menu, String position) {
		MenuPosition pos = getPosition(position);
		if (pos == null)
			pos = new MenuPosition(position);
		pos.setFkMenuTree(menu);
		super.getHibernateTemplate().saveOrUpdate(pos);
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
