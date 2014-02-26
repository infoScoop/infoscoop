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

package org.infoscoop.api.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ApiLogFilter implements Filter{
	private static Log log = LogFactory.getLog(ApiLogFilter.class);
	
	@Override
	public void destroy() {}

	@Override
	public void init(FilterConfig arg0) throws ServletException {}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		String path = ((HttpServletRequest)req).getPathInfo().toString();
		String method = ((HttpServletRequest)req).getMethod().toString();
		String remote = req.getRemoteAddr();

		try{
			log.info(path+" " + method+" - " + remote);
			long start = System.currentTimeMillis();
			chain.doFilter(req, res);
			long stop = System.currentTimeMillis();		
			log.info(path+" ("+(stop-start)+"ms) " + method+" - " + remote);
		}catch(Exception e){
			log.warn(e.getMessage() + " - " + remote);
		}
	}
}
