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

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.infoscoop.account.DomainManager;
import org.infoscoop.dao.model.TabTemplate;
import org.infoscoop.dao.model.TabTemplatePersonalizeGadget;
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
				DetachedCriteria.forClass(TabTemplate.class).add(
						Expression.eq(TabTemplate.PROP_TEMP, Integer.valueOf(0))).add(
								Expression.eq(TabTemplate.PROP_FK_DOMAIN_ID, DomainManager.getContextDomainId())).addOrder(
								Order.asc(TabTemplate.PROP_ORDER_INDEX)));
	}

	@SuppressWarnings("unchecked")
	public List<TabTemplate> getHisotry(String tabId) {
		return super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(TabTemplate.class).add(
						Expression.eq(TabTemplate.PROP_TAB_ID, tabId)).add(
						Expression
								.eq(TabTemplate.PROP_TEMP, Integer.valueOf(2)))
						.add(
								Expression.eq(TabTemplate.PROP_FK_DOMAIN_ID,
										DomainManager.getContextDomainId()))
						.addOrder(Order.desc(TabTemplate.PROP_UPDATED_AT)));
	}

	@SuppressWarnings("unchecked")
	public List<TabTemplate> findByTabId(String tabId) {
		return super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(TabTemplate.class).add(
						Expression.ne(TabTemplate.PROP_TAB_ID, tabId)).add(
						Expression
								.ne(TabTemplate.PROP_TEMP, Integer.valueOf(1)))
						.add(
								Expression.eq(TabTemplate.PROP_FK_DOMAIN_ID,
										DomainManager.getContextDomainId()))
						.addOrder(Order.desc(TabTemplate.PROP_UPDATED_AT)));
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
	
	public int getMaxOrderIndex(){
		String queryString = "select max(OrderIndex) from TabTemplate";
		List<Integer> result = super.getHibernateTemplate().find(queryString);
		if(result.get(0) == null)
			return -1;
		else
			return result.get(0).intValue();
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

	public TabTemplatePersonalizeGadget getColumnWidgetBySibling(String tabId,
			String siblingId, Integer columnNum) {
		
		return (TabTemplatePersonalizeGadget) super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(TabTemplatePersonalizeGadget.class)
				.add(Expression.eq(TabTemplatePersonalizeGadget.PROP_FK_TAB_TEMPLATE, tabId))
				.add(Expression.eq(TabTemplatePersonalizeGadget.PROP_ID, siblingId))
				.add(Expression.eq(TabTemplatePersonalizeGadget.PROP_COLUMN_NUM, columnNum))).get(0);
	}
	
	@SuppressWarnings("unchecked")
	public TabTemplate getByTabId(String tabId){
		List<TabTemplate> results = super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(TabTemplate.class)
				.add(Expression.eq(TabTemplate.PROP_TAB_ID, tabId)).add(Expression.eq(TabTemplate.PROP_TEMP, Integer.valueOf(0)))
		);
		if(results.isEmpty())
			return null;
		else
			return results.get(0);
	}

	public void delete(TabTemplate tab) {
		super.getHibernateTemplate().delete(tab);
		super.getHibernateTemplate().flush();
	}

	public void deleteParsonalizeGadget(Integer id) {
		super.getHibernateTemplate().bulkUpdate("delete from TabTemplatePersonalizeGadget where id = ?", new Object[]{id});
	}

}
