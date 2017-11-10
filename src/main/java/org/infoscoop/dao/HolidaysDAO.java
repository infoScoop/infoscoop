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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;


import org.hibernate.Query;
import org.hibernate.Session;
import org.infoscoop.dao.model.HOLIDAYSPK;
import org.infoscoop.dao.model.Holidays;
import org.infoscoop.util.SpringUtil;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class HolidaysDAO extends HibernateDaoSupport {
//	private static Log log = LogFactory.getLog(ForbiddenURLDAO.class);
	
	public static HolidaysDAO newInstance() {
        return (HolidaysDAO)SpringUtil.getContext().getBean("holidaysDAO");
	}
	
	public Holidays getHoliday( String lang, String country, String squareid ) {
		return ( Holidays )super.getHibernateTemplate().get( Holidays.class,new HOLIDAYSPK( lang,country,squareid ));
	}
	public void updateHoliday( String lang,String country,String data, String squareid ) {
		Holidays holiday = new Holidays(new HOLIDAYSPK( lang,country,squareid ));
		holiday.setData( data );
		holiday.setUpdatedat( new Date());
		
		super.getHibernateTemplate().saveOrUpdate( holiday );
	}
	public void deleteHoliday( String lang,String country, String squareid ) {
		Holidays holiday = ( Holidays )super.getHibernateTemplate().get(
				Holidays.class,new HOLIDAYSPK( lang,country,squareid ));
		if( holiday != null )
			super.getHibernateTemplate().delete( holiday );
	}
	
	public Collection getHolidayLocales(String squareid) {
		String queryString = "select distinct id.Lang,id.Country from Holidays where Id.Squareid = ? order by id.Country,id.Lang";
		Collection langCountries = super.getHibernateTemplate().find( queryString, squareid );
		
		Collection locales = new ArrayList();
		for( Iterator ite=langCountries.iterator();ite.hasNext();) {
			Object[] langCountry = ( Object[] )ite.next();
			
			locales.add( new Locale( ( String )langCountry[0],( String )langCountry[1]));
		}
		
		return locales;
	}

	public int deleteBySquareId(String squareid) {
		String queryString = "delete from Holidays where Id.Squareid = ?";
		return super.getHibernateTemplate().bulkUpdate( queryString,
				new Object[] { squareid } );
	}

	public void copySquare(String squareId, String defaultSquareId) {
		Session session = super.getSession();
		Query sq = session.getNamedQuery("is_holidays.copySquare");
		sq.setString("squareId", squareId);
		sq.setString("defaultSquareId", defaultSquareId);
		sq.executeUpdate();
	}
}
