package org.infoscoop.dao.model.base;

import java.io.Serializable;


/**
 * This is an object that contains data related to the PROXYCONF table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="PROXYCONF"
 */

public abstract class BaseProxyconf  implements Serializable {

	public static String REF = "Proxyconf";
	public static String PROP_LASTMODIFIED = "Lastmodified";
	public static String PROP_DATA = "Data";
	public static String PROP_TEMP = "Temp";


	// constructors
	public BaseProxyconf () {
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BaseProxyconf (
		java.lang.Integer temp,
		java.lang.String data) {

		this.setTemp(temp);
		this.setData(data);
		initialize();
	}

	protected void initialize () {}



	// fields
	private java.lang.Integer temp;
	private java.lang.String data;
	private java.util.Date lastmodified;






	/**
	 * Return the value associated with the column: TEMP
	 */
	public java.lang.Integer getTemp () {
		return temp;
	}

	/**
	 * Set the value related to the column: TEMP
	 * @param temp the TEMP value
	 */
	public void setTemp (java.lang.Integer temp) {
		this.temp = temp;
	}



	/**
	 * Return the value associated with the column: DATA
	 */
	public java.lang.String getData () {
		return data;
	}

	/**
	 * Set the value related to the column: DATA
	 * @param data the DATA value
	 */
	public void setData (java.lang.String data) {
		this.data = data;
	}



	/**
	 * Return the value associated with the column: LASTMODIFIED
	 */
	public java.util.Date getLastmodified () {
		return lastmodified;
	}

	/**
	 * Set the value related to the column: LASTMODIFIED
	 * @param lastmodified the LASTMODIFIED value
	 */
	public void setLastmodified (java.util.Date lastmodified) {
		this.lastmodified = lastmodified;
	}







	public String toString () {
		return super.toString();
	}


}