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

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.infoscoop.dao.model.Gadget;
import org.infoscoop.util.SpringUtil;
import org.infoscoop.util.XmlUtil;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class GadgetDAO extends HibernateDaoSupport {
	private static Log log = LogFactory.getLog(GadgetDAO.class);
	
	public static GadgetDAO newInstance(){
		
        return (GadgetDAO)SpringUtil.getContext().getBean("gadgetDAO");
    }
	
	public static void main(String args[]) throws IOException{
	}
	
	public Gadget select( String type ) {
		return select( type,"/",type +".xml");
	}
	
	public Element getGadgetElement(String type) {
		Gadget gadget = select(type);
		if (gadget == null)
			return null;
		try {
			String xml = new String(gadget.getData(), "UTF-8");
			return ((Document) XmlUtil.string2Dom(xml)).getDocumentElement();
		} catch (Exception e) {
			log.error("", e);
		}
		return null;
	}
	
	public List<Gadget> selectGadgetXMLs() {
		String queryString = "from Gadget where path = '/' and name in "
			+"( select concat(type,'.xml') from Gadget ) and name = concat(type,'.xml')";
		
		return super.getHibernateTemplate().find( queryString );
	}
	public Gadget select(String type, String path,String name ) {
		//select data from ${schema}.gadget where type = ? and fileType = ?
		List result = ( List )super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass( Gadget.class )
					.add( Expression.eq( Gadget.PROP_TYPE,type ))
					.add( Expression.eq( Gadget.PROP_PATH,path ))
					.add( Expression.eq( Gadget.PROP_NAME,name )));
		if( result == null || result.size() == 0 )
			return null;
		
		return ( Gadget )result.get(0);
	}
	
	public void insert(String type,String path,String name, byte[] xml) {
		_insert( type,path,name,xml );
	}
	public void _insert(String type,String path,String name, byte[] xml) {
		Gadget gadget = new Gadget();
		gadget.setType( type );
		gadget.setPath( path );
		gadget.setName( name );
		gadget.setData( xml );
		gadget.setLastmodified( new Date());
		
		super.getHibernateTemplate().save( gadget );
	}
	
	public void update( String type,String path,String name,byte[] data ) {
		Gadget resource = select( type,path,name );
		
		if( resource == null )
			return;
		
		resource.setData( data );
		resource.setLastmodified( new Date() );
		
		super.getHibernateTemplate().update( resource );
	}
	
	public boolean delete( String type,String path,String name ) {
		Gadget resource = select( type,path,name );
		
		System.out.println( type+","+path+","+name+"="+resource );
		if( resource == null )
			return false;
		
		super.getHibernateTemplate().delete( resource );
		return true;
	}
	
	public int deleteType(String type) {
		//delete from ${schema}.gadget where type = ?
		String queryString = "delete from Gadget where Type = ?";
		
		return super.getHibernateTemplate().bulkUpdate( queryString,
				new Object[] { type } );
	}
	
	public List<Gadget> list( String type ) {
		return super.getHibernateTemplate().findByCriteria( DetachedCriteria.forClass( Gadget.class )
				.add( Expression.eq( Gadget.PROP_TYPE,type ))
				.addOrder( Order.asc( Gadget.PROP_NAME )));
	}
	
	public List<Gadget> list( String type,String path ) {
		return super.getHibernateTemplate().findByCriteria( DetachedCriteria.forClass( Gadget.class )
				.add( Expression.eq( Gadget.PROP_TYPE,type ))
				.add( Expression.eq( Gadget.PROP_PATH,path ))
				.addOrder( Order.asc( Gadget.PROP_NAME )));
	}

	public List<Gadget> selectConfsByType(List<String> types) {
		return super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(Gadget.class).add(
						Expression.in(Gadget.PROP_NAME, types)).add(
						Expression.eq(Gadget.PROP_PATH, "/")));
	}
}
