package org.infoscoop.web;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

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
		Calendar cal = Calendar.getInstance();
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
