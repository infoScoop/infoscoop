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


public abstract class BaseUSERPREFPK implements Serializable {

	protected int hashCode = Integer.MIN_VALUE;

	private java.lang.String widgetId;
	private java.lang.String name;


	public BaseUSERPREFPK () {}
	
	public BaseUSERPREFPK (
		java.lang.String widgetId,
		java.lang.String name) {

		this.setWidgetId(widgetId);
		this.setName(name);
	}


	/**
	 * Return the value associated with the column: fk_widget_id
	 */
	public java.lang.String getWidgetId () {
		return widgetId;
	}

	/**
	 * Set the value related to the column: fk_widget_id
	 * @param widgetId the fk_widget_id value
	 */
	public void setWidgetId (java.lang.String widgetId) {
		this.widgetId = widgetId;
	}



	/**
	 * Return the value associated with the column: name
	 */
	public java.lang.String getName () {
		return name;
	}

	/**
	 * Set the value related to the column: name
	 * @param name the name value
	 */
	public void setName (java.lang.String name) {
		this.name = name;
	}




	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof org.infoscoop.dao.model.USERPREFPK)) return false;
		else {
			org.infoscoop.dao.model.USERPREFPK mObj = (org.infoscoop.dao.model.USERPREFPK) obj;
			if (null != this.getWidgetId() && null != mObj.getWidgetId()) {
				if (!this.getWidgetId().equals(mObj.getWidgetId())) {
					return false;
				}
			}
			else {
				return false;
			}
			if (null != this.getName() && null != mObj.getName()) {
				if (!this.getName().equals(mObj.getName())) {
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
			StringBuilder sb = new StringBuilder();
			if (null != this.getWidgetId()) {
				sb.append(this.getWidgetId().hashCode());
				sb.append(":");
			}
			else {
				return super.hashCode();
			}
			if (null != this.getName()) {
				sb.append(this.getName().hashCode());
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
