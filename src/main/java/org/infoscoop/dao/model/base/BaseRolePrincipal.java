package org.infoscoop.dao.model.base;

import java.io.Serializable;


/**
 * This is an object that contains data related to the is_role_principals table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="is_role_principals"
 */

public abstract class BaseRolePrincipal  implements Serializable {

	public static String REF = "RolePrincipal";
	public static String PROP_NAME = "Name";
	public static String PROP_FK_ROLE = "FkRole";
	public static String PROP_TYPE = "Type";
	public static String PROP_ID = "Id";


	// constructors
	public BaseRolePrincipal () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseRolePrincipal (java.lang.Integer id) {
		this.setId(id);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BaseRolePrincipal (
		java.lang.Integer id,
		org.infoscoop.dao.model.Role fkRole,
		java.lang.String type,
		java.lang.String name) {

		this.setId(id);
		this.setFkRole(fkRole);
		this.setType(type);
		this.setName(name);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private java.lang.Integer id;

	// fields
	private java.lang.String type;
	private java.lang.String name;

	// many to one
	private org.infoscoop.dao.model.Role fkRole;



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
	 * Return the value associated with the column: type
	 */
	public java.lang.String getType () {
		return type;
	}

	/**
	 * Set the value related to the column: type
	 * @param type the type value
	 */
	public void setType (java.lang.String type) {
		this.type = type;
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
	 * Return the value associated with the column: fk_role_id
	 */
	public org.infoscoop.dao.model.Role getFkRole () {
		return fkRole;
	}

	/**
	 * Set the value related to the column: fk_role_id
	 * @param fkRole the fk_role_id value
	 */
	public void setFkRole (org.infoscoop.dao.model.Role fkRole) {
		this.fkRole = fkRole;
	}




	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof org.infoscoop.dao.model.RolePrincipal)) return false;
		else {
			org.infoscoop.dao.model.RolePrincipal rolePrincipal = (org.infoscoop.dao.model.RolePrincipal) obj;
			if (null == this.getId() || null == rolePrincipal.getId()) return false;
			else return (this.getId().equals(rolePrincipal.getId()));
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