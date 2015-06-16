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

package org.infoscoop.util.spring;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.dao.model.RestrictionKey;
import org.infoscoop.service.RestrictionKeyService;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class CheckExpirationInterceptor extends HandlerInterceptorAdapter {
	private static Log log = LogFactory.getLog(CheckExpirationInterceptor.class);
	
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		
		boolean isExpired = true;
		String key = request.getParameter("key");
		
		RestrictionKeyService keyService = RestrictionKeyService.getHandle();
		RestrictionKey keyEntity = keyService.getRestrictionEntity(key);
		
		if(keyEntity == null){
			log.info("The key was not found : " + key);
		}
		else if(keyEntity.isExpiredKey()){
			log.info("The key has expired : " + key + ", uid : " + keyEntity.getUid());
			keyService.deleteRestrictionEntity(keyEntity);
		}
		else{
			isExpired = false;
		}
		
		if(isExpired){
			request.getRequestDispatcher("/prepare/urlExpired").forward(request, response);
			return false;
		};
		return super.preHandle(request, response, handler);
	}
}
