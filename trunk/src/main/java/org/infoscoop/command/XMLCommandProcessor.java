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
	XMLCommandProcessor(){
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
