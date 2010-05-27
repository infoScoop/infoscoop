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

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.infoscoop.service.GadgetService;


public class GadgetServiceCommand extends ServiceCommand {
	public CommandResponse execute(String commandName, HttpServletRequest req,
			HttpServletResponse resp) throws Exception {
		if("getGadgetJson".equals(commandName)) {
			return getJson( req,resp );
		} 
		return super.execute(commandName, req, resp);
	}
	public CommandResponse getJson(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Locale locale = request.getLocale();
		int timeout = 0;
		
		return new CommandResponse(true,(( GadgetService )service ).getGadgetJson( locale,timeout ));
	}
}
