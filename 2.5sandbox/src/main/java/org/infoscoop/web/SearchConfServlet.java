package org.infoscoop.web;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.service.SearchEngineService;
import org.infoscoop.util.I18NUtil;
import org.infoscoop.util.SpringUtil;

public class SearchConfServlet extends HttpServlet {
	private static final long serialVersionUID = 3442930551294149947L;

	private static Log log = LogFactory.getLog(SearchConfServlet.class);

	protected void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		doPost(req, res);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {

		//SearchEngineService service = SearchEngineService.getHandle();
		try {
			res.setHeader("Content-Type", "text/xml; charset=UTF-8");
			Writer w = res.getWriter();
			SearchEngineService service = (SearchEngineService) SpringUtil.getBean("searchEngineService");
			String searchConf = service.getSearchEngine();
			searchConf = I18NUtil.resolveForXML(I18NUtil.TYPE_SEARCH, searchConf, req
					.getLocale());
			w.write(searchConf);
			w.flush();
			w.close();
			res.getWriter();
		} catch (Exception e) {
			log.error("",e);
			res.sendError(500, e.getMessage());
		}
	}

}
