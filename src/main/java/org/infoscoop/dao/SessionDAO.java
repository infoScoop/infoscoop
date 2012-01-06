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
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.infoscoop.dao.model.Session;
import org.infoscoop.util.SpringUtil;
import org.infoscoop.web.HttpStatusCode;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class SessionDAO extends HibernateDaoSupport {

	private static Log log = LogFactory.getLog(SessionDAO.class);

	public static SessionDAO newInstance() {
		return (SessionDAO) SpringUtil.getContext().getBean("sessionDAO");
	}

	@SuppressWarnings("unchecked")
    public String getUid(String sessionId) {
		Iterator<Session> results = super
				.getHibernateTemplate()
				.findByCriteria(
						DetachedCriteria.forClass(Session.class).add(
								Expression
										.eq(Session.PROP_SESSIONID, sessionId)))
				.iterator();
		if (results.hasNext()) {
			Session session = results.next();
			return session.getUid();
		}
		return null;
	}
	
	public String getSessionId(String uid) {
		if (uid == null)
			throw new RuntimeException("uid must be set.");
		
		Iterator results = super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(Session.class).add(
						Expression.eq("Uid", uid))
						).iterator();
		if( results.hasNext() ){
			Session session = ( Session )results.next();
			return session.getSessionid();
		}else
			return null;
	}

	private Session findSessionByUid(String uid) {
		if (uid == null)
			throw new RuntimeException("uid must be set.");
		
		//select sessionId from ${schema}.session where uid=?
		String queryString = "from Session where Uid = ?";
		Iterator results = super.getHibernateTemplate().find( queryString,
				new Object[] { uid } ).iterator();
		if(results.hasNext())
			return ( Session )results.next();
		return null;
	}

	public String newSessionId(String uid) {
		if (uid == null)
			throw new RuntimeException("uid must be set.");
		String newSessionId = numberSessionId(uid);
		if (log.isInfoEnabled())
			log.info("newSessionId: uid=" + uid + ", sessionId="+ newSessionId);
		
		Session session = findSessionByUid(uid);
		if (session == null)
			session = new Session(uid);
		session.setSessionid( newSessionId );
		
		session.setLogindatetime(new Date());

		super.getHibernateTemplate().saveOrUpdate( session );
		return newSessionId;
	}

	private String numberSessionId(String uid) {
		return uid + System.currentTimeMillis();
	}
	
	public void deleteSessionId(String uid) {
		String queryString = "delete from Session where Uid = ?";
		super.getHibernateTemplate().bulkUpdate(queryString, new Object[] { uid });
	}
	
	/**
	 * Return the session counting in the designated days.
	 * @param period
	 * @return
	 */
	public int getActiveSessionsCount(final int period){
		return (Integer)super.getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(org.hibernate.Session session)
					throws HibernateException, SQLException {
				
				Criteria crit = session.createCriteria(Session.class);
				
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.DATE, period);
				
				Date startDate = cal.getTime();
				Date endDate = new Date();
				
				crit.add(Restrictions.gt("Logindatetime", startDate));
				crit.add(Restrictions.lt("Logindatetime", endDate));
				Integer rowCount = (Integer)crit.setProjection(Projections.rowCount()).uniqueResult();
				
				return rowCount;
			}
			
		});
	}
	
	/**
	 * Get the count, regarding the past one week as an effective session.
	 * @return
	 */
	public int getActiveSessionsCount(){
		return getActiveSessionsCount(-7);
	}
	
	public void setForceReload( String uid ) {
		Session session = findSessionByUid( uid );
		if( session == null )
			return;
		
		session.setSessionid( HttpStatusCode.MSD_FORCE_RELOAD );
		
		super.getHibernateTemplate().update( session );
	}
}
