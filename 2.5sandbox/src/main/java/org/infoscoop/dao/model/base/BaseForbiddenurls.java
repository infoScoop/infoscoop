package org.infoscoop.dao.model.base;

import java.io.Serializable;


/**
 * This is an object that contains data related to the PORTALADMINS table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="PORTALADMINS"
 */

public abstract class BaseForbiddenurls  implements Serializable {

	public static String REF = "Forbiddenurls";
	public static String PROP_ID = "Id";
	public static String PROP_URL = "Url";


	// constructors
	public BaseForbiddenurls () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseForbiddenurls (java.lang.Long id) {
		this.setId(id);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private Long id;



	/**
	 * Return the unique identifier of this class
     * @hibernate.id
     *  generator-class="sequence"
     *  column="UID"
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

	private String url;
	public String getUrl() {
		return url;
	}
	public void setUrl( String url ) {
		this.url = url;
	}

	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof org.infoscoop.dao.model.Portaladmins)) return false;
		else {
			org.infoscoop.dao.model.Forbiddenurls forbiddenurls = (org.infoscoop.dao.model.Forbiddenurls) obj;
			if (null == this.getId() || null == forbiddenurls.getId()) return false;
			else return (this.getId().equals(forbiddenurls.getId()));
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