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
 * This is an object that contains data related to the SEARCHENGINE table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="SEARCHENGINE"
 */

public abstract class BaseSearchengine  implements Serializable {

	public static String REF = "Searchengine";
	public static String PROP_DATA = "Data";
	public static String PROP_TEMP = "Temp";


	// constructors
	public BaseSearchengine () {
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BaseSearchengine (
		java.lang.Integer temp,
		java.lang.String data) {

		this.setTemp(temp);
		this.setData(data);
		initialize();
	}

	protected void initialize () {}


	// fields
	private java.lang.Integer temp;
	private java.lang.String data;

	/**
	 * Return the value associated with the column: TEMP
	 */
	public java.lang.Integer getTemp () {
		return temp;
	}

	/**
	 * Set the value related to the column: TEMP
	 * @param temp the TEMP value
	 */
	public void setTemp (java.lang.Integer temp) {
		this.temp = temp;
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

	public String toString () {
		return super.toString();
	}


}
