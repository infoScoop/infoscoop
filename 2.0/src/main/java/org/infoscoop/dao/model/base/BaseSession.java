package org.infoscoop.dao.model.base;

import java.io.Serializable;


/**
 * This is an object that contains data related to the `SESSION` table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="`SESSION`"
 */

public abstract class BaseSession  implements Serializable {

	public static String REF = "Session";
	public static String PROP_LOGINDATETIME = "Logindatetime";
	public static String PROP_UID = "Uid";
	public static String PROP_SESSIONID = "Sessionid";


	// constructors
	public BaseSession () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseSession (java.lang.String uid) {
		this.setUid(uid);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BaseSession (
		java.lang.String uid,
		java.lang.String sessionid) {

		this.setUid(uid);
		this.setSessionid(sessionid);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private java.lang.String uid;

	// fields
	private java.lang.String sessionid;
	private java.util.Date logindatetime;



	/**
	 * Return the unique identifier of this class
     * @hibernate.id
     *  column="`UID`"
     */
	public java.lang.String getUid () {
		return uid;
	}

	/**
	 * Set the unique identifier of this class
	 * @param uid the new ID
	 */
	public void setUid (java.lang.String uid) {
		this.uid = uid;
		this.hashCode = Integer.MIN_VALUE;
	}




	/**
	 * Return the value associated with the column: SESSIONID
	 */
	public java.lang.String getSessionid () {
		return sessionid;
	}

	/**
	 * Set the value related to the column: SESSIONID
	 * @param sessionid the SESSIONID value
	 */
	public void setSessionid (java.lang.String sessionid) {
		this.sessionid = sessionid;
	}



	/**
	 * Return the value associated with the column: LOGINDATETIME
	 */
	public java.util.Date getLogindatetime () {
		return logindatetime;
	}

	/**
	 * Set the value related to the column: LOGINDATETIME
	 * @param logindatetime the LOGINDATETIME value
	 */
	public void setLogindatetime (java.util.Date logindatetime) {
		this.logindatetime = logindatetime;
	}




	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof org.infoscoop.dao.model.Session)) return false;
		else {
			org.infoscoop.dao.model.Session session = (org.infoscoop.dao.model.Session) obj;
			if (null == this.getUid() || null == session.getUid()) return false;
			else return (this.getUid().equals(session.getUid()));
		}
	}

	public int hashCode () {
		if (Integer.MIN_VALUE == this.hashCode) {
			if (null == this.getUid()) return super.hashCode();
			else {
				String hashStr = this.getClass().getName() + ":" + this.getUid().hashCode();
				this.hashCode = hashStr.hashCode();
			}
		}
		return this.hashCode;
	}


	public String toString () {
		return super.toString();
	}


}