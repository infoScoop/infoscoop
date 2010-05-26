package org.infoscoop.batch.migration.v200to210;

import java.sql.Connection;
import java.sql.ResultSet;

import javax.sql.DataSource;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.infoscoop.batch.migration.HibernateBeansTask;
import org.infoscoop.batch.migration.SQLTask;
import org.infoscoop.dao.model.Searchengine;
import org.infoscoop.util.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class SearchEngineConvertTask implements HibernateBeansTask.BeanTask2 {
	private Document xml;

	public SearchEngineConvertTask() {
	}
	
	public void execute(Project project, Object object) throws BuildException {
		Searchengine bean = (Searchengine) object;
		try {
			Element defaultSearch = (Element) xml.getElementsByTagName(
					"defaultSearch").item(0);
			NodeList searchEngines = defaultSearch
					.getElementsByTagName("searchEngine");
			for (int i = 0; i < searchEngines.getLength(); i++) {
				Element searchEngine = (Element) searchEngines.item(i);
				if (!searchEngine.hasAttribute("defaultSelected")) {
					searchEngine.setAttribute("defaultSelected", "true");
				}
			}
			bean.setDocument(xml);
		} catch (Exception e) {
			throw new BuildException(e);
		}
	}
	
	public void prepare( Project project ) throws BuildException {
		String schemaName = project.getProperty("SCHEMA_NAME");
		String backupTableSuffix = project.getProperty("BACKUP_TABLE_SUFFIX");
		
		String queryString = "select data from " + schemaName
				+ ".is_searchengines" + backupTableSuffix + " where temp=0";
		
		DataSource dataSource = ( DataSource )SQLTask.getContext().getBean("dataSource");
		Connection connection = null;
		
		try {
			connection = dataSource.getConnection();
			
			ResultSet rs = connection.createStatement().executeQuery( queryString );
			if( rs.next() ) {
				String data = rs.getString("data");
				xml = (Document) XmlUtil.string2Dom(data);
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
