package org.infoscoop.service;

public class MenuAuthorization{
	String principalClass;
	String regx;
	String[] actions;
	public MenuAuthorization(String principalClassName, String regx, String[] actions) throws ClassNotFoundException{
		this.principalClass = principalClassName;
		this.regx = regx;
		this.actions = actions;
	}
}