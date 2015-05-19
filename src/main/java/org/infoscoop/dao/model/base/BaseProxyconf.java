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

import org.infoscoop.dao.model.ProxyconfPK;


/**
 * This is an object that contains data related to the PROXYCONF table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="PROXYCONF"
 */

public abstract class BaseProxyconf  implements Serializable {

	public static String REF = "Proxyconf";
	public static String PROP_LASTMODIFIED = "Lastmodified";
	public static String PROP_DATA = "Data";
	public static String PROP_TEMP = "Temp";


	// constructors
	public BaseProxyconf () {
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BaseProxyconf (
		ProxyconfPK id,
		java.lang.String data) {

		this.setId(id);
		this.setData(data);
		initialize();
	}

	protected void initialize () {}



	// fields
	private ProxyconfPK id;
	private java.lang.String data;
	private java.util.Date lastmodified;






	/**
	 * Return the value associated with the column: TEMP
	 */
	public ProxyconfPK getId () {
		return id;
	}

	/**
	 * Set the value related to the column: TEMP
	 * @param temp the TEMP value
	 */
	public void setId (ProxyconfPK id) {
		this.id = id;
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







	public String toString () {
		return super.toString();
	}


}
