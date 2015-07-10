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

public abstract class BaseLogo implements Serializable {

	private int hashCode = Integer.MIN_VALUE;

	public static String REF = "Logo";
	public static String PROP_ID = "Id";
	public static String PROP_SQUAREID = "Squareid";
	public static String PROP_LOGO = "Logo";
	public static String PROP_TYPE = "Type";

	// primary key
	private Long id;

	// fields
	private String squareId;
	private byte[] logo;
	private String type;

	public BaseLogo() {
		initialize();
	}

	public BaseLogo(String squareId, byte[] logo, String type) {
		this.setSquareid(squareId);
		this.setLogo(logo);
		this.setType(type);
	}

	protected void initialize () {}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSquareid() {
		return squareId;
	}

	public void setSquareid(String squareId) {
		this.squareId = squareId;
	}

	public byte[] getLogo() {
		return logo;
	}

	public void setLogo(byte[] logo) {
		this.logo = logo;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof org.infoscoop.dao.model.Logo)) return false;
		else {
			org.infoscoop.dao.model.Logo logo = (org.infoscoop.dao.model.Logo) obj;
			if (null == this.getId() || null == logo.getId()) return false;
			else return (this.getId().equals(logo.getId()));
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
}
