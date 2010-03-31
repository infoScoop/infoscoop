package org.infoscoop.admin.command;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.infoscoop.service.PropertiesService;


public class PropertiesServiceCommand extends ServiceCommand {
	public CommandResponse execute(String commandName, HttpServletRequest req,
			HttpServletResponse resp) throws Exception {
		if ("getPropertiesJson".equals(commandName)) {
			return getPropertiesJson(req, resp);
		}

		return super.execute(commandName, req, resp);
	}

	public CommandResponse getPropertiesJson(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Locale locale = request.getLocale();

		return new CommandResponse(true, ((PropertiesService) service)
				.getPropertiesJson(locale));
	}
}
