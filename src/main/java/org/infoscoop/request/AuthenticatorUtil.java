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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;



import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.dao.model.AuthCredential;
import org.infoscoop.service.AuthCredentialService;
import org.infoscoop.util.RSAKeyManager;
import org.infoscoop.util.SpringUtil;
import org.infoscoop.util.StringUtil;

public class AuthenticatorUtil {
	private static Log log = LogFactory.getLog(AuthenticatorUtil.class);

	private static final Set rssMimeSet = new HashSet();	
	static {
		rssMimeSet.add("application/rss+xml");
		rssMimeSet.add("application/rdf+xml");
		rssMimeSet.add("application/xml");
		rssMimeSet.add("text/plain");
	}
	
	public static void doAuthentication(HttpClient client, HttpMethod method, ProxyRequest request) throws ProxyAuthenticationException {
		String authCredentialId = request.getRequestHeader("authCredentialId");
		String authType = request.getRequestHeader("authType");
		if(authCredentialId != null || authType != null){
			String uid = null;
			String pwd = null;
			if(authCredentialId != null){
				AuthCredential credential = AuthCredentialService.getHandle().getCredential(request.getPortalUid(), authCredentialId);
				if(credential == null){
					return;
				}
				authType = credential.getAuthType();
				uid = credential.getAuthUid();
				pwd = StringUtil.getNullSafe(credential.getAuthPasswd());
			}else{
				uid = request.getRequestHeader("authuserid");
				pwd = request.getRequestHeader("authpassword");
				if(pwd == null)pwd = "";//Headers are not sent if Ajax and setRequestHedaer("hoge","")
			}

			if("postCredential".equalsIgnoreCase( authType ))
				authType = "postCredential";
			
			Authenticator authenticator = (Authenticator)SpringUtil.getBean(authType + "Authenticator");
			if(log.isDebugEnabled())log.debug("Use authenticator: " + authenticator);
			log.info("AuthType: " + authType);
			switch(authenticator.getCredentialType()){
			case Authenticator.WIDGET_PREFS_CREDENTIAL:
				//Crypt cryptInstance = Crypt.gerCryptInstance();
				RSAKeyManager rsa = RSAKeyManager.getInstance();
				
				try {
					pwd = rsa.decrypt(pwd.trim());
				} catch (Exception e) {
					log.error("", e);
					request.putResponseHeader("WWW-Authenticate", authType);
					throw new ProxyAuthenticationException("invalid password.", e);
				}
				if(uid == null || "".equals(uid)){
					request.putResponseHeader("WWW-Authenticate", authType);
					throw new ProxyAuthenticationException("Userid may not be null.");
				}
				break;
			case Authenticator.PORTAL_CREDENTIAL :
				uid = request.getPortalUid();
				pwd = "";
				if(uid == null || "".equals(uid)){
					request.putResponseHeader("WWW-Authenticate", authType);
					throw new ProxyAuthenticationException("Userid may not be null.");
				}
				break;
			default:
			}
			if(log.isDebugEnabled())log.debug("Authenticate uid: " + uid + ", password: " + pwd);
			authenticator.doAuthentication(client, request, method, uid, pwd);
		}
	}
	
}
