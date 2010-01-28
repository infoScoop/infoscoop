package org.infoscoop.request;


import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.infoscoop.util.RequestUtil;

public class PostCredentialAuthenticator implements Authenticator{
	
	private String uidParamName = "userid";
	private String pwdParamName = "passwd";

	public String getUidParamName() {
		return uidParamName;
	}
	public void setUidParamName(String uidParamName) {
		this.uidParamName = uidParamName;
	}
	public String getPwdParamName() {
		return pwdParamName;
	}
	public void setPwdParamName(String pwdParamName) {
		this.pwdParamName = pwdParamName;
	}

	public void doAuthentication(HttpClient client, ProxyRequest request, HttpMethod method, String uid, String pwd) throws ProxyAuthenticationException {
		String uidParamNameHeader = request.getRequestHeader(UID_PARAM_NAME);
		String passwdParamNameHeader = request.getRequestHeader(PASSWD_PARAM_NAME);
		String uidParamName = (uidParamNameHeader != null) ? uidParamNameHeader : this.uidParamName;
		String passwdParamName = (passwdParamNameHeader != null) ? passwdParamNameHeader : this.pwdParamName;
		try{
			request.addIgnoreHeader("Content-Type");
			request.addIgnoreHeader("content-length");
			
			method.addRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
			NameValuePair[] params = new NameValuePair[2];
			
			PostMethod pMethod = (PostMethod)method;
			String queryString = pMethod.getQueryString();
			
			// Delete the same parameter's name that has already exist.
			pMethod.removeParameter(uidParamName);
			pMethod.removeParameter(passwdParamName);
			queryString = RequestUtil.removeQueryStringParam(queryString, uidParamName);
			queryString = RequestUtil.removeQueryStringParam(queryString, passwdParamName);
			
			pMethod.setQueryString(queryString);
			
			params[0] = new NameValuePair(uidParamName, uid);
			params[1] = new NameValuePair(passwdParamName, pwd);
			pMethod.setRequestBody(params);
			
			pMethod.addRequestHeader("content-length", String.valueOf(pMethod.getRequestEntity().getContentLength()));
		}catch(Exception e){
			throw new ProxyAuthenticationException(e);
		}
	}

	public int getCredentialType() {
		return Authenticator.WIDGET_PREFS_CREDENTIAL;
	}
}
