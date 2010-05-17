package org.infoscoop.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class SetCharacterEncodingFilter implements Filter {
	private String encoding;

	public void destroy() {
		this.encoding = null;
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		if (this.encoding != null)
			request.setCharacterEncoding(this.encoding);
		chain.doFilter(request, response);
	}

	public void init(FilterConfig config) throws ServletException {
		this.encoding = config.getInitParameter("encoding");
	}

}
