package org.infoscoop.admin.command;

import java.io.StringReader;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.fortuna.ical4j.data.CalendarParserImpl;
import net.fortuna.ical4j.data.UnfoldingReader;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.infoscoop.dao.model.Holiday;
import org.infoscoop.request.filter.ical.BasicCalendarHandler;
import org.infoscoop.request.filter.ical.Event;
import org.infoscoop.service.HolidaysService;

public class HolidaysServiceCommand extends ServiceCommand {
	public CommandResponse execute(String commandName, HttpServletRequest req,
			HttpServletResponse resp) throws Exception {
		if("downloadHoliday".equals( commandName )) {
			return downloadData( req,resp );
		} else if("uploadHoliday".equals( commandName )) {
			return uploadData(req, resp);
		}
		
		return super.execute(commandName, req, resp);
	}
	
	public CommandResponse downloadData(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String lang = request.getParameter("lang");
		String country = request.getParameter("country");
		
		Holiday holiday = (( HolidaysService )service ).getHoliday( lang,country );
		if( holiday == null )
			return new CommandResponse( false,"Holiday not Found.");
		
		response.setContentType("text/plain; header=absent; charset=UTF-8");
		
		String fileName = ( country + "_" + lang +  ".ics");
		response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");
		
		String data = holiday.getData();
		if( data == null )
			data = "";
		
		return new CommandResponse( true,data );
	}
	public CommandResponse uploadData(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String lang = request.getParameter("lang");
		String country = request.getParameter("country");
		
		ServletFileUpload fileUpload = new ServletFileUpload( new DiskFileItemFactory());

		String source;
		try {
			FileItem data = null;
			for( Iterator ite=fileUpload.parseRequest(request).iterator();ite.hasNext();) {
				FileItem item = ( FileItem )ite.next();
				if( item.getFieldName().equals("csvFile"))
					data = item;
			}
			
			source = data.getString("UTF-8");
			
			CalendarParserImpl parser = new CalendarParserImpl();
			parser.parse( new UnfoldingReader(new StringReader( source )),new BasicCalendarHandler(){
				public void endVEVENT( Event event ) {
				}
			});
		} catch( Exception ex ) {
			log.error("Please specify the iCalendar file.",ex );
			
			return new CommandResponse(true, "Please specify the iCalendar file.");
		}
		
		String result;
		(( HolidaysService )service ).updateHoliday( lang,country,source );
		
		result = (( HolidaysService )service ).getHoliday( lang,country ).getData();
		
		return new CommandResponse( true, result );
	}
}