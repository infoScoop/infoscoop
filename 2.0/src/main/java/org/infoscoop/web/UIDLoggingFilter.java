package org.infoscoop.web;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.MDC;

public class UIDLoggingFilter implements javax.servlet.Filter {
	public void doFilter(ServletRequest req, ServletResponse resp,
			FilterChain chain) throws IOException, ServletException {
		String uid = ( String )(( HttpServletRequest )req ).getSession().getAttribute("Uid");
		if( uid == null )
			uid = "";
		
		MDC.put("uid",uid );
		chain.doFilter( req,resp );
		MDC.remove("uid");
	}
	public void init( FilterConfig config ) throws ServletException {
	}
	public void destroy() {
	}
}
