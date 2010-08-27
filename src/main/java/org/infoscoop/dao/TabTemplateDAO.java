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
import org.infoscoop.dao.model.GadgetInstance;
import org.infoscoop.dao.model.TabTemplate;
import org.infoscoop.dao.model.TabTemplateParsonalizeGadget;
import org.infoscoop.util.SpringUtil;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class TabTemplateDAO extends HibernateDaoSupport {

	private static Log log = LogFactory.getLog(TabTemplateDAO.class);

	public static TabTemplateDAO newInstance() {
		return (TabTemplateDAO) SpringUtil.getContext().getBean("tabTemplateDAO");
	}

	@SuppressWarnings("unchecked")
	public List<TabTemplate> all() {
		return super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(TabTemplate.class));
	}


	@SuppressWarnings("unchecked")
	public TabTemplate get(String id) {
		List<TabTemplate> items = super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(TabTemplate.class).add(
						Expression.eq(TabTemplate.PROP_ID, Integer.valueOf(id))));
		if (items.size() == 1)
			return items.get(0);
		return null;
	}
	
	/*	

	@SuppressWarnings("unchecked")
	public List<TabTemplate> getChildItems(String parentId) {
		return super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(TabTemplate.class).add(
						Expression.eq(TabTemplate.PROP_PARENT_ID, parentId)));
	}
*/
	public void save(TabTemplate item) {
		super.getHibernateTemplate().saveOrUpdate(item);
	}

	public TabTemplateParsonalizeGadget getColumnWidgetBySibling(String tabId,
			String siblingId, Integer columnNum) {
		
		return (TabTemplateParsonalizeGadget) super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(TabTemplateParsonalizeGadget.class)
				.add(Expression.eq(TabTemplateParsonalizeGadget.PROP_FK_TAB_TEMPLATE, tabId))
				.add(Expression.eq(TabTemplateParsonalizeGadget.PROP_ID, siblingId))
				.add(Expression.eq(TabTemplateParsonalizeGadget.PROP_COLUMN_NUM, columnNum))).get(0);
	}

	public void delete(TabTemplate tab) {
		super.getHibernateTemplate().delete(tab);
	}
	
}
