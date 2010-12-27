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
 * This is an object that contains data related to the IS_PORTALLAYOUTS table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="IS_PORTALLAYOUTS"
 */

public abstract class BasePortallayout  implements Serializable {

	public static String REF = "Portallayout";
	public static String PROP_LAYOUT = "Layout";
	public static String PROP_ID = "Id";


	// constructors
	public BasePortallayout () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BasePortallayout (org.infoscoop.dao.model.PortallayoutsPK id) {
		this.setId(id);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BasePortallayout (
		org.infoscoop.dao.model.PortallayoutsPK id,
		java.lang.String layout) {

		this.setId(id);
		this.setLayout(layout);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private org.infoscoop.dao.model.PortallayoutsPK id;

	// fields
	private java.lang.String layout;



	/**
	 * Return the unique identifier of this class
     * @hibernate.id
     */
	public org.infoscoop.dao.model.PortallayoutsPK getId () {
		return id;
	}

	/**
	 * Set the unique identifier of this class
	 * @param id the new ID
	 */
	public void setId (org.infoscoop.dao.model.PortallayoutsPK id) {
		this.id = id;
		this.hashCode = Integer.MIN_VALUE;
	}




	/**
	 * Return the value associated with the column: layout
	 */
	public java.lang.String getLayout () {
		return layout;
	}

	/**
	 * Set the value related to the column: layout
	 * @param layout the layout value
	 */
	public void setLayout (java.lang.String layout) {
		this.layout = layout;
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