package org.infoscoop.dao.model.base;

import java.io.Serializable;


/**
 * This is an object that contains data related to the IS_OAUTH_CONSUMERS table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="IS_OAUTH_CONSUMERS"
 */

public abstract class BaseOAuthConsumerProp  implements Serializable {

	public static String REF = "OAuthConsumerProp";
	public static String PROP_SERVICE_NAME = "ServiceName";
	public static String PROP_SIGNATURE_METHOD = "SignatureMethod";
	public static String PROP_DESCRIPTION = "Description";
	public static String PROP_CONSUMER_SECRET = "ConsumerSecret";
	public static String PROP_ID = "Id";
	public static String PROP_CONSUMER_KEY = "ConsumerKey";


	// constructors
	public BaseOAuthConsumerProp () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseOAuthConsumerProp (java.lang.String id) {
		this.setId(id);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BaseOAuthConsumerProp (
		java.lang.String id,
		java.lang.String serviceName) {

		this.setId(id);
		this.setServiceName(serviceName);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private java.lang.String id;

	// fields
	private java.lang.String serviceName;
	private java.lang.String consumerKey;
	private java.lang.String consumerSecret;
	private java.lang.String signatureMethod;
	private java.lang.String description;

	private java.util.Set<org.infoscoop.dao.model.OAuthGadgetUrl> OAuthGadgetUrl;
	private java.util.Set<org.infoscoop.dao.model.OAuthToken> OAuthToken;
	private java.util.Set<org.infoscoop.dao.model.OAuth2Token> OAuth2Token;

	/**
	 * Return the unique identifier of this class
     * @hibernate.id
     *  column="id"
     */
	public java.lang.String getId () {
		return id;
	}

	/**
	 * Set the unique identifier of this class
	 * @param id the new ID
	 */
	public void setId (java.lang.String id) {
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

	public java.lang.String getDescription() {
		return (description != null)? description : "";
	}

	public void setDescription(java.lang.String description) {
		this.description = description;
	}
	
    public void setOAuthGadgetUrl(java.util.Set<org.infoscoop.dao.model.OAuthGadgetUrl> OAuthGadgetUrl) {  
        this.OAuthGadgetUrl = OAuthGadgetUrl;  
    }  
       
    public java.util.Set<org.infoscoop.dao.model.OAuthGadgetUrl> getOAuthGadgetUrl() {  
        return OAuthGadgetUrl;  
    }  

    public void setOAuthToken(java.util.Set<org.infoscoop.dao.model.OAuthToken> OAuthToken) {  
        this.OAuthToken = OAuthToken;  
    }  
       
    public java.util.Set<org.infoscoop.dao.model.OAuthToken> getOAuthToken() {  
        return OAuthToken;  
    }  

    public void setOAuth2Token(java.util.Set<org.infoscoop.dao.model.OAuth2Token> OAuth2Token) {  
        this.OAuth2Token = OAuth2Token;  
    }  
       
    public java.util.Set<org.infoscoop.dao.model.OAuth2Token> getOAuth2Token() {  
        return OAuth2Token;  
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