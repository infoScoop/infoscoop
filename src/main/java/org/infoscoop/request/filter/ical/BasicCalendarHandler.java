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

import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.fortuna.ical4j.data.ContentHandler;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.DateList;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.parameter.CuType;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.RRule;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

public abstract class BasicCalendarHandler implements ContentHandler {
	private static final Log log = LogFactory
			.getLog(BasicCalendarHandler.class);

	StringWriter icstxt = new StringWriter();

	Calendar cal = Calendar.getInstance();

	String propName = "";

	boolean isFirst = true;

	boolean dateOnly = false;

	private Event event = new Event();

	String rruleStr = "";

	boolean isVevent = false;

	private String startDateString;
	private String endDateString;

	private String cutype_ = "";

	private String attendee_ = "";

	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

	public abstract void endVEVENT(Event event);

	public void startCalendar() {
	}

	public void endCalendar() {
	}

	public void startComponent(String compName) {
		if (compName.equalsIgnoreCase(Component.VEVENT)) {
			isVevent = true;
		} else {
			isVevent = false;
		}
	}

	public void endComponent(String arg0) {
		if (!isVevent)
			return;
		if (rruleStr.length() > 0) {
			try {
				Date orgStart = event.getDtstartDate();
				Date orgEnd = event.getDtendDate();
				long span = (orgStart != null && orgEnd != null) ? orgEnd
						.getTime() - orgStart.getTime() : 0;
				List recurEventList = getRecurEventsList();

				for (int i = 0; i < recurEventList.size(); i++) {
					net.fortuna.ical4j.model.Date start = (net.fortuna.ical4j.model.Date) recurEventList
							.get(i);
					event.setDtstart(start);
					cal.setTime(start);
					Date dtendDate = event.getDtendDate();
					if (dtendDate != null) {
						cal.setTimeInMillis(cal.getTimeInMillis() + span);
						event.setDtend(cal.getTime());
					}
					cal.setTime(start);
					endVEVENT(event);
				}
			} catch (ParseException e) {
				log.warn("Failed to analysis the RRULE.", e);
			}
		} else {
			endVEVENT(event);
		}
		initVariables();
	}

	public void startProperty(String name) {
		if (!isVevent)
			return;
		propName = name;
	}

	public void propertyValue(String propValue) throws URISyntaxException,
			ParseException, IOException {

		if (!isVevent)
			return;
		propValue = propValue.replaceAll("\\\\n", "\n");
		if (propName.equalsIgnoreCase(Property.DTSTART)) {
			event.setDtstart(propValue);
			cal.setTime(event.getDtstartDate());//What is this for?
		} else if (propName.equalsIgnoreCase(Property.DTEND)) {
			event.setDtend(propValue);
		} else if (propName.equalsIgnoreCase(Property.SUMMARY)) {
			event.setSummary(propValue);
		} else if (propName.equalsIgnoreCase(Property.ORGANIZER)) {
			event.setOrganizer(propValue);
		} else if (propName.equalsIgnoreCase(Property.LOCATION)) {
			event.setLocation(propValue);
		} else if (propName.equalsIgnoreCase(Property.CATEGORIES)){
			event.setCategory(propValue);
		} else if (propName.equalsIgnoreCase(Property.DESCRIPTION)) {
			if (!"".equals(propValue.trim()))
				event.setDescription(propValue);
		} else if (propName.equalsIgnoreCase(Property.URL)) {
			event.setUrl(propValue);
		} else if (propName.equalsIgnoreCase(Property.UID)) {
			event.setUid(propValue);
		} else if (propName.equalsIgnoreCase(Property.RRULE)) {	
			rruleStr = propValue;
			RRule rule = new RRule();
			rule.setValue(rruleStr);
			if (rule.getRecur().getUntil() != null) {
				DateFormat df = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
				event.setRruleLimit(df.format(rule.getRecur().getUntil()));
				event.setRrule(rule.getRecur().getFrequency());
			} else { 
				event.setRrule(rule.getRecur().getFrequency());
			}
		} else if (propName.equalsIgnoreCase(Property.DURATION)) {
			event.setDuration(propValue);
		} else if (propName.equalsIgnoreCase(Property.ATTENDEE)) {
			if (attendee_ != null && !attendee_.endsWith(","))
				attendee_ += ",";

			if (cutype_ != null){
				if (cutype_.equalsIgnoreCase(CuType.RESOURCE.getValue()))
					event.setResource(propValue);
			    else if (cutype_.equalsIgnoreCase(CuType.ROOM.getValue()))
			    	event.setRoom(propValue);
			    else
			    	attendee_ += "account : " + JSONObject.quote(propValue);

			} else {
				attendee_ += "account : " + JSONObject.quote(propValue);
			}
			event.setAttendee(attendee_);
			attendee_ = "";
		} else if (propName.equalsIgnoreCase("X-CALURL")) {
			event.setDavHref(propValue);
		}
	}

	public List getRecurEventsList() throws ParseException {
		// Set a period to get a schedule.
		Date first = null;
		Date last = null;
		if (this.startDateString != null) {
			first = dateFormat.parse(this.startDateString);
			last  = dateFormat.parse(this.endDateString);
		} else {
			java.util.Calendar cal = Calendar.getInstance();
			Date now = cal.getTime();
			cal.add(Calendar.MONTH, -36);
			first = cal.getTime();

			cal.setTime(now);
			cal.add(Calendar.MONTH, +48);
			last = cal.getTime();
		}

		RRule rule = new RRule();
		rule.setValue(rruleStr);
		Recur rec = rule.getRecur();
		DateList dayList = rec.getDates(event.getDtstartDate(), new DateTime(first), new DateTime(last),
				Value.DATE);
		return dayList;
	}

	public void endProperty(String arg0) {
		if (!isVevent)
			return;
		propName = "";
	}

	public void parameter(String name, String value) throws URISyntaxException {
		if (propName.equalsIgnoreCase(Property.ORGANIZER)) {
			if(name.equalsIgnoreCase(Parameter.CN)){
				event.setOrganizerCn( value );
			}
		} else if (propName.equalsIgnoreCase(Property.ATTENDEE)) {
			if (attendee_ != null && attendee_.length() != 0) {
				if (!name.equalsIgnoreCase(Parameter.CUTYPE))
					attendee_ += ",";
			}
			if (name.equalsIgnoreCase(Parameter.CUTYPE))
				cutype_ = value;
			else if (name.equalsIgnoreCase(Parameter.ROLE))
				attendee_ += "role : " + JSONObject.quote(value);
			else if (name.equalsIgnoreCase(Parameter.PARTSTAT))
				attendee_ += "partstat :" + JSONObject.quote(value);
			else if (name.equalsIgnoreCase(Parameter.CN))
				attendee_ += "cn :" + JSONObject.quote(value);
		}
	}

	private void initVariables() {
		event.init();
		rruleStr = "";
	}

	public void setEndDateString(String endDateString) {
		this.endDateString = endDateString;
	}

	public void setStartDateString(String startDateString) {
		this.startDateString = startDateString;
	}

}
