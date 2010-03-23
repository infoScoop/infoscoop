package org.infoscoop.web;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Field;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.util.I18NUtil;
import org.infoscoop.web.i18n.BufferHttpServletResponseWrapper;

public class I18NFilter implements Filter {
	private static Log log = LogFactory.getLog(I18NFilter.class);

	private String type;

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		BufferHttpServletResponseWrapper bufResponse = new BufferHttpServletResponseWrapper(
				(HttpServletResponse) response);
		chain.doFilter(request, bufResponse);
		bufResponse.flushBuffer();
		String body = bufResponse.getStringContent();

		try {
			body = I18NUtil.resolve(type, body, request.getLocale());
		} catch (Exception e) {
			log.error("database error occurred. ",e);
		}
		
		if (bufResponse.getLocale() != null)
			response.setLocale(bufResponse.getLocale());
		response.setContentLength(body.getBytes("utf-8").length);
		
		Writer out = null;
		try{
			out = new OutputStreamWriter(response.getOutputStream(), "utf-8");
			out.write(body);
			out.flush();
		}finally{
			if(out!=null)
				out.close();
		}
	}

	public void init(FilterConfig config) throws ServletException {
		String typeVar = config.getInitParameter("type");
		if (log.isInfoEnabled())
			log.info("initialize : type=" + typeVar);
		Field f;
		try {
			f = I18NUtil.class.getDeclaredField(typeVar);
			type = (String) f.get(null);
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

}
