package org.infoscoop.admin.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ICommand {

	/**
	 * @param httpservletrequest
	 * @param httpservletresponse
	 * @throws Exception 
	 */
	public CommandResponse execute(HttpServletRequest httpservletrequest, HttpServletResponse httpservletresponse) ;

}
