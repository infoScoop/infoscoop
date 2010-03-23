package org.infoscoop.web;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.service.PortalLayoutService;
import org.infoscoop.util.SpringUtil;

public class PortalLayoutServlet extends HttpServlet {
	private static final long serialVersionUID = "org.infoscoop.web.PortalLayoutServlet"
			.hashCode();
	
	private static Log logger = LogFactory.getLog(PortalLayoutServlet.class);

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		doPost(request, response);
	}
	
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String uid = (String) request.getSession().getAttribute("Uid");
		
		if (logger.isInfoEnabled()) {
			logger.info("uid:[" + uid + "]: doPost");
		}
		
		String type = request.getParameter("type");
		if (type == null) {
			logger.error("It is an unjust summons. Type is not appointed.");
			response.sendError(500);
		}

		response.setContentType("text/javascript; charset=UTF-8");
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");

		Writer out = response.getWriter();
		
		try {
			
			PortalLayoutService service = (PortalLayoutService)SpringUtil.getBean("PortalLayoutService");
			String js = service.getPortalLayout(type);
			
			out.write(js);
		} catch (Exception e){
			logger.error("--- unexpected error occurred.", e);
			response.sendError(500);
		} 
		out.flush();
		out.close();
		
	}
	
}
