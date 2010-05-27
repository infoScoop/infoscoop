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

public abstract class BaseMessage  implements Serializable {

	public static String REF = "Message";
	public static String PROP_BODY = "Body";
	public static String PROP_DISPLAYFROM = "Displayfrom";
	public static String PROP_OPTION = "Option";
	public static String PROP_TYPE = "Type";
	public static String PROP_TO = "To";
	public static String PROP_TOJSON = "Tojson";
	public static String PROP_ID = "Id";
	public static String PROP_POSTED_TIME = "PostedTime";
	public static String PROP_FROM = "From";


	// constructors
	public BaseMessage () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseMessage (java.lang.Long id) {
		this.setId(id);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BaseMessage (
		java.lang.Long id,
		java.lang.String from,
		java.util.Date postedTime,
		java.lang.String type) {

		this.setId(id);
		this.setFrom(from);
		this.setPostedTime(postedTime);
		this.setType(type);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private java.lang.Long id;

	// fields
	private java.lang.String from;
	private java.lang.String displayfrom;
	private java.lang.String to;
	private java.lang.String tojson;
	private java.lang.String body;
	private java.util.Date postedTime;
	private java.lang.String type;
	private java.lang.String option;



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
	 * Return the value associated with the column: FROM
	 */
	public java.lang.String getFrom () {
		return from;
	}

	/**
	 * Set the value related to the column: FROM
	 * @param from the FROM value
	 */
	public void setFrom (java.lang.String from) {
		this.from = from;
	}



	/**
	 * Return the value associated with the column: DISPLAYFROM
	 */
	public java.lang.String getDisplayfrom () {
		return displayfrom;
	}

	/**
	 * Set the value related to the column: DISPLAYFROM
	 * @param displayfrom the DISPLAYFROM value
	 */
	public void setDisplayfrom (java.lang.String displayfrom) {
		this.displayfrom = displayfrom;
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
	 * Return the value associated with the column: TOJSON
	 */
	public java.lang.String getTojson () {
		return tojson;
	}

	/**
	 * Set the value related to the column: TOJSON
	 * @param tojson the TOJSON value
	 */
	public void setTojson (java.lang.String tojson) {
		this.tojson = tojson;
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
	 * Return the value associated with the column: POSTED_TIME
	 */
	public java.util.Date getPostedTime () {
		return postedTime;
	}

	/**
	 * Set the value related to the column: POSTED_TIME
	 * @param postedTime the POSTED_TIME value
	 */
	public void setPostedTime (java.util.Date postedTime) {
		this.postedTime = postedTime;
	}



	/**
	 * Return the value associated with the column: TYPE
	 */
	public java.lang.String getType () {
		return type;
	}

	/**
	 * Set the value related to the column: TYPE
	 * @param type the TYPE value
	 */
	public void setType (java.lang.String type) {
		this.type = type;
	}



	/**
	 * Return the value associated with the column: OPTION
	 */
	public java.lang.String getOption () {
		return option;
	}

	/**
	 * Set the value related to the column: OPTION
	 * @param option the OPTION value
	 */
	public void setOption (java.lang.String option) {
		this.option = option;
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
