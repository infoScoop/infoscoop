package org.infoscoop.dao;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.dao.model.Property;
import org.infoscoop.util.SpringUtil;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class PropertiesDAO extends HibernateDaoSupport {
	
	private static Log log = LogFactory.getLog(PropertiesDAO.class);

	public static PropertiesDAO newInstance() {
        return (PropertiesDAO)SpringUtil.getContext().getBean("propertiesDAO");
	}
	
	public Property findProperty( String name ) {
		String queryString = "from Property where name=?";
		
		List properties = super.getHibernateTemplate().find( queryString,
				new Object[]{ name });
		if( properties.isEmpty() )
			return null;
		
		return ( Property )properties.get(0);
	}
    
	/**
	 * Return the list of property including the detail.
	 * The list includes Map(id, value, desc).
	 * @return
	 */
	public List findAllProperties(){
		//select * from ${schema}.properties
		String queryString = "from Property order by Advanced";
		
		return super.getHibernateTemplate().find( queryString );
	}
	
	/**
	 * Update the data.
	 * 
	 * @param res
	 * @param id
	 * @param value
	 * @throws DataResourceException
	 */
	public void update(String id, String value) {
		Property property = findProperty( id );
		if( property == null )
			return;
		
		property.setValue( value );

		if(log.isInfoEnabled())
			log.info("param[]: update successfully.");
	}
	
	public static void main(String args[]){
		System.out.println(newInstance().findAllProperties());
	}
}