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
 * This is an object that contains data related to the IS_PORTALADMINS table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="IS_PORTALADMINS"
 */

public abstract class BasePortaladmins  implements Serializable {

	public static String REF = "Portaladmins";
	public static String PROP_ROLEID = "Roleid";
	public static String PROP_ID = "Id";
	public static String PROP_ADMINROLE = "adminrole";
	public static String PROP_UID = "Uid";


	// constructors
	public BasePortaladmins () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BasePortaladmins (java.lang.String id) {
		this.setId(id);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BasePortaladmins (
		java.lang.String id,
		java.lang.String uid) {

		this.setId(id);
		this.setUid(uid);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private java.lang.String id;

	// fields
	private java.lang.String uid;
	private java.lang.String roleid;

	// many to one
	private org.infoscoop.dao.model.Adminrole adminrole;



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
	 * Return the value associated with the column: UID
	 */
	public java.lang.String getUid () {
		return uid;
	}

	/**
	 * Set the value related to the column: UID
	 * @param uid the UID value
	 */
	public void setUid (java.lang.String uid) {
		this.uid = uid;
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
	 * Return the value associated with the column: Roleid
	 */
	public org.infoscoop.dao.model.Adminrole getAdminrole () {
		return adminrole;
	}

	/**
	 * Set the value related to the column: Roleid
	 * @param adminrole the Roleid value
	 */
	public void setAdminrole (org.infoscoop.dao.model.Adminrole adminrole) {
		this.adminrole = adminrole;
	}




	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof org.infoscoop.dao.model.Portaladmins)) return false;
		else {
			org.infoscoop.dao.model.Portaladmins portaladmins = (org.infoscoop.dao.model.Portaladmins) obj;
			if (null == this.getId() || null == portaladmins.getId()) return false;
			else return (this.getId().equals(portaladmins.getId()));
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
