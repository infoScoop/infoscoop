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

import org.infoscoop.dao.model.Portaladmin;
import org.infoscoop.service.PortalAdminsService;
import org.infoscoop.util.SpringUtil;


public class CheckAdminFilter implements Filter {
	
	private Set administratorUidList = new HashSet();
	private String mode;

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
			Portaladmin portalAdmin = (Portaladmin) it.next();
			this.administratorUidList.add( portalAdmin.getUid() );
		}
	}
}