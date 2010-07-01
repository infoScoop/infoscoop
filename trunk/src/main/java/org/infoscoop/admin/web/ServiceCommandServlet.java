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

package org.infoscoop.admin.web;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.admin.command.CommandResponse;
import org.infoscoop.admin.command.ServiceCommand;
import org.infoscoop.service.PortalAdminsService;
import org.infoscoop.util.SpringUtil;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

public class ServiceCommandServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static Log log = LogFactory.getLog( ServiceCommandServlet.class );
	
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		service(req, resp);
	}
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		service(req, resp);
	}
	
	@SuppressWarnings("unchecked")
	protected void service( HttpServletRequest req,HttpServletResponse resp ) throws IOException {
		String uri = req.getRequestURI();
		String services = "/services/";
		String path = uri.substring( uri.indexOf( services )+services.length() );
		
		String[] servicePaths = path.split("/");
		String serviceName = servicePaths[0];
		String commandName = servicePaths[1];
		if(log.isInfoEnabled())
			log.info("Call "  + commandName + " comman of "+ serviceName + " service.");
		
		Object temp = null;
		try {
			temp = SpringUtil.getBean( serviceName );
		} catch( NoSuchBeanDefinitionException ex ) {
			log.error("", ex );
		}
		
		if( temp == null || !( temp instanceof ServiceCommand ) ) {
			resp.sendError( 500, "Service Not Found" );
			return;
		}
		
		ServiceCommand serviceCommand = ( ServiceCommand )temp;
		try{
			resp.setContentType("text/plain; charset=UTF-8");
			resp.setHeader("Pragma", "no-cache");
			resp.setHeader("Cache-Control", "no-cache");
			
			CommandResponse commandResp;
			try {
				// check the roll.
				Properties notPermittedPatterns = serviceCommand.getNotPermittedPatterns();
				
				Set permissionTypeList = notPermittedPatterns.keySet();
				List<String> myPermissionList = PortalAdminsService.getHandle().getMyPermissionList();
				String notPermittedPattern = ".*";
				
				
				boolean notMatched = false;
				myPermissionList.add("*");
				for(String myPermissionType : myPermissionList){
					notPermittedPattern = notPermittedPatterns.getProperty(myPermissionType, ".*");
					if(commandName.matches(notPermittedPattern))
						continue;
					
					// when there is no pattern that isn't permitted
					notMatched = true;
					break;
				}
				
				if(!notMatched)
					resp.sendError( 403, "It is an access to the service that has not been permitted." );
				
				commandResp = serviceCommand.execute( commandName,req,resp );
			} catch ( Throwable ex ) {
				log.error(ex.getMessage(), ex);
				Throwable cause;
				while(( cause = ex.getCause()) != null )
					ex = cause;
				
				commandResp = new CommandResponse(false, ex.getClass().getName()+": "+ex.getMessage());
			}
			
			int status = 200;
			String responseBody = commandResp.getResponseBody();
			if ( !commandResp.isSuccess())
				status = 500;
			
			resp.setStatus(status);
			if( responseBody == null )
				responseBody = "command is "+( ( status == 200 )? "succeed!":"failed");
			
			resp.getWriter().write( responseBody );
			
			if(log.isInfoEnabled())
				log.info("Execution result of service command is " +  commandResp.isSuccess() + ".");
			
		} catch ( Exception ex ) {
			log.error("Unexpected error occurred.", ex );
			resp.sendError( 500, ex.getMessage());
		}
	}
}
