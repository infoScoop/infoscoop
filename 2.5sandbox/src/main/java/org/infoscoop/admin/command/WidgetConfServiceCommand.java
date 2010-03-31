package org.infoscoop.admin.command;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.infoscoop.service.WidgetConfService;


public class WidgetConfServiceCommand extends ServiceCommand {
	public CommandResponse execute(String commandName, HttpServletRequest req,
			HttpServletResponse resp) throws Exception {
		if("getWidgetConfJson".equals(commandName)) {
			return getJson( req,resp );
		} else if("getWidgetConfJsonByType".equals( commandName )) {
			return getJsonByType(req , resp);
		}
		
		return super.execute(commandName, req, resp);
	}
	public CommandResponse getJson(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Locale locale = request.getLocale();
		
		return new CommandResponse(true,(( WidgetConfService )service ).getWidgetConfsJson( locale ));
	}
	public CommandResponse getJsonByType(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Locale locale = request.getLocale();
		
		String type = request.getParameter("type");
		
		return new CommandResponse(true,(( WidgetConfService )service ).getWidgetConfJsonByType( type,locale ));
	}
}
