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
			String searchConf = service.getSearchEngineXmlWithAcl();
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
