package org.infoscoop.dao.model.base;

import java.io.Serializable;


/**
 * This is an object that contains data related to the IS_USERS table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="IS_USERS"
 */

public abstract class BaseUser  implements Serializable {

	public static String REF = "User";
	public static String PROP_NAME = "Name";
	public static String PROP_EMAIL = "Email";
	public static String PROP_ID = "Id";
	public static String PROP_ADMIN = "Admin";
	public static String PROP_FK_DOMAIN_ID = "FkDomainId";


	// constructors
	public BaseUser () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseUser (java.lang.String id) {
		this.setId(id);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BaseUser (
		java.lang.String id,
		java.lang.String name,
		java.lang.String email,
		java.lang.Integer admin) {

		this.setId(id);
		this.setName(name);
		this.setEmail(email);
		this.setAdmin(admin);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private java.lang.String id;

	// fields
	private java.lang.Integer fkDomainId;
	private java.lang.String name;
	private java.lang.String email;
	private java.lang.Integer admin;

	// collections
	private java.util.Set<org.infoscoop.dao.model.Group> groups;



	/**
	 * Return the unique identifier of this class
     * @hibernate.id
     *  generator-class="native"
     *  column="id"
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
	 * Return the value associated with the column: fk_domain_id
	 */
	public java.lang.Integer getFkDomainId () {
		return fkDomainId;
	}

	/**
	 * Set the value related to the column: fk_domain_id
	 * @param fkDomainId the fk_domain_id value
	 */
	public void setFkDomainId (java.lang.Integer fkDomainId) {
		this.fkDomainId = fkDomainId;
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



	/**
	 * Return the value associated with the column: email
	 */
	public java.lang.String getEmail () {
		return email;
	}

	/**
	 * Set the value related to the column: email
	 * @param email the email value
	 */
	public void setEmail (java.lang.String email) {
		this.email = email;
	}



	/**
	 * Return the value associated with the column: admin
	 */
	public java.lang.Integer getAdmin () {
		return admin;
	}

	/**
	 * Set the value related to the column: admin
	 * @param admin the admin value
	 */
	public void setAdmin (java.lang.Integer admin) {
		this.admin = admin;
	}



	/**
	 * Return the value associated with the column: Groups
	 */
	public java.util.Set<org.infoscoop.dao.model.Group> getGroups () {
		return groups;
	}

	/**
	 * Set the value related to the column: Groups
	 * @param groups the Groups value
	 */
	public void setGroups (java.util.Set<org.infoscoop.dao.model.Group> groups) {
		this.groups = groups;
	}

	public void addToGroups (org.infoscoop.dao.model.Group group) {
		if (null == getGroups()) setGroups(new java.util.TreeSet<org.infoscoop.dao.model.Group>());
		getGroups().add(group);
	}




	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof org.infoscoop.dao.model.User)) return false;
		else {
			org.infoscoop.dao.model.User user = (org.infoscoop.dao.model.User) obj;
			if (null == this.getId() || null == user.getId()) return false;
			else return (this.getId().equals(user.getId()));
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