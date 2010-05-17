package org.infoscoop.request;

public class ProxyAuthenticationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -960080902866345421L;

	public ProxyAuthenticationException(Exception e) {
		super(e);
	}

	public ProxyAuthenticationException(String msg) {
		super(msg);
	}

	public ProxyAuthenticationException(String msg, Exception e) {
		super(msg, e);
	}

}
