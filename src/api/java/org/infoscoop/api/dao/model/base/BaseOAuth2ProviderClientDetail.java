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

public class BaseOAuth2ProviderClientDetail implements Serializable {
	private static final long serialVersionUID = 1L;

	public static String REF = "OAuth2ProviderClientDetail";
	public static String PROP_ID = "id";
	public static String PROP_TITLE = "title";
	public static String PROP_RESOURCE_IDS = "resourceIds";
	public static String PROP_SECRET = "secret";
	public static String PROP_SCOPE = "scope";
	public static String PROP_GRANT_TYPES = "grantTypes";
	public static String PROP_REDIRECT_URL = "redirectUrl";
	public static String PROP_AUTHORITIES = "authorities";
	public static String PROP_DELETE_FLG = "deleteFlg";
	public static String PROP_ACCESS_TOKEN_VALIDITY = "accessTokenValidity";
	public static String PROP_REFRESH_TOKEN_VALIDITY = "refreshTokenValidity";
	public static String PROP_ADDITIONAL_INFORMATION = "additionalInformation";

	
	public BaseOAuth2ProviderClientDetail() {
		initialize();
	}
	public BaseOAuth2ProviderClientDetail (java.lang.String id) {
		this.setId(id);
		initialize();
	}
	
	public BaseOAuth2ProviderClientDetail (java.lang.String id, java.lang.String title, java.lang.String secret) {
		this.setId(id);
		this.setTitle(title);
		this.setSecret(secret);
		initialize();
	}

	protected void initialize () {}
	
	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private java.lang.String id;

	// fields
	private java.lang.String title;
	private java.lang.String resourceIds;
	private java.lang.String secret;
	private java.lang.String scope;
	private java.lang.String grantTypes;
	private java.lang.String redirectUrl;
	private java.lang.String authorities;
	private java.lang.Boolean deleteFlg;
	private java.lang.Integer accessTokenValidity;
	private java.lang.Integer refreshTokenValidity;
	private java.lang.String additionalInformation;

	public java.lang.String getId() {
		return id;
	}
	public void setId(java.lang.String id) {
		this.id = id;
	}
	public java.lang.String getTitle() {
		return title;
	}
	public void setTitle(java.lang.String title) {
		this.title = title;
	}
	public java.lang.String getResourceIds() {
		return resourceIds;
	}
	public void setResourceIds(java.lang.String resourceIds) {
		this.resourceIds = resourceIds;
	}
	public java.lang.String getSecret() {
		return secret;
	}
	public void setSecret(java.lang.String secret) {
		this.secret = secret;
	}
	public java.lang.String getScope() {
		return scope;
	}
	public void setScope(java.lang.String scope) {
		this.scope = scope;
	}
	public java.lang.String getGrantTypes() {
		return grantTypes;
	}
	public void setGrantTypes(java.lang.String grantTypes) {
		this.grantTypes = grantTypes;
	}
	public java.lang.String getRedirectUrl() {
		return redirectUrl;
	}
	public void setRedirectUrl(java.lang.String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}
	public java.lang.String getAuthorities() {
		return authorities;
	}
	public void setAuthorities(java.lang.String authorities) {
		this.authorities = authorities;
	}
	public java.lang.Boolean getDeleteFlg() {
		return deleteFlg;
	}
	public void setDeleteFlg(java.lang.Boolean deleteFlg){
		this.deleteFlg = deleteFlg;
	}
	public java.lang.Integer getAccessTokenValidity() {
		return accessTokenValidity;
	}
	public void setAccessTokenValidity(java.lang.Integer accessTokenValidity) {
		this.accessTokenValidity = accessTokenValidity;
	}
	public java.lang.Integer getRefreshTokenValidity() {
		return refreshTokenValidity;
	}
	public void setRefreshTokenValidity(java.lang.Integer refreshTokenValidity) {
		this.refreshTokenValidity = refreshTokenValidity;
	}
	public java.lang.String getAdditionalInformation() {
		return additionalInformation;
	}
	public void setAdditionalInformation(java.lang.String additionalInformation) {
		this.additionalInformation = additionalInformation;
	}

	
	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof org.infoscoop.api.dao.model.OAuth2ProviderClientDetail)) return false;
		else {
			org.infoscoop.api.dao.model.OAuth2ProviderClientDetail clientDetail = (org.infoscoop.api.dao.model.OAuth2ProviderClientDetail) obj;
			if (null == this.getId() || null == clientDetail.getId()) return false;
			else return (this.getId().equals(clientDetail.getId()));
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
