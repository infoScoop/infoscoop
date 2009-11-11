package org.infoscoop.dao.model.base;

import java.io.Serializable;


/**
 * This is an object that contains data related to the I18N table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="I18N"
 */

public abstract class BaseI18n  implements Serializable {

	public static String REF = "I18n";
	public static String PROP_MESSAGE = "Message";
	public static String PROP_ID = "Id";


	// constructors
	public BaseI18n () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseI18n (org.infoscoop.dao.model.I18NPK id) {
		this.setId(id);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BaseI18n (
		org.infoscoop.dao.model.I18NPK id,
		java.lang.String message) {

		this.setId(id);
		this.setMessage(message);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private org.infoscoop.dao.model.I18NPK id;

	// fields
	private java.lang.String message;



	/**
	 * Return the unique identifier of this class
     * @hibernate.id
     */
	public org.infoscoop.dao.model.I18NPK getId () {
		return id;
	}

	/**
	 * Set the unique identifier of this class
	 * @param id the new ID
	 */
	public void setId (org.infoscoop.dao.model.I18NPK id) {
		this.id = id;
		this.hashCode = Integer.MIN_VALUE;
	}




	/**
	 * Return the value associated with the column: MESSAGE
	 */
	public java.lang.String getMessage () {
		return message;
	}

	/**
	 * Set the value related to the column: MESSAGE
	 * @param message the MESSAGE value
	 */
	public void setMessage (java.lang.String message) {
		this.message = message;
	}




	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof org.infoscoop.dao.model.I18n)) return false;
		else {
			org.infoscoop.dao.model.I18n i18n = (org.infoscoop.dao.model.I18n) obj;
			if (null == this.getId() || null == i18n.getId()) return false;
			else return (this.getId().equals(i18n.getId()));
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