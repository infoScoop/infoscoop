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

import net.oauth.OAuthConsumer;

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
