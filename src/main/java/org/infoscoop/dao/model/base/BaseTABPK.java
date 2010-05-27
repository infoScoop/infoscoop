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


public abstract class BaseTABPK implements Serializable {

	protected int hashCode = Integer.MIN_VALUE;

	private java.lang.String uid;
	private java.lang.String id;


	public BaseTABPK () {}
	
	public BaseTABPK (
		java.lang.String uid,
		java.lang.String id) {

		this.setUid(uid);
		this.setId(id);
	}


	/**
	 * Return the value associated with the column: UID
	 */
	public java.lang.String getUid () {
		return uid;
	}

	/**
	 * Set the value related to the column: UID
	 * @param uid the UID value
	 */
	public void setUid (java.lang.String uid) {
		this.uid = uid;
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




	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof org.infoscoop.dao.model.TABPK)) return false;
		else {
			org.infoscoop.dao.model.TABPK mObj = (org.infoscoop.dao.model.TABPK) obj;
			if (null != this.getUid() && null != mObj.getUid()) {
				if (!this.getUid().equals(mObj.getUid())) {
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
			return true;
		}
	}

	public int hashCode () {
		if (Integer.MIN_VALUE == this.hashCode) {
			StringBuffer sb = new StringBuffer();
			if (null != this.getUid()) {
				sb.append(this.getUid().hashCode());
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
			this.hashCode = sb.toString().hashCode();
		}
		return this.hashCode;
	}


}
