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

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MultiHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.infoscoop.dao.model.StaticTab;
import org.infoscoop.dao.model.TABLAYOUTPK;
import org.infoscoop.dao.model.TabLayout;
import org.infoscoop.service.TabLayoutService;
import org.infoscoop.util.SpringUtil;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class TabLayoutDAO extends HibernateDaoSupport {

	private static Log log = LogFactory.getLog(TabLayoutDAO.class);

	public TabLayoutDAO() {
	}

	public static TabLayoutDAO newInstance() {

		return (TabLayoutDAO) SpringUtil.getContext().getBean("tabLayoutDAO");

	}

	/**
	 * Get unique record.
	 *
	 * @param tabId
	 * @param roleOrder
	 * @param temp
	 * @return
	 * @throws Exception
	 */
	public TabLayout selectByPK(String tabId, Integer roleOrder, Integer temp, String squareId) throws Exception {
		return super.getHibernateTemplate().get(TabLayout.class, new TABLAYOUTPK(tabId, roleOrder, temp, squareId));
	}

	/**
	 * Get the temporary data of the tab ID that you appointed.
	 *
	 * @param tabId
	 * @return
	 * @throws Exception
	 */
	public List selectByTabId(String tabId, String squareId) throws Exception {
		return selectByTempTabId(TabLayout.TEMP_TRUE, tabId, squareId);
	}

	/**
	 * Get the data of the temp flag which you appointed.
	 *
	 * @param temp
	 * @return List<TabLayout>
	 */
	public List selectByTemp(Integer temp, String squareId) {
		return super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(TabLayout.class).add(
						Expression.eq("Id.Temp", temp)).add(
						Expression.eq("Id.Squareid", squareId)).addOrder(
						Order.asc("Id.Roleorder")));
	}

	/**
	 * Get the data of the temp flag and tabid which you appointed.
	 *
	 * @param temp
	 * @return List<TabLayout>
	 */
	public List selectByTempTabId(Integer temp, String tabId, String squareId) {
		return super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(TabLayout.class).add(
						Expression.eq("Id.Temp", temp)).add(
						Expression.eq("Id.Squareid", squareId)).add(
						Expression.eq("Id.Tabid", tabId)).addOrder(
						Order.asc("Id.Roleorder")));
	}
	
	/**
	 * Get the tab ID data of temporary.
	 *
	 * @return List Map tabId,tabNumber
	 */
	public List selectTabId(String squareId) {
		String queryString = "SELECT distinct Id.Tabid, Tabnumber FROM TabLayout WHERE Id.Temp = ? and Id.Squareid = ? ORDER BY Id.Tabid ASC";
		List tabIdNumberList = super.getHibernateTemplate()
				.find(
						queryString,
						new Object[] { TabLayout.TEMP_TRUE, squareId });

		List result = new ArrayList();
		Object[] commandberArray = null;
		for (Iterator ite = tabIdNumberList.iterator(); ite.hasNext();) {
			Object[] objs = (Object[]) ite.next();
			String tabId = (String) objs[0];
			Integer tabNumber = (Integer) objs[1];
			if (StaticTab.COMMANDBAR_TAB_ID.equals(tabId)) {
				commandberArray = objs;
			} else {

				if (tabNumber == null) {
					result.add(0, objs);
				} else {
					result.add(objs);
				}
			}
		}

		if (commandberArray != null)
			result.add(0, commandberArray);

		return result;
	}

	
	public String selectLockingUid(String tabId, String squareId) {
		String queryString = "SELECT distinct Workinguid FROM TabLayout WHERE Id.Temp = ? AND Id.Tabid = ? AND Id.Squareid = ?";
		HibernateTemplate template = super.getHibernateTemplate();
		List result = template.find(queryString,
				new Object[] { TabLayout.TEMP_TRUE, tabId, squareId });
		if (result.size() == 0)
			return null;
		return (String) result.get(0);
	}

	/**
	 * Insert the data.
	 *
	 * @param tabLayout
	 */
	public void insert(TabLayout tabLayout) {
		if(TabLayout.TEMP_TRUE.equals(tabLayout.getId().getTemp()))
			tabLayout.setTemplastmodified(new Date());
		super.getHibernateTemplate().save(tabLayout);

		if (log.isInfoEnabled())
			log.info("insert successfully.");
	}

	/**
	 * Insert the data.
	 *
	 * @param dataMap
	 */
	public void insert(Map dataMap, String squareId) {
		String widgets = (String) dataMap.get("widgets");
		String tabId = (String) dataMap.get("tabId");
		String tabNumber = (String) dataMap.get("tabNumber");
//		String deleteFlag = (String) dataMap.get("deleteFlag");
		String layout = (String) dataMap.get("layout");
		String defaultUid = (String) dataMap.get("defaultUid");
		String roleName = (String) dataMap.get("roleName");
		String role = (String) dataMap.get("role");
		String principalType = (String) dataMap.get("principalType");
		Integer roleOrder = (Integer) dataMap.get("roleOrder");
		String temp = (String) dataMap.get("temp");
		String workinguid = (String) dataMap.get("workinguid");
		String widgetsLastmodified = new SimpleDateFormat("yyyyMMddHHmmssSSS")
				.format(new Date());
		String tabDesc = (String) dataMap.get("tabDesc");

		TabLayout tablayout = new TabLayout();
		tablayout.setId(new TABLAYOUTPK(tabId, roleOrder, new Integer(temp), squareId));
		tablayout.setWidgets(widgets);
		
		if(TabLayout.TEMP_TRUE.equals(tablayout.getId().getTemp()))
			tablayout.setTemplastmodified(new Date());
		
		tablayout.setLayout(layout);
		tablayout.setDefaultuid(defaultUid);
		tablayout.setRolename(roleName);
		tablayout.setRole(role);
		tablayout.setPrincipaltype(principalType);
		tablayout.setWidgetslastmodified(widgetsLastmodified);
		tablayout.setWorkinguid(workinguid);

		super.getHibernateTemplate().save(tablayout);

		if (log.isInfoEnabled())
			log.info("insert successfully.");
	}
	
	public void update(TabLayout entity){
		super.getHibernateTemplate().update(entity);
	}
	
	/**
	 * Update the data of the last update day of the tab.
	 */
	public void updateLastmodifiedByTabId(String tabId, String squareId) {
		HibernateTemplate templete = super.getHibernateTemplate();

		String widgetsLastmodified = new SimpleDateFormat("yyyyMMddHHmmssSSS")
				.format(new Date());
		List tabLayouts = templete.findByCriteria(DetachedCriteria.forClass(TabLayout.class).add(
				Expression.eq("Id.Tabid", tabId)).add(
				Expression.eq("Id.Squareid", squareId)));

		TabLayout tablayout;
		for (Iterator ite = tabLayouts.iterator(); ite.hasNext();) {
			tablayout = (TabLayout) ite.next();
			tablayout.setWidgetslastmodified(widgetsLastmodified);

			templete.update(tablayout);
		}

		if (log.isInfoEnabled())
			log.info("param[]: update " + tabLayouts.size()
					+ " records successfully.");
	}

	/**
	 * Update the tab of the temporary data.
	 */
	public void deleteTempByTabId(String tabId, String squareId) {
		HibernateTemplate templete = super.getHibernateTemplate();
		List tabLayouts = templete.findByCriteria(DetachedCriteria.forClass(TabLayout.class).add(
				Expression.eq("Id.Tabid", tabId)).add(
				Expression.eq("Id.Squareid", squareId)).add(
				Expression.eq("Id.Temp", TabLayout.TEMP_TRUE)));

		super.getHibernateTemplate().deleteAll(tabLayouts);
	}

	/**
	 * Delete all the data of the temp flag which you appointed.
	 *
	 * @param temp
	 */
	public void deleteByTemp(String tabId, Integer temp, String squareId) {
		String queryString = "delete from TabLayout where Id.Temp = ? AND Id.Tabid = ? AND Id.Squareid = ?";
		super.getHibernateTemplate().bulkUpdate(queryString,
				new Object[] { temp, tabId, squareId });
	}

	/**
	 * Delete entities.
	 * @param entities
	 */
	public void delete(Collection<TabLayout> entities){
		super.getHibernateTemplate().deleteAll(entities);
	}
	
	/**
	 * Overwrite and copy.
	 * @param uid
	 * @param toTemp If "toTemp" is true, copy the public performance data for temporary data. If it's false, temporary data
	 */
	/*
	public void copy(String uid, boolean toTemp) {
		this.deleteByTemp(toTemp ? TabLayout.TEMP_TRUE : TabLayout.TEMP_FALSE);
		
		List<TabLayout> tabLayouts = selectByTemp(toTemp ? TabLayout.TEMP_FALSE
				: TabLayout.TEMP_TRUE);
		
		if(tabLayouts.size() == 0){
			throw new RuntimeException("The record for a copy is not found. ");
		}
		
		for (TabLayout tabLayout : tabLayouts) {
			TABLAYOUTPK id = tabLayout.getId();
			TABLAYOUTPK newid = new TABLAYOUTPK(id.getTabid(), id
					.getRoleorder(), toTemp ? TabLayout.TEMP_TRUE
					: TabLayout.TEMP_FALSE);
			TabLayout newTabLayout = new TabLayout(newid, tabLayout.getRole(),
					tabLayout.getRolename(), tabLayout.getPrincipaltype(),
					tabLayout.getWidgets(), tabLayout.getLayout(), uid);
			newTabLayout.setDefaultuid(tabLayout.getDefaultuid());
			newTabLayout.setWidgetslastmodified(tabLayout
					.getWidgetslastmodified());
			insert(newTabLayout);
		}
	}
	*/

	/**
	 * Overwrite and copy.
	 * @param uid
	 * @param toTemp If "toTemp" is true, copy the public performance data for temporary data. If it's false, temporary data
	 */
	public void copyByTabId(String uid, String tabId, boolean toTemp, String squareId) {
		List<TabLayout> currentTabLayouts = selectByTempTabId(toTemp ? TabLayout.TEMP_TRUE : TabLayout.TEMP_FALSE, tabId, squareId);
		delete(currentTabLayouts);
		
		List<TabLayout> tabLayouts = selectByTempTabId(toTemp ? TabLayout.TEMP_FALSE : TabLayout.TEMP_TRUE, tabId, squareId);
		
		if(tabLayouts.size() == 0){
			throw new RuntimeException("The record for a copy is not found. ");
		}
		
		for (TabLayout tabLayout : tabLayouts) {
			TABLAYOUTPK id = tabLayout.getId();
			TABLAYOUTPK newid = new TABLAYOUTPK(id.getTabid(), id
					.getRoleorder(), toTemp ? TabLayout.TEMP_TRUE
					: TabLayout.TEMP_FALSE, id.getSquareid());
			TabLayout newTabLayout = new TabLayout(newid, tabLayout.getRole(),
					tabLayout.getRolename(), tabLayout.getPrincipaltype(),
					tabLayout.getWidgets(), tabLayout.getLayout(), uid);
			newTabLayout.setDefaultuid(tabLayout.getDefaultuid());
			newTabLayout.setWidgetslastmodified(tabLayout
					.getWidgetslastmodified());
			insert(newTabLayout);
		}
	}
	
	/**
	 * Return the MultiHashMap includes all the recoeds in tablayout table.
	 *
	 * @return MultiHashMap
	 *         <UL>
	 *         <LI>key: tabId</LI>
	 *         <LI>value: XmlObject List</LI>
	 *         </UL>
	 */
	public MultiHashMap getTabLayout(final String tabId, final String squareId) {
		HibernateTemplate templete = super.getHibernateTemplate();

		MultiHashMap map = (MultiHashMap) templete
				.execute(new HibernateCallback() {

					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {

						Criteria cri = session.createCriteria(TabLayout.class).add(
								Expression.eq("Id.Temp", TabLayout.TEMP_FALSE)).add(
								Expression.eq("Id.Squareid", squareId));
						
						cri.createAlias("statictab", "st", CriteriaSpecification.LEFT_JOIN);
						cri.add(
								Restrictions.eq(
										"st." + StaticTab.PROP_DELETEFLAG,
										StaticTab.DELETEFLAG_FALSE));
						
						if (tabId != null) {
							if (tabId.equals("0")) {
								cri.add(Expression.or(Expression.eq("Id.Tabid",
										tabId), Expression.eq("Id.Tabid",
										StaticTab.COMMANDBAR_TAB_ID)));
							} else {
								cri.add(Expression.eq("Id.Tabid", tabId));
							}
						}
						cri.addOrder(Order.asc("Id.Roleorder"));

						Map map = new MultiHashMap();
						TabLayout tablayout;
						for (Iterator ite = cri.list().iterator(); ite
								.hasNext();) {
							tablayout = (TabLayout) ite.next();
							
							int disableDefault = tablayout.getStatictab().getDisabledefault();
							
							if(disableDefault == StaticTab.DISABLE_DEFAULT_TRUE
								&& TabLayoutService.DEFAULT_ROLE_NAME.equals(tablayout.getRolename()))
								continue;
							
							map.put(tablayout.getId().getTabid(), tablayout);
						}

						return map;
					}
				});

		return map;
	}
	
	public Date findLatestLastModifiedTime(final String tabId, final String squareId){

		Date latestLastModifiedTime = (Date)super.getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Criteria cri = session.createCriteria(TabLayout.class);
				
				cri.add(Expression.eq("Id.Tabid", tabId));
				cri.add(Expression.eq("Id.Squareid", squareId));
				cri.add(Expression.eq("Id.Temp", TabLayout.TEMP_TRUE));
				
				Projection projection = Projections.projectionList()  
				    .add(Projections.max("Templastmodified"));
				cri.setProjection(projection);
				try {
					return (Date)cri.uniqueResult();
				} catch (Exception e) {
					logger.error("parsing error", e);
					throw new RuntimeException();
				}
			}
			
		});
		
		return latestLastModifiedTime;
	}
	
	public int deleteBySquareId(String squareid) {
		String queryString = "delete from TabLayout where Id.Squareid = ?";
		
		return super.getHibernateTemplate().bulkUpdate( queryString,
				new Object[] { squareid } );
	}
	
	public void copySquare(String squareId, String defaultSquareId) {
		Session session  = super.getSession();

		Query q = (Query)session.getNamedQuery("is_tablayouts.copySquare");
		q.setString("squareId", squareId);
		q.setString("defaultSquareId", defaultSquareId);
		q.executeUpdate();
	}
}
