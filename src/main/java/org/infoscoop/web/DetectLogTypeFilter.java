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

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;


import org.apache.log4j.MDC;
import org.infoscoop.log.PortalLogFilter;

public class DetectLogTypeFilter implements javax.servlet.Filter {
	public void doFilter(ServletRequest req, ServletResponse resp,
			FilterChain chain) throws IOException, ServletException {

		HttpServletRequest httpreq = (HttpServletRequest) req;
		String reqUri = httpreq.getRequestURI();
		String adminContext_admin = httpreq.getContextPath() + "/admin";
		String adminContext_manager = httpreq.getContextPath() + "/manager";
		String referer = httpreq.getHeader("Referer");

		if ((adminContext_admin != null && reqUri.indexOf(adminContext_admin) != -1)
				|| (adminContext_manager != null && reqUri.indexOf(adminContext_manager) != -1)
				|| (referer != null && referer.indexOf(adminContext_admin) != -1)
				|| (referer != null && referer.indexOf(adminContext_manager) != -1)) {
			MDC.put("logType", PortalLogFilter.LOGTYPE_ADMIN);
		} else {
			MDC.put("logType", PortalLogFilter.LOGTYPE_MAIN);
		}

		chain.doFilter(req, resp);

		MDC.remove("logType");
	}

	public void init(FilterConfig config) throws ServletException {
	}

	public void destroy() {
	}
}
