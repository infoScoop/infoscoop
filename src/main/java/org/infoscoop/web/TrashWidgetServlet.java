package org.infoscoop.web;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.dao.WidgetDAO;
import org.infoscoop.dao.model.Widget;
import org.json.JSONArray;

public class TrashWidgetServlet extends HttpServlet {
	private static final long serialVersionUID = "org.infoscoop.web.TrashWidgetServlet"
			.hashCode();

	private Log log = LogFactory.getLog(this.getClass());

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);

	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String uid = (String) request.getSession().getAttribute("Uid");

		if (log.isInfoEnabled()) {
			log.info("uid:[" + uid + "]: doPost");
		}

		response.setContentType("text/plain; charset=UTF-8");
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");

		Writer writer = response.getWriter();
		JSONArray responseArray = new JSONArray();
		try {
			WidgetDAO dao = WidgetDAO.newInstance();
			List widgets = dao.getDeletedWidget(uid);
			for (Iterator it = widgets.iterator(); it.hasNext();) {
				Widget widget = (Widget) it.next();
				responseArray.put(widget.toJSONObject());
			}
			String jsonStr = responseArray.toString();
			writer.write(jsonStr);
		} catch (Exception e) {
			log.error("An exception occurred.", e);
			response.sendError(500, e.getMessage());
		}
	}
}
