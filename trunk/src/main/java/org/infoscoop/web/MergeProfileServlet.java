package org.infoscoop.web;

import java.io.IOException;
import java.util.HashMap;
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
import org.infoscoop.dao.model.Preference;
import org.infoscoop.dao.model.TABPK;
import org.infoscoop.dao.model.Tab;
import org.infoscoop.dao.model.USERPREFPK;
import org.infoscoop.dao.model.UserPref;
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
					.add( Expression.eq("id.Uid",uid )).list().iterator();ite.hasNext(); ) {
					Tab tab = ( Tab )ite.next();
					Tab newTab = cloneTab( uid.toLowerCase(),tab );
					
					session.save( newTab );
					session.delete( tab );
				}
				
				for( Iterator ite=session.createCriteria( Widget.class )
						.add( Expression.eq("Uid",uid )).list().iterator();ite.hasNext();) {
					Widget widget = ( Widget )ite.next();
					Widget newWidget = cloneWidget( uid.toLowerCase(),widget );
					
					session.save( newWidget );
					session.flush();
					for( UserPref userPref : newWidget.getUserPrefs().values() ) {
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
		Tab newTab = new Tab( new TABPK( uid,tab.getId().getId() ) );
		newTab.setData( tab.getData() );
		newTab.setDefaultuid( tab.getDefaultuid() );
		newTab.setName( tab.getName() );
		newTab.setOrder( tab.getOrder() );
		newTab.setType( tab.getType() );
		newTab.setWidgetlastmodified( tab.getWidgetlastmodified() );
		
		return newTab;
	}
	private Widget cloneWidget( String uid,Widget widget ) {
		Widget newWidget = new Widget();
		newWidget.setUid( uid );
		newWidget.setColumn( widget.getColumn() );
		
		Map<String,UserPref> userPrefs = widget.getUserPrefs();
		for( String key : userPrefs.keySet() ) {
			UserPref userPref = new UserPref( new USERPREFPK( null,key ));
			userPref.setValue( (( UserPref )userPrefs.get( key )).getValue() );
			newWidget.getUserPrefs().put( key, userPref );
		}
		
		newWidget.setDefaultuid( widget.getDefaultuid() );
		newWidget.setDeletedate( widget.getDeletedate() );
		newWidget.setHref( widget.getHref() );
		newWidget.setIgnoreheader( widget.getIgnoreheader() );
		newWidget.setIsstatic( widget.getIsstatic() );
		newWidget.setParentid( widget.getParentid() );
		newWidget.setSiblingid( widget.getSiblingid() );
		newWidget.setTabid( widget.getTabid() );
		newWidget.setTitle( widget.getTitle() );
		newWidget.setType( widget.getType() );
		newWidget.setWidgetid( widget.getWidgetid() );
		
		return newWidget;
	}
}