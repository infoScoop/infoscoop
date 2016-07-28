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

package org.infoscoop.admin.web;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
import org.infoscoop.dao.model.Portaladmins;
import org.infoscoop.service.PortalAdminsService;
import org.infoscoop.util.SpringUtil;


public class CheckAdminFilter implements Filter {
	
	private Set administratorUidList = new HashSet();
	private String mode;
	private static Log log = LogFactory.getLog(CheckAdminFilter.class);
	public static String PARAM_SQUARE_ID = "MSDPortal-SquareId";

	public void destroy() {
		
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
		HttpServletRequest httpReq = (HttpServletRequest)request;
		HttpServletResponse httpRes = (HttpServletResponse)response;
		String uid = (String) httpReq.getSession().getAttribute("Uid");

		try {
			loadData();
		} catch (Exception e) {
			throw new ServletException(e);
		}

		if (mode != null && mode.equals("SET_ADMIN")) {
			if ( this.administratorUidList.contains(uid))
				request.setAttribute("isAdministrator", Boolean.TRUE);
			filterChain.doFilter(request, response);
		} else {
			if (this.administratorUidList.contains(uid)) {
				filterChain.doFilter(request, response);
			} else {
				httpRes.sendError(403, "You are not administrator.");
			}
		}
	}

	public void init(FilterConfig config) throws ServletException {
		this.mode = config.getInitParameter("mode");
	}

	private void loadData() throws Exception {
		PortalAdminsService service = (PortalAdminsService) SpringUtil
				.getBean("PortalAdminsService");
		List adminsList = service.getPortalAdmins();
		this.administratorUidList.clear();
		for (Iterator it = adminsList.iterator(); it.hasNext();) {
			Portaladmins portalAdmin = (Portaladmins) it.next();
			this.administratorUidList.add( portalAdmin.getUid() );
		}
	}
}
