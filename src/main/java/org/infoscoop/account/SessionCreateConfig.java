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

package org.infoscoop.account;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.util.SpringUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class have setting for SessionManagerFilter.
 * @author hr-endoh
 *
 */
public class SessionCreateConfig {
	private static Log log = LogFactory.getLog(SessionCreateConfig.class);

	private Collection<PrincipalDef> principalDefs = new ArrayList<PrincipalDef>();

	private boolean loginAuthentication = true;
	private String uidHeader = null;
	private String usernameHeader = null;
	private Map<String, String> roleHeaderMap = new HashMap<String,String>();

	public static SessionCreateConfig getInstance(){
		return (SessionCreateConfig) SpringUtil.getBean("sessionCreateConfig");
	}

	public void setHeaderPrincipalConfig(String confJsonStr){
		if(confJsonStr == null || confJsonStr.trim().length() == 0) return;

		try {
			JSONArray roleHeaderNames = new JSONArray(confJsonStr);
			for(int i = 0; i < roleHeaderNames.length(); i++){
				JSONObject roleHeaderName = roleHeaderNames.getJSONObject(i);
				String type = roleHeaderName.getString("type");
				String label = roleHeaderName.getString("displayName");
				String header = roleHeaderName.getString("headerName");
				principalDefs.add(new HeaderPrincipalDef(type, label, header));
			}

			for(PrincipalDef def : this.principalDefs){
				HeaderPrincipalDef headerDef = (HeaderPrincipalDef)def;
				roleHeaderMap.put(headerDef.headerName, def.getType());
			}

		} catch (JSONException e) {
			log.error("Session Config invalid.", e);
		}
	}

	public Collection<PrincipalDef> getPrincipalDefs(){
		return this.principalDefs;
	}


	static class HeaderPrincipalDef extends PrincipalDef{

		private String headerName;

		public HeaderPrincipalDef(String type, String label, String headerName) {
			super(type, label);
			log.info("Principal Header name for " + type +  " is " + headerName);
			this.headerName = headerName;
		}

	}

	public static boolean doLogin(){
		return getInstance().loginAuthentication;
	}

	public boolean isLoginAuthentication() {
		return loginAuthentication;
	}

	public void setLoginAuthentication(boolean loginAuthentication) {
		this.loginAuthentication = loginAuthentication;
	}

	public String getUidHeader() {
		return uidHeader;
	}
	
	public boolean hasUidHeader() {
		return uidHeader != null && uidHeader.trim().length() != 0;
	}

	public void setUidHeader(String uidHeader) {
		if(uidHeader == null || uidHeader.trim().length() == 0) return;
		if(log.isInfoEnabled())
			log.info("Header name for userid is " + uidHeader);
		this.uidHeader = uidHeader;
	}

	public String getUsernameHeader() {
		return usernameHeader;
	}

	public void setUsernameHeader(String usernameHeader) {
		if(usernameHeader == null || usernameHeader.trim().length() == 0) return;

		if(log.isInfoEnabled())
			log.info("Header name for username is " + usernameHeader);

		this.usernameHeader = usernameHeader;
	}

	public Map<String, String> getRoleHeaderMap(){
		return this.roleHeaderMap;
	}
}
