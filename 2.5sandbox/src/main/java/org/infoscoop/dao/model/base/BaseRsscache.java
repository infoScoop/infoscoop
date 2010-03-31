package org.infoscoop.dao.model.base;

import java.io.Serializable;


/**
 * This is an object that contains data related to the RSSCACHE table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="RSSCACHE"
 */

public abstract class BaseRsscache  implements Serializable {

	public static String REF = "Rsscache";
	public static String PROP_RSS = "Rss";
	public static String PROP_ID = "Id";


	// constructors
	public BaseRsscache () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseRsscache (org.infoscoop.dao.model.RSSCACHEPK id) {
		this.setId(id);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private org.infoscoop.dao.model.RSSCACHEPK id;

	// fields
	private byte[] rss;



	/**
	 * Return the unique identifier of this class
     * @hibernate.id
     */
	public org.infoscoop.dao.model.RSSCACHEPK getId () {
		return id;
	}

	/**
	 * Set the unique identifier of this class
	 * @param id the new ID
	 */
	public void setId (org.infoscoop.dao.model.RSSCACHEPK id) {
		this.id = id;
		this.hashCode = Integer.MIN_VALUE;
	}




	/**
	 * Return the value associated with the column: RSS
	 */
	public byte[] getRss () {
		return rss;
	}

	/**
	 * Set the value related to the column: RSS
	 * @param rss the RSS value
	 */
	public void setRss (byte[] rss) {
		this.rss = rss;
	}




	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof org.infoscoop.dao.model.Rsscache)) return false;
		else {
			org.infoscoop.dao.model.Rsscache rsscache = (org.infoscoop.dao.model.Rsscache) obj;
			if (null == this.getId() || null == rsscache.getId()) return false;
			else return (this.getId().equals(rsscache.getId()));
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