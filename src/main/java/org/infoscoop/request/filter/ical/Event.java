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

/**
 *
 */
package org.infoscoop.request.filter.ical;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import org.infoscoop.context.UserContext;

public class Event {
	public static final String DATE_RORMAT = "yyyy/MM/dd";

	private DateFormat dateFormat = new SimpleDateFormat(DATE_RORMAT);
	private DateFormat timeFormat = new SimpleDateFormat(
	"HH:mm");

	private String dtstartTime = "";

	private net.fortuna.ical4j.model.Date dtstartDate = null;

	private String dtendTime = "";

	private net.fortuna.ical4j.model.Date dtendDate = null;
	
	private net.fortuna.ical4j.model.Date limitDate = null;

	private String summary = "";

	private String location = "";
	
	private String category = "";

	private String description = "";

	private String url = "";

	private String davHref = "";

	private String duration = "";

	private boolean isSetDuration = false;

	private String organizer;

	private String organizerCn;

	private String room = "";

	private String rrule = "";
	
	private String uid = "";

	private ArrayList<String> resource = new ArrayList<String>();

	private ArrayList<String> attendees = new ArrayList<String>();

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDtend() {
		if (dtendDate != null){
			dateFormat.setTimeZone(UserContext.instance().getUserInfo().getClientTimezone());
			return dateFormat.format(dtendDate);
		}
		return "";
	}

	public void setDtend(String dtend) throws ParseException {
		dtend = dtend.replaceAll("-", "").replaceAll(":", "");
		String[] dates = dtend.split("T");
		if (dates.length > 1) {
			String timeStr = dates[1];
			String hour = "00";
			String minute = "00";
			String second = "00";
			try{
				hour = timeStr.substring(0, 2);
				minute = timeStr.substring(2, 4);
				second = timeStr.substring(4, 6);
			}catch(IndexOutOfBoundsException e){}
			dtend = dates[0] + "T" + hour + minute + second;

			if(timeStr.lastIndexOf('Z') >= 0){
				dtend += "Z";
			}

			dtendDate = new net.fortuna.ical4j.model.DateTime(dtend);
			timeFormat.setTimeZone(UserContext.instance().getUserInfo().getClientTimezone());
			dtendTime = timeFormat.format(dtendDate);
		}else{
			dtendDate = new net.fortuna.ical4j.model.Date(dtend);
		}

	}

	public void setDtend(Date dtend) throws ParseException {
		dtendDate = new net.fortuna.ical4j.model.Date(dtend);
	}

	public String getDtstart() {
		dateFormat.setTimeZone(UserContext.instance().getUserInfo().getClientTimezone());
		return dateFormat.format(dtstartDate);
	}

	public void setDtstart(String dtstart) throws ParseException {
		dtstart = dtstart.replaceAll("-", "").replaceAll(":", "");
		String[] dates = dtstart.split("T");
		if (dates.length > 1) {

			String timeStr = dates[1];
			String hour = "00";
			String minute = "00";
			String second = "00";
			try{
				hour = timeStr.substring(0, 2);
				minute = timeStr.substring(2, 4);
				second = timeStr.substring(4, 6);
			}catch(IndexOutOfBoundsException e){}
			dtstart = dates[0]+ "T" + hour + minute + second;

			if(timeStr.lastIndexOf('Z') >= 0){
				dtstart += "Z";
			}
			dtstartDate = new net.fortuna.ical4j.model.DateTime(dtstart);
			timeFormat.setTimeZone(UserContext.instance().getUserInfo().getClientTimezone());
			dtstartTime = timeFormat.format(dtstartDate);
		}else{
			dtstartDate = new net.fortuna.ical4j.model.Date(dtstart);
		}
		if(!isSetDuration && duration != ""){
			setDuration(duration);
		}
	}

	public void setDtstart(Date dtstart) throws ParseException {
		dtstartDate = new net.fortuna.ical4j.model.Date(dtstart);
	}

	public String getDuration() {
		return duration;
	}

	/**
	 * If the "DURATION" set, calculate "DTEND" from "DTSTART" and set it.
	 * (A priority is "DURATION" > "DTEND".)
	 * In addition, we hope that "DTSTART" is defined than "DURATION" earlier.
	 * @param duration
	 */
	public void setDuration(String duration) {
		this.duration = duration;
		if (dtstartDate == null)
			return;
		net.fortuna.ical4j.model.Dur dur= new net.fortuna.ical4j.model.Dur(duration);
		dtendDate = new net.fortuna.ical4j.model.DateTime(dur.getTime(dtstartDate));
		timeFormat.setTimeZone(UserContext.instance().getUserInfo().getClientTimezone());
		String tmpDtendTime = timeFormat.format(dtendDate);
		if(!"00:00".equals(tmpDtendTime))
			this.dtendTime = tmpDtendTime;
		this.isSetDuration = true;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
	
	public String getCategory() {
		return category;
	}
	
	public void setCategory(String category) {
		this.category = category;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDavHref() {
		return davHref;
	}

	public void setDavHref(String href) {
		this.davHref = href;
	}

	public net.fortuna.ical4j.model.Date getDtendDate() {
		return dtendDate;
	}

	public net.fortuna.ical4j.model.Date getDtstartDate() {
		return dtstartDate;
	}

	public String getDtstartTime() {
		return dtstartTime;
	}

	public String getDtendTime() {
		return dtendTime;
	}

	public void init() {
		uid = "";
		dtstartDate = null;
		dtstartTime = "";
		dtendDate = null;
		dtendTime = "";
		summary = "";
		location = "";
		category = "";
		description = "";
		url = "";
		room = "";
		limitDate = null;
		rrule = "";
		resource = new ArrayList<String>();
		attendees = new ArrayList<String>();
		duration = "";
		this.organizer = "";
		this.organizerCn = "";
	}

	public String getOrganizer() {
		if(this.organizerCn != null){
			return "CN=" + organizerCn + ";" + this.organizer;
		}else{
			return this.organizer;
		}
	}

	public void setOrganizer(String organizer) {
		this.organizer =organizer;
	}

	public void setOrganizerCn(String value) {
		this.organizerCn = value;
	}

	public void setRoom(String room) {
		this.room = room;
	}

	public String getRoom() {
		return this.room;
	}

	public void setResource(String resource) {
		this.resource.add(resource);
	}

	public ArrayList<String> getResource() {
		return this.resource;
	}

	public void setAttendee(String attendees){
		this.attendees.add(attendees);
	}

	public ArrayList<String> getAttendees(){
		return this.attendees;
	}

	public void setRrule(String rrule) {
		this.rrule = rrule;
	}
	
    public void setRruleLimit(String limit) throws ParseException {
    	limit = limit.replaceAll("-", "").replaceAll(":", "");
		String[] dates = limit.split("T");
		if (dates.length > 1) {
			String timeStr = dates[1];
			String hour = "00";
			String minute = "00";
			String second = "00";
			try{
				hour = timeStr.substring(0, 2);
				minute = timeStr.substring(2, 4);
				second = timeStr.substring(4, 6);
			}catch(IndexOutOfBoundsException e){}
			limit = dates[0]+ "T" + hour + minute + second;

			if(timeStr.lastIndexOf('Z') >= 0){
				limit += "Z";
			}
			limitDate = new net.fortuna.ical4j.model.DateTime(limit);
		}else{
			limitDate = new net.fortuna.ical4j.model.Date(limit);
		}
    }
    
    private String getRruleLimit() {
		dateFormat.setTimeZone(UserContext.instance().getUserInfo().getClientTimezone());
    	return dateFormat.format(limitDate);
    }

	public String getRrule() {
		if (limitDate != null)
			return this.rrule + ";" + getRruleLimit();
		else 
		return this.rrule;
	}
	
	public String getUid() {
		return this.uid;
	}
	
	public void setUid(String uid) {
		this.uid = uid;
	}
}
