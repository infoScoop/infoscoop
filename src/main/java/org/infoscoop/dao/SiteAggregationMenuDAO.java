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


import org.hibernate.Query;
import org.hibernate.Session;
import org.infoscoop.dao.model.Siteaggregationmenu;
import org.infoscoop.dao.model.SiteaggregationmenuPK;
import org.infoscoop.util.SpringUtil;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class SiteAggregationMenuDAO extends HibernateDaoSupport {
    public static SiteAggregationMenuDAO newInstance() {
		return (SiteAggregationMenuDAO) SpringUtil.getContext().getBean(
				"siteAggregationMenuDAO");
	}

	/**
	 * get the data
	 * 
	 * @param tempFlag
	 * @return
	 * @throws DataResourceException
	 */
	public Siteaggregationmenu select(String menuType, String squareid) {
		return (Siteaggregationmenu) super.getHibernateTemplate().get(
				Siteaggregationmenu.class,
				new SiteaggregationmenuPK(menuType, squareid));
	}

	/*
	public void delete(Siteaggregationmenu entity){
		super.getHibernateTemplate().delete(entity);
	}
	*/
	
	/**
	 * update the data
	 * 
	 * @param menuXml
	 * @param tempFlag
	 * @throws DataResourceException
	 */
	public void update(Siteaggregationmenu entity) {
		super.getHibernateTemplate().saveOrUpdate(entity);
		
	}
	
	public void copySquare(String squareId, String defaultSquareId) {
		Session session  = super.getSession();

		Query q = (Query)session.getNamedQuery("is_menus.copySquare");
		q.setString("squareId", squareId);
		q.setString("defaultSquareId", defaultSquareId);
		q.executeUpdate();
	}
}
