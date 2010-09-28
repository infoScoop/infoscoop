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
 * This is an object that contains data related to the TAB table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="TAB"
 */

public abstract class BaseTab  implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static String REF = "Tab";
	public static String PROP_TYPE = "Type";
	public static String PROP_DATA = "Data";
	public static String PROP_ORDER = "Order";
	public static String PROP_NAME = "Name";
	public static String PROP_DEFAULTUID = "Defaultuid";
	public static String PROP_ID = "Id";
	public static String PROP_WIDGETLASTMODIFIED = "Widgetlastmodified";

	// constructors
	public BaseTab () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseTab (org.infoscoop.dao.model.TABPK id) {
		this.setId(id);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private org.infoscoop.dao.model.TABPK id;

	private java.lang.String name;
	private java.lang.Integer order;
	private java.lang.String type;
	private java.lang.String data;
	private java.lang.String widgetlastmodified;

	/**
	 * Return the unique identifier of this class
     * @hibernate.id
     */
	public org.infoscoop.dao.model.TABPK getId () {
		return id;
	}

	/**
	 * Set the unique identifier of this class
	 * @param id the new ID
	 */
	public void setId (org.infoscoop.dao.model.TABPK id) {
		this.id = id;
		this.hashCode = Integer.MIN_VALUE;
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
	 * Return the value associated with the column: ORDER
	 */
	public java.lang.Integer getOrder () {
		return order;
	}

	/**
	 * Set the value related to the column: ORDER
	 * @param order the ORDER value
	 */
	public void setOrder (java.lang.Integer order) {
		this.order = order;
	}



	/**
	 * Return the value associated with the column: TYPE
	 */
	public java.lang.String getType () {
		return type;
	}

	/**
	 * Set the value related to the column: TYPE
	 * @param type the TYPE value
	 */
	public void setType (java.lang.String type) {
		this.type = type;
	}



	/**
	 * Return the value associated with the column: DATA
	 */
	public java.lang.String getData () {
		return data;
	}

	/**
	 * Set the value related to the column: DATA
	 * @param data the DATA value
	 */
	public void setData (java.lang.String data) {
		this.data = data;
	}



	/**
	 * Return the value associated with the column: WIDGETLASTMODIFIED
	 */
	public java.lang.String getWidgetlastmodified () {
		return widgetlastmodified;
	}

	/**
	 * Set the value related to the column: WIDGETLASTMODIFIED
	 * @param widgetlastmodified the WIDGETLASTMODIFIED value
	 */
	public void setWidgetlastmodified (java.lang.String widgetlastmodified) {
		this.widgetlastmodified = widgetlastmodified;
	}

	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof org.infoscoop.dao.model.Tab)) return false;
		else {
			org.infoscoop.dao.model.Tab tab = (org.infoscoop.dao.model.Tab) obj;
			if (null == this.getId() || null == tab.getId()) return false;
			else return (this.getId().equals(tab.getId()));
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
