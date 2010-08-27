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

package org.infoscoop.command;

import org.w3c.dom.Element;

abstract public class XMLCommandProcessor {

	protected Element commandXml;
	protected CommandResult result;
	protected String uid;
	
	protected String commanId;
	
	/**
	 * @param command
     *            an element of command
     * @param commandXml
	 */
	protected XMLCommandProcessor(){
	}
	
	public void initialize(String uid, Element commandXml){
		this.uid = uid;
		this.commandXml = commandXml;
	}
	
	public String getCommandId() {
		return this.commandXml.getAttribute("id").trim();
	}
	
    /**
     * execute the command of XML.
     * @return an element that containts a result of executing command.
     * @throws Exception 
     */
    abstract public void execute() throws Exception;
    
    /**
     * get an object that is result of executing command.
     * @return
     */
	public CommandResult getResult() {
		return this.result;
	}
	
	void setResult(CommandResult result){
		this.result = result;
	}

}
