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

public class BaseAccountSquare implements Serializable {

	public static String REF = "AccountSquare";
	public static String PROP_UID = "Uid";
	public static String PROP_SQUARE_ID = "squareId";

	public BaseAccountSquare() {
		initialize();
	}

	public BaseAccountSquare(String uid, String squareId) {
		this.setAccountId(uid);
		this.setSquareId(squareId);
	}

	protected void initialize () {}

	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private Long id;

	// fields
	private java.lang.String accountId;
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
			org.infoscoop.dao.model.AccountSquare accountSquare = (org.infoscoop.dao.model.AccountSquare) o;
			if (null == this.getId() || null == accountSquare.getId()) return false;
			else return (this.getId().equals(accountSquare.getId()));
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(hashCode);
	}
}
