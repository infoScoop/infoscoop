package org.infoscoop.dao;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.infoscoop.dao.model.Holiday;
import org.infoscoop.util.SpringUtil;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class HolidaysDAO extends HibernateDaoSupport {
//	private static Log log = LogFactory.getLog(ForbiddenURLDAO.class);
	
	public static HolidaysDAO newInstance() {
        return (HolidaysDAO)SpringUtil.getContext().getBean("holidaysDAO");
	}
	
	public Holiday getHoliday(String lang, String country) {
		return (Holiday) super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(Holiday.class).add(
						Restrictions.eq("lang", lang)).add(
						Restrictions.eq("country", country)));
	}

	public void updateHoliday(String lang, String country, String data) {
		Holiday holiday = new Holiday();
		holiday.setLang(lang);
		holiday.setCountry(country);
		holiday.setData(data);
		holiday.setUpdatedAt(new Date());

		super.getHibernateTemplate().saveOrUpdate( holiday );
	}
	public void deleteHoliday( String lang,String country ) {
		Holiday holiday = getHoliday(lang, country);
		if (holiday != null)
			super.getHibernateTemplate().delete(holiday);
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
