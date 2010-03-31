package org.infoscoop.service;

import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

import org.infoscoop.dao.HolidaysDAO;
import org.infoscoop.dao.model.Holiday;
import org.infoscoop.util.SpringUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HolidaysService {

	private HolidaysDAO holidaysDAO;
	 
	/**
	 * @param holidaysDAO
	 */
	public void setHolidaysDAO(HolidaysDAO holidaysDAO) {
		this.holidaysDAO = holidaysDAO;
	}

	/**
	 * @return
	 */
	public static HolidaysService getHandle() {
		return (HolidaysService) SpringUtil.getBean("HolidaysService");
	}
	
	public Holiday getHoliday( Locale locale ) {
		String lang = locale.getLanguage();
		if( lang == null )
			lang = "ALL";
		
		String country = locale.getCountry();
		if( country == null )
			country = "ALL";
		
		Holiday holiday = holidaysDAO.getHoliday( lang,country );
		if( holiday == null && !country.equalsIgnoreCase("ALL"))
			holiday = holidaysDAO.getHoliday( lang,"ALL");
		
		if( holiday == null && !lang.equalsIgnoreCase("ALL"))
			holiday = holidaysDAO.getHoliday("ALL",country );
		
		if( holiday == null )
			holiday = holidaysDAO.getHoliday("ALL","ALL");
		
		return holiday;
	}
	public Holiday getHoliday( String lang,String country ) {
		return holidaysDAO.getHoliday( lang,country );
	}
	public String getHolidayData( String lang,String country ) {
		return getHoliday( lang,country ).getData();
	}
	
	public void updateHoliday( String lang,String country,String data ) {
		holidaysDAO.updateHoliday(lang, country, data);
	}
	public void deleteHoliday( String lang,String country ) {
		holidaysDAO.deleteHoliday(lang, country );
	}
	
	public JSONArray getHolidayLocalesJSON() throws JSONException {
		Collection locales = holidaysDAO.getHolidayLocales();
		
		JSONArray localeArray = new JSONArray();
		for (Iterator it = locales.iterator(); it.hasNext();) {
			Locale locale = (Locale) it.next();
			JSONObject obj = new JSONObject();
			obj.put("country", locale.getCountry());
			String lang = locale.getLanguage();
			if( lang.equalsIgnoreCase("all"))
				lang = "ALL";
			
			obj.put("lang", lang);
			localeArray.put(obj);
		}
		return localeArray;
	}
	
}
