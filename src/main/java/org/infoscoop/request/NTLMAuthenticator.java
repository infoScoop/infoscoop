/* infoScoop OpenSource
 * Copyright (C) 2010 Beacon IT Inc.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0-standalone.html>.
 */

package org.infoscoop.request;

import java.net.URL;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NTCredentials;
import org.apache.commons.httpclient.auth.AuthScope;

public class NTLMAuthenticator implements Authenticator{
	private String domainController;
	
	public void doAuthentication(HttpClient client, ProxyRequest request, HttpMethod method, String uid, String pwd) throws ProxyAuthenticationException {
		try{
			client.getParams().setAuthenticationPreemptive(true);
			String[] uidDomain = uid.split("\\\\");
			String domain = "";
			if(uidDomain.length > 1){
				domain = uidDomain[0].trim();
				uid = uidDomain[1].trim();
			}else{
				uid = uidDomain[0].trim();
			}
			// create the information of certification(an userID and a password).
			Credentials credentials = new NTCredentials(uid, pwd, domainController, domain);
			// the scope of the certification.
			URL urlObj = new URL(method.getURI().toString());
			AuthScope scope1 = new AuthScope(urlObj.getHost(), urlObj.getPort(), null);
			// set a pair of a scope and an information of the certification.
			client.getState().setCredentials(scope1, credentials);
		}catch(Exception e){
			throw new ProxyAuthenticationException(e);
		}
	}

	
	public int getCredentialType() {
		return Authenticator.WIDGET_PREFS_CREDENTIAL;
	}


	public void setDomainController(String domainController) {
		this.domainController = domainController;
	}
}
