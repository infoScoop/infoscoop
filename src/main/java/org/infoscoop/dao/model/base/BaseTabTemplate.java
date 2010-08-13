package org.infoscoop.dao.model.base;

import java.io.Serializable;


/**
 * This is an object that contains data related to the is_tabtemplates table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="is_tabtemplates"
 */

public abstract class BaseTabTemplate  implements Serializable {

	public static String REF = "Tabtemplate";
	public static String PROP_ACCESS_LEVEL = "AccessLevel";
	public static String PROP_PUBLISHED = "Published";
	public static String PROP_ID = "Id";
	public static String PROP_TAB_NAME = "TabName";


	// constructors
	public BaseTabTemplate () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseTabTemplate (java.lang.String id) {
		this.setId(id);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BaseTabTemplate (
		java.lang.String id,
		java.lang.String tabName,
		java.lang.Integer published,
		java.lang.Integer accessLevel) {

		this.setId(id);
		this.setTabName(tabName);
		this.setPublished(published);
		this.setAccessLevel(accessLevel);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private java.lang.String id;

	// fields
	private java.lang.String tabName;
	private java.lang.Integer published;
	private java.lang.Integer accessLevel;



	/**
	 * Return the unique identifier of this class
     * @hibernate.id
     *  column="tab_id"
     */
	public java.lang.String getId () {
		return id;
	}

	/**
	 * Set the unique identifier of this class
	 * @param id the new ID
	 */
	public void setId (java.lang.String id) {
		this.id = id;
		this.hashCode = Integer.MIN_VALUE;
	}




	/**
	 * Return the value associated with the column: tab_name
	 */
	public java.lang.String getTabName () {
		return tabName;
	}

	/**
	 * Set the value related to the column: tab_name
	 * @param tabName the tab_name value
	 */
	public void setTabName (java.lang.String tabName) {
		this.tabName = tabName;
	}



	/**
	 * Return the value associated with the column: published
	 */
	public java.lang.Integer getPublished () {
		return published;
	}

	/**
	 * Set the value related to the column: published
	 * @param published the published value
	 */
	public void setPublished (java.lang.Integer published) {
		this.published = published;
	}



	/**
	 * Return the value associated with the column: access_level
	 */
	public java.lang.Integer getAccessLevel () {
		return accessLevel;
	}

	/**
	 * Set the value related to the column: access_level
	 * @param accessLevel the access_level value
	 */
	public void setAccessLevel (java.lang.Integer accessLevel) {
		this.accessLevel = accessLevel;
	}




	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof org.infoscoop.dao.model.TabTemplate)) return false;
		else {
			org.infoscoop.dao.model.TabTemplate tabtemplate = (org.infoscoop.dao.model.TabTemplate) obj;
			if (null == this.getId() || null == tabtemplate.getId()) return false;
			else return (this.getId().equals(tabtemplate.getId()));
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