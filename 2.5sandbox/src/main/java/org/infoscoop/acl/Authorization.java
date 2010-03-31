package org.infoscoop.acl;

public class Authorization {
	private String role;
	private String[] actions;
	
	Authorization(String role, String[] actions){
		this.role = role;
		this.actions = actions;
	}
	
	public String getRole(){
		return this.role;
	}
	
	public String[] getActions(){
		return this.actions;
	}
}
