package org.infoscoop.batch.migration.v1300to1400;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import javax.sql.*;


import org.apache.tools.ant.*;
import org.infoscoop.batch.migration.*;
import org.infoscoop.dao.model.Properties;
import org.json.JSONArray;
import org.json.JSONException;


public class PropertiesConvertTask implements HibernateBeansTask.BeanTask2 {
	private Map<String,String> properties;
	public PropertiesConvertTask() {
	}
	
	public void execute( Project project,Object object ) throws BuildException {
		Properties bean = ( Properties )object;
		String id = bean.getId();
		
		if("sideMenuTabs".equals( id )) {
			String value = properties.get( id );
			
			try {
				JSONArray array = new JSONArray( value );
				
				value = "";
				for( int i=0;i<array.length();i++ ) {
					String v = array.getString( i );
					if( v.startsWith("\"") && v.endsWith("\""))
						v = v.substring( 1,v.length() -1 );
					
					value += ( i > 0 ?"|":"")+v.replaceAll("\\|","%7C");
				}
			} catch( JSONException ex ) {
				throw new BuildException( ex );
			}
			
			bean.setValue( value );
			
			project.log("Property["+id+"] => "+value );
		}
		if("menuAutoRefresh".equals( id )) {
			String value = properties.get( "refreshMenu" );
			bean.setValue(value);
		}
	}
	
	public void prepare( Project project ) throws BuildException {
		String schemaName = project.getProperty("SCHEMA_NAME");
		String backupTableSuffix = project.getProperty("BACKUP_TABLE_SUFFIX");
		
		String queryString = "select id,value from "+schemaName
			+".properties"+backupTableSuffix;
		
		DataSource dataSource = ( DataSource )SQLTask.getContext().getBean("dataSource");
		Connection connection = null;
		
		properties = new HashMap<String, String>();
		try {
			connection = dataSource.getConnection();
			
			ResultSet rs = connection.createStatement().executeQuery( queryString );
			while( rs.next() ) {
				String id = rs.getString("id");
				String value = rs.getString("value");
				
				properties.put( id,value );
			}
		} catch( Exception ex ) {
			throw new BuildException( ex );
		} finally {
			if( connection != null ) {
				try {
					connection.close();
					connection = null;
				} catch( Exception ex ) {
				}
			}
		}
	}
	
	public void finish( Project project ) throws BuildException {
	}
}
