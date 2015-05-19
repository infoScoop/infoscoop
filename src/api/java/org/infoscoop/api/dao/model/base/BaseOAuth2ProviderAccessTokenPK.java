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

package org.infoscoop.api.dao.model.base;

import java.io.Serializable;

import org.infoscoop.api.dao.model.OAuth2ProviderAccessTokenPK;


public abstract class BaseOAuth2ProviderAccessTokenPK implements Serializable {

	protected int hashCode = Integer.MIN_VALUE;

	private java.lang.String id;
	private java.lang.String squareid;


	public BaseOAuth2ProviderAccessTokenPK () {}
	
	public BaseOAuth2ProviderAccessTokenPK ( java.lang.String id,java.lang.String squareid ) {

		this.setId(id);
		this.setSquareid(squareid);
	}


	public java.lang.String getId () {
		return id;
	}

	public void setId (java.lang.String id) {
		this.id = id;
	}

	public java.lang.String getSquareid() {
		return squareid;
	}

	public void setSquareid(java.lang.String squareid) {
		this.squareid = squareid;
	}


	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof OAuth2ProviderAccessTokenPK)) return false;
		else {
			OAuth2ProviderAccessTokenPK mObj = (OAuth2ProviderAccessTokenPK) obj;
			if (null != this.getId() && null != mObj.getId()) {
				if (!this.getId().equals(mObj.getId())) {
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
			if (null != this.getId()) {
				sb.append(this.getId().hashCode());
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
