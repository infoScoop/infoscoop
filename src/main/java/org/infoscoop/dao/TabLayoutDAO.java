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
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.infoscoop.dao.model.TabLayout;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class TabLayoutDAO extends HibernateDaoSupport {

	private static Log log = LogFactory.getLog(TabLayoutDAO.class);

	public TabLayoutDAO() {
	}

	/**
	 * Get the temporary data of the tab ID that you appointed.
	 * 
	 * @param tabId
	 * @return
	 * @throws Exception
	 */
	public List selectByTabId(String tabId) throws Exception {
		return selectByTabId(tabId, true);
	}

	/**
	 * Get the data.
	 * 
	 * @param tabId
	 * @return
	 * @throws Exception
	 */
	public List selectByTabId(String tabId, boolean temp) throws Exception {
		return super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(TabLayout.class).add(
						Restrictions.eq("tabId", tabId)).add(
						Restrictions.eq("temp", temp)).addOrder(
						Order.asc("id.Roleorder")));
	}

	/**
	 * Get the data of the temp flag which you appointed.
	 * 
	 * @param temp
	 * @return List<TabLayout>
	 */
	public List selectByTemp(boolean temp) {
		return super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(TabLayout.class).add(
						Restrictions.eq("temp", temp)).addOrder(
						Order.asc("roleOrder")));
	}

	/**
	 * Get the tab ID data of temporary.
	 * 
	 * @return List Map tabId,tabNumber
	 * @throws DataResourceException
	 */
	public List selectTabId() {
		String queryString = "SELECT distinct tabId, Tabnumber FROM Tablayout"
				+ " WHERE deleteFlag = ? AND temp = ? ORDER BY tabId ASC";
		List tabIdNumberList = super.getHibernateTemplate().find(queryString,
				new Object[] { "deleteFlag", true });

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
		String queryString = "SELECT distinct Workinguid FROM Tablayout WHERE temp = ?";
		HibernateTemplate template = super.getHibernateTemplate();
		List result = template.find(queryString, new Object[] { true });
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
		tablayout.setTabId(tabId);
		
		tablayout.setTabId(tabId);
		tablayout.setRoleOrder(roleOrder);
		tablayout.setTemp(Integer.parseInt(temp) > 0);
		tablayout.setWidgets(widgets);
		if (tabNumber != null)
			tablayout.setTabNumber(new Integer(tabNumber));

		tablayout.setDeleteFlag(new Integer(deleteFlag));
		tablayout.setLayout(layout);
		tablayout.setDefaultUid(defaultUid);
		tablayout.setRolename(roleName);
		tablayout.setRole(role);
		tablayout.setPrincipalType(principalType);
		tablayout.setWidgetsLastmodified(widgetsLastmodified);
		tablayout.setWorkingUid(workinguid);

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
				TabLayout.class).add(Restrictions.eq("id.Tabid", tabId)));

		TabLayout tablayout;
		for (Iterator ite = tabLayouts.iterator(); ite.hasNext();) {
			tablayout = (TabLayout) ite.next();
			tablayout.setWidgetsLastmodified(widgetsLastmodified);

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
				TabLayout.class).add(Restrictions.eq("tabId", tabId)).add(
						Restrictions.eq("temp", true)));

		TabLayout tablayout;
		for (Iterator ite = tabLayouts.iterator(); ite.hasNext();) {
			tablayout = (TabLayout) ite.next();
			tablayout.setDeleteFlag(new Integer(deleteFlag));

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
				TabLayout.class).add(Restrictions.eq("tabId", tabId)).add(
						Restrictions.eq("temp", true)));

		super.getHibernateTemplate().deleteAll(tabLayouts);
	}

	/**
	 * Delete all the data of the temp flag which you appointed.
	 * 
	 * @param temp
	 */
	public void deleteByTemp(boolean temp) {
		String queryString = "delete from Tablayout where temp = ?";
		super.getHibernateTemplate().bulkUpdate(queryString,
				new Object[] { temp });
	}

	/**
	 * Overwrite and copy.
	 * @param uid
	 * @param toTemp If "toTemp" is true, copy the public performance data for temporary data. If it's false, temporary data
	 */
	public void copy(String uid, boolean toTemp) {
		this.deleteByTemp(toTemp);
		List<TabLayout> tabLayouts = selectByTemp(toTemp);
		for (TabLayout tabLayout : tabLayouts) {
			TabLayout newTablayout = new TabLayout();
			newTablayout.setTabId(tabLayout.getTabId());
			newTablayout.setRoleOrder(tabLayout.getRoleOrder());
			newTablayout.setTemp(tabLayout.isTemp());
			newTablayout.setRole(tabLayout.getRole());
			newTablayout.setRolename(tabLayout.getRolename());
			newTablayout.setPrincipalType(tabLayout.getPrincipalType());
			newTablayout.setWidgets(tabLayout.getWidgets());
			newTablayout.setLayout(tabLayout.getLayout());
			newTablayout.setDeleteFlag(tabLayout.getDeleteFlag());
			newTablayout.setWorkingUid(uid);
			newTablayout.setDefaultUid(tabLayout.getDefaultUid());
			newTablayout.setTabNumber(tabLayout.getTabNumber());
			newTablayout.setWidgetsLastmodified(tabLayout
					.getWidgetsLastmodified());
			insert(newTablayout);
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
				Restrictions.not(Restrictions.eq("tabId", "commandbar")))
				.setProjection(
						Projections.projectionList().add(
								Projections.max("tabId")).add(
								Projections.max("tabNumber"))));

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
	public MultiHashMap getTablayout(final String tabId) {
		HibernateTemplate templete = super.getHibernateTemplate();

		MultiHashMap map = (MultiHashMap) templete
				.execute(new HibernateCallback() {

					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {

						Criteria cri = session.createCriteria(TabLayout.class)
								.add(
										Restrictions.eq(
												"deleteFlag",
												false))
								.add(
										Restrictions.eq("temp",
												false));

						if (tabId != null) {
							if (tabId.equals("0")) {
								cri.add(Restrictions.or(Restrictions.eq("tabId",
										tabId), Restrictions.eq("tabId",
										"commandbar")));
							} else {
								cri.add(Restrictions.eq("tabId", tabId));
							}
						}
						cri.addOrder(Order.asc("roleOrder"));

						Map map = new MultiHashMap();
						TabLayout tablayout;
						for (Iterator ite = cri.list().iterator(); ite
								.hasNext();) {
							tablayout = (TabLayout) ite.next();
							map.put(tablayout.getTabId(), tablayout);
						}

						return map;
					}
				});

		return map;
	}
}