package org.infoscoop.batch.migration.v1300to1400;

import java.sql.*;

import javax.sql.*;


import org.apache.tools.ant.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.infoscoop.batch.migration.*;
import org.infoscoop.dao.model.Gadget;


public class GadgetConvertTask implements HibernateBeansTask.BeanTask2 {
	private Connection connection;
	private PreparedStatement pstat;
	
	private Session session;
	
	public GadgetConvertTask() {
	}
	
	public void execute( Project project,Object object ) throws BuildException {
		Gadget bean = ( Gadget )object;
		String type = bean.getType();
		
		try {
			pstat.setString(1,"upload__"+type );
			ResultSet rs = pstat.executeQuery();
			
			while( rs.next()) {
				String fileType = rs.getString("filetype");
				byte[] data = rs.getBytes("data");
				
				System.out.println( type+","+"/"+","+fileType );
				Gadget gadget = new Gadget();
				gadget.setType( type );
				gadget.setPath("/");
				gadget.setName( fileType );
				gadget.setData( data );
				
				session.save( gadget );
				session.flush();
			}
		} catch( Exception ex ) {
			finish( project );
			
			throw new BuildException( ex );
		}
	}

	public void prepare(Project project) throws BuildException {
		DataSource dataSource = ( DataSource )SQLTask.getContext().getBean("dataSource");
		Connection connection = null;
		
		String schemaName = project.getProperty("SCHEMA_NAME");
		String backupTableSuffix = project.getProperty("BACKUP_TABLE_SUFFIX");
		
		String queryString = "select filetype,data from "+schemaName
			+".gadget"+backupTableSuffix +" where type=? and filetype != 'gadget'";
		
		try {
			connection = dataSource.getConnection();
			pstat = connection.prepareStatement( queryString );
		} catch( Exception ex ) {
			finish( project );
			
			throw new BuildException( ex );
		}
		
		SessionFactory sessionFactory = ( SessionFactory )SQLTask.getContext().getBean("sessionFactory");
		session = sessionFactory.openSession();
	}
	
	public void finish( Project project ) throws BuildException {
		if( connection != null ) {
			try {
				connection.close();
			} catch( Exception ex ) {
				//ignore
			}
		}
		
		if( session != null )
			session.close();
	}
}
