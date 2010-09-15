package org.infoscoop.dao.model.base;

import java.io.Serializable;


/**
 * This is an object that contains data related to the is_users table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="is_users"
 */

public abstract class BaseUser  implements Serializable {

	public static String REF = "User";
	public static String PROP_NAME = "Name";
	public static String PROP_EMAIL = "Email";


	// constructors
	public BaseUser () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseUser (java.lang.String email) {
		this.setEmail(email);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BaseUser (
		java.lang.String email,
		java.lang.String name) {

		this.setEmail(email);
		this.setName(name);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private java.lang.String email;

	// fields
	private java.lang.String name;



	/**
	 * Return the unique identifier of this class
     * @hibernate.id
     *  generator-class="assigned"
     *  column="email"
     */
	public java.lang.String getEmail () {
		return email;
	}

	/**
	 * Set the unique identifier of this class
	 * @param email the new ID
	 */
	public void setEmail (java.lang.String email) {
		this.email = email;
		this.hashCode = Integer.MIN_VALUE;
	}




	/**
	 * Return the value associated with the column: name
	 */
	public java.lang.String getName () {
		return name;
	}

	/**
	 * Set the value related to the column: name
	 * @param name the name value
	 */
	public void setName (java.lang.String name) {
		this.name = name;
	}




	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof org.infoscoop.dao.model.User)) return false;
		else {
			org.infoscoop.dao.model.User user = (org.infoscoop.dao.model.User) obj;
			if (null == this.getEmail() || null == user.getEmail()) return false;
			else return (this.getEmail().equals(user.getEmail()));
		}
	}

	public int hashCode () {
		if (Integer.MIN_VALUE == this.hashCode) {
			if (null == this.getEmail()) return super.hashCode();
			else {
				String hashStr = this.getClass().getName() + ":" + this.getEmail().hashCode();
				this.hashCode = hashStr.hashCode();
			}
		}
		return this.hashCode;
	}


	public String toString () {
		return super.toString();
	}


}