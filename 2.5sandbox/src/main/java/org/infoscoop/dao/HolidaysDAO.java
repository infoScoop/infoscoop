package org.infoscoop.dao;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;


import org.infoscoop.dao.model.HOLIDAYSPK;
import org.infoscoop.dao.model.Holidays;
import org.infoscoop.util.SpringUtil;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class HolidaysDAO extends HibernateDaoSupport {
//	private static Log log = LogFactory.getLog(ForbiddenURLDAO.class);
	
	public static HolidaysDAO newInstance() {
        return (HolidaysDAO)SpringUtil.getContext().getBean("holidaysDAO");
	}
	
	public Holidays getHoliday( String lang,String country ) {
		return ( Holidays )super.getHibernateTemplate().get( Holidays.class,new HOLIDAYSPK( lang,country ));
	}
	public void updateHoliday( String lang,String country,String data ) {
		Holidays holiday = new Holidays(new HOLIDAYSPK( lang,country ));
		holiday.setData( data );
		holiday.setUpdatedat( new Date());
		
		super.getHibernateTemplate().saveOrUpdate( holiday );
	}
	public void deleteHoliday( String lang,String country ) {
		Holidays holiday = ( Holidays )super.getHibernateTemplate().get(
				Holidays.class,new HOLIDAYSPK( lang,country ));
		if( holiday != null )
			super.getHibernateTemplate().delete( holiday );
	}
	
	public Collection getHolidayLocales() {
		String queryString = "select distinct id.Lang,id.Country from Holidays";
		Collection langCountries = super.getHibernateTemplate().find( queryString );
		
		Collection locales = new HashSet();
		for( Iterator ite=langCountries.iterator();ite.hasNext();) {
			Object[] langCountry = ( Object[] )ite.next();
			
			locales.add( new Locale( ( String )langCountry[0],( String )langCountry[1]));
		}
		
		return locales;
	}
}
