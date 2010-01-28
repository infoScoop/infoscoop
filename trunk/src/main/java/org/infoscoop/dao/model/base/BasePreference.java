package org.infoscoop.dao.model.base;

import java.io.Serializable;


/**
 * This is an object that contains data related to the PREFERENCE table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="PREFERENCE"
 */

public abstract class BasePreference  implements Serializable {

	public static String REF = "Preference";
	public static String PROP_DATA = "Data";
	public static String PROP_ID = "Uid";


	// constructors
	public BasePreference () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BasePreference (java.lang.String id) {
		this.setUid(id);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BasePreference (
		java.lang.String uid,
		java.lang.String data) {

		this.setUid(uid);
		this.setData(data);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private java.lang.String uid;

	// fields
	private java.lang.String data;



	/**
	 * Return the unique identifier of this class
     * @hibernate.id
     *  generator-class="sequence"
     *  column="UID"
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
	 * Return the value associated with the column: DATA
	 */
	public java.lang.String getData () {
		return data;
	}

	/**
	 * Set the value related to the column: DATA
	 * @param data the DATA value
	 */
	public void setData (java.lang.String data) {
		this.data = data;
	}




	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof org.infoscoop.dao.model.Preference)) return false;
		else {
			org.infoscoop.dao.model.Preference preference = (org.infoscoop.dao.model.Preference) obj;
			if (null == this.getUid() || null == preference.getUid()) return false;
			else return (this.getUid().equals(preference.getUid()));
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