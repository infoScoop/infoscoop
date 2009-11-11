package org.infoscoop.dao.model.base;

import java.io.Serializable;


/**
 * This is an object that contains data related to the I18NLASTMODIFIED table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="I18NLASTMODIFIED"
 */

public abstract class BaseI18nlastmodified  implements Serializable {

	public static String REF = "I18nlastmodified";
	public static String PROP_LASTMODIFIED = "Lastmodified";
	public static String PROP_ID = "Id";


	// constructors
	public BaseI18nlastmodified () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseI18nlastmodified (java.lang.String id) {
		this.setId(id);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private java.lang.String id;

	// fields
	private java.util.Date lastmodified;



	/**
	 * Return the unique identifier of this class
     * @hibernate.id
     *  generator-class="sequence"
     *  column="TYPE"
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
	 * Return the value associated with the column: LASTMODIFIED
	 */
	public java.util.Date getLastmodified () {
		return lastmodified;
	}

	/**
	 * Set the value related to the column: LASTMODIFIED
	 * @param lastmodified the LASTMODIFIED value
	 */
	public void setLastmodified (java.util.Date lastmodified) {
		this.lastmodified = lastmodified;
	}




	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof org.infoscoop.dao.model.I18nlastmodified)) return false;
		else {
			org.infoscoop.dao.model.I18nlastmodified i18nlastmodified = (org.infoscoop.dao.model.I18nlastmodified) obj;
			if (null == this.getId() || null == i18nlastmodified.getId()) return false;
			else return (this.getId().equals(i18nlastmodified.getId()));
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