package org.infoscoop.dao.model.base;

import java.io.Serializable;


/**
 * This is an object that contains data related to the IS_OAUTH_TOKENS table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="IS_OAUTH_TOKENS"
 */

public abstract class BaseOAuthToken  implements Serializable {

	public static String REF = "OAuthToken";
	public static String PROP_ACCESS_TOKEN = "accessToken";
	public static String PROP_ID = "Id";
	public static String PROP_TOKEN_SECRET = "tokenSecret";
	public static String PROP_REQUEST_TOKEN = "requestToken";


	// constructors
	public BaseOAuthToken () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseOAuthToken (org.infoscoop.dao.model.OAUTH_TOKEN_PK id) {
		this.setId(id);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BaseOAuthToken (
		org.infoscoop.dao.model.OAUTH_TOKEN_PK id,
		java.lang.String tokenSecret) {

		this.setId(id);
		this.setTokenSecret(tokenSecret);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private org.infoscoop.dao.model.OAUTH_TOKEN_PK id;

	// fields
	private java.lang.String requestToken;
	private java.lang.String accessToken;
	private java.lang.String tokenSecret;
	private java.lang.String squareId;


	/**
	 * Return the unique identifier of this class
     * @hibernate.id
     */
	public org.infoscoop.dao.model.OAUTH_TOKEN_PK getId () {
		return id;
	}

	/**
	 * Set the unique identifier of this class
	 * @param id the new ID
	 */
	public void setId (org.infoscoop.dao.model.OAUTH_TOKEN_PK id) {
		this.id = id;
		this.hashCode = Integer.MIN_VALUE;
	}




	/**
	 * Return the value associated with the column: request_token
	 */
	public java.lang.String getRequestToken () {
		return requestToken;
	}

	/**
	 * Set the value related to the column: request_token
	 * @param requestToken the request_token value
	 */
	public void setRequestToken (java.lang.String requestToken) {
		this.requestToken = requestToken;
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



	/**
	 * Return the value associated with the column: token_secret
	 */
	public java.lang.String getTokenSecret () {
		return tokenSecret;
	}

	/**
	 * Set the value related to the column: token_secret
	 * @param tokenSecret the token_secret value
	 */
	public void setTokenSecret (java.lang.String tokenSecret) {
		this.tokenSecret = tokenSecret;
	}

	public String getSquareid() {
		return squareId;
	}

	public void setSquareid(String squareId) {
		this.squareId = squareId;
	}

	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof org.infoscoop.dao.model.OAuthToken)) return false;
		else {
			org.infoscoop.dao.model.OAuthToken oAuthToken = (org.infoscoop.dao.model.OAuthToken) obj;
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