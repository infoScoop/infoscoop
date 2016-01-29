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

public class BasePrincipal implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static String REF = "Principal";
	public static String PROP_NAME = "name";
	public static String PROP_ACCOUNT_ATTR_NAME = "accountAttrName";
	public static String PROP_SQUARE_ID = "squareId";

	public BasePrincipal() {
		initialize();
	}

	public BasePrincipal(String name, String accountAttrName, String squareId) {
		this.setName(accountAttrName);
		this.setAccountAttrName(accountAttrName);
		this.setSquareId(squareId);
		initialize();
	}
	
	protected void initialize () {}
	
	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private Long id;

	// fields
	private java.lang.String name;
	private java.lang.String accountAttrName;
	private java.lang.String squareId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public java.lang.String getName() {
		return name;
	}

	public void setName(java.lang.String name) {
		this.name = name;
	}

	public java.lang.String getAccountAttrName() {
		return accountAttrName;
	}

	public void setAccountAttrName(java.lang.String accountAttrName) {
		this.accountAttrName = accountAttrName;
	}

	public java.lang.String getSquareId() {
		return squareId;
	}

	public void setSquareId(java.lang.String squareId) {
		this.squareId = squareId;
	}

	@Override
	public boolean equals(Object o) {
		if (null == o) return false;
		if (!(o instanceof org.infoscoop.dao.model.AccountAttr)) return false;
		else {
			org.infoscoop.dao.model.Principal principal = (org.infoscoop.dao.model.Principal) o;
			if (null == this.getId() || null == principal.getId()) return false;
			else return (this.getId().equals(principal.getId()));
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
