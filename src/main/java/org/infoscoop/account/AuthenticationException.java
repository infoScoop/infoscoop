package org.infoscoop.account;

public class AuthenticationException extends Exception {
    
	public AuthenticationException(Exception e) {
		super(e);
	}

	public AuthenticationException(String msg) {
		super(msg);
	}

}
