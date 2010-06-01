package org.infoscoop.dao.model.base;

import java.io.Serializable;


/**
 * This is an object that contains data related to the USERPREFS table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="USERPREFS"
 */

public abstract class BaseUserPref  implements Serializable {

	public static String REF = "UserPref";
	public static String PROP_SHORT_VALUE = "shortValue";
	public static String PROP_LONG_VALUE = "longValue";
	public static String PROP_ID = "Id";


	// constructors
	public BaseUserPref () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseUserPref (org.infoscoop.dao.model.USERPREFPK id) {
		this.setId(id);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private org.infoscoop.dao.model.USERPREFPK id;

	// fields
	private java.lang.String shortValue;
	private java.lang.String longValue;



	/**
	 * Return the unique identifier of this class
     * @hibernate.id
     */
	public org.infoscoop.dao.model.USERPREFPK getId () {
		return id;
	}

	/**
	 * Set the unique identifier of this class
	 * @param id the new ID
	 */
	public void setId (org.infoscoop.dao.model.USERPREFPK id) {
		this.id = id;
		this.hashCode = Integer.MIN_VALUE;
	}




	/**
	 * Return the value associated with the column: value
	 */
	public java.lang.String getShortValue () {
		return (shortValue != null ? shortValue : "");
	}

	/**
	 * Set the value related to the column: value
	 * @param shortValue the value value
	 */
	public void setShortValue (java.lang.String shortValue) {
		this.shortValue = shortValue;
	}



	/**
	 * Return the value associated with the column: long_value
	 */
	public java.lang.String getLongValue () {
		return (longValue != null ? longValue : "");
	}

	/**
	 * Set the value related to the column: long_value
	 * @param longValue the long_value value
	 */
	public void setLongValue (java.lang.String longValue) {
		this.longValue = longValue;
	}




	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof org.infoscoop.dao.model.UserPref)) return false;
		else {
			org.infoscoop.dao.model.UserPref userPref = (org.infoscoop.dao.model.UserPref) obj;
			if (null == this.getId() || null == userPref.getId()) return false;
			else return (this.getId().equals(userPref.getId()));
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