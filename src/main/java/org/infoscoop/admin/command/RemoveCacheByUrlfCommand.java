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

package org.infoscoop.admin.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.service.CacheService;

public class RemoveCacheByUrlfCommand implements ICommand {

	private static Log log = LogFactory.getLog(RemoveCacheByUrlfCommand.class);

	public CommandResponse execute(HttpServletRequest request, HttpServletResponse response) {
		
		String url = request.getParameter("url");
		if(url == null){
			return new CommandResponse(false, "Must specify url.");
		}
		CacheService.getHandle().deleteCacheByUrl(url);
		return new CommandResponse(true, null);
	}
}
