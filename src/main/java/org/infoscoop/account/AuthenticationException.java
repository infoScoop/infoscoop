package org.infoscoop.account;

public class AuthenticationException extends Exception {
	private String resourceId;
	
	public AuthenticationException(Exception e) {
		super(e);
	}

	public AuthenticationException(String msg) {
		super(msg);
	}

	@Deprecated
	public String getResourceId(){
		return resourceId;
	}
	
	@Deprecated
	public void setResourceId(String resourceId){
		this.resourceId = resourceId;
	}
	
}
