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
 * This is an object that contains data related to the PROPERTIES table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="PROPERTIES"
 */

public abstract class BaseProperties  implements Serializable {

	public static String REF = "Properties";
	public static String PROP_VALUE = "Value";
	public static String PROP_REQUIRED = "Required";
	public static String PROP_DESCRIPTION = "Description";
	public static String PROP_REGEX = "Regex";
	public static String PROP_DATATYPE = "Datatype";
	public static String PROP_REGEXMSG = "Regexmsg";
	public static String PROP_CATEGORY = "Category";
	public static String PROP_ENUMVALUE = "Enumvalue";
	public static String PROP_ADVANCED = "advanced";
	public static String PROP_ID = "Id";


	// constructors
	public BaseProperties () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseProperties (java.lang.String id) {
		this.setId(id);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BaseProperties (
		java.lang.String id,
		java.lang.Integer required) {

		this.setId(id);
		this.setRequired(required);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private java.lang.String id;

	// fields
	private java.lang.String category;
	private java.lang.String value;
	private java.lang.String description;
	private java.lang.String datatype;
	private java.lang.String enumvalue;
	private java.lang.Integer required;
	private java.lang.String regex;
	private java.lang.String regexmsg;
	private java.lang.Integer advanced;



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




	/**
	 * Return the value associated with the column: CATEGORY
	 */
	public java.lang.String getCategory () {
		return category;
	}

	/**
	 * Set the value related to the column: CATEGORY
	 * @param category the CATEGORY value
	 */
	public void setCategory (java.lang.String category) {
		this.category = category;
	}



	/**
	 * Return the value associated with the column: ADVANCED
	 */
	public java.lang.Integer getAdvanced () {
		return advanced;
	}

	/**
	 * Set the value related to the column: ADVANCED
	 * @param advanced the ADVANCED value
	 */
	public void setAdvanced (java.lang.Integer advanced) {
		this.advanced = advanced;
	}



	/**
	 * Return the value associated with the column: VALUE
	 */
	public java.lang.String getValue () {
		return value;
	}

	/**
	 * Set the value related to the column: VALUE
	 * @param value the VALUE value
	 */
	public void setValue (java.lang.String value) {
		this.value = value;
	}



	/**
	 * Return the value associated with the column: DESCRIPTION
	 */
	public java.lang.String getDescription () {
		return description;
	}

	/**
	 * Set the value related to the column: DESCRIPTION
	 * @param description the DESCRIPTION value
	 */
	public void setDescription (java.lang.String description) {
		this.description = description;
	}



	/**
	 * Return the value associated with the column: DATATYPE
	 */
	public java.lang.String getDatatype () {
		return datatype;
	}

	/**
	 * Set the value related to the column: DATATYPE
	 * @param datatype the DATATYPE value
	 */
	public void setDatatype (java.lang.String datatype) {
		this.datatype = datatype;
	}



	/**
	 * Return the value associated with the column: ENUMVALUE
	 */
	public java.lang.String getEnumvalue () {
		return enumvalue;
	}

	/**
	 * Set the value related to the column: ENUMVALUE
	 * @param enumvalue the ENUMVALUE value
	 */
	public void setEnumvalue (java.lang.String enumvalue) {
		this.enumvalue = enumvalue;
	}



	/**
	 * Return the value associated with the column: REQUIRED
	 */
	public java.lang.Integer getRequired () {
		return required;
	}

	/**
	 * Set the value related to the column: REQUIRED
	 * @param required the REQUIRED value
	 */
	public void setRequired (java.lang.Integer required) {
		this.required = required;
	}



	/**
	 * Return the value associated with the column: REGEX
	 */
	public java.lang.String getRegex () {
		return regex;
	}

	/**
	 * Set the value related to the column: REGEX
	 * @param regex the REGEX value
	 */
	public void setRegex (java.lang.String regex) {
		this.regex = regex;
	}



	/**
	 * Return the value associated with the column: REGEXMSG
	 */
	public java.lang.String getRegexmsg () {
		return regexmsg;
	}

	/**
	 * Set the value related to the column: REGEXMSG
	 * @param regexmsg the REGEXMSG value
	 */
	public void setRegexmsg (java.lang.String regexmsg) {
		this.regexmsg = regexmsg;
	}




	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof org.infoscoop.dao.model.Properties)) return false;
		else {
			org.infoscoop.dao.model.Properties properties = (org.infoscoop.dao.model.Properties) obj;
			if (null == this.getId() || null == properties.getId()) return false;
			else return (this.getId().equals(properties.getId()));
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
