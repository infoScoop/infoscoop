package org.infoscoop.batch.migration.v230to300;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Set;
import java.util.TreeSet;

import javax.sql.DataSource;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.infoscoop.batch.migration.HibernateBeansTask;
import org.infoscoop.batch.migration.SQLTask;
import org.infoscoop.dao.OAuthConsumerDAO;
import org.infoscoop.dao.OAuthTokenDAO;
import org.infoscoop.dao.model.OAuthConsumerProp;
import org.infoscoop.dao.model.OAuthGadgetUrl;
import org.infoscoop.util.SpringUtil;
import org.w3c.util.UUID;

/**
 * Convert OAuth data.
 * @author nishiumi
 *
 */
public class OAuthConvertTask implements HibernateBeansTask.BeanTask2 {
	private Connection bakConnection;
	private Session session;

	static {
		String beanDefinitionsParam = "datasource.xml,dataaccess.xml";
		String[] beanDefinitions = beanDefinitionsParam.split(",");
		for(int i = 0; i < beanDefinitions.length; i++){
			beanDefinitions[i] = beanDefinitions[i].trim();
		}
		
		SpringUtil.initContext(beanDefinitions);
	}
	public OAuthConvertTask() {
	}
	
	public void prepare( Project project ) throws BuildException {
		String schemaName = project.getProperty("SCHEMA_NAME");
		String backupTableSuffix = project.getProperty("BACKUP_TABLE_SUFFIX");
		String dbms = project.getProperty("DBMS");
		DataSource dataSource = ( DataSource )SQLTask.getContext().getBean("dataSource");
		
		SessionFactory sessionFactory = ( SessionFactory )SQLTask.getContext().getBean("sessionFactory");
		session = sessionFactory.openSession();
		
		try {
			String queryString = "select service_name, consumer_key, consumer_secret, signature_method, gadget_url, gadget_url_key from "+schemaName+"IS_OAUTH_CONSUMERS" + backupTableSuffix;
			project.log(dbms + ": " + queryString);
			
			ResultSet resultSet = null;
			
			bakConnection = dataSource.getConnection();
			PreparedStatement pstat = bakConnection.prepareStatement( queryString );
			
			project.log("Start OAuth Consumer migration.");
			boolean isSuccess = false;
			try {
				resultSet = pstat.executeQuery();
				while( resultSet.next()) {
					String service_name = resultSet.getString("service_name");
					String consumer_key = resultSet.getString("consumer_key");
					String consumer_secret = resultSet.getString("consumer_secret");
					String signature_method = resultSet.getString("signature_method");
					
					String oauth_consumer_id = new UUID().toString();
					
					OAuthConsumerProp consumer = new OAuthConsumerProp(oauth_consumer_id);
					consumer.setServiceName(service_name);
					consumer.setConsumerKey(consumer_key);
					consumer.setConsumerSecret(consumer_secret);
					consumer.setSignatureMethod(signature_method);
					consumer.setDescription("");
					
					String gadget_url = resultSet.getString("gadget_url");
					String gadget_url_key = resultSet.getString("gadget_url_key");
					
					OAuthGadgetUrl gadgetUrl = new OAuthGadgetUrl();
					gadgetUrl.setFkOauthId(oauth_consumer_id);
					gadgetUrl.setGadgetUrl(gadget_url);
					gadgetUrl.setGadgetUrlKey(gadget_url_key);
					
					Set<OAuthGadgetUrl> gadgetUrlSet = new TreeSet<OAuthGadgetUrl>();
					gadgetUrlSet.add(gadgetUrl);
					
					consumer.setOAuthGadgetUrl(gadgetUrlSet);
					OAuthConsumerDAO.newInstance().save(consumer);
					
					project.log(" *convert oauth consumer: service_name=" + service_name);
					project.log(" *convert oauth gadgetUrl: URL=" + gadgetUrl);
				}
				project.log("OAuth Consumer migration is succeed.");
				isSuccess = true;
			}finally {
				if( resultSet != null )
					resultSet.close();
				
				if(!isSuccess)
					project.log("OAuth Consumer migration is failed.");
			}
			
			project.log("Start OAuth Token migration.");
			isSuccess = false;
			try {
				if("mysql".equalsIgnoreCase(dbms.trim())){
					queryString = "select UID, gadget_url, gadget_url_key, service_name, request_token, access_token, token_secret from "+schemaName+"IS_OAUTH_TOKENS" + backupTableSuffix;
				}else{
					queryString = "select \"UID\", gadget_url, gadget_url_key, service_name, request_token, access_token, token_secret from "+schemaName+"IS_OAUTH_TOKENS" + backupTableSuffix;
				}
				project.log(dbms + ": " + queryString);
				resultSet = null;
				
				bakConnection = dataSource.getConnection();
				pstat = bakConnection.prepareStatement( queryString );

				resultSet = pstat.executeQuery();
				while( resultSet.next()) {
					String uid = resultSet.getString("UID");
					String service_name = resultSet.getString("service_name");
					String request_token = resultSet.getString("request_token");
					String access_token = resultSet.getString("access_token");
					String token_secret = resultSet.getString("token_secret");
					
					String gadget_url = resultSet.getString("gadget_url");
					
					OAuthTokenDAO.newInstance().saveAccessToken(uid, gadget_url, service_name, request_token, access_token, token_secret);
					project.log(" *convert oauth token: gadget_url=" + gadget_url + ", service_name=" + service_name);
				}
				project.log("OAuth Token migration is succeed.");
				isSuccess = true;
			}finally {
				if( resultSet != null )
					resultSet.close();
				
				if(!isSuccess)
					project.log("OAuth Token migration is failed.");
			}

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

	public void execute(Project project, Object object) throws BuildException {
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
