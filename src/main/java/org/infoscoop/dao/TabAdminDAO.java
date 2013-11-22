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
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.infoscoop.dao.model.TabAdmin;
import org.infoscoop.dao.model.TabAdminPK;
import org.infoscoop.util.SpringUtil;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * The DAO class to get and update the information of tabadmins.
 * 
 * @author nishiumi
 * 
 */
public class TabAdminDAO extends HibernateDaoSupport {

	private static Log log = LogFactory.getLog(TabAdminDAO.class);

	public static TabAdminDAO newInstance() {

		return (TabAdminDAO) SpringUtil.getContext().getBean("tabAdminDAO");

	}

	public TabAdminDAO() {
	}

	public void deleteByTabId(String tabId) {
		String queryString = "delete from TabAdmin where Tabid = ?";
		super.getHibernateTemplate().bulkUpdate(queryString,
				new Object[] { tabId });
	}

	public void delete(Collection<TabAdmin> entities) {
		super.getHibernateTemplate().deleteAll(entities);
	}

	public void insert(TabAdmin entity) {
		super.getHibernateTemplate().save(entity);
	}

	public void insert(String tabId, String userId) {
		TabAdmin entity = new TabAdmin(tabId, userId);
		super.getHibernateTemplate().save(entity);
	}

	public TabAdmin get(String tabId, String userId) {
		return super.getHibernateTemplate().get(TabAdmin.class,
				new TabAdminPK(tabId, userId));
	}
	
	public List<TabAdmin> getTabAdmins(String tabId) {
		return super
				.getHibernateTemplate()
				.findByCriteria(DetachedCriteria.forClass(TabAdmin.class));
	}
	
}
