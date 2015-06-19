/* infoScoop OpenSource
 * Copyright (C) 2010 Beacon IT Inc.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0-standalone.html>.
 */

package org.infoscoop.dao;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.infoscoop.dao.model.Properties;
import org.infoscoop.util.SpringUtil;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class PropertiesDAO extends HibernateDaoSupport {
	
	private static Log log = LogFactory.getLog(PropertiesDAO.class);

	public static PropertiesDAO newInstance() {
        return (PropertiesDAO)SpringUtil.getContext().getBean("propertiesDAO");
	}
	
	public Properties findProperty( String name, String squareId ) {
		//select value from ${schema}.properties where id=?
		String queryString = "from Properties where Id.Id =? and Id.Squareid = ?";
		
		List properties = super.getHibernateTemplate().find( queryString,
				new Object[]{ name, squareId });
		if( properties.isEmpty() )
			return null;
		
		return ( Properties )properties.get(0);
	}
    
	/**
	 * Return the list of property including the detail.
	 * The list includes Map(id, value, desc).
	 * @return
	 */
	public List findAllProperties(String squareId){
		//select * from ${schema}.properties
		String queryString = "from Properties where Id.Squareid = ? order by Advanced";
		
		return super.getHibernateTemplate().find( queryString, squareId );
	}
	
	/**
	 * Update the data.
	 */
	public void update(String id, String value, String squareId) {
		Properties property = findProperty( id, squareId );
		if( property == null )
			return;
		
		property.setValue( value );

		if(log.isInfoEnabled())
			log.info("param[]: update successfully.");
	}
	
	public static void main(String args[]){
		System.out.println(newInstance().findAllProperties("default"));
	}
	
	public void copySquare(String squareId, String defaultSquareId) {
		Session session  = super.getSession();

		Query q = (Query)session.getNamedQuery("is_properties.copySquare");
		q.setString("squareId", squareId);
		q.setString("defaultSquareId", defaultSquareId);
		q.executeUpdate();
	}
}
