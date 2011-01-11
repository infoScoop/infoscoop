package org.infoscoop.dao.model.base;

import java.io.Serializable;


/**
 * This is an object that contains data related to the IS_AUTHCREDENTIALS table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="IS_AUTHCREDENTIALS"
 */

public abstract class BaseAuthCredential  implements Serializable {

	public static String REF = "AuthCredential";
	public static String PROP_SYS_NUM = "SysNum";
	public static String PROP_AUTH_PASSWD = "authPasswd";
	public static String PROP_ID = "Id";
	public static String PROP_AUTH_UID = "AuthUid";
	public static String PROP_AUTH_DOMAIN = "AuthDomain";
	public static String PROP_AUTH_TYPE = "AuthType";
	public static String PROP_UID = "Uid";
	public static String PROP_FK_DOMAIN_ID = "FkDomainId";


	// constructors
	public BaseAuthCredential () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseAuthCredential (java.lang.Long id) {
		this.setId(id);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BaseAuthCredential (
		java.lang.Long id,
		java.lang.Integer fkDomainId,
		java.lang.String uid,
		java.lang.String authType,
		java.lang.String authUid) {

		this.setId(id);
		this.setFkDomainId(fkDomainId);
		this.setUid(uid);
		this.setAuthType(authType);
		this.setAuthUid(authUid);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private java.lang.Long id;

	// fields
	private java.lang.Integer fkDomainId;
	private java.lang.String uid;
	private java.lang.Integer sysNum;
	private java.lang.String authType;
	private java.lang.String authDomain;
	private java.lang.String authUid;
	private java.lang.String authPasswd;



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




	/**
	 * Return the value associated with the column: fk_domain_id
	 */
	public java.lang.Integer getFkDomainId () {
		return fkDomainId;
	}

	/**
	 * Set the value related to the column: fk_domain_id
	 * @param fkDomainId the fk_domain_id value
	 */
	public void setFkDomainId (java.lang.Integer fkDomainId) {
		this.fkDomainId = fkDomainId;
	}



	/**
	 * Return the value associated with the column: `UID`
	 */
	public java.lang.String getUid () {
		return uid;
	}

	/**
	 * Set the value related to the column: `UID`
	 * @param uid the `UID` value
	 */
	public void setUid (java.lang.String uid) {
		this.uid = uid;
	}



	/**
	 * Return the value associated with the column: SYSNUM
	 */
	public java.lang.Integer getSysNum () {
		return sysNum;
	}

	/**
	 * Set the value related to the column: SYSNUM
	 * @param sysNum the SYSNUM value
	 */
	public void setSysNum (java.lang.Integer sysNum) {
		this.sysNum = sysNum;
	}



	/**
	 * Return the value associated with the column: AUTHTYPE
	 */
	public java.lang.String getAuthType () {
		return authType;
	}

	/**
	 * Set the value related to the column: AUTHTYPE
	 * @param authType the AUTHTYPE value
	 */
	public void setAuthType (java.lang.String authType) {
		this.authType = authType;
	}



	/**
	 * Return the value associated with the column: AUTHDOMAIN
	 */
	public java.lang.String getAuthDomain () {
		return authDomain;
	}

	/**
	 * Set the value related to the column: AUTHDOMAIN
	 * @param authDomain the AUTHDOMAIN value
	 */
	public void setAuthDomain (java.lang.String authDomain) {
		this.authDomain = authDomain;
	}



	/**
	 * Return the value associated with the column: AUTHUID
	 */
	public java.lang.String getAuthUid () {
		return authUid;
	}

	/**
	 * Set the value related to the column: AUTHUID
	 * @param authUid the AUTHUID value
	 */
	public void setAuthUid (java.lang.String authUid) {
		this.authUid = authUid;
	}



	/**
	 * Return the value associated with the column: AUTHPASSWD
	 */
	public java.lang.String getAuthPasswd () {
		return authPasswd;
	}

	/**
	 * Set the value related to the column: AUTHPASSWD
	 * @param authPasswd the AUTHPASSWD value
	 */
	public void setAuthPasswd (java.lang.String authPasswd) {
		this.authPasswd = authPasswd;
	}




	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof org.infoscoop.dao.model.AuthCredential)) return false;
		else {
			org.infoscoop.dao.model.AuthCredential authCredential = (org.infoscoop.dao.model.AuthCredential) obj;
			if (null == this.getId() || null == authCredential.getId()) return false;
			else return (this.getId().equals(authCredential.getId()));
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