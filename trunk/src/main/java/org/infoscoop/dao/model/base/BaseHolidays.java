package org.infoscoop.dao.model.base;

import java.io.Serializable;
import java.util.Date;


/**
 * This is an object that contains data related to the I18N table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="I18N"
 */

public abstract class BaseHolidays  implements Serializable {

	public static String REF = "Holidays";
	public static String PROP_DATA = "Data";
	public static String PROP_ID = "Id";


	// constructors
	public BaseHolidays () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseHolidays (org.infoscoop.dao.model.HOLIDAYSPK id) {
		this.setId(id);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BaseHolidays (
		org.infoscoop.dao.model.HOLIDAYSPK id,
		java.lang.String data,
		Date updatedAt ) {

		this.setId(id);
		this.setData(data);
		this.setUpdatedat( updatedAt );
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private org.infoscoop.dao.model.HOLIDAYSPK id;

	// fields
	private java.lang.String data;
	
	private Date updatedAt;



	/**
	 * Return the unique identifier of this class
     * @hibernate.id
     */
	public org.infoscoop.dao.model.HOLIDAYSPK getId () {
		return id;
	}

	/**
	 * Set the unique identifier of this class
	 * @param id the new ID
	 */
	public void setId (org.infoscoop.dao.model.HOLIDAYSPK id) {
		this.id = id;
		this.hashCode = Integer.MIN_VALUE;
	}




	/**
	 * Return the value associated with the column: MESSAGE
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
	
	public Date getUpdatedat() {
		return updatedAt;
	}
	public void setUpdatedat( Date updatedAt ) {
		this.updatedAt = updatedAt;
	}



	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof org.infoscoop.dao.model.I18n)) return false;
		else {
			org.infoscoop.dao.model.Holidays holidays = (org.infoscoop.dao.model.Holidays) obj;
			if (null == this.getId() || null == holidays.getId()) return false;
			else return (this.getId().equals(holidays.getId()));
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