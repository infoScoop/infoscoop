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
import java.util.Collection;
import java.util.HashSet;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.unirita.saas.helper.SaaSAccountHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpStatus;
import org.infoscoop.context.UserContext;
import org.infoscoop.service.SquareService;

public class CheckSquareFilter implements javax.servlet.Filter {
	private Log log = LogFactory.getLog(this.getClass());
	private Collection<String> excludePaths = new HashSet<String>();
	private Collection<String> excludePathx = new HashSet<String>();
	
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpReq = (HttpServletRequest) req;
		HttpServletResponse httpRes = (HttpServletResponse) res;
		String uid = (String) httpReq.getSession().getAttribute("Uid");
		
		try{
			//If an uid is empty, we don't check.
			if (!isExcludePath(httpReq.getServletPath()) && (uid != null || !"true".equalsIgnoreCase( req.getParameter(CheckDuplicateUidFilter.IS_PREVIEW )))) {
				String squareId = UserContext.instance().getUserInfo().getCurrentSquareId();
				
				if(!SquareService.getHandle().existsSquare(squareId)){
					
					httpRes.setHeader( HttpStatusCode.HEADER_NAME,
							HttpStatusCode.MSD_FORCE_RELOAD );
					if (log.isInfoEnabled())
						log.info("squareId: " + squareId + " is not exists. status="
								+ HttpStatusCode.MSD_FORCE_RELOAD);
					
					if("/comsrv".equals(httpReq.getServletPath())){
						httpRes.sendError(HttpStatus.SC_INTERNAL_SERVER_ERROR);
					}else{
						httpRes.sendRedirect(httpReq.getContextPath() + "/square/closed.jsp");
					}
					return;
				}
				else if(!SaaSAccountHelper.isExistsUserInSquare(uid, squareId)){
					httpRes.sendRedirect(httpReq.getContextPath() + "/square/forbidden.jsp");
				}
			}
		}catch(Exception e){
			log.error("unexpected error occurred.", e);
			throw new ServletException(e);
		}

		chain.doFilter(req, res);
	}

	public void init(FilterConfig config) throws ServletException {

		String excludePathStr = config.getInitParameter("excludePath");
		if(excludePathStr != null){
			String[] pathArray = excludePathStr.split(",");
			for(int i = 0; i < pathArray.length; i++){
				String path = pathArray[i].trim();
				if( path.endsWith("*")) {
					excludePathx.add( path.substring(0,path.length() -1 ));
				} else {
					excludePaths.add( path );
				}
			}
		}
	}

	public void destroy() {
	}

	private boolean isExcludePath( String path ) {
		if( excludePaths.contains( path ))
			return true;

		for( String p : excludePathx ) {
			if( path.startsWith( p ))
				return true;
		}

		return false;
	}
}
