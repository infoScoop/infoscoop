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

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.*;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.dao.model.Gadget;
import org.infoscoop.service.GadgetResourceService;
import org.infoscoop.service.GadgetService;
import org.infoscoop.util.DateUtility;
import org.joda.time.format.DateTimeFormat;

public class GadgetResourceServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static Log log = LogFactory.getLog(GadgetResourceServlet.class);

	@Override
	protected void doGet( HttpServletRequest req, HttpServletResponse resp )
			throws ServletException, IOException {
		String reqUri = req.getRequestURI();
		String resUri = reqUri.substring( reqUri.indexOf("/gadget/") +"/gadget/".length() );
		
		String type = resUri.substring( 0,resUri.indexOf("/"));
		String typeUri = resUri.substring( type.length() );
		
		String path = typeUri.substring( 0,typeUri.lastIndexOf("/") +1 );
		String name = typeUri.substring( path.length() );

		if( path.length() > 1 && path.charAt( path.length() -1 ) == '/')
			path = path.substring( 0,path.length()-1 );
		
		if( name.equals( type+".xml") && "/".equals( path ) ) {
			resp.sendError( 403 );
			return;
		}
		
		String ifModifiedSinceStr = req.getHeader("if-modified-since");
		
		GadgetResourceService service = GadgetResourceService.getHandle();
		Gadget gadget = service.getResource( type,path,name );
		if( gadget == null ) {
			log.info("Gadget Resource: [" +type +"]@[" +path +"]/[" +name +"]"+" 404 Not Found" );
			
			resp.sendError( 404 );
			return;
		}
		
		DateFormat dateFormat = DateUtility.newGMTDateFormat();
		// IE does not consider time zone
		dateFormat.setTimeZone( new SimpleTimeZone( 0, "GMT"));
		
		if( ifModifiedSinceStr != null ) {
			try {
				long ifModifiedSince = dateFormat.parse( ifModifiedSinceStr ).getTime();
				
				// Delete millisecond 
				Calendar c = Calendar.getInstance();
				c.setTime( gadget.getLastmodified() );
				c.set( Calendar.MILLISECOND,0 );
				long lastModified = c.getTimeInMillis();
				
				if( lastModified <= ifModifiedSince ) {
					log.info("Gadget Resource: [" +type +"]@[" +path +"]/[" +name +"]"+" 302 Not Modified" );
					
					resp.sendError( 304 );
					return;
				}
			} catch( Exception e ) {
				log.error("", e);
			}
		}
		
		log.info("Gadget Resource: [" +type +"]@[" +path +"]/[" +name +"]"+" 200 Success" );

		resp.setContentType(getServletContext().getMimeType(name));
		if( gadget.getLastmodified() != null )
			resp.setHeader("Last-Modified",dateFormat.format( gadget.getLastmodified() ));
		
		byte[] data = gadget.getData();
		resp.setContentLength( data.length );
		resp.getOutputStream().write( data );
		resp.getOutputStream().flush();
	}
}
