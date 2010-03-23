package org.infoscoop.dao.model.base;

import java.io.Serializable;


/**
 * This is an object that contains data related to the AUTHCREDENTIAL table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="AUTHCREDENTIAL"
 */

public abstract class BaseAuthcredential  implements Serializable {

	public static String REF = "Authcredential";
	public static String PROP_AUTHUID = "Authuid";
	public static String PROP_SYSNUM = "Sysnum";
	public static String PROP_AUTHPASSWD = "Authpasswd";
	public static String PROP_AUTHDOMAIN = "Authdomain";
	public static String PROP_AUTHTYPE = "Authtype";
	public static String PROP_ID = "Id";
	public static String PROP_UID = "Uid";


	// constructors
	public BaseAuthcredential () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseAuthcredential (java.lang.Long id) {
		this.setId(id);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BaseAuthcredential (
		java.lang.Long id,
		java.lang.String uid,
		java.lang.Integer sysnum,
		java.lang.String authtype,
		java.lang.String authuid,
		java.lang.String authpasswd) {

		this.setId(id);
		this.setUid(uid);
		this.setSysNum(sysnum);
		this.setAuthType(authtype);
		this.setAuthUid(authuid);
		this.setAuthPasswd(authpasswd);
		initialize();
	}

	protected void initialize () {
		sysNum = new Integer(0);
	}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private java.lang.Long id;

	// fields
	private java.lang.String uid;
	private java.lang.Integer sysNum;
	private java.lang.String authType;
	private java.lang.String authDomain;
	private java.lang.String authUid;
	private java.lang.String authPasswd;

	/**
	 * Return the unique identifier of this class
     * @hibernate.id
     *  generator-class="sequence"
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
	 * Return the value associated with the column: UID
	 */
	public java.lang.String getUid () {
		return uid;
	}

	/**
	 * Set the value related to the column: UID
	 * @param uid the UID value
	 */
	public void setUid (java.lang.String uid) {
		this.uid = uid;
	}


	/**
	 * @return
	 */
	public java.lang.Integer getSysNum() {
		return sysNum;
	}

	/**
	 * @param sysNum
	 */
	public void setSysNum(java.lang.Integer sysNum) {
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
	 * @param authtype the AUTHTYPE value
	 */
	public void setAuthType (java.lang.String authtype) {
		this.authType = authtype;
	}



	/**
	 * Return the value associated with the column: AUTHDOMAIN
	 */
	public java.lang.String getAuthDomain () {
		return authDomain;
	}

	/**
	 * Set the value related to the column: AUTHDOMAIN
	 * @param authdomain the AUTHDOMAIN value
	 */
	public void setAuthDomain (java.lang.String authdomain) {
		this.authDomain = authdomain;
	}



	/**
	 * Return the value associated with the column: AUTHUID
	 */
	public java.lang.String getAuthUid () {
		return authUid;
	}

	/**
	 * Set the value related to the column: AUTHUID
	 * @param authuid the AUTHUID value
	 */
	public void setAuthUid (java.lang.String authuid) {
		this.authUid = authuid;
	}



	/**
	 * Return the value associated with the column: AUTHPASSWD
	 */
	public java.lang.String getAuthPasswd () {
		return authPasswd;
	}

	/**
	 * Set the value related to the column: AUTHPASSWD
	 * @param authpasswd the AUTHPASSWD value
	 */
	public void setAuthPasswd (java.lang.String authpasswd) {
		this.authPasswd = authpasswd;
	}


	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof org.infoscoop.dao.model.AuthCredential)) return false;
		else {
			org.infoscoop.dao.model.AuthCredential authcredential = (org.infoscoop.dao.model.AuthCredential) obj;
			if (null == this.getId() || null == authcredential.getId()) return false;
			else return (this.getId().equals(authcredential.getId()));
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