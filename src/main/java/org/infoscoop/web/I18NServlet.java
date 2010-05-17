package org.infoscoop.web;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.infoscoop.util.I18NUtil;


public class I18NServlet extends HttpServlet {
	private static final long serialVersionUID = "org.infoscoop.web.I18NServlet"
			.hashCode();

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		String type = req.getParameter("type");
		String str = req.getParameter("s");
		Writer out = null;
		try {
			str = I18NUtil.resolve(type, str, req.getLocale());
			res.setContentLength(str.getBytes("utf-8").length);
			out = new OutputStreamWriter(res.getOutputStream(), "utf-8");
			out.write(str);
			out.flush();
		} catch (Exception e) {
			throw new ServletException(e);
		} finally{
			if(out != null)
				out.close();
		}
	}

}