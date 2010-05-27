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
 * This is an object that contains data related to the KEYWORD table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="KEYWORD"
 */

public abstract class BaseKeyword  implements Serializable {

	public static String REF = "Keyword";
	public static String PROP_TYPE = "Type";
	public static String PROP_KEYWORD = "Keyword";
	public static String PROP_DATE = "Date";
	public static String PROP_ID = "Id";
	public static String PROP_UID = "Uid";


	// constructors
	public BaseKeyword () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseKeyword (java.lang.Long id) {
		this.setId(id);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BaseKeyword (
		java.lang.Long id,
		java.lang.String uid,
		java.lang.Integer type,
		java.lang.String keyword,
		java.lang.String date) {

		this.setId(id);
		this.setUid(uid);
		this.setType(type);
		this.setKeyword(keyword);
		this.setDate(date);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private java.lang.Long id;

	// fields
	private java.lang.String uid;
	private java.lang.Integer type;
	private java.lang.String keyword;
	private java.lang.String date;



	/**
	 * Return the unique identifier of this class
     * @hibernate.id
     *  generator-class="sequence"
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
	 * Return the value associated with the column: TYPE
	 */
	public java.lang.Integer getType () {
		return type;
	}

	/**
	 * Set the value related to the column: TYPE
	 * @param type the TYPE value
	 */
	public void setType (java.lang.Integer type) {
		this.type = type;
	}



	/**
	 * Return the value associated with the column: KEYWORD
	 */
	public java.lang.String getKeyword () {
		return keyword;
	}

	/**
	 * Set the value related to the column: KEYWORD
	 * @param keyword the KEYWORD value
	 */
	public void setKeyword (java.lang.String keyword) {
		this.keyword = keyword;
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




	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof org.infoscoop.dao.model.Keyword)) return false;
		else {
			org.infoscoop.dao.model.Keyword keyword = (org.infoscoop.dao.model.Keyword) obj;
			if (null == this.getId() || null == keyword.getId()) return false;
			else return (this.getId().equals(keyword.getId()));
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
