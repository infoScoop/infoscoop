package org.infoscoop.batch.migration.v1300to1400;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.infoscoop.batch.migration.HibernateBeansTask;
import org.infoscoop.batch.migration.SQLTask;
import org.infoscoop.dao.model.Portallayout;

public class PortalLayoutTask implements HibernateBeansTask.BeanTask2 {
	private Map<String,String> data = new HashMap<String,String>();
	public void execute(Project project, Object object) throws BuildException {
		Portallayout portalLayout = ( Portallayout )object;
		String name = portalLayout.getName();
		if(!"contentFooter".equals( name ) && data.containsKey( name ) )
			portalLayout.setLayout( data.get( name ));
	}
	
	public void prepare(Project project) throws BuildException {
		data.clear();
		
		String schemaName = project.getProperty("SCHEMA_NAME");
		String backupTableSuffix = project.getProperty("BACKUP_TABLE_SUFFIX");
		
		String queryString = "select name,layout from "+schemaName+".portallayout"+backupTableSuffix;
		DataSource dataSource = ( DataSource )SQLTask.getContext().getBean("dataSource");
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			ResultSet resultSet = connection.createStatement().executeQuery( queryString );
			while( resultSet.next() ) {
				String name = resultSet.getString("name");
				String layout = resultSet.getString("layout");
				
				data.put( name,layout );
			}
		} catch( Exception ex ) {
			throw new BuildException( ex );
		} finally {
			try {
				if( connection != null )
					connection.close();
			} catch( Exception ex2 ) { }
		}
	}
	public void finish(Project project) throws BuildException {
	}
}