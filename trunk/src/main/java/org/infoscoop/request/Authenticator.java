package org.infoscoop.request;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;

public interface Authenticator {
	public static int WIDGET_PREFS_CREDENTIAL = 0;
	public static int PORTAL_CREDENTIAL = 1;

	public static String UID_PARAM_NAME = "_authUidParamName";
	public static String PASSWD_PARAM_NAME = "_authPasswdParamName";
	
	void doAuthentication(HttpClient client, ProxyRequest request,  HttpMethod method, String uid, String pwd) throws ProxyAuthenticationException;
	int getCredentialType();
}
