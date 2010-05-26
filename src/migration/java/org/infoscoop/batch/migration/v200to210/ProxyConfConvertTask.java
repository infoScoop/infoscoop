package org.infoscoop.batch.migration.v200to210;

import java.sql.Connection;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.sql.DataSource;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.infoscoop.batch.migration.HibernateBeansTask;
import org.infoscoop.batch.migration.SQLTask;
import org.infoscoop.dao.model.Proxyconf;


public class ProxyConfConvertTask implements HibernateBeansTask.BeanTask2 {
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
	"yyyy-MM-dd HH:mm:ss.SSS");
	private String data;
	private Date lastmodified;
	
	public ProxyConfConvertTask() {
	}
	
	public void execute(Project project, Object object) throws BuildException {
		Proxyconf bean = (Proxyconf) object;
		bean.setData(data);
		bean.setLastmodified(lastmodified);
	}
	
	public void prepare( Project project ) throws BuildException {
		String schemaName = project.getProperty("SCHEMA_NAME");
		String backupTableSuffix = project.getProperty("BACKUP_TABLE_SUFFIX");
		
		String queryString = "select data,lastmodified from "+schemaName
			+".is_proxyconfs"+backupTableSuffix+" where temp=0";
		
		DataSource dataSource = ( DataSource )SQLTask.getContext().getBean("dataSource");
		Connection connection = null;
		
		try {
			connection = dataSource.getConnection();
			
			ResultSet rs = connection.createStatement().executeQuery( queryString );
			if( rs.next() ) {
				data = rs.getString("data");
				lastmodified = parseDate(rs.getString("lastmodified"));
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

	private Date parseDate(String value) throws ParseException {
		return DATE_FORMAT.parse(value);
	}
}
