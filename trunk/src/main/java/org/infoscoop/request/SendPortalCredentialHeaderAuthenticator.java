package org.infoscoop.request;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;

public class SendPortalCredentialHeaderAuthenticator implements Authenticator{
	
	private String uidHeaderName = null;
	public String getUidHeaderName() {
		return uidHeaderName;
	}
	public void setUidHeaderName(String name){
		this.uidHeaderName = name; 
	}

	public void doAuthentication(HttpClient client, ProxyRequest request, HttpMethod method, String uid, String pwd) throws ProxyAuthenticationException {
		String uidHeaderNameHeader = request.getRequestHeader(UID_PARAM_NAME);
		String uidHeaderName = (uidHeaderNameHeader != null) ? uidHeaderNameHeader : this.uidHeaderName;
		try{
			method.removeRequestHeader(uidHeaderName);
			method.addRequestHeader(uidHeaderName, uid);
		}catch(Exception e){
			throw new ProxyAuthenticationException(e);
		}
	}

	public int getCredentialType() {
		return Authenticator.PORTAL_CREDENTIAL;
	}
	
}

