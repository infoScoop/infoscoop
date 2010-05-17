package org.infoscoop.batch.migration;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.sql.DataSource;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SQLTask extends Task {
	public static String PROPERTY_SCHEMA_NAME = "SCHEMA_NAME";
	private static ApplicationContext context; // dirty na scope ...
	public static ApplicationContext getContext() {
		if( context == null ) {
			try {
				context = new ClassPathXmlApplicationContext( new String[]{"datasource.xml"});
			} catch( Exception ex ) {
				throw new RuntimeException( ex.getMessage(),ex );
			}
		}
		
		return context;
	}
	
	private static Connection connection;
	
	boolean quiet = false;
	public void setQuiet( boolean quiet ) {
		this.quiet = quiet;
	}

	List SQLs = new ArrayList();
	public SQLDef createSQL() {
		SQLDef sqlDef = new SQLDef(super.getProject().getProperty(PROPERTY_SCHEMA_NAME));
		
		SQLs.add( sqlDef );
		
		return sqlDef;
	}
	
	public void addSQL(String sql){
		SQLDef sqlDef = new SQLDef(super.getProject().getProperty(PROPERTY_SCHEMA_NAME));
		sqlDef.addText(sql);
		
		SQLs.add( sqlDef );
	}
	
	List properties = new ArrayList();
	public Property createProperty() {
		Property property = new Property();
		
		properties.add( property );
		
		return property;
	}
	
	public void execute() throws BuildException {
		try {
			if( connection == null ) {
				synchronized( SQLTask.class ) {
					if( connection == null )
						connection = (( DataSource )getContext().getBean("dataSource")).getConnection();
				}
			}
			
			try {
				for( Iterator it = SQLs.iterator(); it.hasNext(); ) {
					SQLDef sqlDef = (SQLDef)it.next();
					String sqls = replaceProperty( sqlDef.getSQLString());
					for( String sql : sqls.split(";") ) {
						log( sql, Project.MSG_INFO );
						Statement stat = connection.createStatement();
						stat.execute( sql );
					}
				}
			} catch( Exception ex ) {
				int level = quiet? Project.MSG_DEBUG:Project.MSG_ERR;
				log("error:"+ ex.getMessage()+" cause "+ex.getCause(),level );
				StackTraceElement[] traces = ex.getStackTrace();
				for( int i=0;i<traces.length;i++ )
					log("\t"+traces[i],level );
				
				if( quiet )
					return;
				
				throw new BuildException( ex );
			} finally {
//				connection.close();
			}
			
			log("success.");
		} catch (SQLException e) {
			throw new BuildException( e );
		}
		
	}
	
	public String replaceProperty( String sql ) {
		for( int i=0;i<properties.size();i++ ) {
			Property property = ( Property )properties.get( i );
			sql = sql.replaceAll("\\$\\{"+property.name+"\\}",property.value );
		}
		
		return sql;
	}
	
	public static class Property {
		private String name;
		public void setName( String name ) {
			this.name = name;
		}
		
		private String value;
		public void addText( String value ) {
			this.value = value;
		}
	}
	
	@Override
	protected void finalize() throws Throwable {
		try {
			if( connection != null ) {
				synchronized( SQLTask.class ) {
					connection.close();
					connection = null;
				}
			}
		} catch( Exception ex ) {
			// ignore
		}
		
		super.finalize();
	}
}
