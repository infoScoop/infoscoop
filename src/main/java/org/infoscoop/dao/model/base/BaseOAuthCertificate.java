package org.infoscoop.dao.model.base;

import java.io.Serializable;


/**
 * This is an object that contains data related to the is_oauth_certificate table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="is_oauth_certificate"
 */

public abstract class BaseOAuthCertificate  implements Serializable {

	public static String REF = "OAuthCertificate";
	public static String PROP_CERTIFICATE = "Certificate";
	public static String PROP_CONSUMER_KEY = "ConsumerKey";
	public static String PROP_PRIVATE_KEY = "PrivateKey";


	// constructors
	public BaseOAuthCertificate () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseOAuthCertificate (java.lang.String consumerKey) {
		this.setConsumerKey(consumerKey);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private java.lang.String consumerKey;

	// fields
	private java.lang.String privateKey;
	private java.lang.String certificate;



	/**
	 * Return the unique identifier of this class
     * @hibernate.id
     *  column="consumer_key"
     */
	public java.lang.String getConsumerKey () {
		return consumerKey;
	}

	/**
	 * Set the unique identifier of this class
	 * @param consumerKey the new ID
	 */
	public void setConsumerKey (java.lang.String consumerKey) {
		this.consumerKey = consumerKey;
		this.hashCode = Integer.MIN_VALUE;
	}




	/**
	 * Return the value associated with the column: private_key
	 */
	public java.lang.String getPrivateKey () {
		return privateKey;
	}

	/**
	 * Set the value related to the column: private_key
	 * @param privateKey the private_key value
	 */
	public void setPrivateKey (java.lang.String privateKey) {
		this.privateKey = privateKey;
	}



	/**
	 * Return the value associated with the column: certificate
	 */
	public java.lang.String getCertificate () {
		return certificate;
	}

	/**
	 * Set the value related to the column: certificate
	 * @param certificate the certificate value
	 */
	public void setCertificate (java.lang.String certificate) {
		this.certificate = certificate;
	}




	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof org.infoscoop.dao.model.OAuthCertificate)) return false;
		else {
			org.infoscoop.dao.model.OAuthCertificate oAuthCertificate = (org.infoscoop.dao.model.OAuthCertificate) obj;
			if (null == this.getConsumerKey() || null == oAuthCertificate.getConsumerKey()) return false;
			else return (this.getConsumerKey().equals(oAuthCertificate.getConsumerKey()));
		}
	}

	public int hashCode () {
		if (Integer.MIN_VALUE == this.hashCode) {
			if (null == this.getConsumerKey()) return super.hashCode();
			else {
				String hashStr = this.getClass().getName() + ":" + this.getConsumerKey().hashCode();
				this.hashCode = hashStr.hashCode();
			}
		}
		return this.hashCode;
	}


	public String toString () {
		return super.toString();
	}


}