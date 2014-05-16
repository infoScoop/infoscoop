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


public abstract class BaseTABLAYOUTPK implements Serializable {

	protected int hashCode = Integer.MIN_VALUE;

	private java.lang.String tabid;
	private java.lang.Integer roleorder;
	private java.lang.Integer temp;


	public BaseTABLAYOUTPK () {}
	
	public BaseTABLAYOUTPK (
		java.lang.String tabid,
		java.lang.Integer roleorder,
		java.lang.Integer temp) {

		this.setTabid(tabid);
		this.setRoleorder(roleorder);
		this.setTemp(temp);
	}


	/**
	 * Return the value associated with the column: TABID
	 */
	public java.lang.String getTabid () {
		return tabid;
	}

	/**
	 * Set the value related to the column: TABID
	 * @param tabid the TABID value
	 */
	public void setTabid (java.lang.String tabid) {
		this.tabid = tabid;
	}



	/**
	 * Return the value associated with the column: ROLEORDER
	 */
	public java.lang.Integer getRoleorder () {
		return roleorder;
	}

	/**
	 * Set the value related to the column: ROLEORDER
	 * @param roleorder the ROLEORDER value
	 */
	public void setRoleorder (java.lang.Integer roleorder) {
		this.roleorder = roleorder;
	}



	/**
	 * Return the value associated with the column: TEMP
	 */
	public java.lang.Integer getTemp () {
		return temp;
	}

	/**
	 * Set the value related to the column: TEMP
	 * @param deleteflag the TEMP value
	 */
	public void setTemp (java.lang.Integer temp) {
		this.temp = temp;
	}




	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof org.infoscoop.dao.model.TABLAYOUTPK)) return false;
		else {
			org.infoscoop.dao.model.TABLAYOUTPK mObj = (org.infoscoop.dao.model.TABLAYOUTPK) obj;
			if (null != this.getTabid() && null != mObj.getTabid()) {
				if (!this.getTabid().equals(mObj.getTabid())) {
					return false;
				}
			}
			else {
				return false;
			}
			if (null != this.getRoleorder() && null != mObj.getRoleorder()) {
				if (!this.getRoleorder().equals(mObj.getRoleorder())) {
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
				sb.append(this.getTabid().hashCode()).append(":");
			}
			if (null != this.getRoleorder()) {
				sb.append(this.getRoleorder().hashCode()).append(":");
			}
			if (null != this.getTemp()) {
				sb.append(this.getTemp().hashCode());
			}
			
			if(sb.toString().length() == 0)
				return super.hashCode();
			
			this.hashCode = sb.toString().hashCode();
		}
		return this.hashCode;
	}


}
