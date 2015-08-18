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
 * @hibernate.class
 *  table="IS_SQUARES"
 */

public abstract class BaseSquare  implements Serializable {

	public static String REF = "Square";
	public static String PROP_ID = "Id";
	public static String PROP_NAME = "name";
	public static String PROP_DESCRIPTION = "description";
	public static String PROP_LASTMODIFIED = "lastmodified";
	public static String PROP_OWNER ="owner";
	public static String PROP_MAX_USER_NUM = "maxUserNum";


	// constructors
	public BaseSquare () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseSquare (java.lang.String id) {
		this.setId(id);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BaseSquare (
		java.lang.String id,
		java.lang.String name,
		java.lang.String description,
		java.util.Date lastmodified,
		java.lang.String owner,
		java.lang.Integer maxUserNum) {

		this.setId(id);
		this.setName(name);
		this.setDescription(description);
		this.setLastmodified(lastmodified);
		this.setOwner(owner);
		this.
		initialize();
	}

	protected void initialize () {}

	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private java.lang.String id;

	// fields
	private java.lang.String name;
	private java.lang.String description;
	private java.util.Date lastmodified;
	private java.lang.String owner;
	private java.lang.Integer maxUserNum;

	/**
	 * Return the unique identifier of this class
     * @hibernate.id
     *  generator-class="sequence"
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

	public int getHashCode() {
		return hashCode;
	}

	public void setHashCode(int hashCode) {
		this.hashCode = hashCode;
	}

	public java.lang.String getName() {
		return name;
	}

	public void setName(java.lang.String name) {
		this.name = name;
	}

	public java.lang.String getDescription() {
		return description;
	}

	public void setDescription(java.lang.String description) {
		this.description = description;
	}

	public java.util.Date getLastmodified() {
		return lastmodified;
	}

	public void setLastmodified(java.util.Date lastmodified) {
		this.lastmodified = lastmodified;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public Integer getMaxUserNum() {
		return maxUserNum;
	}

	public void setMaxUserNum(Integer maxUserNum) {
		this.maxUserNum = maxUserNum;
	}

	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof org.infoscoop.dao.model.Cache)) return false;
		else {
			org.infoscoop.dao.model.Cache cache = (org.infoscoop.dao.model.Cache) obj;
			if (null == this.getId() || null == cache.getId()) return false;
			else return (this.getId().equals(cache.getId()));
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
