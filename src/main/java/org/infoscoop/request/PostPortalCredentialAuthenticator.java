package org.infoscoop.request;

public class PostPortalCredentialAuthenticator extends PostCredentialAuthenticator{

	public int getCredentialType() {
		return Authenticator.PORTAL_CREDENTIAL;
	}
	
}

