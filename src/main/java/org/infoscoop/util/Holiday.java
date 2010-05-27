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

package org.infoscoop.util;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import net.fortuna.ical4j.data.CalendarParserImpl;
import net.fortuna.ical4j.data.UnfoldingReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.request.filter.ical.BasicCalendarHandler;
import org.infoscoop.request.filter.ical.Event;

/**
 * @author hr-endoh
 *
 */
public class Holiday {
	private final static long serialVersionUID = "org.infoscoop.util.Holiday"
			.hashCode();

	private static final Log log = LogFactory.getLog(Holiday.class);

	private static Holiday singleton;

	private List holidaysList = new ArrayList();

	static public synchronized Holiday getSingleton() {
		if (singleton == null) {
			singleton = new Holiday();
		}
		return singleton;
	}

	private Holiday() {
		try {
			InputStream is = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream("holiday_ja.ics");
			Reader reader = new InputStreamReader(is, "utf-8");
//			Reader reader = ICalendarUtil.convertRdf2Ics(is);

			CalendarParserImpl parser = new CalendarParserImpl();
			DTStartHandler handler = new DTStartHandler();
			parser.parse(new UnfoldingReader(reader), handler);
			log.info("initialize the setting of the holiday.");
		} catch (Exception e) {
			log.error("failed to initialize the setting of the holiday.", e);
		}
	}

	public List getHolidaysList() {
		return holidaysList;
	}

	public int getFreshDays(Calendar cal, int freshDays) {

		if (isHoliday(cal) || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
				|| cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
			freshDays++;
			cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), (cal
					.get(Calendar.DATE) - 1));
			freshDays = getFreshDays(cal, freshDays);
		}

		return freshDays;

	}

	private boolean isHoliday(Calendar cal) {
		String compareDate = new SimpleDateFormat(Event.DATE_RORMAT).format(cal.getTime());

		if (holidaysList != null && holidaysList.contains(compareDate))
			return true;

		return false;

	}

	class DTStartHandler extends BasicCalendarHandler {
		public void endVEVENT(Event event) {
			holidaysList.add(event.getDtstart());
		}
	}
}
