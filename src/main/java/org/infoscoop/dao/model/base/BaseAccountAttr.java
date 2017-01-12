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
import java.util.Objects;

public class BaseAccountAttr implements Serializable {

	public static String REF = "AccountAttr";
	public static String PROP_UID = "accountId";
	public static String PROP_NAME = "name";
	public static String PROP_VALUE = "value";
	public static String PROP_SYSTEM = "system";
	public static String PROP_SQUARE_ID = "squareId";

	public static Integer PROP_SYSTEM_TRUE = 1;
	public static Integer PROP_SYSTEM_FALSE = 0;
	
	public BaseAccountAttr() {
		initialize();
	}

	public BaseAccountAttr(String uid, String name, String value, Boolean system, String squareId) {
		this.setAccountId(uid);
		this.setName(name);
		this.setValue(value);
		this.setSystem(system);
		this.setSquareId(squareId);
		initialize();
	}

	protected void initialize () {}

	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private Long id;

	// fields
	private java.lang.String accountId;
	private java.lang.String name;
	private java.lang.String value;
	private java.lang.Boolean system;
	private java.lang.String squareId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public java.lang.String getAccountId() {
		return accountId;
	}

	public void setAccountId(java.lang.String accountId) {
		this.accountId = accountId;
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

	public Boolean getSystem() {
		return system;
	}

	public void setSystem(Boolean system) {
		this.system = system;
	}

	public String getSquareId() {
		return squareId;
	}

	public void setSquareId(String squareId) {
		this.squareId = squareId;
	}

	@Override
	public boolean equals(Object o) {
		if (null == o) return false;
		if (!(o instanceof org.infoscoop.dao.model.AccountAttr)) return false;
		else {
			org.infoscoop.dao.model.AccountAttr accountAttr = (org.infoscoop.dao.model.AccountAttr) o;
			if (null == this.getId() || null == accountAttr.getId()) return false;
			else return (this.getId().equals(accountAttr.getId()));
		}
	}

	@Override
	public int hashCode() {
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
