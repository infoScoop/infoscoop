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

public class BaseOAuth2ProviderRefreshToken implements Serializable {
	private static final long serialVersionUID = 1L;

	public static String REF = "OAuth2ProviderAccessToken";
	public static String PROP_ID = "id";
	public static String PROP_TOKEN = "token";
	public static String PROP_AUTHENTICATION = "authentication";
	public static String PROP_SQUARE_ID = "squareId";
	
	public BaseOAuth2ProviderRefreshToken() {
		initialize();
	}

	public BaseOAuth2ProviderRefreshToken (java.lang.String id) {
		this.setId(id);
		initialize();
	}

	public BaseOAuth2ProviderRefreshToken (java.lang.String id, java.lang.String squareId) {
		this.setId(id);
		this.setSquareId(squareId);
		initialize();
	}

	protected void initialize () {}
	
	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private java.lang.String id;

	// fields
	private byte[] token;
	private byte[] authentication;
	private java.lang.String squareId;

	public String getId() {
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

	public byte[] getAuthentication() {
		return authentication;
	}

	public void setAuthentication(byte[] authentication) {
		this.authentication = authentication;
	}

	public java.lang.String getSquareId() {
		return squareId;
	}
	public void setSquareId(java.lang.String squareId) {
		this.squareId = squareId;
	}

	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof org.infoscoop.api.dao.model.OAuth2ProviderRefreshToken)) return false;
		else {
			org.infoscoop.api.dao.model.OAuth2ProviderRefreshToken accessToken = (org.infoscoop.api.dao.model.OAuth2ProviderRefreshToken) obj;
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
