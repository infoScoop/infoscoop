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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.infoscoop.account.DomainManager;
import org.infoscoop.dao.model.UserPref;
import org.infoscoop.dao.model.Widget;
import org.infoscoop.util.SpringUtil;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * The DAO class to get and update the widget information.
 *
 * @author nakata
 *
 */
public class WidgetDAO extends HibernateDaoSupport{

    private static Log log = LogFactory.getLog(WidgetDAO.class);

    public static WidgetDAO newInstance(){

        return (WidgetDAO)SpringUtil.getContext().getBean("widgetDAO");

    }

    public WidgetDAO(){}


	public void addWidget( String uid,  String defaultUid,  String tabId, Widget widget,int isStatic ){
		widget.setUid( uid );
		widget.setFkDomainId(DomainManager.getContextDomainId());
		widget.setTabid( tabId );
		widget.setIsstatic( Integer.valueOf( isStatic ));

		addWidget( widget );
	}
    public void addWidget(Widget widget){
    	addWidget(widget, true);
    }
    
    public void addWidget(Widget widget, boolean saveUserPrefs){
//    	System.out.println("ID:"+widget.getId() );
//    	System.out.println( widget.getUid()+","+widget.getTabid()+","+widget.getWidgetid()+","+widget.getDeletedate() );
		widget.setCreatedate(new Date().getTime());
		super.getHibernateTemplate().save(widget);
		super.getHibernateTemplate().flush();
		if(saveUserPrefs)
			saveUserPrefs( widget );
    }
    
    public List<String> getWidgetTypes(String uid){
    	String query = "select distinct w.Type from Widget w where Uid = ? and Deletedate = 0";
		List result = super.getHibernateTemplate().find(query,
				new Object[] { uid });
		return result;
    }
    
	public int getWidgetCountByType(final String type) {
		return (Integer) super.getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(org.hibernate.Session session)
							throws HibernateException, SQLException {
						Criteria crit = session.createCriteria(Widget.class);
						crit.add(Restrictions.eq(Widget.PROP_TYPE, type));
						crit.add(Restrictions.eq(Widget.PROP_DELETEDATE, 0L));
						crit.setProjection(Projections.rowCount());
						return (Integer) crit.uniqueResult();
					}

				});
	}
    
	public Widget getWidget(String uid, String tabId, String widgetId ){
		String query = "from Widget where Uid = ? and Tabid = ? and Widgetid = ? and Deletedate = 0";
		List result = super.getHibernateTemplate().find(query,
				new Object[] { uid, tabId, widgetId });
		if( !result.isEmpty() )
			return ( Widget )result.get(0);

		return null;
    }
	public List getExistsWidgets( String uid,List widgetIds ) {
		if( widgetIds.size() == 0 )
			return new ArrayList();

		return super.getHibernateTemplate().findByCriteria( DetachedCriteria.forClass( Widget.class )
				.add( Expression.eq("Uid",uid ))
				.add( Expression.eq("Deletedate",Long.valueOf( 0 )))
				.add( Expression.in("Widgetid",widgetIds )));
	}

	public boolean exist(String uid, String widgetId) {
		List result = super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(Widget.class).add(
						Expression.eq("Uid", uid)).add(
						Expression.eq("Deletedate", Long.valueOf(0))).add(
						Expression.eq("Widgetid", widgetId)));
		return result.size() > 0;
	}

    public void updateWidget( Widget widget ) {
    	super.getHibernateTemplate().update( widget );
		super.getHibernateTemplate().flush();
    	updateUserPrefs( widget );
    }

	/**
	 * get the widget that was deleted.
	 * @param uid
	 * @return
	 */
	public List getDeletedWidget(String uid) {
		String queryString = "from Widget where Uid = ? and Deletedate > 0 order by Deletedate desc,Id desc";

		return super.getHibernateTemplate().find( queryString,
				new Object[] { uid });
	}

	public void delete(Widget widget) {
			if(log.isInfoEnabled())
				log.info("deleteWidget: uid=" + widget.getUid() +",tabId=" + widget.getTabid() + ",widgetId=" + widget.getWidgetid());

			super.getHibernateTemplate().delete(widget);
	}

	public int deleteWidget(String uid, String tabId, String widgetId, long deleteDate) {
		if(log.isInfoEnabled())
			log.info("deleteWidget:uid=" + uid +",tabId=" + tabId + ",widgetId=" + widgetId + ",deleteDate=" + deleteDate);

		String updateQuery = "update Widget set Deletedate = ?,Tabid = '' where Uid = ? and Tabid = ? and (Widgetid = ? or Parentid = ?) and Deletedate = 0";
		return super.getHibernateTemplate().bulkUpdate(
				updateQuery,
				new Object[]{ Long.valueOf(deleteDate), uid, tabId, widgetId, widgetId });
	}

	public void deleteWidget( String uid ) {
		long deleteDate = new Date().getTime();

		String queryString = "update Widget set Deletedate = ? where Uid = ? and deleteDate = 0 and Isstatic = 0";

		super.getHibernateTemplate().bulkUpdate( queryString,
				new Object[]{ deleteDate,uid });

		queryString = "delete from Widget where Uid = ? and Isstatic = 1";

		super.getHibernateTemplate().bulkUpdate( queryString,
				new Object[] { uid });
	}


	public void deleteWidget( String uid, Integer tabId ) {
		long deleteDate = new Date().getTime();

		String queryString = "update Widget set Deletedate = ? where Uid = ? and tabId = ? and deleteDate = 0 and Isstatic = 0";

		super.getHibernateTemplate().bulkUpdate( queryString,
				new Object[]{ deleteDate,uid,tabId.toString() });

		queryString = "delete from Widget where Uid = ? and tabId = ? and Isstatic = 1";

		super.getHibernateTemplate().bulkUpdate( queryString,
				new Object[] { uid, tabId.toString() });
	}


	public void deleteStaticWidgetByTabId(String tabId) {
		String queryString = "delete from Widget where tabId = ? and Isstatic = 1";

		super.getHibernateTemplate().bulkUpdate( queryString,
				new Object[] {  tabId});
	}

	public void deleteStaticWidgetByTabIdAndWidgetId(String tabId,
			String widgetId) {
		String queryString = "delete from Widget where tabId = ? and widgetId = ? and Isstatic = 1";
		super.getHibernateTemplate().bulkUpdate( queryString,
				new Object[] { tabId, widgetId });
	}
	public int emptyWidget(String uid, String widgetId, long deleteDate) {
		/*
		return getJdbcTemplate().update(this.getQuery("emptyWidget"),
				new Object[] { uid, widgetId, widgetId, new Long(deleteDate) });
				*/
		String updateQuery = "delete from Widget where Uid = ? and (Widgetid = ? or Parentid = ?) and Deletedate = ?";
		return super.getHibernateTemplate().bulkUpdate(
				updateQuery,
				new Object[]{uid, widgetId,widgetId, Long.valueOf(deleteDate)});

	}

	public int emptyWidget(String uid, String widgetId, String tabId,
			long deleteDate) {
		/*
		return getJdbcTemplate().update(
				this.getQuery("emptyWidgetByTabId"),
				new Object[] { uid, widgetId, widgetId, tabId,
						new Long(deleteDate) });
						*/
		String updateQuery = "delete from Widget where Uid = ? and (Widgetid = ? or Parentid = ?) and Tabid = ? and Deletedate = ?";
		return super.getHibernateTemplate().bulkUpdate(
				updateQuery,
				new Object[]{uid, widgetId,widgetId, tabId, Long.valueOf(deleteDate)});

	}
	/*
	public void wipeWidget(String uid, String tabId, String widgetId) {
		getJdbcTemplate().update(this.getQuery("wipeWidget"),
				new Object[] { uid, widgetId, widgetId, tabId });
	}*/

	public void emptyDeletedWidgets(String uid){
//		String queryString = "delete from Widget where Uid = ? and Deletedate > 0";
//		super.getHibernateTemplate().bulkUpdate( queryString,new Object[]{ uid } );

		super.getHibernateTemplate().deleteAll(getDeletedWidget(uid));
	}

	public void emptyWidgets( String uid,String tabId,int isStatic ) {
		String queryString = "delete from Widget where Uid=? and TabId=? and Isstatic=?";

		super.getHibernateTemplate().bulkUpdate( queryString,
				new Object[]{ uid,tabId, Integer.valueOf( isStatic )});
	}

	public Map<String,UserPref> getUserPrefs( String id ) {
		Map<String,UserPref> userPrefs = new HashMap<String,UserPref>();
		List<UserPref> results = super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass( UserPref.class )
				.add( Expression.eq("Id.WidgetId",id )));
		for( UserPref userPref : results )
			userPrefs.put( userPref.getId().getName(),userPref );

		return userPrefs;
	}
	public void saveUserPrefs( Widget widget ) {
		
		Map<String,UserPref> userPrefs = widget.getUserPrefs();
		for( UserPref userPref  : userPrefs.values()) {
			userPref.getId().setWidgetId( widget.getId());
			super.getHibernateTemplate().saveOrUpdate( userPref );
		}
	}
	
	public void updateUserPrefs( Widget widget ) {
		List<UserPref> c1 = super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass( UserPref.class )
				.add( Expression.eq("Id.WidgetId",widget.getId())));
		Map<String,UserPref> current = new HashMap<String,UserPref>();
		for( UserPref userPref : c1 )
			current.put( userPref.getId().getName(),userPref );

		Map<String,UserPref> userPrefs = widget.getUserPrefs();
		Set<String> keySet = new HashSet<String>( userPrefs.keySet() );
		keySet.addAll( current.keySet() );

		for( String key : keySet ) {
			UserPref newPref = userPrefs.get( key );
			if(newPref == null) {
				super.getHibernateTemplate().delete( current.get( key ));
			} else {
				UserPref userPref = current.get(key);
				if (userPref == null) {// new user pref
					userPref = userPrefs.get(key);
				} else {// already existing user pref (for Hibernate)
					userPref.setValue(newPref.getValue());
					if (userPref.getId().getWidgetId() == null)
						userPref.getId().setWidgetId(widget.getId());
				}
				super.getHibernateTemplate().saveOrUpdate( userPref );
			}
		}
		super.getHibernateTemplate().flush();
	}
	
	public void updateUserPrefs( Collection<?> widgets ) {
		for( Object widget : widgets )
			updateUserPrefs( ( Widget )widget );
	}

	public void markMenuItemUpdated(Integer id) {
		super.getHibernateTemplate().bulkUpdate("update Widget w set w.MenuUpdated = 1 where w.FkMenuId = ?", id);
	}


}
