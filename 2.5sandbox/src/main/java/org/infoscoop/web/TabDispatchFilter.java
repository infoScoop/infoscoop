package org.infoscoop.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * tab.jsp dispatch for Lite
 * @author nishiumi
 *
 */
public class TabDispatchFilter implements Filter {
	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest httpReq = (HttpServletRequest)request;
		
		String tabOrderStr = httpReq.getServletPath().substring( "/tab/".length() );
		try{
			Integer.parseInt(tabOrderStr);
			request.setAttribute("tabOrder", tabOrderStr);
			request.getRequestDispatcher("/tab.jsp").forward(request, response);
		}catch(NumberFormatException e){
			request.getRequestDispatcher("/" + tabOrderStr).forward(request, response);	
		}
	}

	public void init(FilterConfig arg0) throws ServletException {
		
	}

}
