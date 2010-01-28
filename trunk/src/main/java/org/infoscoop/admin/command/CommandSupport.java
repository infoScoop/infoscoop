package org.infoscoop.admin.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public abstract class CommandSupport {
	protected Log log = LogFactory.getLog( getClass() );
	
	public abstract CommandResponse execute(String commandName, HttpServletRequest req,
			HttpServletResponse resp) throws Exception;

}