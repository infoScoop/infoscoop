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
 * This is an object that contains data related to the SITEAGGREGATIONMENU_TEMP table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="SITEAGGREGATIONMENU_TEMP"
 */

public abstract class BaseSiteaggregationmenu_temp  implements Serializable {

	public static String REF = "Siteaggregationmenu_temp";
	public static String PROP_LASTMODIFIED = "Lastmodified";
	public static String PROP_DATA = "Data";
	public static String PROP_WORKINGUID = "Workinguid";
	public static String PROP_ID = "Id";


	// constructors
	public BaseSiteaggregationmenu_temp () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseSiteaggregationmenu_temp (org.infoscoop.dao.model.SITEAGGREGATIONMENU_TEMPPK id) {
		this.setId(id);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BaseSiteaggregationmenu_temp (
		org.infoscoop.dao.model.SITEAGGREGATIONMENU_TEMPPK id,
		java.lang.String data) {

		this.setId(id);
		this.setData(data);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private org.infoscoop.dao.model.SITEAGGREGATIONMENU_TEMPPK id;

	// fields
	private java.lang.String data;
	private java.lang.String workinguid;
	private java.util.Date lastmodified;



	/**
	 * Return the unique identifier of this class
     * @hibernate.id
     */
	public org.infoscoop.dao.model.SITEAGGREGATIONMENU_TEMPPK getId () {
		return id;
	}

	/**
	 * Set the unique identifier of this class
	 * @param id the new ID
	 */
	public void setId (org.infoscoop.dao.model.SITEAGGREGATIONMENU_TEMPPK id) {
		this.id = id;
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



	/**
	 * Return the value associated with the column: WORKINGUID
	 */
	public java.lang.String getWorkinguid () {
		return workinguid;
	}

	/**
	 * Set the value related to the column: WORKINGUID
	 * @param workinguid the WORKINGUID value
	 */
	public void setWorkinguid (java.lang.String workinguid) {
		this.workinguid = workinguid;
	}



	/**
	 * Return the value associated with the column: LASTMODIFIED
	 */
	public java.util.Date getLastmodified () {
		return lastmodified;
	}

	/**
	 * Set the value related to the column: LASTMODIFIED
	 * @param lastmodified the LASTMODIFIED value
	 */
	public void setLastmodified (java.util.Date lastmodified) {
		this.lastmodified = lastmodified;
	}




	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof org.infoscoop.dao.model.Siteaggregationmenu_temp)) return false;
		else {
			org.infoscoop.dao.model.Siteaggregationmenu_temp siteaggregationmenu_temp = (org.infoscoop.dao.model.Siteaggregationmenu_temp) obj;
			if (null == this.getId() || null == siteaggregationmenu_temp.getId()) return false;
			else return (this.getId().equals(siteaggregationmenu_temp.getId()));
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
