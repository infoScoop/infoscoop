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

import org.infoscoop.dao.model.CachePK;


/**
 * This is an object that contains data related to the CACHE table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="CACHE"
 */

public abstract class BaseCache  implements Serializable {

	public static String REF = "Cache";
	public static String PROP_URL_KEY = "UrlKey";
	public static String PROP_URL = "Url";
	public static String PROP_BODY = "Body";
	public static String PROP_TIMESTAMP = "Timestamp";
	public static String PROP_ID = "Id";
	public static String PROP_HEADERS = "Headers";
	public static String PROP_UID = "Uid";


	// constructors
	public BaseCache () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseCache (CachePK id) {
		this.setId(id);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BaseCache (
		CachePK id,
		java.lang.String uid,
		java.lang.String url,
		java.lang.String urlKey,
		java.util.Date timestamp,
		java.lang.String headers,
		java.lang.String body) {

		this.setId(id);
		this.setUid(uid);
		this.setUrl(url);
		this.setUrlKey(urlKey);
		this.setTimestamp(timestamp);
		this.setHeaders(headers);
		this.setBody(body);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private CachePK id;

	// fields
	private java.lang.String uid;
	private java.lang.String url;
	private java.lang.String urlKey;
	private java.util.Date timestamp;
	private java.lang.String headers;
	private java.lang.String body;



	/**
	 * Return the unique identifier of this class
     * @hibernate.id
     *  generator-class="sequence"
     *  column="ID"
     */
	public CachePK getId () {
		return id;
	}

	/**
	 * Set the unique identifier of this class
	 * @param id the new ID
	 */
	public void setId (CachePK id) {
		this.id = id;
		this.hashCode = Integer.MIN_VALUE;
	}




	/**
	 * Return the value associated with the column: UID
	 */
	public java.lang.String getUid () {
		return uid;
	}

	/**
	 * Set the value related to the column: UID
	 * @param uid the UID value
	 */
	public void setUid (java.lang.String uid) {
		this.uid = uid;
	}



	/**
	 * Return the value associated with the column: URL
	 */
	public java.lang.String getUrl () {
		return url;
	}

	/**
	 * Set the value related to the column: URL
	 * @param url the URL value
	 */
	public void setUrl (java.lang.String url) {
		this.url = url;
	}



	/**
	 * Return the value associated with the column: URL_KEY
	 */
	public java.lang.String getUrlKey () {
		return urlKey;
	}

	/**
	 * Set the value related to the column: URL_KEY
	 * @param urlKey the URL_KEY value
	 */
	public void setUrlKey (java.lang.String urlKey) {
		this.urlKey = urlKey;
	}



	/**
	 * Return the value associated with the column: TIMESTAMP
	 */
	public java.util.Date getTimestamp () {
		return timestamp;
	}

	/**
	 * Set the value related to the column: TIMESTAMP
	 * @param timestamp the TIMESTAMP value
	 */
	public void setTimestamp (java.util.Date timestamp) {
		this.timestamp = timestamp;
	}



	/**
	 * Return the value associated with the column: HEADERS
	 */
	public java.lang.String getHeaders () {
		return headers;
	}

	/**
	 * Set the value related to the column: HEADERS
	 * @param headers the HEADERS value
	 */
	public void setHeaders (java.lang.String headers) {
		this.headers = headers;
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




	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof org.infoscoop.dao.model.Cache)) return false;
		else {
			org.infoscoop.dao.model.Cache cache = (org.infoscoop.dao.model.Cache) obj;
			if (null == this.getId() || null == cache.getId()) return false;
			else return (this.getId().equals(cache.getId()));
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
