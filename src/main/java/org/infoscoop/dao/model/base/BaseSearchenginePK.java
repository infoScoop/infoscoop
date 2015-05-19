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


public abstract class BaseSearchenginePK implements Serializable {

	protected int hashCode = Integer.MIN_VALUE;

	private java.lang.Integer temp;
	private java.lang.String squareid;


	public BaseSearchenginePK () {}
	
	public BaseSearchenginePK ( java.lang.Integer temp,java.lang.String squareid ) {

		this.setTemp(temp);
		this.setSquareid(squareid);
	}

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

	public java.lang.String getSquareid() {
		return squareid;
	}

	public void setSquareid(java.lang.String squareid) {
		this.squareid = squareid;
	}


	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof org.infoscoop.dao.model.SearchenginePK)) return false;
		else {
			org.infoscoop.dao.model.SearchenginePK mObj = (org.infoscoop.dao.model.SearchenginePK) obj;
			if (null != this.getTemp() && null != mObj.getTemp()) {
				if (!this.getTemp().equals(mObj.getTemp())) {
					return false;
				}
			}
			else {
				return false;
			}
			if (null != this.getSquareid() && null != mObj.getSquareid()) {
				if (!this.getSquareid().equals(mObj.getSquareid())) {
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
			if (null != this.getTemp()) {
				sb.append(this.getTemp().hashCode());
				sb.append(":");
			}
			else {
				return super.hashCode();
			}
			if (null != this.getSquareid()) {
				sb.append(this.getSquareid().hashCode());
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
