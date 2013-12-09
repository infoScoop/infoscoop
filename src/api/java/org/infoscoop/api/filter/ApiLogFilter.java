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
