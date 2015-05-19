package org.infoscoop.dao.model.base;

import java.io.Serializable;

public class BaseOAuthGadgetUrl implements Serializable, Comparable<org.infoscoop.dao.model.OAuthGadgetUrl> {
	public static String REF = "OAuthGadgetUrl";
	public static String PROP_FKOAUTHID = "FkOauthId";
	public static String PROP_GADGET_URL = "GadgetUrl";
	public static String PROP_GADGET_URL_KEY = "GadgetUrlKey";		
	public static String PROP_ID = "Id";
	public static String PROP_SQUARE_ID = "Squareid";

	// constructors
	public BaseOAuthGadgetUrl () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseOAuthGadgetUrl (java.lang.Long id) {
		this.setId(id);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BaseOAuthGadgetUrl (
		java.lang.Long id,
		java.lang.String gadgetUrl,
		java.lang.String squareid) {

		this.setId(id);
		this.setGadgetUrl(gadgetUrl);
		this.setSquareid(squareid);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private java.lang.Long id;

	// fields
	private java.lang.String fkOauthId;
	private java.lang.String gadgetUrl;
	private java.lang.String gadgetUrlKey;
	private java.lang.String squareid;


	/**
	 * Return the unique identifier of this class
     * @hibernate.id
     *  generator-class="native"
     *  column="ID"
     */
	public java.lang.Long getId () {
		return id;
	}

	/**
	 * Set the unique identifier of this class
	 * @param id the new ID
	 */
	public void setId (java.lang.Long id) {
		this.id = id;
		this.hashCode = Integer.MIN_VALUE;
	}




	public java.lang.String getFkOauthId () {
		return fkOauthId;
	}

	public void setFkOauthId (java.lang.String fkOauthId) {
		this.fkOauthId = fkOauthId;
	}

	public java.lang.String getGadgetUrl () {
		return gadgetUrl;
	}

	public void setGadgetUrl (java.lang.String gadgetUrl) {
		this.gadgetUrl = gadgetUrl;
	}

	public java.lang.String getGadgetUrlKey () {
		return gadgetUrlKey;
	}

	public void setGadgetUrlKey (java.lang.String gadgetUrlKey) {
		this.gadgetUrlKey = gadgetUrlKey;
	}

	public java.lang.String getSquareid() {
		return squareid;
	}

	public void setSquareid(java.lang.String squareid) {
		this.squareid = squareid;
	}




	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof org.infoscoop.dao.model.OAuthGadgetUrl)) return false;
		else {
			org.infoscoop.dao.model.OAuthGadgetUrl oauthgadgeturl = (org.infoscoop.dao.model.OAuthGadgetUrl) obj;
			if (null == this.getId() || null == oauthgadgeturl.getId()) return false;
			else return (this.getId().equals(oauthgadgeturl.getId()));
		}
	}
	
	public int compareTo (org.infoscoop.dao.model.OAuthGadgetUrl obj) {
		return 1;
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
