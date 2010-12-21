package org.infoscoop.dao.model.base;

import java.io.Serializable;


/**
 * This is an object that contains data related to the is_oauth_2legged_consumers table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="is_oauth_2legged_consumers"
 */

public abstract class BaseOAuth2LeggedConsumer  implements Serializable {

	public static String REF = "OAuth2LeggedConsumer";
	public static String PROP_SERVICE_NAME = "ServiceName";
	public static String PROP_SIGNATURE_METHOD = "SignatureMethod";
	public static String PROP_CONSUMER_SECRET = "ConsumerSecret";
	public static String PROP_ID = "Id";
	public static String PROP_CONSUMER_KEY = "ConsumerKey";


	// constructors
	public BaseOAuth2LeggedConsumer () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseOAuth2LeggedConsumer (java.lang.Long id) {
		this.setId(id);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BaseOAuth2LeggedConsumer (
		java.lang.Long id,
		java.lang.String serviceName) {

		this.setId(id);
		this.setServiceName(serviceName);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private java.lang.Long id;

	// fields
	private java.lang.String serviceName;
	private java.lang.String consumerKey;
	private java.lang.String consumerSecret;
	private java.lang.String signatureMethod;



	/**
	 * Return the unique identifier of this class
     * @hibernate.id
     *  generator-class="native"
     *  column="id"
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




	/**
	 * Return the value associated with the column: service_name
	 */
	public java.lang.String getServiceName () {
		return serviceName;
	}

	/**
	 * Set the value related to the column: service_name
	 * @param serviceName the service_name value
	 */
	public void setServiceName (java.lang.String serviceName) {
		this.serviceName = serviceName;
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




	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof org.infoscoop.dao.model.OAuth2LeggedConsumer)) return false;
		else {
			org.infoscoop.dao.model.OAuth2LeggedConsumer oAuth2LeggedConsumer = (org.infoscoop.dao.model.OAuth2LeggedConsumer) obj;
			if (null == this.getId() || null == oAuth2LeggedConsumer.getId()) return false;
			else return (this.getId().equals(oAuth2LeggedConsumer.getId()));
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