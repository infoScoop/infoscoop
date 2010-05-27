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
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.service.PropertiesService;
import org.json.JSONObject;

public class PropertiesServlet extends HttpServlet {

	private static final long serialVersionUID = "org.infoscoop.web.PropertiesServlet"
			.hashCode();
	private static Log log = LogFactory.getLog(PropertiesServlet.class);

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		long start = System.currentTimeMillis();

		try {
			response.setHeader("Content-Type", "text/plane; charset=UTF-8");
			Writer w = response.getWriter();
			w.write(makeJavaScript( PropertiesService.getHandle().getPropertiesMap()));
			w.flush();
		} catch (Exception e) {
			response.sendError(500, e.getMessage());
		}
		long end = System.currentTimeMillis();
		if(log.isDebugEnabled())
			log.debug("--- PropertiesServlet doPost: " + (end - start));
	}

	/**
	 * @param propsMap
	 * @return
	 */
	private String makeJavaScript(Map propsMap) throws Exception {
		StringBuffer sb = new StringBuffer();
		for (Iterator it = propsMap.keySet().iterator(); it.hasNext();) {
			boolean noQuote = false;
			String id = (String) it.next();
			String value = (String) propsMap.get(id);
			if (value != null && !"".equals(value)) {
				if (value.equals("true") || value.equals("false")
						|| value.matches("^[-,+]?\\d+(\\.\\d+)?$")) {
					noQuote = true;
				}
			} else {
				value = "";
			}
			if (id != null) {
				sb.append("var ");
				sb.append(id);
				sb.append("=");
				if (!noQuote)
					value = JSONObject.quote( value );
				sb.append(value);
				sb.append(";");
				sb.append("\n");
			}
		}
		return sb.toString();
	}

}
