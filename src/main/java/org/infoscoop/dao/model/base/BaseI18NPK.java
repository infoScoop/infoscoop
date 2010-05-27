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


public abstract class BaseI18NPK implements Serializable {

	protected int hashCode = Integer.MIN_VALUE;

	private java.lang.String country;
	private java.lang.String id;
	private java.lang.String lang;
	private java.lang.String type;


	public BaseI18NPK () {}
	
	public BaseI18NPK (
		java.lang.String country,
		java.lang.String id,
		java.lang.String lang,
		java.lang.String type) {

		this.setCountry(country);
		this.setId(id);
		this.setLang(lang);
		this.setType(type);
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
	 * Return the value associated with the column: ID
	 */
	public java.lang.String getId () {
		return id;
	}

	/**
	 * Set the value related to the column: ID
	 * @param id the ID value
	 */
	public void setId (java.lang.String id) {
		this.id = id;
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




	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof org.infoscoop.dao.model.I18NPK)) return false;
		else {
			org.infoscoop.dao.model.I18NPK mObj = (org.infoscoop.dao.model.I18NPK) obj;
			if (null != this.getCountry() && null != mObj.getCountry()) {
				if (!this.getCountry().equals(mObj.getCountry())) {
					return false;
				}
			}
			else {
				return false;
			}
			if (null != this.getId() && null != mObj.getId()) {
				if (!this.getId().equals(mObj.getId())) {
					return false;
				}
			}
			else {
				return false;
			}
			if (null != this.getLang() && null != mObj.getLang()) {
				if (!this.getLang().equals(mObj.getLang())) {
					return false;
				}
			}
			else {
				return false;
			}
			if (null != this.getType() && null != mObj.getType()) {
				if (!this.getType().equals(mObj.getType())) {
					return false;
				}
			}
			else {
				return false;
			}
			return true;
		}
	}

	public int hashCode () {
		if (Integer.MIN_VALUE == this.hashCode) {
			StringBuffer sb = new StringBuffer();
			if (null != this.getCountry()) {
				sb.append(this.getCountry().hashCode());
				sb.append(":");
			}
			else {
				return super.hashCode();
			}
			if (null != this.getId()) {
				sb.append(this.getId().hashCode());
				sb.append(":");
			}
			else {
				return super.hashCode();
			}
			if (null != this.getLang()) {
				sb.append(this.getLang().hashCode());
				sb.append(":");
			}
			else {
				return super.hashCode();
			}
			if (null != this.getType()) {
				sb.append(this.getType().hashCode());
				sb.append(":");
			}
			else {
				return super.hashCode();
			}
			this.hashCode = sb.toString().hashCode();
		}
		return this.hashCode;
	}


}
