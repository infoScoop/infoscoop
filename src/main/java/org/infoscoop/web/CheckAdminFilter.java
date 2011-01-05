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
import java.util.HashSet;
import java.util.Set;

import javax.security.auth.Subject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.infoscoop.acl.ISAdminPrincipal;
import org.infoscoop.acl.ISPrincipal;


public class CheckAdminFilter implements Filter {
	
	private Set administratorUidList = new HashSet();
	private String mode;

	public void destroy() {
		
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
		HttpServletRequest httpReq = (HttpServletRequest)request;
		HttpServletResponse httpRes = (HttpServletResponse)response;
		Subject loginUser = (Subject) httpReq.getSession().getAttribute(SessionManagerFilter.LOGINUSER_SUBJECT_ATTR_NAME);
		
		if (loginUser.getPrincipals(ISAdminPrincipal.class).isEmpty()) {
			httpRes.sendError(403, "You are not administrator.");
		} else {
			filterChain.doFilter(new ManagerRequestWrapper(httpReq), response);
		}
	}

	public void init(FilterConfig config) throws ServletException {
		this.mode = config.getInitParameter("mode");
	}

	public class ManagerRequestWrapper extends HttpServletRequestWrapper{

		public ManagerRequestWrapper(HttpServletRequest request) {
			super(request);
		}

		@Override
		public String getHeader(String name) {
			if("Ignore-Access-Control".equalsIgnoreCase(name)){
				return "true";
			}
			return super.getHeader(name);
		}
		
		
	}
}
