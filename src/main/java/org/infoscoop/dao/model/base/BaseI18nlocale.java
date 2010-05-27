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
 * This is an object that contains data related to the I18NLOCALE table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="I18NLOCALE"
 */

public abstract class BaseI18nlocale  implements Serializable {

	public static String REF = "I18nlocale";
	public static String PROP_TYPE = "Type";
	public static String PROP_LANG = "Lang";
	public static String PROP_COUNTRY = "Country";
	public static String PROP_ID = "Id";


	// constructors
	public BaseI18nlocale () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseI18nlocale (java.lang.Long id) {
		this.setId(id);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BaseI18nlocale (
		java.lang.Long id,
		java.lang.String type,
		java.lang.String country,
		java.lang.String lang) {

		this.setId(id);
		this.setType(type);
		this.setCountry(country);
		this.setLang(lang);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private java.lang.Long id;

	// fields
	private java.lang.String type;
	private java.lang.String country;
	private java.lang.String lang;



	/**
	 * Return the unique identifier of this class
     * @hibernate.id
     *  generator-class="native"
     *  column="ID"
     */
	public java.lang.Long getId () {
		return id;
	}

	/**
	 * Set the unique identifier of this class
	 * @param id the new ID
	 */
	public void setId (java.lang.Long id) {
		this.id = id;
		this.hashCode = Integer.MIN_VALUE;
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
	 * Return the value associated with the column: COUNTRY
	 */
	public java.lang.String getCountry () {
		return country;
	}

	/**
	 * Set the value related to the column: COUNTRY
	 * @param country the COUNTRY value
	 */
	public void setCountry (java.lang.String country) {
		this.country = country;
	}



	/**
	 * Return the value associated with the column: LANG
	 */
	public java.lang.String getLang () {
		return lang;
	}

	/**
	 * Set the value related to the column: LANG
	 * @param lang the LANG value
	 */
	public void setLang (java.lang.String lang) {
		this.lang = lang;
	}




	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof org.infoscoop.dao.model.I18nlocale)) return false;
		else {
			org.infoscoop.dao.model.I18nlocale i18nlocale = (org.infoscoop.dao.model.I18nlocale) obj;
			if (null == this.getId() || null == i18nlocale.getId()) return false;
			else return (this.getId().equals(i18nlocale.getId()));
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
