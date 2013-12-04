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

import com.fasterxml.jackson.annotation.JsonRootName;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

/**
 * This is an object that contains data related to the TABLAYOUT table. Do not
 * modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 * 
 * @hibernate.class table="TABLAYOUT"
 */

@JsonRootName("tabLayout")
@XStreamAlias("tabLayout")
public abstract class BaseTablayout implements Serializable {

	public static String REF = "TabLayout";
	// public static String PROP_TABNUMBER = "Tabnumber";
	public static String PROP_WIDGETSLASTMODIFIED = "Widgetslastmodified";
	// public static String PROP_DELETEFLAG = "Deleteflag";
	public static String PROP_ROLENAME = "Rolename";
	public static String PROP_ROLE = "Role";
	public static String PROP_DEFAULTUID = "Defaultuid";
	public static String PROP_PRINCIPALTYPE = "Principaltype";
	public static String PROP_ID = "Id";
	public static String PROP_LAYOUT = "Layout";
	public static String PROP_WIDGETS = "Widgets";
	public static String PROP_WORKINGUID = "Workinguid";
	public static String PROP_TEMP_LASTMODIFIED = "Templastmodified";

	// constructors
	public BaseTablayout() {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseTablayout(org.infoscoop.dao.model.TABLAYOUTPK id) {
		this.setId(id);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BaseTablayout(org.infoscoop.dao.model.TABLAYOUTPK id,
			java.lang.String role, java.lang.String rolename,
			java.lang.String principaltype, java.lang.String widgets,
			java.lang.String layout, java.lang.String workinguid) {

		this.setId(id);
		this.setRole(role);
		this.setRolename(rolename);
		this.setPrincipaltype(principaltype);
		this.setWidgets(widgets);
		this.setLayout(layout);
		// this.setDeleteflag(deleteflag);
		this.setWorkinguid(workinguid);
		initialize();
	}

	protected void initialize() {
	}

	@XStreamOmitField
	private int hashCode = Integer.MIN_VALUE;

	// primary key
	@XStreamOmitField
	private org.infoscoop.dao.model.TABLAYOUTPK id;

	// fields
	@XStreamAlias("roleRegx")
	private java.lang.String role;
	
	private java.lang.String rolename;
	private java.lang.String principaltype;
	
	@XStreamAsAttribute
	private java.lang.String defaultuid;
	
	private java.lang.String widgets;
	private java.lang.String layout;
	
	@XStreamOmitField
	private java.lang.String widgetslastmodified;
	
	@XStreamOmitField
	private java.lang.String workinguid;
	
	@XStreamOmitField
	private java.util.Date templastmodified;

	@XStreamOmitField
	private org.infoscoop.dao.model.StaticTab statictab;

	@XStreamAsAttribute
	private Integer roleOrder;

	@XStreamAsAttribute
	private Integer temp;

	/**
	 * Return the unique identifier of this class
	 * 
	 * @hibernate.id
	 */
	public org.infoscoop.dao.model.TABLAYOUTPK getId() {
		return id;
	}

	/**
	 * Set the unique identifier of this class
	 * 
	 * @param id
	 *            the new ID
	 */
	public void setId(org.infoscoop.dao.model.TABLAYOUTPK id) {
		this.id = id;
		this.hashCode = Integer.MIN_VALUE;
		this.roleOrder = id.getRoleorder();
		this.temp = id.getTemp();
	}

	public java.util.Date getTemplastmodified() {
		return templastmodified;
	}

	public void setTemplastmodified(java.util.Date templastmodified) {
		this.templastmodified = templastmodified;
	}

	public org.infoscoop.dao.model.StaticTab getStatictab() {
		return statictab;
	}

	public void setStatictab(org.infoscoop.dao.model.StaticTab staticTab) {
		statictab = staticTab;
	}

	/**
	 * Return the value associated with the column: ROLE
	 */
	public java.lang.String getRole() {
		return role;
	}

	/**
	 * Set the value related to the column: ROLE
	 * 
	 * @param role
	 *            the ROLE value
	 */
	public void setRole(java.lang.String role) {
		this.role = role;
	}

	/**
	 * Return the value associated with the column: ROLENAME
	 */
	public java.lang.String getRolename() {
		return rolename;
	}

	/**
	 * Set the value related to the column: ROLENAME
	 * 
	 * @param rolename
	 *            the ROLENAME value
	 */
	public void setRolename(java.lang.String rolename) {
		this.rolename = rolename;
	}

	/**
	 * Return the value associated with the column: PRINCIPALTYPE
	 */
	public java.lang.String getPrincipaltype() {
		return principaltype;
	}

	/**
	 * Set the value related to the column: PRINCIPALTYPE
	 * 
	 * @param principaltype
	 *            the PRINCIPALTYPE value
	 */
	public void setPrincipaltype(java.lang.String principaltype) {
		this.principaltype = principaltype;
	}

	/**
	 * Return the value associated with the column: DEFAULTUID
	 */
	public java.lang.String getDefaultuid() {
		return defaultuid;
	}

	/**
	 * Set the value related to the column: DEFAULTUID
	 * 
	 * @param defaultuid
	 *            the DEFAULTUID value
	 */
	public void setDefaultuid(java.lang.String defaultuid) {
		this.defaultuid = defaultuid;
	}

	/**
	 * Return the value associated with the column: WIDGETS
	 */
	public java.lang.String getWidgets() {
		return widgets;
	}

	/**
	 * Set the value related to the column: WIDGETS
	 * 
	 * @param widgets
	 *            the WIDGETS value
	 */
	public void setWidgets(java.lang.String widgets) {
		this.widgets = widgets;
	}

	/**
	 * Return the value associated with the column: LAYOUT
	 */
	public java.lang.String getLayout() {
		return layout;
	}

	/**
	 * Set the value related to the column: LAYOUT
	 * 
	 * @param layout
	 *            the LAYOUT value
	 */
	public void setLayout(java.lang.String layout) {
		this.layout = layout;
	}

	/**
	 * Return the value associated with the column: WIDGETSLASTMODIFIED
	 */
	public java.lang.String getWidgetslastmodified() {
		return widgetslastmodified;
	}

	/**
	 * Set the value related to the column: WIDGETSLASTMODIFIED
	 * 
	 * @param widgetslastmodified
	 *            the WIDGETSLASTMODIFIED value
	 */
	public void setWidgetslastmodified(java.lang.String widgetslastmodified) {
		this.widgetslastmodified = widgetslastmodified;
	}

	/**
	 * Return the value associated with the column: TABNUMBER
	 */
	/*
	 * public java.lang.Integer getTabnumber () { return tabnumber; }
	 */

	/**
	 * Set the value related to the column: TABNUMBER
	 * 
	 * @param tabnumber
	 *            the TABNUMBER value
	 */
	/*
	 * public void setTabnumber (java.lang.Integer tabnumber) { this.tabnumber =
	 * tabnumber; }
	 */

	/**
	 * Return the value associated with the column: DELETEFLAG
	 */
	/*
	 * public java.lang.Integer getDeleteflag () { return deleteflag; }
	 */

	/**
	 * Set the value related to the column: DELETEFLAG
	 * 
	 * @param deleteflag
	 *            the DELETEFLAG value
	 */
	/*
	 * public void setDeleteflag (java.lang.Integer deleteflag) {
	 * this.deleteflag = deleteflag; }
	 */

	/**
	 * Return the value associated with the column: WORKINGUID
	 */
	public java.lang.String getWorkinguid() {
		return workinguid;
	}

	/**
	 * Set the value related to the column: WORKINGUID
	 * 
	 * @param defaultuid
	 *            the WORKINGUID value
	 */
	public void setWorkinguid(java.lang.String workinguid) {
		this.workinguid = workinguid;
	}

	public boolean equals(Object obj) {
		if (null == obj)
			return false;
		if (!(obj instanceof org.infoscoop.dao.model.TabLayout))
			return false;
		else {
			org.infoscoop.dao.model.TabLayout tablayout = (org.infoscoop.dao.model.TabLayout) obj;
			if (null == this.getId() || null == tablayout.getId())
				return false;
			else
				return (this.getId().equals(tablayout.getId()));
		}
	}

	public int hashCode() {
		if (Integer.MIN_VALUE == this.hashCode) {
			if (null == this.getId())
				return super.hashCode();
			else {
				String hashStr = this.getClass().getName() + ":"
						+ this.getId().hashCode();
				this.hashCode = hashStr.hashCode();
			}
		}
		return this.hashCode;
	}

	public String toString() {
		return super.toString();
	}

}
