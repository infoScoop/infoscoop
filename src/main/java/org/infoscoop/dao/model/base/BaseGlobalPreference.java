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

public abstract class BaseGlobalPreference implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int hashCode = Integer.MIN_VALUE;

	public static String REF = "GlobalPreference";
	public static String PROP_ID = "Id";
	public static String PROP_UID = "Uid";
	public static String PROP_NAME = "Name";
	public static String PROP_VALUE = "Value";

	// primary key
	private Long id;

	// fields
	private String uid;
	private String name;
	private String value;

	public BaseGlobalPreference() {
		initialize();
	}

	public BaseGlobalPreference(String uid, String name, String value) {
		this.setUid(uid);
		this.setName(name);
		this.setValue(value);
	}

	protected void initialize () {}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
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
