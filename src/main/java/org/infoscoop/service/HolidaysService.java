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

package org.infoscoop.service;

import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

import org.infoscoop.context.UserContext;
import org.infoscoop.dao.HolidaysDAO;
import org.infoscoop.dao.model.Holidays;
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
	
	public Holidays getHoliday( Locale locale ) {
		String squareid = UserContext.instance().getUserInfo().getCurrentSquareId();
		String lang = locale.getLanguage();
		if( lang == null )
			lang = "ALL";
		
		String country = locale.getCountry();
		if( country == null )
			country = "ALL";
		
		Holidays holiday = holidaysDAO.getHoliday( lang,country,squareid );
		if( holiday == null && !country.equalsIgnoreCase("ALL"))
			holiday = holidaysDAO.getHoliday( lang,"ALL",squareid );
		
		if( holiday == null && !lang.equalsIgnoreCase("ALL"))
			holiday = holidaysDAO.getHoliday("ALL",country,squareid );
		
		if( holiday == null )
			holiday = holidaysDAO.getHoliday("ALL","ALL",squareid);
		
		return holiday;
	}
	public Holidays getHoliday( String lang,String country ) {
		String squareid = UserContext.instance().getUserInfo().getCurrentSquareId();
		return holidaysDAO.getHoliday( lang,country,squareid );
	}
	public String getHolidayData( String lang,String country ) {
		return getHoliday( lang,country ).getData();
	}
	
	public void updateHoliday( String lang,String country,String data ) {
		String squareid = UserContext.instance().getUserInfo().getCurrentSquareId();
		holidaysDAO.updateHoliday(lang, country, data, squareid);
	}
	public void deleteHoliday( String lang,String country ) {
		String squareid = UserContext.instance().getUserInfo().getCurrentSquareId();
		holidaysDAO.deleteHoliday(lang, country, squareid);
	}
	
	public JSONArray getHolidayLocalesJSON() throws JSONException {
		String squareid = UserContext.instance().getUserInfo().getCurrentSquareId();
		Collection locales = holidaysDAO.getHolidayLocales(squareid);
		
		JSONArray localeArray = new JSONArray();
		for (Iterator it = locales.iterator(); it.hasNext();) {
			Locale locale = (Locale) it.next();
			JSONObject obj = new JSONObject();
			obj.put("country", locale.getCountry());
			String lang = locale.getLanguage();
			if( lang.equalsIgnoreCase("all"))
				lang = "ALL";
			if(lang.equalsIgnoreCase("pt-br"))
				lang = "pt-BR";
			obj.put("lang", lang);
			localeArray.put(obj);
		}
		return localeArray;
	}
	
}
