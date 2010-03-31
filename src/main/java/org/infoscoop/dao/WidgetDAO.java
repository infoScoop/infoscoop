package org.infoscoop.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.infoscoop.dao.model.SystemMessage;
import org.infoscoop.dao.model.UserPref;
import org.infoscoop.dao.model.Widget;
import org.infoscoop.service.SiteAggregationMenuService.ForceUpdateUserPref;
import org.infoscoop.util.SpringUtil;
import org.json.JSONException;
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
		widget.setDefaultUid( defaultUid );
		widget.setTabId( tabId );
		widget.setIsStatic( new Integer( isStatic ));

		addWidget( widget );
	}
    public void addWidget(Widget widget){
//    	System.out.println("ID:"+widget.getId() );
//    	System.out.println( widget.getUid()+","+widget.getTabid()+","+widget.getWidgetid()+","+widget.getDeletedate() );
		widget.setCreateDate(new Date().getTime());
		super.getHibernateTemplate().save(widget);
		super.getHibernateTemplate().flush();
		updateUserPrefs( widget );
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
				.add( Restrictions.eq("Uid",uid ))
				.add( Restrictions.eq("Deletedate",new Long( 0 )))
				.add( Restrictions.in("Widgetid",widgetIds )));
	}

    public void updateWidget( Widget widget ) {
    	super.getHibernateTemplate().update( widget );
		super.getHibernateTemplate().flush();
    	updateUserPrefs( widget );
    }

    /**
     * Update the property of the widget appointed by widgetID collectively.
     * Please appoint only the property that you want to change,
     * except for it, it's maintained the value before changed for the properety.
     * @param widgetId
     * @param setProperties, Set<String> removePropNames
     * @throws Exception
     */
    public void updateWidgetProperties(final String widgetId, String title, String href, final Map<String, ForceUpdateUserPref> setProperties, Set<ForceUpdateUserPref> removePropNames) throws Exception{
    	if (log.isInfoEnabled())
			log.info("updateWidgetProperties : widgetId=" + widgetId
					+ ", set properties=" + setProperties + ", remove properties:" +  removePropNames);
    	long start = System.currentTimeMillis();

		List<Widget> widgetList = super.getHibernateTemplate()
			.findByCriteria(DetachedCriteria.forClass(Widget.class)
					.add(Restrictions.eq("Menuid", widgetId)));
		SystemMessageDAO sysMessageDao = SystemMessageDAO.newInstance();
		int i = 0;
		for( Widget widget : widgetList ) {
    		String oldTitle = widget.getTitle();
			if(title != null){
				widget.setTitle(title);
				if(widget.getDeleteDate() == 0){
					sysMessageDao.insert(new SystemMessage(
							widget.getUid(),
							"ms_title_update_by_admin",
							oldTitle + "," + title));
				}
			}
			if(href != null)
				widget.setHref(href);

			for(Map.Entry<String, ForceUpdateUserPref> pref : setProperties.entrySet()){
				String oldValue = null;
				UserPref up = widget.getUserPrefsMap().get(pref.getKey());
				if(up != null)oldValue = up.getId().getValue();

				ForceUpdateUserPref updatePref = pref.getValue();

				if(widget.getType().startsWith("g_") &&  "url".equals(pref.getKey())){
					widget.setType("g_" + updatePref.getValue());
				}else{

					widget.setUserPref(pref.getKey(), updatePref.getValue());
					if(!updatePref.isImplied() && widget.getDeleteDate() == 0){
						if(oldValue != null){
							sysMessageDao.insert(new SystemMessage(
									widget.getUid(),
									"ms_up_update_by_admin",
									widget.getTitle() + "," + pref.getKey() + "," + oldValue + "," + updatePref.getValue()));
						}else{
							sysMessageDao.insert(new SystemMessage(
									widget.getUid(),
									"ms_up_update_from_default_by_admin",
									widget.getTitle() + "," + pref.getKey() + "," + updatePref.getValue()));
						}
					}
				}
			}
			for(ForceUpdateUserPref removeProp : removePropNames){
				// The special processing for gadgets is not necessary, Beacouse gadget's URL is require property.
				widget.removeUserPref(removeProp.getName());
				if(!removeProp.isImplied() && widget.getDeleteDate() == 0){
					sysMessageDao.insert(new SystemMessage(
							widget.getUid(),
							"ms_up_revert_by_admin",
							widget.getTitle() + "," + removeProp.getName()));
				}

			}
			
    		updateWidget( widget );
    		if(i % 20 ==0){
    			this.getHibernateTemplate().flush();
    			this.getHibernateTemplate().clear();
    		}
    		i++;

		}

    	long end = System.currentTimeMillis();
		if (log.isInfoEnabled())
			log.info("end updateWidgetProperties : " + (end - start) + " ms");
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
				log.info("deleteWidget: uid=" + widget.getUid() +",tabId=" + widget.getTabId() + ",widgetId=" + widget.getWidgetId());

			super.getHibernateTemplate().delete(widget);
	}

	public int deleteWidget(String uid, String tabId, String widgetId, long deleteDate) {
		if(log.isInfoEnabled())
			log.info("deleteWidget:uid=" + uid +",tabId=" + tabId + ",widgetId=" + widgetId + ",deleteDate=" + deleteDate);

		String updateQuery = "update Widget set Deletedate = ?,Tabid = '' where Uid = ? and Tabid = ? and (Widgetid = ? or Parentid = ?) and Deletedate = 0";
		return super.getHibernateTemplate().bulkUpdate(
				updateQuery,
				new Object[]{ new Long(deleteDate), uid, tabId, widgetId, widgetId });
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

	/*
	public void deleteWidget(Widget widget, long deleteDate) {
		widget.getId().setDeletedate(new Long(deleteDate));
		super.getHibernateTemplate().update(widget);
	}
	*/

	public int emptyWidget(String uid, String widgetId, long deleteDate) {
		/*
		return getJdbcTemplate().update(this.getQuery("emptyWidget"),
				new Object[] { uid, widgetId, widgetId, new Long(deleteDate) });
				*/
		String updateQuery = "delete from Widget where Uid = ? and (Widgetid = ? or Parentid = ?) and Deletedate = ?";
		return super.getHibernateTemplate().bulkUpdate(
				updateQuery,
				new Object[]{uid, widgetId,widgetId, new Long(deleteDate)});

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
				new Object[]{uid, widgetId,widgetId, tabId, new Long(deleteDate)});

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
				new Object[]{ uid,tabId,new Integer( isStatic )});
	}

	public List getWidgetRanking(int maxCount, int freshDay) {
		Session session = null;
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1 * freshDay);
		long lastTime = cal.getTimeInMillis();

		int looseMaxCount = maxCount + 10;
		try{
			session = super.getSession();

			Query msql = (Query) session.getNamedQuery(
					"menuWidgetRanking");
			List<Object[]> mr = msql.setMaxResults(looseMaxCount).list();

			Query msqll = (Query) session.getNamedQuery(
					"menuWidgetRankingLast");
			List<Object[]> mrl = msqll.setLong("CREATEDATE", lastTime)
					.setMaxResults(looseMaxCount).list();

			mr = joinRanking(mr, mrl);

			Query wsql = (Query) session.getNamedQuery(
					"widgetRanking");
			List<Object[]> wr = wsql.setMaxResults(looseMaxCount).list();

			Query wsall = (Query) session.getNamedQuery(
					"widgetRankingLast");
			List<Object[]> wrl = wsall.setLong("CREATEDATE", lastTime)
					.setMaxResults(looseMaxCount).list();

			wr = joinRanking(wr, wrl);

			Query usql = (Query) session.getNamedQuery(
					"urlRanking");
			List<Object[]> ur = usql.setMaxResults(looseMaxCount).list();
			Query usqll = (Query) session.getNamedQuery(
					"urlRankingLast");
			List<Object[]> url = usqll.setLong("CREATEDATE", lastTime)
					.setMaxResults(looseMaxCount).list();

			ur = joinRanking(ur, url);

			TreeSet<Object[]> ranks = new TreeSet(new Comparator<Object[]>() {
				public int compare(Object[] o1, Object[] o2) {
					long cntl_1 = (Long) o1[4];
					long cntl_2 = (Long) o2[4];
					if (cntl_1 != cntl_2)
						return (int) (cntl_2 - cntl_1);
					long cnt_1 = (Long) o1[3];
					long cnt_2 = (Long) o2[3];
					if (cnt_1 != cnt_2)
						return (int) (cnt_2 - cnt_1);
					return 1;
				}
			});
			for (Object[] rank : mr) {
				ranks.add(rank);
			}
			for (Object[] rank : wr) {
				ranks.add(rank);
			}
			for (Object[] rank : ur) {
				ranks.add(rank);
			}

			List<Object[]> ranksSub = new ArrayList();
			int i = 0;
			for (Object[] rank : ranks) {
				ranksSub.add(rank);
				if (++i > maxCount)
					break;
			}
			return ranksSub;

		}finally{
			if(session != null)
				session.close();
		}
	}

	private List joinRanking(List l, List l10) {
		loop10: for (Iterator<Object[]> it10 = l10.iterator(); it10.hasNext();) {
			Object[] cols10 = it10.next();
			for (Iterator<Object[]> it = l.iterator(); it.hasNext();) {
				Object[] cols = it.next();
				if (eq(cols[0],cols10[0]) &&
					eq(cols[1],cols10[1]) &&
					eq(cols[2],cols10[2])) {
					cols[4] = cols10[4];
					continue loop10;
				}
			}
			//add the object that is only in a new popular list.
			l.add(cols10);
		}
		return l;
	}
	private boolean eq( Object o1,Object o2 ) {
		if( o1 == null )
			return ( o1 == o2 );
		
		return o1.equals( o2 );
	}

	public static void main(String args[]) throws JSONException{
		WidgetDAO widgetDAO = WidgetDAO.newInstance();
		List<Object[]> list = widgetDAO.getWidgetRanking(20, 10);
		for (Object[] obj : list) {
			for (int i = 0; i < obj.length; i++) {
				System.out.print(obj[i] + "\t");
			}
			System.out.println();
		}

	}

	public Map<String,UserPref> getUserPrefs( String id ) {
		Map<String,UserPref> userPrefs = new HashMap<String,UserPref>();
		List<UserPref> results = super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass( UserPref.class )
				.add( Restrictions.eq("Id.WidgetId",id )));
		for( UserPref userPref : results )
			userPrefs.put( userPref.getId().getName(),userPref );

		return userPrefs;
	}
	public void updateUserPrefs( Widget widget ) {
		List<UserPref> c1 = super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass( UserPref.class )
				.add( Restrictions.eq("Id.WidgetId",widget.getId())));
		Map<String,UserPref> current = new HashMap<String,UserPref>();
		for( UserPref userPref : c1 )
			current.put( userPref.getId().getName(),userPref );

		Map<String,UserPref> userPrefs = widget.getUserPrefsMap();
		Set<String> keySet = new HashSet<String>( userPrefs.keySet() );
		keySet.addAll( current.keySet() );

		for( String key : keySet ) {
			if( !userPrefs.containsKey( key )) {
				super.getHibernateTemplate().delete( current.get( key ));
			} else {
				UserPref userPref = userPrefs.get( key );
				if( userPref.getId().getWidgetId() == 0 )
					userPref.getId().setWidgetId(widget.getId());

				super.getHibernateTemplate().saveOrUpdate( userPref );
			}
		}
		super.getHibernateTemplate().flush();
	}
	public void updateUserPrefs( Collection<?> widgets ) {
		for( Object widget : widgets )
			updateUserPrefs( ( Widget )widget );
	}
}
