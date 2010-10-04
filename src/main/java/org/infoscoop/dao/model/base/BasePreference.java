package org.infoscoop.dao.model.base;

import java.io.Serializable;


/**
 * This is an object that contains data related to the IS_PREFERENCES table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="IS_PREFERENCES"
 */

public abstract class BasePreference  implements Serializable {

	public static String REF = "Preference";
	public static String PROP_DATA = "Data";
	public static String PROP_ID = "Id";


	// constructors
	public BasePreference () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BasePreference (org.infoscoop.dao.model.PreferencePK id) {
		this.setId(id);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BasePreference (
		org.infoscoop.dao.model.PreferencePK id,
		java.lang.String data) {

		this.setId(id);
		this.setData(data);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private org.infoscoop.dao.model.PreferencePK id;

	// fields
	private java.lang.String data;



	/**
	 * Return the unique identifier of this class
     * @hibernate.id
     */
	public org.infoscoop.dao.model.PreferencePK getId () {
		return id;
	}

	/**
	 * Set the unique identifier of this class
	 * @param id the new ID
	 */
	public void setId (org.infoscoop.dao.model.PreferencePK id) {
		this.id = id;
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
			if (null == this.getId() || null == preference.getId()) return false;
			else return (this.getId().equals(preference.getId()));
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