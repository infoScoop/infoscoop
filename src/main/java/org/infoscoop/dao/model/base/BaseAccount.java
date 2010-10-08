package org.infoscoop.dao.model.base;

import java.io.Serializable;


/**
 * This is an object that contains data related to the IS_ACCOUNTS table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="IS_ACCOUNTS"
 */

public abstract class BaseAccount  implements Serializable {

	public static String REF = "Account";
	public static String PROP_NAME = "name";
	public static String PROP_PASSWORD = "password";
	public static String PROP_ID = "Id";


	// constructors
	public BaseAccount () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseAccount (org.infoscoop.dao.model.ACCOUNTPK id) {
		this.setId(id);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private org.infoscoop.dao.model.ACCOUNTPK id;

	// fields
	private java.lang.String name;
	private java.lang.String password;



	/**
	 * Return the unique identifier of this class
     * @hibernate.id
     */
	public org.infoscoop.dao.model.ACCOUNTPK getId () {
		return id;
	}

	/**
	 * Set the unique identifier of this class
	 * @param id the new ID
	 */
	public void setId (org.infoscoop.dao.model.ACCOUNTPK id) {
		this.id = id;
		this.hashCode = Integer.MIN_VALUE;
	}




	/**
	 * Return the value associated with the column: NAME
	 */
	public java.lang.String getName () {
		return name;
	}

	/**
	 * Set the value related to the column: NAME
	 * @param name the NAME value
	 */
	public void setName (java.lang.String name) {
		this.name = name;
	}



	/**
	 * Return the value associated with the column: PASSWORD
	 */
	public java.lang.String getPassword () {
		return password;
	}

	/**
	 * Set the value related to the column: PASSWORD
	 * @param password the PASSWORD value
	 */
	public void setPassword (java.lang.String password) {
		this.password = password;
	}




	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof org.infoscoop.dao.model.Account)) return false;
		else {
			org.infoscoop.dao.model.Account account = (org.infoscoop.dao.model.Account) obj;
			if (null == this.getId() || null == account.getId()) return false;
			else return (this.getId().equals(account.getId()));
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