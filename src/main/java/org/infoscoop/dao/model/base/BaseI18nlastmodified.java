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

import org.infoscoop.dao.model.I18NlastmodifiedPK;


/**
 * This is an object that contains data related to the I18NLASTMODIFIED table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="I18NLASTMODIFIED"
 */

public abstract class BaseI18nlastmodified  implements Serializable {

	public static String REF = "I18nlastmodified";
	public static String PROP_LASTMODIFIED = "Lastmodified";
	public static String PROP_ID = "Id";


	// constructors
	public BaseI18nlastmodified () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseI18nlastmodified (I18NlastmodifiedPK id) {
		this.setId(id);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private I18NlastmodifiedPK id;

	// fields
	private java.util.Date lastmodified;



	/**
	 * Return the unique identifier of this class
     * @hibernate.id
     *  generator-class="sequence"
     *  column="TYPE"
     */
	public I18NlastmodifiedPK getId () {
		return id;
	}

	/**
	 * Set the unique identifier of this class
	 * @param id the new ID
	 */
	public void setId (I18NlastmodifiedPK id) {
		this.id = id;
		this.hashCode = Integer.MIN_VALUE;
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
		if (!(obj instanceof org.infoscoop.dao.model.I18nlastmodified)) return false;
		else {
			org.infoscoop.dao.model.I18nlastmodified i18nlastmodified = (org.infoscoop.dao.model.I18nlastmodified) obj;
			if (null == this.getId() || null == i18nlastmodified.getId()) return false;
			else return (this.getId().equals(i18nlastmodified.getId()));
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
