package org.infoscoop.dao.model.base;

import java.io.Serializable;


/**
 * This is an object that contains data related to the IS_GROUPS table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="IS_GROUPS"
 */

public abstract class BaseGroup  implements Serializable {

	public static String REF = "Group";
	public static String PROP_NAME = "Name";
	public static String PROP_ID = "Id";
	public static String PROP_FK_DOMAIN_ID = "FkDomainId";


	// constructors
	public BaseGroup () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseGroup (java.lang.String id) {
		this.setId(id);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BaseGroup (
		java.lang.String id,
		java.lang.String name) {

		this.setId(id);
		this.setName(name);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private java.lang.String id;

	// fields
	private java.lang.Integer fkDomainId;
	private java.lang.String name;

	// collections
	private java.util.Set<org.infoscoop.dao.model.User> users;



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
	 * Return the value associated with the column: Users
	 */
	public java.util.Set<org.infoscoop.dao.model.User> getUsers () {
		return users;
	}

	/**
	 * Set the value related to the column: Users
	 * @param users the Users value
	 */
	public void setUsers (java.util.Set<org.infoscoop.dao.model.User> users) {
		this.users = users;
	}

	public void addToUsers (org.infoscoop.dao.model.User user) {
		getUsers().add(user);
	}




	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof org.infoscoop.dao.model.Group)) return false;
		else {
			org.infoscoop.dao.model.Group group = (org.infoscoop.dao.model.Group) obj;
			if (null == this.getId() || null == group.getId()) return false;
			else return (this.getId().equals(group.getId()));
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