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
import org.infoscoop.dao.model.TabTemplate;
import org.infoscoop.dao.model.TabTemplateStaticGadget;
import org.infoscoop.util.SpringUtil;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class TabTemplateStaticGadgetDAO extends HibernateDaoSupport {

	private static Log log = LogFactory.getLog(TabTemplateStaticGadgetDAO.class);

	public static TabTemplateStaticGadgetDAO newInstance() {
		return (TabTemplateStaticGadgetDAO) SpringUtil.getContext().getBean("tabTemplateStaticGadgetDAO");
	}

	@SuppressWarnings("unchecked")
	public List<TabTemplateStaticGadget> all() {
		return super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(TabTemplateStaticGadget.class));
	}


	@SuppressWarnings("unchecked")
	public TabTemplateStaticGadget get(String id) {
		List<TabTemplateStaticGadget> staticGadgets = super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(TabTemplateStaticGadget.class).add(
						Expression.eq(TabTemplateStaticGadget.PROP_ID, Integer.valueOf(id))));
		if (staticGadgets.size() == 1)
			return staticGadgets.get(0);
		return null;
	}
	
	public void save(TabTemplateStaticGadget staticGadget) {
		super.getHibernateTemplate().saveOrUpdate(staticGadget);
	}

	public void delete(TabTemplateStaticGadget staticGadget) {
		super.getHibernateTemplate().delete(staticGadget);
	}
		
	@SuppressWarnings("unchecked")
	public TabTemplateStaticGadget getByContainerId(String containerId, TabTemplate tabTemplate) {
		List<TabTemplateStaticGadget> staticGadgets = super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(TabTemplateStaticGadget.class)
				.add(Expression.eq(TabTemplateStaticGadget.PROP_CONTAINER_ID, containerId))
				.add(Expression.eq(TabTemplateStaticGadget.PROP_FK_TAB_TEMPLATE, tabTemplate)));
		if(staticGadgets.size() == 1)
			return staticGadgets.get(0);
		return null;
	}
	
	public TabTemplateStaticGadget getByTabTemplate(TabTemplate tab){
		return (TabTemplateStaticGadget) super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(TabTemplateStaticGadget.class)
				.add(Expression.eq(TabTemplateStaticGadget.PROP_FK_TAB_TEMPLATE, tab))).get(0);
	}
}
