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
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.infoscoop.dao.model.Preference;
import org.infoscoop.dao.model.PreferencePK;
import org.infoscoop.util.SpringUtil;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * The DAO class to get and update the information of the widget.
 * 
 * @author nakata
 * 
 */
public class PreferenceDAO extends HibernateDaoSupport{

    private static Log log = LogFactory.getLog(PreferenceDAO.class);

    private PreferenceDAO(){};
    
	public static PreferenceDAO newInstance() {
        return (PreferenceDAO)SpringUtil.getContext().getBean("preferenceDAO");
	}
    
    /**
     * Get the DOM of an appointed user.
     * 
     * @param uid
     *            userID
     * @return A top node Object of DOM that includes include the user setting information of an appointed user. If there is not it, we return the empty DOM.
     */
    public Preference select(String uid, String squareid) {
    	if(uid == null)return null;
    	return (Preference)super.getHibernateTemplate().get(Preference.class, new PreferencePK(uid, squareid));
    }

    /**
     * Update the data of an appointed user.<BR>
     * 
     * @param uid
     *            A userID that is target for operating data.
     * @param widgetsNode
     *            A Node object having the data of "widgets" of DOM to save.
     * @throws DataResourceException
     */
    public void update(Preference entity) {
    	if(log.isInfoEnabled())
        	log.info("update preference for uid: " + entity.getId().getUid() +  ", squareid: " + entity.getId().getSquareid() + ".");
    	
    	super.getHibernateTemplate().saveOrUpdate(entity);
    	if(log.isInfoEnabled())
    		log.info("uid[" + entity.getId().getUid() + "] squareid[" + entity.getId().getSquareid() + "]: Save XML successfully.");
    }
    
    /**
     * Delete the data of an appointed user.<BR>
     * @param entity
     */
    public void delete(Preference entity) {
    	if(log.isInfoEnabled())
        	log.info("delete preference for uid: " + entity.getId().getUid() + ", squareid: " + entity.getId().getSquareid() + " .");
    	
    	super.getHibernateTemplate().delete(entity);;
    	if(log.isInfoEnabled())
    		log.info("uid[" + entity.getId().getUid() + "] squareid[" + entity.getId().getSquareid() + "]: Delete Preference successfully.");
    }

	/**
	 * Return the users count.
	 * 
	 * @return
	 */
	public int getTotalUsersCount(final String squareid) {
		return (Integer) super.getHibernateTemplate().execute(
				new HibernateCallback() {

					public Object doInHibernate(org.hibernate.Session session)
							throws HibernateException, SQLException {

						Criteria crit = session
								.createCriteria(Preference.class);
						
						crit.add(Expression.eq("Id.Squareid", squareid));
						crit.setProjection(Projections.rowCount());
						Integer rowCount = (Integer) crit.uniqueResult();

						return rowCount;
					}

				});
	}


	/**
	 * Return the users count.
	 * 
	 * @return
	 */
	public List<String> getUserIdList(final String squareid) {
		return (List) super.getHibernateTemplate().execute(
				new HibernateCallback() {

					public Object doInHibernate(org.hibernate.Session session)
							throws HibernateException, SQLException {

						Criteria crit = session
								.createCriteria(Preference.class);
						
						crit.add(Expression.eq("Id.Squareid", squareid));
						Projection projection = Projections.property(Preference.PROP_ID);
						crit.setProjection(projection);
						
						return crit.list();
					}

				});
	}

}
