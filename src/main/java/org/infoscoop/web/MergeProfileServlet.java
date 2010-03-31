package org.infoscoop.web;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Restrictions;
import org.infoscoop.dao.model.Preference;
import org.infoscoop.dao.model.Tab;
import org.infoscoop.dao.model.UserPref;
import org.infoscoop.dao.model.UserprefId;
import org.infoscoop.dao.model.Widget;
import org.infoscoop.util.SpringUtil;

public class MergeProfileServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String currentUid = ( String )req.getSession().getAttribute("Uid");
		String uid = req.getParameter("Uid");
		if( !currentUid.equalsIgnoreCase( uid )) {
			resp.sendError( 500 );
			return;
		}
		
		String isPreview = req.getParameter(CheckDuplicateUidFilter.IS_PREVIEW);
		if( "true".equalsIgnoreCase( isPreview )) {
			resp.sendRedirect("index.jsp?" + CheckDuplicateUidFilter.IS_PREVIEW + "=true&Uid=" + uid);
			return;
		}
		
		SessionFactory sessionFactory = ( SessionFactory )SpringUtil.getBean("sessionFactory");
		Session session = sessionFactory.openSession();
		Transaction transaction = session.beginTransaction();
		try {
			String[] queries = new String[] {
				"delete from Cache where lower(Uid) = ? and Uid != ?",
				"delete from Rsscache where lower(id.Uid) = ? and id.Uid != ?",
				"delete from Preference where lower(Uid) = ? and Uid != ?",
				"delete from Tab where lower(id.Uid) = ? and id.Uid != ?",
				"delete from Widget where lower(id.Uid) = ? and id.Uid != ?",
				"delete from AuthCredential where lower(Uid) = ? and Uid != ?",
				"update AuthCredential set Uid = ? where Uid = ?"
			};
			
			for( int i=0;i<queries.length;i++ ) {
				Query query = session.createQuery( queries[i] );
				query.setString( 0,uid.toLowerCase() );
				query.setString( 1,uid );
				query.executeUpdate();
			}
			
			if( !uid.toLowerCase().equals( uid )) {
				Preference preference = ( Preference )session.get( Preference.class,uid );
				Preference newPref = new Preference( uid.toLowerCase() );
				newPref.setData( preference.getData());
				session.save( newPref );
				session.delete( preference );
				
				for( Iterator ite=session.createCriteria( Tab.class )
					.add( Restrictions.eq("id.Uid",uid )).list().iterator();ite.hasNext(); ) {
					Tab tab = ( Tab )ite.next();
					Tab newTab = cloneTab( uid.toLowerCase(),tab );
					
					session.save( newTab );
					session.delete( tab );
				}
				
				for( Iterator ite=session.createCriteria( Widget.class )
						.add( Restrictions.eq("Uid",uid )).list().iterator();ite.hasNext();) {
					Widget widget = ( Widget )ite.next();
					Widget newWidget = cloneWidget( uid.toLowerCase(),widget );
					
					session.save( newWidget );
					session.flush();
					for( UserPref userPref : newWidget.getUserPrefsMap().values() ) {
						userPref.getId().setWidgetId( newWidget.getId());
						session.save( userPref );
					}
					session.delete( widget );
				}
			}
			
			transaction.commit();
			session.flush();
		} catch( Exception ex ) {
			transaction.rollback();
			throw new ServletException( ex );
		} finally {
			session.close();
		}

		req.getSession().setAttribute("Uid",uid.toLowerCase() );
		resp.sendRedirect("index.jsp");
	}
	
	private Tab cloneTab( String uid,Tab tab ) {
		Tab newTab = new Tab(uid, tab.getTabId());
		newTab.setData( tab.getData() );
		newTab.setDefaultUid( tab.getDefaultUid() );
		newTab.setName( tab.getName() );
		newTab.setOrder( tab.getOrder() );
		newTab.setType( tab.getType() );
		newTab.setWidgetLastModified( tab.getWidgetLastModified() );
		
		return newTab;
	}
	private Widget cloneWidget( String uid,Widget widget ) {
		Widget newWidget = new Widget();
		newWidget.setUid( uid );
		newWidget.setColumn( widget.getColumn() );
		
		Map<String,UserPref> userPrefs = widget.getUserPrefsMap();
		for( String key : userPrefs.keySet() ) {
			UserPref userPref = new UserPref(new UserprefId(widget.getId(), key), widget);
			userPref.setValue( (( UserPref )userPrefs.get( key )).getValue() );
			newWidget.getUserPrefsMap().put( key, userPref );
		}
		
		newWidget.setDefaultUid( widget.getDefaultUid() );
		newWidget.setDeleteDate( widget.getDeleteDate() );
		newWidget.setHref( widget.getHref() );
		newWidget.setIgnoreHeader( widget.isIgnoreHeader() );
		newWidget.setIsStatic( widget.getIsStatic() );
		newWidget.setParentId( widget.getParentId() );
		newWidget.setSiblingId( widget.getSiblingId() );
		newWidget.setTabId( widget.getTabId() );
		newWidget.setTitle( widget.getTitle() );
		newWidget.setType( widget.getType() );
		newWidget.setWidgetId( widget.getWidgetId() );
		
		return newWidget;
	}
}