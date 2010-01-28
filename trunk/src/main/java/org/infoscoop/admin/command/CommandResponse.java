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
