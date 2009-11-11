package org.infoscoop.request;

import java.net.URL;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;

public class BasicAuthenticator implements Authenticator{
	public void doAuthentication(HttpClient client, ProxyRequest request, HttpMethod method, String uid, String pwd) throws ProxyAuthenticationException {
		try{
			client.getParams().setAuthenticationPreemptive(true);
			// create the information of certification(an userID and a password).
			Credentials defaultcreds1 = new UsernamePasswordCredentials(uid, pwd);
			// the scope of the certification.
			URL urlObj = new URL(method.getURI().toString());
			AuthScope scope1 = new AuthScope(urlObj.getHost(), urlObj.getPort(), null);
			// set a pair of a scope and an information of the certification.
			client.getState().setCredentials(scope1, defaultcreds1);
		}catch(Exception e){
			throw new ProxyAuthenticationException(e);
		}
	}

	
	public int getCredentialType() {
		return Authenticator.WIDGET_PREFS_CREDENTIAL;
	}
}