package org.infoscoop.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.dao.model.Holiday;
import org.infoscoop.service.HolidaysService;

public class HolidayServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Log log = LogFactory.getLog(this.getClass());
	
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		String country = request.getParameter("country");
		String lang = request.getParameter("lang");
		
		Holiday holiday;
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
		
		response.setContentType("text/plain;charset=utf-8");
		if( holiday != null ) {
			if( holiday.getData() != null )
				response.getWriter().write( holiday.getData());
		} else {
			log.error("Holiday ICS for default Locale ALL_ALL was deleted. Needs initialize of holidays table.");
			response.sendError( 500 );
		}
	}
}
