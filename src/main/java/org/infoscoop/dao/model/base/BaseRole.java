package org.infoscoop.dao.model.base;

import java.io.Serializable;


/**
 * This is an object that contains data related to the is_roles table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="is_roles"
 */

public abstract class BaseRole  implements Serializable {

	public static String REF = "Role";
	public static String PROP_NAME = "Name";
	public static String PROP_DESCRIPTION = "Description";
	public static String PROP_ID = "Id";


	// constructors
	public BaseRole () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseRole (java.lang.Integer id) {
		this.setId(id);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BaseRole (
		java.lang.Integer id,
		java.lang.String name) {

		this.setId(id);
		this.setName(name);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private java.lang.Integer id;

	// fields
	private java.lang.String name;
	private java.lang.String description;

	// collections
	private java.util.List<org.infoscoop.dao.model.RolePrincipal> rolePrincipals;



	/**
	 * Return the unique identifier of this class
     * @hibernate.id
     *  generator-class="native"
     *  column="id"
     */
	public java.lang.Integer getId () {
		return id;
	}

	/**
	 * Set the unique identifier of this class
	 * @param id the new ID
	 */
	public void setId (java.lang.Integer id) {
		this.id = id;
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



	/**
	 * Return the value associated with the column: description
	 */
	public java.lang.String getDescription () {
		return description;
	}

	/**
	 * Set the value related to the column: description
	 * @param description the description value
	 */
	public void setDescription (java.lang.String description) {
		this.description = description;
	}



	/**
	 * Return the value associated with the column: RolePrincipals
	 */
	public java.util.List<org.infoscoop.dao.model.RolePrincipal> getRolePrincipals () {
		return rolePrincipals;
	}

	/**
	 * Set the value related to the column: RolePrincipals
	 * @param rolePrincipals the RolePrincipals value
	 */
	public void setRolePrincipals (java.util.List<org.infoscoop.dao.model.RolePrincipal> rolePrincipals) {
		this.rolePrincipals = rolePrincipals;
	}




	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof org.infoscoop.dao.model.Role)) return false;
		else {
			org.infoscoop.dao.model.Role role = (org.infoscoop.dao.model.Role) obj;
			if (null == this.getId() || null == role.getId()) return false;
			else return (this.getId().equals(role.getId()));
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