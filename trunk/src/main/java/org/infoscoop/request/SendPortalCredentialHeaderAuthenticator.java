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

