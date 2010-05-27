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
 * This is an object that contains data related to the SITEAGGREGATIONMENU table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="SITEAGGREGATIONMENU"
 */

public abstract class BaseSiteaggregationmenu  implements Serializable {

	public static String REF = "Siteaggregationmenu";
	public static String PROP_TYPE = "Type";
	public static String PROP_DATA = "Data";


	// constructors
	public BaseSiteaggregationmenu () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseSiteaggregationmenu (java.lang.String type) {
		this.setType(type);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BaseSiteaggregationmenu (
		java.lang.String type,
		java.lang.String data) {

		this.setType(type);
		this.setData(data);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private java.lang.String type;

	// fields
	private java.lang.String data;



	/**
	 * Return the unique identifier of this class
     * @hibernate.id
     *  column="TYPE"
     */
	public java.lang.String getType () {
		return type;
	}

	/**
	 * Set the unique identifier of this class
	 * @param type the new ID
	 */
	public void setType (java.lang.String type) {
		this.type = type;
		this.hashCode = Integer.MIN_VALUE;
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




	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof org.infoscoop.dao.model.Siteaggregationmenu)) return false;
		else {
			org.infoscoop.dao.model.Siteaggregationmenu siteaggregationmenu = (org.infoscoop.dao.model.Siteaggregationmenu) obj;
			if (null == this.getType() || null == siteaggregationmenu.getType()) return false;
			else return (this.getType().equals(siteaggregationmenu.getType()));
		}
	}

	public int hashCode () {
		if (Integer.MIN_VALUE == this.hashCode) {
			if (null == this.getType()) return super.hashCode();
			else {
				String hashStr = this.getClass().getName() + ":" + this.getType().hashCode();
				this.hashCode = hashStr.hashCode();
			}
		}
		return this.hashCode;
	}


	public String toString () {
		return super.toString();
	}


}
