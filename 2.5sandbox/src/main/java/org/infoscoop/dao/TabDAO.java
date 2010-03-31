package org.infoscoop.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.infoscoop.dao.model.Tab;
import org.infoscoop.dao.model.Widget;
import org.infoscoop.util.SpringUtil;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * The DAO class to get and update the information of the widget.
 * 
 * @author nakata
 * 
 */
public class TabDAO extends HibernateDaoSupport{

    private static Log log = LogFactory.getLog(TabDAO.class);
    
    public static TabDAO newInstance(){
		
        return (TabDAO)SpringUtil.getContext().getBean("tabDAO");
        
    }
    
    public TabDAO(){}
    
    
	public Tab getTab(String uid, String tabId) {
		return (Tab) super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(Tab.class).add(
						Restrictions.eq("UID", uid)).add(
						Restrictions.eq("tabId", tabId)).addOrder(
						Order.asc("Order")));
	}
	
    public Collection getTabs(String uid) throws Exception{
		Collection tabs = ( Collection ) super.getHibernateTemplate()
			.findByCriteria( DetachedCriteria.forClass( Tab.class )
					.add( Restrictions.eq("id.Uid", uid ))
					.addOrder( Order.asc("Order")));
		
		return tabs;
    }
    public void addTab( Tab tab ){
    	super.getHibernateTemplate().save(tab);
    }
	public void updateTab( Tab tab ) {
//		super.getHibernateTemplate().update(tab);
//		super.getHibernateTemplate().flush();
	}
	public void deleteTab( Tab tab ) {
		super.getHibernateTemplate().delete( tab );
	}
	public void deleteTab( String uid ) {
		String queryString = "delete from Tab where Id.Uid=?";
		
		super.getHibernateTemplate().bulkUpdate( queryString,
				new Object[]{ uid });
	}
	
    public void addDynamicWidget( String uid,String defaultUid,String tabId,Widget widget){
    	WidgetDAO.newInstance().addWidget( uid,defaultUid,tabId, widget, 0);
    }
    public void addStaticWidget( String uid,String defaultUid,String tabId,Widget widget){
    	WidgetDAO.newInstance().addWidget( uid,defaultUid,tabId, widget, 1);
    }
    
    public List<Widget> getStaticWidgetList( Tab tab ) {
    	return getStaticWidgetList( tab.getUid(),tab.getTabId() );
    }
	public List<Widget> getStaticWidgetList( String uid,String tabId ) {
		return getWidgetList( uid,tabId,1 );
	}
	public List<Widget> getDynamicWidgetList( Tab tab ) {
		return getDynamicWidgetList( tab.getUid(),tab.getTabId() );
	}
	public List<Widget> getDynamicWidgetList( String uid,String tabId ) {
		List<Widget> widgetList = getWidgetList( uid,tabId,0 );

		Set widgetIdSet = new HashSet();
		for( Iterator widgets=widgetList.iterator();widgets.hasNext(); )
			widgetIdSet.add( (( Widget )widgets.next()).getWidgetId());
		
		for( Iterator widgets=widgetList.iterator();widgets.hasNext(); ) {
			Widget widget = ( Widget )widgets.next();
			String parentId = widget.getParentId();
			if( parentId == null ) continue;
			
			if( !widgetIdSet.contains( widget.getParentId() ))
				widget.setParentId( null );
		}

		Map siblingMap = new HashMap();
		Set firstSet = new HashSet();
		Map lastWidgetMap = new HashMap();
		for(Iterator it = widgetList.iterator(); it.hasNext();){
			Widget w = (Widget)it.next();
			if(w.getSiblingId() != null && !"".equals(w.getSiblingId()) ){
				siblingMap.put(w.getSiblingId(), w);
			}else{
				firstSet.add(w);
			}
		}
		List resultList = new ArrayList();
		
		for(Iterator it = firstSet.iterator(); it.hasNext();){
			Widget w = (Widget)it.next();
			resultList.add(w);
			lastWidgetMap.put(w.getColumn(), w);
			while(siblingMap.containsKey(w.getWidgetId())){
				w = (Widget)siblingMap.get(w.getWidgetId());
				resultList.add(w);
				lastWidgetMap.put(w.getColumn(), w);
			}
		}
		for (Iterator it = widgetList.iterator(); it.hasNext();) {
			Widget w = (Widget) it.next();
			if (!resultList.contains(w)) {
				Widget lastW = (Widget) lastWidgetMap.get(w.getColumn());
				if (lastW != null)
					w.setSiblingId(lastW.getWidgetId());
				resultList.add(w);
				lastWidgetMap.put(w.getColumn(), w);
			}
		}
		
		return resultList;
	}
	private List<Widget> getWidgetList( String uid,String tabId,int type ) {
		String query = "from Widget where Uid=? and Tabid=? and Deletedate = 0 and Isstatic = ?";
		
		return super.getHibernateTemplate().find( query,
				new Object[] { uid,tabId,new Integer( type ) } );
	}
//select * from ${schema}.widget where uid=? and tabId = ? and siblingId = ? and deleteDate = 0 and isStatic = 0
	public Widget getWidgetBySibling( String uid,String tabId, String siblingId) {
		String query;
		if( !"".equals( siblingId ) ) {
			query = "from Widget where Uid=? and Tabid=? and Siblingid=? and Deletedate = 0 and Isstatic=0";
		} else {
			query = "from Widget where Uid=? and Tabid=? and ( Siblingid=? or Siblingid is NULL ) and Deletedate = 0 and Isstatic=0";
		}
		
		return findWidget( query,new Object[] { uid,tabId,siblingId });
	}
	public Widget getSubWidgetBySibling( String uid,String tabId,String siblingId,String parentId,String widgetId ) {
		String query;
		if( !"".equals( siblingId ) ) {
			query = "from Widget where Uid=? and Tabid=? and Siblingid=? and Parentid=? and Widgetid != ? and Deletedate = 0 and Isstatic=0";
		} else {
			query = "from Widget where Uid=? and Tabid=? and ( Siblingid=? or Siblingid is NULL ) and Parentid=? and Widgetid != ? and Deletedate = 0 and Isstatic=0";
		}
		
		return findWidget( query,new Object[] { uid,tabId,siblingId,parentId,widgetId });
	}
	public Widget getColumnWidgetBySibling( String uid,String tabId,String siblingId,Integer column,String widgetId ) {
		String query;
		if( !"".equals( siblingId ) ) {
			query = "from Widget where Uid=? and Tabid=? and Siblingid=? and Column=? and ( Parentid='' or Parentid is NULL ) and Widgetid != ? and Deletedate = 0 and Isstatic=0";
		} else {
			query = "from Widget where Uid=? and Tabid=? and ( Siblingid=? or Siblingid is NULL ) and Column=? and ( Parentid='' or Parentid is NULL ) and Widgetid != ? and Deletedate = 0 and Isstatic=0";
		}
		
		return findWidget( query,new Object[] { uid,tabId,siblingId,column,widgetId });
	}
	
	private Widget findWidget( String queryString,Object[] params ) {
		List result = super.getHibernateTemplate().find( queryString,params );
		if( !result.isEmpty())
			return ( Widget )result.get(0);
		
		return null;
	}
}
