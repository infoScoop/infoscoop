/* infoScoop OpenSource
 * Copyright (C) 2010 Beacon IT Inc.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0-standalone.html>.
 */

package org.infoscoop.dao.model.base;

import java.io.Serializable;


/**
 * This is an object that contains data related to the IS_ADMINROLES table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="IS_ADMINROLES"
 */

public abstract class BaseAdminrole  implements Serializable {

	public static String REF = "Adminrole";
	public static String PROP_PERMISSION = "Permission";
	public static String PROP_ROLEID = "Roleid";
	public static String PROP_ALLOWDELETE = "Allowdelete";
	public static String PROP_NAME = "Name";
	public static String PROP_ID = "Id";


	// constructors
	public BaseAdminrole () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseAdminrole (java.lang.String id) {
		this.setId(id);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BaseAdminrole (
		java.lang.String id,
		java.lang.String roleid,
		java.lang.String name,
		java.lang.String permission) {

		this.setId(id);
		this.setRoleid(roleid);
		this.setName(name);
		this.setPermission(permission);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private java.lang.String id;

	// fields
	private java.lang.String roleid;
	private java.lang.String name;
	private java.lang.String permission;
	private java.lang.Integer allowdelete;

	// collections
	private java.util.Set<org.infoscoop.dao.model.Portaladmins> portaladmins;



	/**
	 * Return the unique identifier of this class
     * @hibernate.id
     *  generator-class="native"
     *  column="ID"
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
	 * Return the value associated with the column: ROLEID
	 */
	public java.lang.String getRoleid () {
		return roleid;
	}

	/**
	 * Set the value related to the column: ROLEID
	 * @param roleid the ROLEID value
	 */
	public void setRoleid (java.lang.String roleid) {
		this.roleid = roleid;
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
	 * Return the value associated with the column: PERMISSION
	 */
	public java.lang.String getPermission () {
		return permission;
	}

	/**
	 * Set the value related to the column: PERMISSION
	 * @param permission the PERMISSION value
	 */
	public void setPermission (java.lang.String permission) {
		this.permission = permission;
	}



	/**
	 * Return the value associated with the column: ALLOWDELETE
	 */
	public java.lang.Integer getAllowdelete () {
		return allowdelete;
	}

	/**
	 * Set the value related to the column: ALLOWDELETE
	 * @param allowdelete the ALLOWDELETE value
	 */
	public void setAllowdelete (java.lang.Integer allowdelete) {
		this.allowdelete = allowdelete;
	}



	/**
	 * Return the value associated with the column: Portaladmins
	 */
	public java.util.Set<org.infoscoop.dao.model.Portaladmins> getPortaladmins () {
		return portaladmins;
	}

	/**
	 * Set the value related to the column: Portaladmins
	 * @param portaladmins the Portaladmins value
	 */
	public void setPortaladmins (java.util.Set<org.infoscoop.dao.model.Portaladmins> portaladmins) {
		this.portaladmins = portaladmins;
	}

	public void addToPortaladmins (org.infoscoop.dao.model.Portaladmins portaladmins) {
		if (null == getPortaladmins()) setPortaladmins(new java.util.TreeSet<org.infoscoop.dao.model.Portaladmins>());
		getPortaladmins().add(portaladmins);
	}




	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof org.infoscoop.dao.model.Adminrole)) return false;
		else {
			org.infoscoop.dao.model.Adminrole adminrole = (org.infoscoop.dao.model.Adminrole) obj;
			if (null == this.getId() || null == adminrole.getId()) return false;
			else return (this.getId().equals(adminrole.getId()));
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
