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


public abstract class BaseHOLIDAYSPK implements Serializable {

	protected int hashCode = Integer.MIN_VALUE;

	private java.lang.String country;
	private java.lang.String lang;


	public BaseHOLIDAYSPK () {}
	
	public BaseHOLIDAYSPK ( java.lang.String lang,java.lang.String country ) {

		this.setCountry(country);
		this.setLang(lang);
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
		if (!(obj instanceof org.infoscoop.dao.model.HOLIDAYSPK)) return false;
		else {
			org.infoscoop.dao.model.HOLIDAYSPK mObj = (org.infoscoop.dao.model.HOLIDAYSPK) obj;
			if (null != this.getCountry() && null != mObj.getCountry()) {
				if (!this.getCountry().equals(mObj.getCountry())) {
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
			if (null != this.getLang()) {
				sb.append(this.getLang().hashCode());
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
