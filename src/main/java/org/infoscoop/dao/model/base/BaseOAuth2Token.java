package org.infoscoop.dao.model.base;

import java.io.Serializable;

public class BaseOAuth2Token implements Serializable {

	public static String REF = "OAuth2Token";
	public static String PROP_TOKEN_TYPE = "tokenType";
	public static String PROP_AUTH_CODE = "authCode";
	public static String PROP_ACCESS_TOKEN = "accessToken";
	public static String PROP_REFRESH_TOKEN = "refreshToken";
	public static String PROP_VALIDITY_PERIOD_UTC = "validityPeriodUTC";
	public static String PROP_ID = "Id";


	// constructors
	public BaseOAuth2Token () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseOAuth2Token (org.infoscoop.dao.model.OAUTH2_TOKEN_PK id) {
		this.setId(id);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private org.infoscoop.dao.model.OAUTH2_TOKEN_PK id;

	// fields
	private java.lang.String tokenType;
	private java.lang.String authCode;
	private java.lang.String accessToken;
	private java.lang.String refreshToken;
	private java.lang.Long validityPeriodUTC;



	/**
	 * Return the unique identifier of this class
     * @hibernate.id
     */
	public org.infoscoop.dao.model.OAUTH2_TOKEN_PK getId () {
		return id;
	}

	/**
	 * Set the unique identifier of this class
	 * @param id the new ID
	 */
	public void setId (org.infoscoop.dao.model.OAUTH2_TOKEN_PK id) {
		this.id = id;
		this.hashCode = Integer.MIN_VALUE;
	}


	public java.lang.String getTokenType () {
		return tokenType;
	}

	public void setTokenType (java.lang.String tokenType) {
		this.tokenType = tokenType;
	}

	
	public java.lang.String getAuthCode () {
		return authCode;
	}

	public void setAuthCode (java.lang.String authCode) {
		this.authCode = authCode;
	}

	/**
	 * Return the value associated with the column: access_token
	 */
	public java.lang.String getAccessToken () {
		return accessToken;
	}

	/**
	 * Set the value related to the column: access_token
	 * @param accessToken the access_token value
	 */
	public void setAccessToken (java.lang.String accessToken) {
		this.accessToken = accessToken;
	}


	public java.lang.String getRefreshToken () {
		return refreshToken;
	}

	public void setRefreshToken (java.lang.String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public java.lang.Long getValidityPeriodUTC () {
		return validityPeriodUTC;
	}
	
	public void setValidityPeriodUTC (java.lang.Long validityPeriodUTC) {
		this.validityPeriodUTC = validityPeriodUTC;
	}


	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof org.infoscoop.dao.model.OAuth2Token)) return false;
		else {
			org.infoscoop.dao.model.OAuth2Token oAuthToken = (org.infoscoop.dao.model.OAuth2Token) obj;
			if (null == this.getId() || null == oAuthToken.getId()) return false;
			else return (this.getId().equals(oAuthToken.getId()));
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
