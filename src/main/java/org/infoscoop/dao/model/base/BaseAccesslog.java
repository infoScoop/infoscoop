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
 * This is an object that contains data related to the ACCESSLOG table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="ACCESSLOG"
 */

public abstract class BaseAccesslog  implements Serializable {

	public static String REF = "Accesslog";
	public static String PROP_DATE = "Date";
	public static String PROP_ID = "Id";
	public static String PROP_UID = "Uid";
	public static String PROP_SQUARE_ID = "Squareid";


	// constructors
	public BaseAccesslog () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseAccesslog (java.lang.Long id) {
		this.setId(id);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BaseAccesslog (
		java.lang.Long id,
		java.lang.String uid,
		java.lang.String date,
		java.lang.String squareid) {

		this.setId(id);
		this.setUid(uid);
		this.setDate(date);
		this.setSquareid(squareid);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private java.lang.Long id;

	// fields
	private java.lang.String uid;
	private java.lang.String date;
	private java.lang.String squareid;


	/**
	 * Return the unique identifier of this class
     * @hibernate.id
     *  generator-class="native"
     *  column="ID"
     */
	public java.lang.Long getId () {
		return id;
	}

	/**
	 * Set the unique identifier of this class
	 * @param id the new ID
	 */
	public void setId (java.lang.Long id) {
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
	 * Return the value associated with the column: DATE
	 */
	public java.lang.String getDate () {
		return date;
	}

	/**
	 * Set the value related to the column: DATE
	 * @param date the DATE value
	 */
	public void setDate (java.lang.String date) {
		this.date = date;
	}

	public java.lang.String getSquareid() {
		return squareid;
	}

	public void setSquareid(java.lang.String squareid) {
		this.squareid = squareid;
	}


	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof org.infoscoop.dao.model.Accesslog)) return false;
		else {
			org.infoscoop.dao.model.Accesslog accesslog = (org.infoscoop.dao.model.Accesslog) obj;
			if (null == this.getId() || null == accesslog.getId()) return false;
			else return (this.getId().equals(accesslog.getId()));
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
