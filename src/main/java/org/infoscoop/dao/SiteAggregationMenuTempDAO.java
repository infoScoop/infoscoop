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
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.infoscoop.dao.model.Siteaggregationmenu_temp;
import org.infoscoop.util.SpringUtil;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class SiteAggregationMenuTempDAO extends HibernateDaoSupport {
	public static final String SITEMENU_ORDER_TEMP_ID = "siteaggregationmenu_ordertemp";
	
    public static SiteAggregationMenuTempDAO newInstance() {
		return (SiteAggregationMenuTempDAO) SpringUtil.getContext().getBean(
				"siteAggregationMenuTempDAO");
	}

	@SuppressWarnings("unchecked")
	public List<Siteaggregationmenu_temp> selectByTypeAndUser(String menuType, String workingUid) {
		DetachedCriteria crit = DetachedCriteria.forClass(Siteaggregationmenu_temp.class);
		crit.add(Expression.eq("Id.Type", menuType));
		crit.add(Expression.eq("Workinguid", workingUid));
		
		return super.getHibernateTemplate().findByCriteria(crit);
	}
	
	@SuppressWarnings("unchecked")
	public Siteaggregationmenu_temp selectBySitetopId(String menuType, String sitetopId){
		DetachedCriteria crit = DetachedCriteria.forClass(Siteaggregationmenu_temp.class);
		crit.add(Expression.eq("Id.Type", menuType));
		crit.add(Expression.eq("Id.Sitetopid", sitetopId));
		
		List<Siteaggregationmenu_temp> list = super.getHibernateTemplate().findByCriteria(crit);
		return (list.size() > 0)? list.get(0) : null;
	}
	
	public void delete(Siteaggregationmenu_temp entity){
		super.getHibernateTemplate().delete(entity);
		super.getHibernateTemplate().flush();
	}
	
	public void deleteByTypeAndUser(String menuType, String workingUid){
		String query = "delete Siteaggregationmenu_temp where Id.Type=? and Workinguid=?";
		super.getHibernateTemplate().bulkUpdate(query, new Object[]{menuType, workingUid});
	}
	
	public void evict(Siteaggregationmenu_temp entity){
		super.getHibernateTemplate().evict(entity);
	}
	
	public void deleteByUser(String workingUid){
		String query = "delete Siteaggregationmenu_temp where Workinguid=?";
		super.getHibernateTemplate().bulkUpdate(query, new Object[]{workingUid});
		super.getHibernateTemplate().flush();
	}
	
	public Date findLatestLastModifiedTime(final String menuType, final String workingUid){

		Date latestLastModifiedTime = (Date)super.getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Criteria cri = session.createCriteria(Siteaggregationmenu_temp.class);
				
				cri.add(Expression.eq("Id.Type", menuType));
				cri.add(Expression.eq("Workinguid", workingUid));
				
				Projection projection = Projections.projectionList()  
				    .add(Projections.max("Lastmodified"));
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
	
	/**
	 * update the data.
	 * 
	 * @param menuXml
	 * @param tempFlag
	 * @throws DataResourceException
	 */
	public void update(Siteaggregationmenu_temp entity) {
		entity.setLastmodified(new Date());
		super.getHibernateTemplate().saveOrUpdate(entity);
	}
	
}
