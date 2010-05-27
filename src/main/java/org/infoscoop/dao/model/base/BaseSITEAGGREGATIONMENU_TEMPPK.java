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


public abstract class BaseSITEAGGREGATIONMENU_TEMPPK implements Serializable {

	protected int hashCode = Integer.MIN_VALUE;

	private java.lang.String type;
	private java.lang.String sitetopid;


	public BaseSITEAGGREGATIONMENU_TEMPPK () {}
	
	public BaseSITEAGGREGATIONMENU_TEMPPK (
		java.lang.String type,
		java.lang.String sitetopid) {

		this.setType(type);
		this.setSitetopid(sitetopid);
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
	 * Return the value associated with the column: SITETOPID
	 */
	public java.lang.String getSitetopid () {
		return sitetopid;
	}

	/**
	 * Set the value related to the column: SITETOPID
	 * @param sitetopid the SITETOPID value
	 */
	public void setSitetopid (java.lang.String sitetopid) {
		this.sitetopid = sitetopid;
	}




	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof org.infoscoop.dao.model.SITEAGGREGATIONMENU_TEMPPK)) return false;
		else {
			org.infoscoop.dao.model.SITEAGGREGATIONMENU_TEMPPK mObj = (org.infoscoop.dao.model.SITEAGGREGATIONMENU_TEMPPK) obj;
			if (null != this.getType() && null != mObj.getType()) {
				if (!this.getType().equals(mObj.getType())) {
					return false;
				}
			}
			else {
				return false;
			}
			if (null != this.getSitetopid() && null != mObj.getSitetopid()) {
				if (!this.getSitetopid().equals(mObj.getSitetopid())) {
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
			StringBuilder sb = new StringBuilder();
			if (null != this.getType()) {
				sb.append(this.getType().hashCode());
				sb.append(":");
			}
			else {
				return super.hashCode();
			}
			if (null != this.getSitetopid()) {
				sb.append(this.getSitetopid().hashCode());
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
