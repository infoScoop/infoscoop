package org.infoscoop.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.infoscoop.service.DomainManageService;

public class DomainManageServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2968873601222919289L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String domain = req.getParameter("domain");
		if (domain == null || domain.equals("")) {
			resp.sendError(400);
			return;
		}
		DomainManageService service = DomainManageService.getHandle();
		try {
			service.newDomain(domain);
		} catch (CloneNotSupportedException e) {
			resp.sendError(500, e.getMessage());
		}
		
		resp.getWriter().write("OK");
	}
	
}
