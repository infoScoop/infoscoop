package org.infoscoop.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.service.SystemMessageService;

public class SystemMessageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static Log log = LogFactory.getLog(SystemMessageServlet.class);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String uid = (String) req.getSession().getAttribute("Uid");

		if (uid == null) {
			resp.sendError(403);
			return;
		}

		try {
			resp.setContentType("text/json;charset=utf-8");
			resp.getWriter().write(SystemMessageService.getHandle().getNonReadMessagesJson(uid));
			resp.setHeader("Pragma", "no-cache");
			resp.setHeader("Cache-Control", "no-cache");
		} catch (Exception e) {
			log.error("Failed getting System message", e);
			resp.sendError(500, e.getMessage());
		}

	}
}
