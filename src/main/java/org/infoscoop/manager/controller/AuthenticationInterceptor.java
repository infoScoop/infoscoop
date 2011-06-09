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

package org.infoscoop.manager.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpStatus;
import org.infoscoop.service.PortalAdminsService;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * Authorization check of management function.
 *
 * @author nishiumi
 *
 */
public class AuthenticationInterceptor extends HandlerInterceptorAdapter {
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {

		if(handler instanceof ControllerInterface){
			ControllerInterface con = (ControllerInterface)handler;

			PortalAdminsService service = PortalAdminsService.getHandle();
			if(con.getRoleName() != null && !service.isPermitted(con.getRoleName())){
				response.sendError(HttpStatus.SC_FORBIDDEN);
				return false;
			}
		}
		return super.preHandle(request, response, handler);
	}
}
