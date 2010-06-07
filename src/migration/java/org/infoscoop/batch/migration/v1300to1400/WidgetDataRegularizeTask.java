package org.infoscoop.batch.migration.v1300to1400;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import javax.sql.*;


import org.apache.tools.ant.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.infoscoop.batch.migration.*;
import org.infoscoop.dao.WidgetDAO;
import org.infoscoop.dao.model.USERPREFPK;
import org.infoscoop.dao.model.UserPref;
import org.infoscoop.dao.model.Widget;
import org.infoscoop.util.SpringUtil;
import org.json.JSONObject;


public class WidgetDataRegularizeTask implements HibernateBeansTask.BeanTask2 {
	private Connection bakConnection;
	private PreparedStatement pstat;
	private Session session;
	
	static {
		String beanDefinitionsParam = "datasource.xml,dataaccess.xml,commands.xml,services.xml,proxyrequest.xml,accountservices.xml";
		String[] beanDefinitions = beanDefinitionsParam.split(",");
		for(int i = 0; i < beanDefinitions.length; i++){
			beanDefinitions[i] = beanDefinitions[i].trim();
		}
		
		SpringUtil.initContext(beanDefinitions);
	}
	
	public WidgetDataRegularizeTask() {
	}
	
	public void execute( Project project,Object object ) throws BuildException {
		Widget widget = ( Widget )object;
		
		try {
			pstat.setString( 1,widget.getUid() );
			pstat.setString( 2,widget.getWidgetid() );
			pstat.setString( 3,widget.getTabid() );
			pstat.setLong( 4,widget.getDeletedate() );
			
			ResultSet resultSet = null;
			try {
				resultSet = pstat.executeQuery();
				if( resultSet.next()) {
					project.log("Regularization "+widget.getId());
					
					Map<String,UserPref> userPrefs = widget.getUserPrefs();
					
					JSONObject data = new JSONObject( resultSet.getString("data"));
					for( Iterator keys=data.keys();keys.hasNext();) {
						String key = ( String )keys.next();
						String value = data.getString( key );
						
						if( "".equals( value ) || value == null ) {
							userPrefs.remove( key );
						} else {
							UserPref userPref = new UserPref( new USERPREFPK( null,key ));
							userPref.setValue( value );
							
							userPrefs.put( key,userPref );
						}
					}
				}
				
				WidgetDAO.newInstance().updateWidget( widget );
			} finally {
				if( resultSet != null )
					resultSet.close();
			}
		} catch( Exception ex ) {
			ex.printStackTrace();
			throw new BuildException("Regularization "+widget.getId()+" Failed. "+ex.getClass(), ex );
		}
	}
	
	public void prepare( Project project ) throws BuildException {
		String schemaName = project.getProperty("SCHEMA_NAME");
		String backupTableSuffix = project.getProperty("BACKUP_TABLE_SUFFIX");
		
//		project.log("schemaName:"+schemaName );
//		project.log("backupTableSuffix: "+backupTableSuffix );
		String sq = "select distinct id from "+schemaName+".is_tabs where \"UID\"="+schemaName+".widget"+backupTableSuffix+".\"UID\"";
		String queryString = "select data from "+schemaName+".widget"+backupTableSuffix+" where"
			+" \"UID\"=? and widgetid=? and tabid=? and deletedate=? and (tabId = '-1' or tabId in ("+sq+"))";

		try {
			DataSource dataSource = ( DataSource )SQLTask.getContext().getBean("dataSource");
			bakConnection = dataSource.getConnection();
			pstat = bakConnection.prepareStatement( queryString );

			SessionFactory sessionFactory = ( SessionFactory )SQLTask.getContext().getBean("sessionFactory");
			session = sessionFactory.openSession();
		} catch( Exception ex ) {
			if( bakConnection != null ) {
				try {
					bakConnection.close();
					bakConnection = null;
				} catch( Exception ex2 ) { }
			}
			throw new BuildException( ex );
		}
	}
	
	public void finish( Project project ) throws BuildException {
		if( bakConnection != null ) {
			try {
				bakConnection.close();
				bakConnection = null;
			} catch( Exception ex ) {
				throw new BuildException( ex );
			}
		}

		if( session != null )
			session.close();
	}
}
