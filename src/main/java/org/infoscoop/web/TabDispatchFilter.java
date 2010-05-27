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
