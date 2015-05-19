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


public abstract class BaseStaticTabPK implements Serializable {

	protected int hashCode = Integer.MIN_VALUE;

	private java.lang.String tabId;
	private java.lang.String squareid;


	public BaseStaticTabPK () {}
	
	public BaseStaticTabPK ( java.lang.String tabId,java.lang.String squareid ) {

		this.setTabid(tabId);
		this.setSquareid(squareid);
	}

	/**
	 * Return the unique identifier of this class
	 * 
	 * @hibernate.id column="id"
	 */
	public java.lang.String getTabid() {
		return tabId;
	}

	/**
	 * Set the unique identifier of this class
	 * 
	 * @param id
	 *            the new ID
	 */
	public void setTabid(java.lang.String tabId) {
		this.tabId = tabId;
		this.hashCode = Integer.MIN_VALUE;
	}

	public java.lang.String getSquareid() {
		return squareid;
	}

	public void setSquareid(java.lang.String squareid) {
		this.squareid = squareid;
	}


	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof org.infoscoop.dao.model.StaticTabPK)) return false;
		else {
			org.infoscoop.dao.model.StaticTabPK mObj = (org.infoscoop.dao.model.StaticTabPK) obj;
			if (null != this.getTabid() && null != mObj.getTabid()) {
				if (!this.getTabid().equals(mObj.getTabid())) {
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
			if (null != this.getTabid()) {
				sb.append(this.getTabid().hashCode());
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
