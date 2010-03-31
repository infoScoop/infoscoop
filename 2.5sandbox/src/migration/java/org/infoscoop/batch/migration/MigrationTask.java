package org.infoscoop.batch.migration;

import java.sql.Connection;

import javax.sql.DataSource;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

public class MigrationTask extends SQLTask {
	
	private String className;
	public void setClass( String className ) {
		this.className = className;
	}
	
	public void execute() throws BuildException {
		
		try {
			DataSource dataSource = ( DataSource )getContext().getBean("dataSource");
			Connection connection = dataSource.getConnection();
			
			Migration migration = ( Migration )Class.forName( className ).newInstance();
			migration.setTask(this);
			migration.setSQLs(SQLs);
			try {
				migration.execute(connection);
				
			} catch( Exception ex ) {
				throw ex;
			} finally {
				connection.close();
			}
			
			log("success.");
		} catch( Exception ex ) {
			int level = quiet? Project.MSG_DEBUG:Project.MSG_ERR;
			log("error:"+ ex.getMessage()+" cause "+ex.getCause(),level );
			StackTraceElement[] traces = ex.getStackTrace();
			for( int i=0;i<traces.length;i++ )
				log("\t"+traces[i],level );
			
			if( quiet )
				return;
			
			throw new BuildException( ex );
		}
	}
}
