package org.infoscoop.web;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.request.filter.MakeMenuFilter;
import org.infoscoop.service.SiteAggregationMenuService;
import org.infoscoop.util.SpringUtil;

public class MenuConfServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3442930551294149947L;
	private static Log log = LogFactory.getLog(MenuConfServlet.class);

	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		doPost(req, res);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String reqUri = req.getRequestURI();
		String srvPath = req.getServletPath();
		String menuType = reqUri.substring(reqUri.lastIndexOf(srvPath) + (srvPath.length() + 1), reqUri.length());
		
		String ignoreAccessControlHeader = req.getHeader("Ignore-Access-Control");
		boolean ignoreAccessControl = false;
		try {
			ignoreAccessControl = Boolean.valueOf( ignoreAccessControlHeader ).booleanValue();
		} catch( Exception ex ) {
			throw new ServletException( ex );
		}
		
		SiteAggregationMenuService service = (SiteAggregationMenuService) SpringUtil.getBean("siteAggregationMenuService");
		try {
			res.setHeader("Content-Type", "text/xml; charset=UTF-8");
			OutputStream w = res.getOutputStream();
			String resStr = service.getMenuTreeXml(menuType, ignoreAccessControl);
			byte[] resBytes = MakeMenuFilter.process(new ByteArrayInputStream(resStr.getBytes("UTF-8")), menuType, menuType, (Locale)req.getLocales().nextElement(), false);
			w.write(resBytes);
			w.flush();
			w.close();
		} catch (Exception e) {
			log .error(e);
			res.sendError(500, e.getMessage());
		}
	}

}
