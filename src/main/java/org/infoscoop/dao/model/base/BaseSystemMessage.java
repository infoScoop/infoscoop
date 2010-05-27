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


/**
 * This is an object that contains data related to the IS_MESSAGES table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="IS_MESSAGES"
 */

public abstract class BaseSystemMessage  implements Serializable {

	public static String REF = "SystemMessage";
	public static String PROP_ID = "Id";
	public static String PROP_TO = "To";
	public static String PROP_BODY = "Body";
	public static String PROP_RESOURCEID = "ResourceId";
	public static String PROP_REPLACEVALUES = "ReplaceValues";
	public static String PROP_ISREAD = "IsRead";


	// constructors
	public BaseSystemMessage () {
		initialize();
	}

	public BaseSystemMessage (String to, String resourceId, String replaceValues) {
		this.to = to;
		this.resourceId = resourceId;
		this.replaceValues = replaceValues;
		initialize();
	}

	public BaseSystemMessage (String to, String body) {
		this.to = to;
		this.body = body;
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseSystemMessage (java.lang.Long id) {
		this.setId(id);
		initialize();
	}

	protected void initialize () {
		this.isRead = 0;
	}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private java.lang.Long id;

	// fields
	private java.lang.String to;
	private java.lang.String body;
	private java.lang.String resourceId;
	private java.lang.String replaceValues;
	private java.lang.Integer isRead ;



	/**
	 * Return the unique identifier of this class
     * @hibernate.id
     *  generator-class="native"
     *  column="ID"
     */
	public java.lang.Long getId () {
		return id;
	}

	/**
	 * Set the unique identifier of this class
	 * @param id the new ID
	 */
	public void setId (java.lang.Long id) {
		this.id = id;
		this.hashCode = Integer.MIN_VALUE;
	}



	/**
	 * Return the value associated with the column: TO
	 */
	public java.lang.String getTo () {
		return to;
	}

	/**
	 * Set the value related to the column: TO
	 * @param to the TO value
	 */
	public void setTo (java.lang.String to) {
		this.to = to;
	}



	/**
	 * Return the value associated with the column: BODY
	 */
	public java.lang.String getBody () {
		return body;
	}

	/**
	 * Set the value related to the column: BODY
	 * @param body the BODY value
	 */
	public void setBody (java.lang.String body) {
		this.body = body;
	}



	/**
	 * Return the value associated with the column: RESOURCEID
	 */
	public java.lang.String getResourceId() {
		return resourceId;
	}

	/**
	 * Set the value related to the column: RESOURCEID
	 * @param body the RESOURCEID value
	 */
	public void setResourceId(java.lang.String resourceId) {
		this.resourceId = resourceId;
	}



	/**
	 * Return the value associated with the column: REPLACEVALUES
	 */
	public java.lang.String getReplaceValues() {
		return replaceValues;
	}

	/**
	 * Set the value related to the column: REPLACEVALUES
	 * @param body the REPLACEVALUES value
	 */
	public void setReplaceValues(java.lang.String replaceValues) {
		this.replaceValues = replaceValues;
	}

	/**
	 * Return the value associated with the column: ISREAD
	 */
	public java.lang.Integer getIsRead() {
		return isRead;
	}

	/**
	 * Set the value related to the column: ISREAD
	 * @param body the ISREAD value
	 */
	public void setIsRead(java.lang.Integer isRead) {
		this.isRead = isRead;
	}

	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof org.infoscoop.dao.model.Message)) return false;
		else {
			org.infoscoop.dao.model.Message message = (org.infoscoop.dao.model.Message) obj;
			if (null == this.getId() || null == message.getId()) return false;
			else return (this.getId().equals(message.getId()));
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


	public String toString () {
		return super.toString();
	}


}
