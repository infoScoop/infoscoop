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

import org.infoscoop.dao.model.GadgetIconPK;


/**
 * This is an object that contains data related to the IS_GADGET_ICONS table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="IS_GADGET_ICONS"
 */

public abstract class BaseGadgetIcon  implements Serializable {

	public static String REF = "GadgetIcon";
	public static String PROP_URL = "Url";
	public static String PROP_TYPE = "Type";


	// constructors
	public BaseGadgetIcon () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseGadgetIcon (GadgetIconPK id) {
		this.setId(id);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BaseGadgetIcon (
		GadgetIconPK id,
		java.lang.String url) {

		this.setId(id);
		this.setUrl(url);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private GadgetIconPK id;

	// fields
	private java.lang.String url;



	public GadgetIconPK getId () {
		return id;
	}

	public void setId (GadgetIconPK id) {
		this.id = id;
		this.hashCode = Integer.MIN_VALUE;
	}




	/**
	 * Return the value associated with the column: URL
	 */
	public java.lang.String getUrl () {
		return url;
	}

	/**
	 * Set the value related to the column: URL
	 * @param url the URL value
	 */
	public void setUrl (java.lang.String url) {
		this.url = url;
	}




	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof org.infoscoop.dao.model.GadgetIcon)) return false;
		else {
			org.infoscoop.dao.model.GadgetIcon gadgetIcon = (org.infoscoop.dao.model.GadgetIcon) obj;
			if (null == this.getId() || null == gadgetIcon.getId()) return false;
			else return (this.getId().equals(gadgetIcon.getId()));
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
