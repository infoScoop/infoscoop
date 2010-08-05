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

/**
 * The object that is return value of a method of processXML mounted by the interface of XMLCommandeProcessor.
 * @author nakata
 *
 */
public class CommandResult {
    private String id;
    private String status;
    private String message;
    
    /**
     * create a new object of CommandResult.
     */
    public CommandResult() {
        id = null;
        status = null;
        message = null;
    }

    /**
     * create a new object of CommandResult by adding the id, the status, and the message.
     * @param id :command id
     * @param status :the string that shows a result of executing command.(It's "ok" or "failed")
     * @param message :the string that shows the reason when the command failed.
     */
    public CommandResult(String id,String status,String message) {
        this.id = id;
        this.status = status;
        this.message = message;
    }


    /**
     * get a command id.
     * @return command id
     */
    public String getId() {
    
        return id;
    }

    /**
     * register a command id.
     * @param id :command id
     */
    public void setId(String id) {
    
        this.id = id;
    }

    /**
     * get an error message.
     * @return error message (If the status is "OK", the return value is null.)
     */
    public String getMessage() {
    
        return message;
    }

    /**
     * register an error message.
     * @param message :error message
     */
    public void setMessage(String message) {
    
        this.message = message;
    }

    /**
     * get a status.
     * @return the string that shows a result of executing command.(It's "ok" or "failed")
     */
    public String getStatus() {
    
        return status;
    }

    /**
     * register a status.
     * @param status the string that shows a result of executing command.(It's "ok" or "failed")
     */
    public void setStatus(String status) {
    
        this.status = status;
    }
    
    public String toString(){
        StringBuffer strBuf = new StringBuffer("<command id=\"" + id +"\" status=\"" + status + "\"");
        
        if(message != null){
            strBuf.append(" message=\"" + message +"\"/>");
        }else{
            strBuf.append("/>");
        }
       
        return strBuf.toString();
    }
    
}
