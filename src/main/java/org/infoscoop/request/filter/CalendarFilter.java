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


package org.infoscoop.request.filter;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.fortuna.ical4j.data.CalendarParserImpl;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.data.UnfoldingReader;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.request.ProxyRequest;
import org.infoscoop.request.filter.ical.BasicCalendarHandler;
import org.infoscoop.request.filter.ical.Event;
import org.infoscoop.request.filter.ical.ICalendarUtil;
import org.json.JSONObject;
import org.xml.sax.SAXException;


public class CalendarFilter extends ProxyFilter{
	
	private static Log log = LogFactory.getLog(CalendarFilter.class);
	static private Pattern GET_DAV_NAMESPACE_PREFIX = Pattern.compile( ".*<(.*):href>.*</.*:href>.*");
	
	public CalendarFilter() {
	}

	protected int preProcess(HttpClient client, HttpMethod method, ProxyRequest request) {
		return 0;
	}

	@Override
	protected boolean allow204() {
		return true;
	}

	protected InputStream postProcess(ProxyRequest request, InputStream responseStream) throws IOException {
		
		String defaultContentType = request.getResponseHeader("Content-Type");
		String startDateStr = request.getRequestHeader("X-IS-STARTDATE");
		String endDateStr = request.getRequestHeader("X-IS-ENDDATE");
		
		if(responseStream != null){
			byte[] responseBytes = process(defaultContentType, startDateStr, endDateStr,responseStream);	
			//request.setResponseBody(new ByteArrayInputStream(responseBytes));

			request.putResponseHeader("Content-Length", Integer.toString(responseBytes.length));
			request.putResponseHeader("Content-Type", "text/xml; charset=\"utf-8\"");
			return new ByteArrayInputStream(responseBytes);
		}else
			return new ByteArrayInputStream(process("[]", 0));
		
	}

	public byte[] process(String aContentType, String startDateStr, String endDateStr, InputStream responseStream) throws IOException {
		
		String charset = null;
		String contentType = null;
		if (aContentType != null) {
			String[] str = aContentType.split("=");
			if (str != null)
				contentType = str[0];
			if (str.length > 1) {
				charset = str[1];
			}
		}
		
		BufferedInputStream bis = new BufferedInputStream(responseStream);

		//Processing of skipping to the first character
		int temp = 0;
		boolean noContent = false;
		bis.mark(1);
		while(true){
			try {
				temp = bis.read();
				if(temp == -1|| (temp >= 0x20 && temp <= 0x7e) ){
					if(temp == -1) {
						noContent = true;
					}
					break;
				}else{
					bis.mark(1);
				}
			} catch (IOException e) {
				log.error("", e);
				break;
			}
		}
		
		// if 200 and empty
		if(noContent){
			bis.close();
			return process("[]", 0);
		}
		
		try {
			bis.reset();
		} catch (IOException e2) {
		}
		//Processing of skipping to the first character up to here

		Reader reader = null;
		boolean isXML = false;
		try {
			if (contentType != null && 
					(contentType.startsWith("text/xml") || 
					contentType.startsWith("application/xml") || 
					contentType.startsWith("application/rss+xml") || 
					contentType.startsWith("application/rdf+xml"))) {				
				isXML = true;
			} else {
				char firstChar = (char) bis.read();
				if(firstChar == '<') {
					isXML = true;
				}
				bis.reset();
			}		
		} catch (IOException e) {
			log.error("", e);
		}
		
		if (isXML) {
			if(isCalDAV(bis)){
				StringBuffer buf = new StringBuffer();
				BufferedReader br = new BufferedReader(new InputStreamReader(bis, charset));
				String s = null;
				boolean append = false;
				boolean inVALARM = false;
				buf.append("BEGIN:VCALENDAR").append("\r\n");
				String davHref = null;
				while( (s = br.readLine()) != null){
					String _davHref = getDAVHref(s, br);
					if(_davHref != null)davHref = _davHref;
					
					if(s.indexOf("BEGIN:VEVENT") >= 0){
						append = true;
					}
					if(s.indexOf("BEGIN:VALARM") >= 0){
						inVALARM = true;
					}
					if(append && !inVALARM){
						if(s.indexOf("END:VEVENT") >= 0 && davHref != null){
							buf.append(davHref).append("\r\n");
							davHref = null;
						}
						buf.append(s).append("\r\n");
					}
					if(s.indexOf("END:VEVENT") >= 0){
						append = false;
					}
					if(s.indexOf("END:VALARM") >= 0){
						inVALARM = false;
					}
				}
				buf.append("END:VCALENDAR");

				if(log.isDebugEnabled())
					log.debug(buf.toString());
				reader = new StringReader(buf.toString());
			}else{
				try {
					reader = ICalendarUtil.convertRdf2Ics(bis);
				} catch (SAXException e) {
					log.error("", e);
					if(log.isInfoEnabled())
						log.info("Unanalyzable RSS information is recieved.[" + e.getLocalizedMessage() + "]");
					return process("Unanalyzable RSS information is recieved. : " + e.getLocalizedMessage(), 1);
				} catch (IOException e) {
					log.error("", e);
					if(log.isInfoEnabled())
						log.info("Unanalyzable RSS information is recieved.[" + e.getLocalizedMessage() + "]");
					return process("Unanalyzable RSS information is recieved.: " + e.getLocalizedMessage(), 1);
				}		
			}
		} else {
			try {
				if (charset != null) 
					reader = new InputStreamReader(bis, charset);
				else
					reader = new InputStreamReader(bis, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				try {
					reader = new InputStreamReader(bis, "UTF-8");
				} catch (UnsupportedEncodingException e1) {
					log.error("", e1);
				}
			}
		}
			
		String result = null;
		try {
			//Prereq：RDF and removal of line break→ ICS and Reader#reset done.
			result = parseICalendar(reader, startDateStr, endDateStr);
		} catch (IOException e) {
			log.error("", e);
			if(log.isInfoEnabled())
				log.info("Unanalyzable ics information is recieved.[" + e.getLocalizedMessage() + "]");
			return process("Unanalyzable ics information is recieved. : " + e.getLocalizedMessage(), 1);
		} catch (ParserException e) {
			log.error("", e);
			if(log.isInfoEnabled())
				log.info("Unanalyzable ics information is recieved.[" + e.getLocalizedMessage() + "]");
			return process("Unanalyzable ics information is recieved. : " + e.getLocalizedMessage(), 1);
		} 
		
		return process("[" + result + "]", 0);
		
	}
	 

	private static boolean isCalDAV(InputStream is) throws IOException{
		is.mark(1);
		byte[] xmldec = new byte[500];
		is.read(xmldec);

		String xmlDecStr = new String(xmldec);
		boolean isCalDAV = false;
		if(xmlDecStr.indexOf("multistatus") > 0){
			isCalDAV = true;
		}
		is.reset();
		if(log.isDebugEnabled()){
			log.debug( isCalDAV ? "Process caldav." : "Process feed." );
		}
		
		return isCalDAV;
	}
	
	/**
	 * 
	 * @param s
	 * @param br
	 * @return
	 * @throws IOException
	 */
	private static String getDAVHref(String s, BufferedReader br) throws IOException{
		if( s.indexOf("href>") < 0) return null;

		String davPrefix = "";
		Matcher matcher = GET_DAV_NAMESPACE_PREFIX.matcher(s);

		if( matcher.find() )
			davPrefix = matcher.group(1) + ":";
		
		int startOfHref = -1;
		int endOfHref = -1;
		if( ( startOfHref = s.indexOf("<" + davPrefix + "href>") ) >= 0 ){
			if( (endOfHref = s.indexOf("</" + davPrefix + "href>") ) >= 0 )
				return "X-CALURL:" + s.substring(startOfHref + 6 + davPrefix.length(), endOfHref);
			else{
				s = br.readLine();
				if( (endOfHref = s.indexOf("</" + davPrefix + "href>") ) >= 0 )
					return "X-CALURL:" + s.substring(0, endOfHref);
				else
					return "X-CALURL:" + s;
			}
		}
		
		
		return null;
	}
	
	public String parseICalendar(Reader reader, String startDateStr, String endDateStr) throws IOException, ParserException{	
		CalendarParserImpl parser = new CalendarParserImpl();
		CalendarHandler handler = new CalendarHandler();
	
		handler.setStartDateString(startDateStr);
		handler.setEndDateString(endDateStr);
		parser.parse(new UnfoldingReader(reader), handler);
		
		return  handler.getResult();
	}
	
	public byte[] process(String result, int statusCode) {
	
		String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +  
			"<response statusCode=\"" + statusCode + "\">" +
			"<![CDATA[" + result + "]]>" +
			"</response>";
		try {
			return xml.getBytes("UTF-8");			
		} catch (UnsupportedEncodingException e) {
		}
		return null;
	
	}
	
	class CalendarHandler extends BasicCalendarHandler {
		StringWriter icstxt = new StringWriter();
		
		public String getResult() {
			return icstxt.toString();
		}
		
		public void endVEVENT(Event event) {
			String dtstart = event.getDtstart();
			String dtstartTime = event.getDtstartTime();
			String dtend = event.getDtend();
			String dtendTime = event.getDtendTime();
			String summary = event.getSummary();
			String organizer = event.getOrganizer();
			String location = event.getLocation();
			String category = event.getCategory();
			String url = event.getUrl();
			String uid = event.getUid();
			String davHref = event.getDavHref();
			String description = event.getDescription();
			String room = event.getRoom();
			String rrule = event.getRrule();
			String resourceList = "";
			String attendeeList = "";
			ArrayList<String> resources = event.getResource();
			for(String resource: resources) {
				if (resourceList.length() != 0)
					resourceList += ",";
				resourceList += "{account : " + JSONObject.quote(resource) + "}";
			}
			
			ArrayList<String> attendees = event.getAttendees();
			for(String attendee:attendees) {
				if (attendeeList.length() != 0)
					attendeeList += ",";
				attendeeList += "{" + attendee + "}";
			}
		
			if (getResult().length() == 0) {
				icstxt.write("{dtstart : " + JSONObject.quote(dtstart)
						+ ", dtstartTime : " + JSONObject.quote(dtstartTime)
						+ ", dtend : " + JSONObject.quote(dtend)
						+ ", dtendTime : " + JSONObject.quote(dtendTime)
						+ ", summary : " + JSONObject.quote(summary)
						+ ", organizer : " + JSONObject.quote(organizer)
						+ ", location : " + JSONObject.quote(location)
						+ ", category : " + JSONObject.quote(category)
						+ ", url : " + JSONObject.quote(url)
						+ ", uid : " + JSONObject.quote(uid)
						+ ", davHref : " + JSONObject.quote(davHref)
						+ ", description : " + JSONObject.quote(description)
						+ ", room : " + JSONObject.quote(room)
						+ ", rruleStr : " + JSONObject.quote(rrule)
						+ ", resourceList : [" + resourceList + "]"
						+ ", attendeeList : [" + attendeeList + "]"
						+ "}");
			} else {
				icstxt.write(", {dtstart : " + JSONObject.quote(dtstart)
						+ ", dtstartTime : " + JSONObject.quote(dtstartTime)
						+ ", dtend : " + JSONObject.quote(dtend)
						+ ", dtendTime : " + JSONObject.quote(dtendTime)
						+ ", summary : " + JSONObject.quote(summary)
						+ ", organizer : " + JSONObject.quote(organizer)
						+ ", location : " + JSONObject.quote(location)
						+ ", category : " + JSONObject.quote(category)
						+ ", url : " + JSONObject.quote(url)
						+ ", uid : " + JSONObject.quote(uid)
						+ ", davHref : " + JSONObject.quote(davHref)
						+ ", description : " + JSONObject.quote(description)
						+ ", room : " + JSONObject.quote(room)
						+ ", rruleStr : " + JSONObject.quote(rrule)
						+ ", resourceList : [" + resourceList + "]"
						+ ", attendeeList : [" + attendeeList + "]"
						+ "}");
			}
			icstxt.flush();
		}
	}
}
