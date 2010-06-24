package org.infoscoop.dao.model.base;

import java.io.Serializable;


/**
 * This is an object that contains data related to the is_oauth_consumers table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="is_oauth_consumers"
 */

public abstract class BaseOAuthConsumerProp  implements Serializable {

	public static String REF = "OAuthConsumerProp";
	public static String PROP_SIGNATURE_METHOD = "SignatureMethod";
	public static String PROP_GADGET_URL = "GadgetUrl";
	public static String PROP_CONSUMER_SECRET = "ConsumerSecret";
	public static String PROP_ID = "Id";
	public static String PROP_CONSUMER_KEY = "ConsumerKey";
	public static String PROP_IS_UPLOAD = "IsUpload";


	// constructors
	public BaseOAuthConsumerProp () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseOAuthConsumerProp (org.infoscoop.dao.model.OAUTH_CONSUMER_PK id) {
		this.setId(id);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BaseOAuthConsumerProp (
		org.infoscoop.dao.model.OAUTH_CONSUMER_PK id,
		java.lang.String gadgetUrl) {

		this.setId(id);
		this.setGadgetUrl(gadgetUrl);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private org.infoscoop.dao.model.OAUTH_CONSUMER_PK id;

	// fields
	private java.lang.String gadgetUrl;
	private java.lang.String consumerKey;
	private java.lang.String consumerSecret;
	private java.lang.String signatureMethod;
	private java.lang.Integer isUpload;

	/**
	 * Return the unique identifier of this class
     * @hibernate.id
     */
	public org.infoscoop.dao.model.OAUTH_CONSUMER_PK getId () {
		return id;
	}

	/**
	 * Set the unique identifier of this class
	 * @param id the new ID
	 */
	public void setId (org.infoscoop.dao.model.OAUTH_CONSUMER_PK id) {
		this.id = id;
		this.hashCode = Integer.MIN_VALUE;
	}




	/**
	 * Return the value associated with the column: gadget_url
	 */
	public java.lang.String getGadgetUrl () {
		return gadgetUrl;
	}

	/**
	 * Set the value related to the column: gadget_url
	 * @param gadgetUrl the gadget_url value
	 */
	public void setGadgetUrl (java.lang.String gadgetUrl) {
		this.gadgetUrl = gadgetUrl;
	}



	/**
	 * Return the value associated with the column: consumer_key
	 */
	public java.lang.String getConsumerKey () {
		return consumerKey;
	}

	/**
	 * Set the value related to the column: consumer_key
	 * @param consumerKey the consumer_key value
	 */
	public void setConsumerKey (java.lang.String consumerKey) {
		this.consumerKey = consumerKey;
	}



	/**
	 * Return the value associated with the column: consumer_secret
	 */
	public java.lang.String getConsumerSecret () {
		return consumerSecret;
	}

	/**
	 * Set the value related to the column: consumer_secret
	 * @param consumerSecret the consumer_secret value
	 */
	public void setConsumerSecret (java.lang.String consumerSecret) {
		this.consumerSecret = consumerSecret;
	}



	/**
	 * Return the value associated with the column: signature_method
	 */
	public java.lang.String getSignatureMethod () {
		return signatureMethod;
	}

	/**
	 * Set the value related to the column: signature_method
	 * @param signatureMethod the signature_method value
	 */
	public void setSignatureMethod (java.lang.String signatureMethod) {
		this.signatureMethod = signatureMethod;
	}


	public java.lang.Integer getIsUpload() {
		return isUpload;
	}

	public void setIsUpload(java.lang.Integer isUpload) {
		this.isUpload = isUpload;
	}



	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof org.infoscoop.dao.model.OAuthConsumerProp)) return false;
		else {
			org.infoscoop.dao.model.OAuthConsumerProp oAuthConsumerProp = (org.infoscoop.dao.model.OAuthConsumerProp) obj;
			if (null == this.getId() || null == oAuthConsumerProp.getId()) return false;
			else return (this.getId().equals(oAuthConsumerProp.getId()));
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