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
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MultiHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.infoscoop.dao.model.TABLAYOUTPK;
import org.infoscoop.dao.model.TabLayout;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class TabLayoutDAO extends HibernateDaoSupport {

	private static Log log = LogFactory.getLog(TabLayoutDAO.class);

	public TabLayoutDAO() {
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
	public TabLayout selectByPK(String tabId, Integer roleOrder, Integer temp) throws Exception {
		return super.getHibernateTemplate().get(TabLayout.class, new TABLAYOUTPK(tabId, roleOrder, temp));
	}

	/**
	 * Get the temporary data of the tab ID that you appointed.
	 *
	 * @param tabId
	 * @return
	 * @throws Exception
	 */
	public List selectByTabId(String tabId) throws Exception {
		return selectByTabId(tabId, TabLayout.TEMP_TRUE);
	}

	/**
	 * Get the data.
	 *
	 * @param tabId
	 * @return
	 * @throws Exception
	 */
	public List selectByTabId(String tabId, Integer temp) throws Exception {
		return super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(TabLayout.class).add(
						Expression.eq("id.Tabid", tabId)).add(
						Expression.eq("id.Temp", temp)).addOrder(
						Order.asc("id.Roleorder")));
	}

	/**
	 * Get the data of the temp flag which you appointed.
	 *
	 * @param temp
	 * @return List<TabLayout>
	 */
	public List selectByTemp(Integer temp) {
		return super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(TabLayout.class).add(
						Expression.eq("id.Temp", temp)).addOrder(
						Order.asc("id.Roleorder")));
	}

	/**
	 * Get the tab ID data of temporary.
	 *
	 * @return List Map tabId,tabNumber
	 * @throws DataResourceException
	 */
	public List selectTabId() {
		String queryString = "SELECT distinct id.Tabid, Tabnumber FROM TabLayout WHERE "
				+ TabLayout.PROP_DELETEFLAG
				+ " = ? AND id.Temp = ? ORDER BY id.Tabid ASC";
		List tabIdNumberList = super.getHibernateTemplate()
				.find(
						queryString,
						new Object[] { TabLayout.DELETEFLAG_FALSE,
								TabLayout.TEMP_TRUE });

		List result = new ArrayList();
		Object[] commandberArray = null;
		for (Iterator ite = tabIdNumberList.iterator(); ite.hasNext();) {
			Object[] objs = (Object[]) ite.next();
			String tabId = (String) objs[0];
			Integer tabNumber = (Integer) objs[1];
			if ("commandbar".equals(tabId)) {
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

	public String selectLockingUid() {
		String queryString = "SELECT distinct Workinguid FROM TabLayout WHERE id.Temp = ?";
		HibernateTemplate template = super.getHibernateTemplate();
		List result = template.find(queryString,
				new Object[] { TabLayout.TEMP_TRUE });
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
		super.getHibernateTemplate().save(tabLayout);

		if (log.isInfoEnabled())
			log.info("insert successfully.");
	}

	/**
	 * Insert the data.
	 *
	 * @param dataMap
	 */
	public void insert(Map dataMap) {
		String widgets = (String) dataMap.get("widgets");
		String tabId = (String) dataMap.get("tabId");
		String tabNumber = (String) dataMap.get("tabNumber");
		String deleteFlag = (String) dataMap.get("deleteFlag");
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

		TabLayout tablayout = new TabLayout();
		tablayout.setId(new TABLAYOUTPK(tabId, roleOrder, new Integer(temp)));
		tablayout.setWidgets(widgets);
		if (tabNumber != null)
			tablayout.setTabnumber(new Integer(tabNumber));

		tablayout.setDeleteflag(new Integer(deleteFlag));
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

	/**
	 * Update the data of the last update day of the tab.
	 *
	 * @param res
	 * @param tabId
	 * @throws DataResourceException
	 */
	public void updateLastmodifiedByTabId(String tabId) {
		HibernateTemplate templete = super.getHibernateTemplate();

		String widgetsLastmodified = new SimpleDateFormat("yyyyMMddHHmmssSSS")
				.format(new Date());
		List tabLayouts = templete.findByCriteria(DetachedCriteria.forClass(
				TabLayout.class).add(Expression.eq("id.Tabid", tabId)));

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
	 * Update the delete flag of the temporary data.
	 *
	 * @param res
	 * @param tabId
	 * @param deleteFlag
	 * @return
	 * @throws DataResourceException
	 */
	public int updateDeleteFlag(String tabId, String deleteFlag) {
		HibernateTemplate templete = super.getHibernateTemplate();

		List tabLayouts = templete.findByCriteria(DetachedCriteria.forClass(
				TabLayout.class).add(Expression.eq("id.Tabid", tabId)).add(
				Expression.eq("id.Temp", TabLayout.TEMP_TRUE)));

		TabLayout tablayout;
		for (Iterator ite = tabLayouts.iterator(); ite.hasNext();) {
			tablayout = (TabLayout) ite.next();
			tablayout.setDeleteflag(new Integer(deleteFlag));

			templete.update(tablayout);
		}
		return tabLayouts.size();
	}

	/**
	 * Update the tab of the temporary data.
	 *
	 * @param res
	 * @param tabId
	 * @throws DataResourceException
	 */
	public void deleteByTabId(String tabId) {
		HibernateTemplate templete = super.getHibernateTemplate();
		List tabLayouts = templete.findByCriteria(DetachedCriteria.forClass(
				TabLayout.class).add(Expression.eq("id.Tabid", tabId)).add(
				Expression.eq("id.Temp", TabLayout.TEMP_TRUE)));

		super.getHibernateTemplate().deleteAll(tabLayouts);
	}

	/**
	 * Delete all the data of the temp flag which you appointed.
	 *
	 * @param temp
	 */
	public void deleteByTemp(Integer temp) {
		String queryString = "delete from TabLayout where id.Temp = ?";
		super.getHibernateTemplate().bulkUpdate(queryString,
				new Object[] { temp });
	}

	/**
	 * Overwrite and copy.
	 * @param uid
	 * @param toTemp If "toTemp" is true, copy the public performance data for temporary data. If it's false, temporary data
	 */
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
					tabLayout.getWidgets(), tabLayout.getLayout(), tabLayout
							.getDeleteflag(), uid);
			newTabLayout.setDefaultuid(tabLayout.getDefaultuid());
			newTabLayout.setTabnumber(tabLayout.getTabnumber());
			newTabLayout.setWidgetslastmodified(tabLayout
					.getWidgetslastmodified());
			insert(newTabLayout);
		}
	}

	/**
	 * @param res
	 * @return
	 */
	public Map selectMax() {
		HibernateTemplate templete = super.getHibernateTemplate();
		List tabLayouts = templete.findByCriteria(DetachedCriteria.forClass(
				TabLayout.class).add(
				Expression.not(Expression.eq("id.Tabid", "commandbar")))
				.setProjection(
						Projections.projectionList().add(
								Projections.max("id.Tabid")).add(
								Projections.max(TabLayout.PROP_TABNUMBER))));

		Map resultMap = new HashMap();
		for (int i = 0; i < tabLayouts.size(); i++) {
			Object[] tablayout = (Object[]) tabLayouts.get(i);
			resultMap.put("tabId", tablayout[0]);
			resultMap.put("tabNumber", tablayout[1]);
		}

		return resultMap;
	}

	/**
	 * Return the MultiHashMap includes all the recoeds in tablayout table.
	 *
	 * @param resource
	 * @return MultiHashMap
	 *         <UL>
	 *         <LI>key: tabId</LI>
	 *         <LI>value: XmlObject List</LI>
	 *         </UL>
	 */
	public MultiHashMap getTabLayout(final String tabId) {
		HibernateTemplate templete = super.getHibernateTemplate();

		MultiHashMap map = (MultiHashMap) templete
				.execute(new HibernateCallback() {

					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {

						Criteria cri = session.createCriteria(TabLayout.class)
								.add(
										Expression.eq(
												TabLayout.PROP_DELETEFLAG,
												TabLayout.DELETEFLAG_FALSE))
								.add(
										Expression.eq("id.Temp",
												TabLayout.TEMP_FALSE));

						if (tabId != null) {
							if (tabId.equals("0")) {
								cri.add(Expression.or(Expression.eq("id.Tabid",
										tabId), Expression.eq("id.Tabid",
										"commandbar")));
							} else {
								cri.add(Expression.eq("id.Tabid", tabId));
							}
						}
						cri.addOrder(Order.asc("id.Roleorder"));

						Map map = new MultiHashMap();
						TabLayout tablayout;
						for (Iterator ite = cri.list().iterator(); ite
								.hasNext();) {
							tablayout = (TabLayout) ite.next();
							map.put(tablayout.getId().getTabid(), tablayout);
						}

						return map;
					}
				});

		return map;
	}
}
