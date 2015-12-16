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
	public static String PROP_UID = "Uid";
	public static String PROP_NAME = "name";
	public static String PROP_VALUE = "value";

	public BaseAccountAttr() {
		initialize();
	}

	public BaseAccountAttr(String uid, String name, String value) {
		this.setUid(uid);
		this.setName(name);
		this.setValue(value);
		initialize();
	}

	protected void initialize () {}

	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private java.lang.String uid;

	// fields
	private java.lang.String name;
	private java.lang.String value;

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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		BaseAccountAttr that = (BaseAccountAttr) o;
		return Objects.equals(hashCode, that.hashCode);
	}

	@Override
	public int hashCode() {
		return Objects.hash(hashCode);
	}
}
