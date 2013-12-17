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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.infoscoop.dao.model.StaticTab;
import org.infoscoop.dao.model.TabAdmin;
import org.infoscoop.dao.model.TabAdminPK;
import org.infoscoop.dao.model.TabLayout;
import org.infoscoop.service.StaticTabService;
import org.infoscoop.util.SpringUtil;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * The DAO class to get and update the information of the widget.
 * 
 * @author nakata
 * 
 */
public class StaticTabDAO extends HibernateDaoSupport {

	private static Log log = LogFactory.getLog(StaticTabDAO.class);

	public static StaticTabDAO newInstance() {

		return (StaticTabDAO) SpringUtil.getContext().getBean("staticTabDAO");

	}

	public StaticTabDAO() {
	}

	public StaticTab getTab(String tabId) {
		DetachedCriteria c = DetachedCriteria.forClass(StaticTab.class);
		c.add(Expression.eq(StaticTab.PROP_ID, tabId));
		c.add(Expression.eq(StaticTab.PROP_DELETEFLAG,
				StaticTab.DELETEFLAG_FALSE));

		List<StaticTab> result = super.getHibernateTemplate().findByCriteria(c);
		return (result.size() > 0) ? (StaticTab) result.get(0) : null;
		// return (StaticTab)super.getHibernateTemplate().get(StaticTab.class,
		// tabId);
	}
	
	public void updateTab(StaticTab entity){
		super.getHibernateTemplate().update(entity);
	}
	
	/**
	 * Get all static tabs without commandBar and portalHeader. 
	 * @return
	 */
	public List getStaticTabList() {
		DetachedCriteria c = DetachedCriteria.forClass(StaticTab.class);
		c.add(Expression.eq(StaticTab.PROP_DELETEFLAG,
				StaticTab.DELETEFLAG_FALSE));
		c.add(Expression.ne(StaticTab.PROP_ID, StaticTab.COMMANDBAR_TAB_ID));
		c.add(Expression.ne(StaticTab.PROP_ID, StaticTab.PORTALHEADER_TAB_ID));

		c.createAlias(TabAdmin.REF, "ta", CriteriaSpecification.LEFT_JOIN);
		c.addOrder(Order.asc(StaticTab.PROP_TABNUMBER));

		return super.getHibernateTemplate().findByCriteria(c);
	}
	
	/**
	 * Get all static tabs with commandBar and portalHeader. 
	 * @return
	 */
	public List getAllStaicLayoutList() {
		DetachedCriteria c = DetachedCriteria.forClass(StaticTab.class);
		c.add(Expression.eq(StaticTab.PROP_DELETEFLAG,
				StaticTab.DELETEFLAG_FALSE));
		
		c.createAlias(TabAdmin.REF, "ta", CriteriaSpecification.LEFT_JOIN);
		c.addOrder(Order.asc(StaticTab.PROP_TABNUMBER));

		return super.getHibernateTemplate().findByCriteria(c);
	}

	/**
	 * get tabId list without commandbar and portalHeader.
	 * 
	 * @return
	 */
	public List getTabIdList() {
		DetachedCriteria c = DetachedCriteria.forClass(StaticTab.class);
		c.add(Expression.ne(StaticTab.PROP_ID, StaticTab.COMMANDBAR_TAB_ID));
		c.add(Expression.ne(StaticTab.PROP_ID, StaticTab.PORTALHEADER_TAB_ID));
		c.add(Expression.eq(StaticTab.PROP_DELETEFLAG,
				StaticTab.DELETEFLAG_FALSE));
		c.setProjection(Projections.property("Tabid"));
		c.addOrder(Order.asc(StaticTab.PROP_TABNUMBER));

		return super.getHibernateTemplate().findByCriteria(c);
	}

	public Collection getAllTabs(String uid) throws Exception {
		Collection tabs = (Collection) super.getHibernateTemplate()
				.findByCriteria(
						DetachedCriteria.forClass(StaticTab.class).addOrder(
								Order.asc("tabNumber")));

		return tabs;
	}
	/**
	 * @param res
	 * @return
	 */
	public Map selectMax() {
		HibernateTemplate templete = super.getHibernateTemplate();
		List staticTabs = templete.findByCriteria(DetachedCriteria.forClass(
				StaticTab.class).add(
				Expression.not(Expression.eq("Tabid", StaticTab.COMMANDBAR_TAB_ID))).add(
				Expression.not(Expression.eq("Tabid", StaticTab.PORTALHEADER_TAB_ID)))
				.setProjection(
						Projections.projectionList().add(
								Projections.max("Tabid")).add(
								Projections.max(StaticTab.PROP_TABNUMBER))));

		Map resultMap = new HashMap();
		for (int i = 0; i < staticTabs.size(); i++) {
			Object[] tablayout = (Object[]) staticTabs.get(i);
			resultMap.put("tabId", tablayout[0]);
			resultMap.put("tabNumber", tablayout[1]);
		}

		return resultMap;
	}
	

	/**
	 * @param res
	 * @return
	 */
	public String selectMaxTabId() {
		HibernateTemplate templete = super.getHibernateTemplate();
		List tabIdList = templete.findByCriteria(DetachedCriteria.forClass(
				StaticTab.class).add(
				Expression.not(Expression.eq("Tabid", StaticTab.COMMANDBAR_TAB_ID))).add(
				Expression.not(Expression.eq("Tabid", StaticTab.PORTALHEADER_TAB_ID)))
				.setProjection(
						Projections.projectionList().add(
								Projections.max("Tabid"))));
		
		String tabId = null;
		for (int i = 0; i < tabIdList.size(); i++) {
			tabId = (String)tabIdList.get(i);
		}
		
		return tabId;
	}
	
	public void saveTab(StaticTab tab) {
		super.getHibernateTemplate().save(tab);
	}

	public void deleteTab(StaticTab tab) {
		super.getHibernateTemplate().delete(tab);
	}

	public void deleteTab(String tabId) {
		super.getHibernateTemplate().delete(getTab(tabId));
	}
	

	/**
	 * Update the delete flag of the temporary data.
	 *
	 * @param res
	 * @param tabId
	 * @param deleteFlag
	 * @return
	 * @throws DataResourceException
	 */
	public void updateDeleteFlag(StaticTab staticTab, Integer deleteFlag) {
		HibernateTemplate templete = super.getHibernateTemplate();
		if(staticTab != null){
			staticTab.setDeleteflag(deleteFlag);
			templete.save(staticTab);
		}
	}	

	public void updateTabNumber(StaticTab staticTab, Integer tabNumber) {
		HibernateTemplate template = super.getHibernateTemplate();
		if (staticTab != null) {
			staticTab.setTabnumber(tabNumber);
			template.save(staticTab);
		}
	}

}
