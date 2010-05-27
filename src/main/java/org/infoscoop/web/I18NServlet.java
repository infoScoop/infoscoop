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
