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

package org.infoscoop.service;

import java.util.List;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.command.XMLCommandProcessor;
import org.infoscoop.util.SpringUtil;
import org.infoscoop.util.XmlUtil;
import org.springframework.beans.BeansException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class CommandExecutionService {
	
	private static Log log = LogFactory.getLog(CommandExecutionService.class);
	public static CommandExecutionService getHandle() {
		return (CommandExecutionService)SpringUtil.getBean("CommandExecutionService");
	}
	
	public void execute(String uid, Element commandXML, List resultList) throws Exception{
		NodeList commandList = commandXML.getElementsByTagName("command");
		
		XMLCommandProcessor[] commands = new XMLCommandProcessor[commandList.getLength()];
		
		for (int i = 0; i < commandList.getLength(); i++) {
			Element commandEl = (Element)commandList.item(i);

			if(log.isDebugEnabled())
				log.debug("Command Elememt: " + XmlUtil.xmlSerialize(commandEl));
			
			
			String type = commandEl.getAttribute("type");
			
			XMLCommandProcessor command;
			try{
				command = (XMLCommandProcessor)SpringUtil.getBean(type);
			}catch(BeansException e){
				log.error("Unexpected error occurred.", e);
				continue;
			}
			command.initialize(uid, commandEl);
			commands[i] = command;
		    //XMLCommandProcessor command = getCommand(context, type, resultList);
			if (command != null) {
				if(log.isInfoEnabled())
					log.info("uid:[" + uid + "]: doPost: "
							+ command.getClass().getName());
				command.execute();
			}else{
				log.error("Command " + type + " is not exist.");
			}
		}
		
		for(int i = 0; i < commands.length; i++){
			if(commands[i] != null){
				if(log.isInfoEnabled() ){
					log.info("Command [" + commands[i].getCommandId() + "] result: " + commands[i].getResult());
				}
				resultList.add(commands[i].getResult());
			}
		}
	}

}
