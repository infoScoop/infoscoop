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
		String adminContext = httpreq.getContextPath() + "/admin";
		String referer = httpreq.getHeader("Referer");

		if ((adminContext != null && reqUri.indexOf(adminContext) != -1)
				|| (referer != null && referer.indexOf(adminContext) != -1)) {//リファラを判定しているのは、プレビューなどの/adminを含まないリクエストに対応するため
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
