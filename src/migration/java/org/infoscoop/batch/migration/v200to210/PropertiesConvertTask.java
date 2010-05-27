package org.infoscoop.batch.migration.v200to210;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.infoscoop.batch.migration.HibernateBeansTask;
import org.infoscoop.batch.migration.SQLTask;
import org.infoscoop.dao.model.Properties;


public class PropertiesConvertTask implements HibernateBeansTask.BeanTask2 {
	private Map<String,String> properties;
	public PropertiesConvertTask() {
	}
	
	public void execute(Project project, Object object) throws BuildException {
		Properties bean = (Properties) object;
		String id = bean.getId();
		String value = properties.get(id);
		if (value != null)
			bean.setValue(value);
	}
	
	public void prepare( Project project ) throws BuildException {
		String schemaName = project.getProperty("SCHEMA_NAME");
		String backupTableSuffix = project.getProperty("BACKUP_TABLE_SUFFIX");
		
		String queryString = "select id,value from "+schemaName
			+"is_properties"+backupTableSuffix;
		
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
