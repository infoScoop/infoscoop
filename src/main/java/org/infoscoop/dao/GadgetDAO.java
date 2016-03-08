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

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.infoscoop.dao.model.Gadget;
import org.infoscoop.util.SpringUtil;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class GadgetDAO extends HibernateDaoSupport {
	public static GadgetDAO newInstance(){
		
        return (GadgetDAO)SpringUtil.getContext().getBean("gadgetDAO");
    }
	
	public static void main(String args[]) throws IOException{
	}
	
	public Gadget select( String type, String squareid ) {
		return select( type,"/",type +".xml",squareid);
	}
	public List<Gadget> selectGadgetXMLs(String squareid) {
		String queryString = "from Gadget where path = '/' and name in "
			+"( select concat(type,'.xml') from Gadget ) and name = concat(type,'.xml') and Squareid=?";
		
		return super.getHibernateTemplate().find( queryString, squareid );
	}
	public Gadget select(String type, String path,String name,String squareid) {
		//select data from ${schema}.gadget where type = ? and fileType = ?
		List result = ( List )super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass( Gadget.class )
					.add( Expression.eq( Gadget.PROP_TYPE,type ))
					.add( Expression.eq( Gadget.PROP_PATH,path ))
					.add( Expression.eq( Gadget.PROP_SQUARE_ID, squareid ))
					.add( Expression.eq( Gadget.PROP_NAME,name )));
		if( result == null || result.size() == 0 )
			return null;
		
		return ( Gadget )result.get(0);
	}
	
	public void insert(String type,String path,String name, byte[] xml, String squareid) {
		_insert( type,path,name,xml,squareid );
	}
	public void _insert(String type,String path,String name, byte[] xml, String squareid) {
		Gadget gadget = new Gadget();
		gadget.setType( type );
		gadget.setPath( path );
		gadget.setName( name );
		gadget.setData( xml );
		gadget.setLastmodified( new Date());
		gadget.setSquareid(squareid);
		
		super.getHibernateTemplate().save( gadget );
	}
	
	public void update( String type,String path,String name,byte[] data, String squareid ) {
		Gadget resource = select( type,path,name,squareid );
		
		if( resource == null )
			return;
		
		resource.setData( data );
		resource.setLastmodified( new Date() );
		
		super.getHibernateTemplate().update( resource );
	}
	
	public boolean delete( String type,String path,String name, String squareid ) {
		Gadget resource = select( type,path,name,squareid );
		
		if( resource == null )
			return false;
		
		super.getHibernateTemplate().delete( resource );
		return true;
	}
	
	public int deleteType(String type, String squareid) {
		//delete from ${schema}.gadget where type = ?
		String queryString = "delete from Gadget where Type = ? and Squareid = ?";
		
		return super.getHibernateTemplate().bulkUpdate( queryString,
				new Object[] { type, squareid } );
	}
	
	public int deleteBySquareId(String squareid) {
		String queryString = "delete from Gadget where Squareid = ?";
		
		return super.getHibernateTemplate().bulkUpdate( queryString,
				new Object[] { squareid } );
	}
	
	public List<Gadget> list( String type, String squareid ) {
		return super.getHibernateTemplate().findByCriteria( DetachedCriteria.forClass( Gadget.class )
				.add( Expression.eq( Gadget.PROP_TYPE,type ))
				.add( Expression.eq( Gadget.PROP_SQUARE_ID, squareid ))
				.addOrder( Order.asc( Gadget.PROP_NAME )));
	}
	
	public List<Gadget> list( String type,String path, String squareid ) {
		return super.getHibernateTemplate().findByCriteria( DetachedCriteria.forClass( Gadget.class )
				.add( Expression.eq( Gadget.PROP_TYPE,type ))
				.add( Expression.eq( Gadget.PROP_PATH,path ))
				.add( Expression.eq( Gadget.PROP_SQUARE_ID, squareid ))
				.addOrder( Order.asc( Gadget.PROP_NAME )));
	}

	public List<Gadget> selectConfsByType(List<String> types, String squareid) {
		return super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(Gadget.class).add(
						Expression.in(Gadget.PROP_NAME, types)).add(
						Expression.eq(Gadget.PROP_PATH, "/")).add(
						Expression.eq( Gadget.PROP_SQUARE_ID, squareid )));
	}

	public void copySquare(String squareId, String defaultSquareId) {
		Session session = super.getSession();
		Query q = (Query)session.getNamedQuery("is_gadgets.copySquare");
		q.setString("squareId", squareId);
		q.setString("defaultSquareId", defaultSquareId);
		q.executeUpdate();
	}
}
