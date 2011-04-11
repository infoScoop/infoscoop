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

package org.infoscoop.web;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.dao.model.Holidays;
import org.infoscoop.request.filter.CalendarFilter;
import org.infoscoop.service.HolidaysService;

public class HolidayServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Log log = LogFactory.getLog(this.getClass());
	
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		String country = request.getParameter("country");
		String lang = request.getParameter("lang");
		
		Holidays holiday;
		if(( country != null && !"".equals( country ))||
			( lang != null && !"".equals( lang )) ) {
			if( "".equals( country ))
				country = request.getLocale().getCountry();
			
			if( "".equals( lang ))
				lang = request.getLocale().getLanguage();

			holiday = HolidaysService.getHandle().getHoliday( lang,country );
		} else {
			holiday = HolidaysService.getHandle().getHoliday( request.getLocale() );
		}
		
		String contentType = "application/xml;charset=utf-8";
		response.setContentType(contentType);
		if( holiday != null ) {
			if( holiday.getData() != null ){
				CalendarFilter filter = new CalendarFilter();
				byte[] resByte = filter.process("text/plain;charset=utf-8", null, null, new ByteArrayInputStream(holiday.getData().getBytes("UTF-8")));

				OutputStream w = null;
				try{
					w = response.getOutputStream();
					w.write( resByte );
				}finally{
					if(w != null)
						w.close();
				}
			}
		} else {
			log.error("Holiday ICS for default Locale ALL_ALL was deleted. Needs initialize of holidays table.");
			response.sendError( 500 );
		}
	}
}

