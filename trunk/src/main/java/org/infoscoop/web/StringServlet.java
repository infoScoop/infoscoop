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
import java.io.Writer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.util.StringUtil;

public class StringServlet extends HttpServlet {
	

	private static final long serialVersionUID = "org.infoscoop.web.StringServlet"
			.hashCode();

	private Log log = LogFactory.getLog(this.getClass());

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		String text = request.getParameter("text");
		String lengthStr = request.getParameter("length");
		int length = Integer.parseInt(lengthStr);
		
		text = new String(text.getBytes("iso-8859-1"), "utf-8");
		
		String transText = StringUtil.getTruncatedString(text, length, "utf-8");
		
		if(log.isDebugEnabled())
			log.debug("text truncated : " + transText
				+ ", length : "+length+", encoding : " + "iso-8859-1");

		response.setContentType("text/plain; charset=UTF-8");
		response.setContentLength( transText.getBytes("utf-8").length );

		Writer writer = response.getWriter();

		writer.write(transText);

		writer.flush();
		writer.close();
	}
	
}
