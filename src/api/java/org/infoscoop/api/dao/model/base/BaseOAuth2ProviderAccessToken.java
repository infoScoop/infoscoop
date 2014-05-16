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

package org.infoscoop.api.dao.model.base;

import java.io.Serializable;

public class BaseOAuth2ProviderAccessToken implements Serializable {
	private static final long serialVersionUID = 1L;

	public static String REF = "OAuth2ProviderAccessToken";
	public static String PROP_ID = "id";
	public static String PROP_TOKEN = "token";
	public static String PROP_AUTHENTICATION_ID = "authenticationId";
	public static String PROP_USER_ID = "userId";
	public static String PROP_CLIENT_ID = "clientId";
	public static String PROP_AUTHENTICATION = "authentication";
	public static String PROP_REFRESH_TOKEN = "refreshToken";

	public BaseOAuth2ProviderAccessToken() {
		initialize();
	}

	public BaseOAuth2ProviderAccessToken (java.lang.String id) {
		this.setId(id);
		initialize();
	}

	protected void initialize () {}
	
	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private java.lang.String id;

	// fields
	private byte[] token;
	private java.lang.String authenticationId;
	private java.lang.String userId;
	private java.lang.String clientId;
	private byte[] authentication;
	private java.lang.String refreshToken;

	public java.lang.String getId() {
		return id;
	}

	public void setId(java.lang.String id) {
		this.id = id;
	}

	public byte[] getToken() {
		return token;
	}

	public void setToken(byte[] token) {
		this.token = token;
	}

	public java.lang.String getAuthenticationId() {
		return authenticationId;
	}

	public void setAuthenticationId(java.lang.String authenticationId) {
		this.authenticationId = authenticationId;
	}

	public java.lang.String getUserId() {
		return userId;
	}

	public void setUserId(java.lang.String userId) {
		this.userId = userId;
	}

	public java.lang.String getClientId() {
		return clientId;
	}

	public void setClientId(java.lang.String clientId) {
		this.clientId = clientId;
	}

	public byte[] getAuthentication() {
		return authentication;
	}

	public void setAuthentication(byte[] authentication) {
		this.authentication = authentication;
	}

	public java.lang.String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(java.lang.String refreshToken) {
		this.refreshToken = refreshToken;
	}
	
	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof org.infoscoop.api.dao.model.OAuth2ProviderAccessToken)) return false;
		else {
			org.infoscoop.api.dao.model.OAuth2ProviderAccessToken accessToken = (org.infoscoop.api.dao.model.OAuth2ProviderAccessToken) obj;
			if (null == this.getId() || null == accessToken.getId()) return false;
			else return (this.getId().equals(accessToken.getId()));
		}
	}

	public int hashCode () {
		if (Integer.MIN_VALUE == this.hashCode) {
			if (null == this.getId()) return super.hashCode();
			else {
				String hashStr = this.getClass().getName() + ":" + this.getId().hashCode();
				this.hashCode = hashStr.hashCode();
			}
		}
		return this.hashCode;
	}

	public String toString () {
		return super.toString();
	}
}
