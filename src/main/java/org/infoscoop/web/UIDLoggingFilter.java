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
import java.util.Enumeration;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.MDC;
import org.springframework.security.core.context.SecurityContextHolder;

public class UIDLoggingFilter implements javax.servlet.Filter {
	public void doFilter(ServletRequest req, ServletResponse resp,
			FilterChain chain) throws IOException, ServletException {
		String uid = ( String )(( HttpServletRequest )req ).getSession().getAttribute("Uid");
		if( uid == null )
			uid = "";
		
		// for infoScoop api log
		Enumeration<?> enu = (( HttpServletRequest )req ).getAttributeNames();
		while(enu.hasMoreElements()){
			String key = (String)enu.nextElement();
			if(key.equals("OAuth2AuthenticationDetails.ACCESS_TOKEN_VALUE")||key.equals("OAuth2AuthenticationDetails.REFRESH_TOKEN_VALUE")){
				Object obj = SecurityContextHolder.getContext().getAuthentication().getName();
				if(obj instanceof String)
					uid = (String)obj;

				break;
			}
		}
		
		MDC.put("uid",uid );
		chain.doFilter( req,resp );
		MDC.remove("uid");
	}
	public void init( FilterConfig config ) throws ServletException {
	}
	public void destroy() {
	}
}
