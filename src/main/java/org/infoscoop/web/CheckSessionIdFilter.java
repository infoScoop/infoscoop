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

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.context.UserContext;
import org.infoscoop.dao.AccessLogDAO;
import org.infoscoop.dao.SessionDAO;
import org.infoscoop.service.LogService;

public class CheckSessionIdFilter implements javax.servlet.Filter {
	private Log log = LogFactory.getLog(this.getClass());

	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpReq = (HttpServletRequest) req;
		HttpServletResponse httpRes = (HttpServletResponse) res;
		String uid = (String) httpReq.getSession().getAttribute("Uid");
		
		//If an uid is empty, we don't check.
		if (uid != null || !"true".equalsIgnoreCase( req.getParameter(CheckDuplicateUidFilter.IS_PREVIEW ))) {

			try{
				String sessionId = httpReq.getHeader("MSDPortal-SessionId");
				String squareId = UserContext.instance().getUserInfo().getCurrentSquareId();

				SessionDAO dao = SessionDAO.newInstance();
				String currentId = dao.getSessionId(uid, squareId);
				if( HttpStatusCode.MSD_FORCE_RELOAD.equals( currentId )) {
					dao.deleteSessionId( uid, squareId );

					httpRes.setHeader( HttpStatusCode.HEADER_NAME,
							HttpStatusCode.MSD_FORCE_RELOAD );
					httpRes.sendError( 500 );
					if (log.isInfoEnabled())
						log.info("session required reload. status="
								+ HttpStatusCode.MSD_FORCE_RELOAD);
					return;
				}

				if (sessionId != null && !sessionId.equals( currentId )) {
					httpRes.setHeader(HttpStatusCode.HEADER_NAME,
							HttpStatusCode.MSD_INVALID_SESSION);
					httpRes.sendError(500);
					if (log.isInfoEnabled())
						log.info("invalid session error occured. status="
								+ HttpStatusCode.MSD_INVALID_SESSION);
					return;
				}

				//Access log
				LogService.getHandle().insertDailyAccessLog(uid);
			}catch(Exception e){
				log.error("The unexcepted error occured", e);
			}
		}

		chain.doFilter(req, res);
	}

	public void init(FilterConfig config) throws ServletException {
	}

	public void destroy() {
	}
}
