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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.dao.LogDAO;

public class AccessStatsServlet extends HttpServlet {

	private static final long serialVersionUID = "org.infoscoop.web.AccessStatsServlet"
			.hashCode();

	private static final String DATE_FORMAT = "yyyyMMddHH";

	private Log log = LogFactory.getLog(this.getClass());

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		doPost(request, response);

	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String rssUrl = request.getParameter("rssUrl");
		LogDAO dao = LogDAO.newInstance();
		
		SimpleDateFormat dateFormat = new SimpleDateFormat( DATE_FORMAT );
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(TimeZone.getTimeZone("UTC"));
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal
				.get(Calendar.DATE), 0, 0, 0);
		cal.add(Calendar.DATE, -1);
		int onedaycnt = dao.getRssAccessCount(rssUrl, dateFormat.format(cal
				.getTime()));
		cal.add(Calendar.DATE, -6);
		int oneweekcnt = dao.getRssAccessCount(rssUrl, dateFormat.format(cal
				.getTime()));
		cal.add(Calendar.DATE, 7);
		cal.add(Calendar.MONTH, -1);
		int onemonthcnt = dao.getRssAccessCount(rssUrl, dateFormat.format(cal
				.getTime()));
		cal.add(Calendar.MONTH, -5);
		int sixmonthcnt = dao.getRssAccessCount(rssUrl, dateFormat.format(cal
				.getTime()));
		int allcnt = dao.getRssAccessCount(rssUrl);
		request.setAttribute("onedaycnt", new Integer(onedaycnt));
		request.setAttribute("oneweekcnt", new Integer(oneweekcnt));
		request.setAttribute("onemonthcnt", new Integer(onemonthcnt));
		request.setAttribute("sixmonthcnt", new Integer(sixmonthcnt));
		request.setAttribute("allcnt", new Integer(allcnt));

		request.getRequestDispatcher("accessStats.jsp").forward(request,
				response);
	}
}
