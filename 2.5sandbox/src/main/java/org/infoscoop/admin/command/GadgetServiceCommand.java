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
