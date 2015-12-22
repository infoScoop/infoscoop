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

import org.infoscoop.dao.model.PortallayoutPK;


/**
 * This is an object that contains data related to the PORTALLAYOUT table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="PORTALLAYOUT"
 */

public abstract class BasePortallayout  implements Serializable {

	public static String REF = "Portallayout";
	public static String PROP_NAME = "Name";
	public static String PROP_LAYOUT = "Layout";
	public static String PROP_ADVANCED = "advanced";


	// constructors
	public BasePortallayout () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BasePortallayout (PortallayoutPK id) {
		this.setId(id);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BasePortallayout (
		PortallayoutPK id,
		java.lang.String layout) {

		this.setId(id);
		this.setLayout(layout);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private PortallayoutPK id;

	// fields
	private java.lang.String layout;
	private java.lang.Integer advanced;



	/**
	 * Return the unique identifier of this class
     * @hibernate.id
     *  generator-class="sequence"
     *  column="NAME"
     */
	public PortallayoutPK getId () {
		return id;
	}

	/**
	 * Set the unique identifier of this class
	 * @param name the new ID
	 */
	public void setId (PortallayoutPK id) {
		this.id = id;
		this.hashCode = Integer.MIN_VALUE;
	}




	/**
	 * Return the value associated with the column: LAYOUT
	 */
	public java.lang.String getLayout () {
		return layout;
	}

	/**
	 * Set the value related to the column: LAYOUT
	 * @param layout the LAYOUT value
	 */
	public void setLayout (java.lang.String layout) {
		this.layout = layout;
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


	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof org.infoscoop.dao.model.Portallayout)) return false;
		else {
			org.infoscoop.dao.model.Portallayout portallayout = (org.infoscoop.dao.model.Portallayout) obj;
			if (null == this.getId() || null == portallayout.getId()) return false;
			else return (this.getId().equals(portallayout.getId()));
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
