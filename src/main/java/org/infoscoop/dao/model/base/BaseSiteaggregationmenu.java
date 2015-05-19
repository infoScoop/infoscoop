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

import org.infoscoop.dao.model.SiteaggregationmenuPK;


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
	public BaseSiteaggregationmenu (SiteaggregationmenuPK id) {
		this.setId(id);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BaseSiteaggregationmenu (
		SiteaggregationmenuPK id,
		java.lang.String data) {

		this.setId(id);
		this.setData(data);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private SiteaggregationmenuPK id;

	// fields
	private java.lang.String data;



	/**
	 * Return the unique identifier of this class
     * @hibernate.id
     *  column="TYPE"
     */
	public SiteaggregationmenuPK getId () {
		return id;
	}

	/**
	 * Set the unique identifier of this class
	 * @param type the new ID
	 */
	public void setId (SiteaggregationmenuPK id) {
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




	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof org.infoscoop.dao.model.Siteaggregationmenu)) return false;
		else {
			org.infoscoop.dao.model.Siteaggregationmenu siteaggregationmenu = (org.infoscoop.dao.model.Siteaggregationmenu) obj;
			if (null == this.getId() || null == siteaggregationmenu.getId()) return false;
			else return (this.getId().equals(siteaggregationmenu.getId()));
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
