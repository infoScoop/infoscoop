package org.infoscoop.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.dao.LogDAO;
import org.infoscoop.dao.LogDAO.RssAccessStats;
import org.infoscoop.dao.LogDAO.RssAccessStatsEntry;
import org.json.JSONArray;
import org.json.JSONObject;

public class AccessStatsListServlet extends HttpServlet {

	private static final long serialVersionUID = "org.infoscoop.web.AccessStatsServlet"
			.hashCode();

	private Log log = LogFactory.getLog(this.getClass());

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		doPost(request, response);

	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/plain; charset=UTF-8");
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");
		String rssUrl = request.getParameter("rssUrl");
		int start = Integer.parseInt(request.getParameter("start"));
		int limit = Integer.parseInt(request.getParameter("limit"));
		LogDAO dao = LogDAO.newInstance();
		RssAccessStats stats = dao.getRssAccessStats(rssUrl, start, limit);
		try {
			JSONObject json = new JSONObject();
			json.put("results", stats.getCount());
			JSONArray data = new JSONArray();
			json.put("data", data);
			for (Iterator it = stats.getEntries().iterator(); it.hasNext();) {
				RssAccessStatsEntry entry = (RssAccessStatsEntry) it.next();
				data.put(entry.toJSONObject());
			}
			PrintWriter out = response.getWriter();
			out.print(json.toString());
		} catch (Exception e) {
			log.error("An exception occurred.", e);
			response.sendError(500, e.getMessage());
		}
	}
}
