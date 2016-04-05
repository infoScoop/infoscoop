package org.infoscoop.web;

import java.io.IOException;
import java.io.Writer;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.service.NotificationService;

public class NotificationServlet extends HttpServlet {
	private static final long serialVersionUID = "org.infoscoop.web.NotificationServlet".hashCode();
	private static Log log = LogFactory.getLog(NotificationServlet.class);

	public void init() {}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String startDateStr = request.getParameter("referenceDate");
		String offsetStr = request.getParameter("offset");
		String limitStr = request.getParameter("limit");
		long startDateLong = 0;
		int offset = 0;
		int limit = -1;
		
		try{
			if(startDateStr != null)
				startDateLong = Long.parseLong(startDateStr);
			if(offsetStr != null)
				offset = Integer.parseInt(offsetStr);
			if(limitStr != null)
				limit = Integer.parseInt(limitStr);
		}catch(NumberFormatException e){
			log.error("invalid parameter.", e);
			throw new ServletException(e);
		}
		Date startDate = new Date(startDateLong);
		
		String resultJSON = NotificationService.getHandle().getMyNotificationsJSON(offset, limit, startDate);
		
		response.setContentType("text/plain; charset=UTF-8");
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");

		response.setContentType("application/json; charset=UTF-8");
		response.setContentLength( resultJSON.getBytes("utf-8").length );
		Writer writer = response.getWriter();
		try {
			writer.write(resultJSON);
		} catch (Exception e) {
			log.error("An exception occurred.", e);
			response.sendError(500, e.getMessage());
		}
	}
}