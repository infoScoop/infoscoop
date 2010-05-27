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

package org.infoscoop.admin.command;

public class CommandResponse {
	private boolean success;
	private String response;
	
	/**
	 * The response object of Control command
	 * @param success :Whether it is success the command or not.
	 * @param response :The response body of command or error message. When the command succeeded and there is no response body, it is specified null.
	 */
	public CommandResponse(boolean success, String response){
		this.success = success;
		this.response = response;
	}
	
	public boolean isSuccess(){
		return this.success;
	}

	public String getResponseBody(){
		return this.response;
	}
}
