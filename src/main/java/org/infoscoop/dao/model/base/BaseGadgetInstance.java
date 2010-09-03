package org.infoscoop.dao.model.base;

import java.io.Serializable;


/**
 * This is an object that contains data related to the is_gadget_instances table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="is_gadget_instances"
 */

public abstract class BaseGadgetInstance  implements Serializable {

	public static String REF = "GadgetInstance";
	public static String PROP_TYPE = "Type";
	public static String PROP_ID = "Id";
	public static String PROP_HREF = "Href";
	public static String PROP_TITLE = "Title";


	// constructors
	public BaseGadgetInstance () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseGadgetInstance (java.lang.Integer id) {
		this.setId(id);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BaseGadgetInstance (
		java.lang.Integer id,
		java.lang.String type,
		java.lang.String title) {

		this.setId(id);
		this.setType(type);
		this.setTitle(title);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private java.lang.Integer id;

	// fields
	private java.lang.String type;
	private java.lang.String title;
	private java.lang.String href;

	// collections
	private java.util.Set<org.infoscoop.dao.model.GadgetInstanceUserpref> gadgetInstanceUserPrefs = new java.util.TreeSet<org.infoscoop.dao.model.GadgetInstanceUserpref>();



	/**
	 * Return the unique identifier of this class
     * @hibernate.id
     *  generator-class="native"
     *  column="id"
     */
	public java.lang.Integer getId () {
		return id;
	}

	/**
	 * Set the unique identifier of this class
	 * @param id the new ID
	 */
	public void setId (java.lang.Integer id) {
		this.id = id;
		this.hashCode = Integer.MIN_VALUE;
	}




	/**
	 * Return the value associated with the column: type
	 */
	public java.lang.String getType () {
		return type;
	}

	/**
	 * Set the value related to the column: type
	 * @param type the type value
	 */
	public void setType (java.lang.String type) {
		this.type = type;
	}



	/**
	 * Return the value associated with the column: title
	 */
	public java.lang.String getTitle () {
		return title;
	}

	/**
	 * Set the value related to the column: title
	 * @param title the title value
	 */
	public void setTitle (java.lang.String title) {
		this.title = title;
	}



	/**
	 * Return the value associated with the column: href
	 */
	public java.lang.String getHref () {
		return href;
	}

	/**
	 * Set the value related to the column: href
	 * @param href the href value
	 */
	public void setHref (java.lang.String href) {
		this.href = href;
	}



	/**
	 * Return the value associated with the column: gadgetInstanceUserPrefs
	 */
	public java.util.Set<org.infoscoop.dao.model.GadgetInstanceUserpref> getGadgetInstanceUserPrefs () {
		return gadgetInstanceUserPrefs;
	}

	/**
	 * Set the value related to the column: gadgetInstanceUserPrefs
	 * @param gadgetInstanceUserPrefs the gadgetInstanceUserPrefs value
	 */
	public void setGadgetInstanceUserPrefs (java.util.Set<org.infoscoop.dao.model.GadgetInstanceUserpref> gadgetInstanceUserPrefs) {
		this.gadgetInstanceUserPrefs = gadgetInstanceUserPrefs;
	}

	public void addTogadgetInstanceUserPrefs (org.infoscoop.dao.model.GadgetInstanceUserpref gadgetInstanceUserpref) {
		if (null == getGadgetInstanceUserPrefs()) setGadgetInstanceUserPrefs(new java.util.TreeSet<org.infoscoop.dao.model.GadgetInstanceUserpref>());
		getGadgetInstanceUserPrefs().add(gadgetInstanceUserpref);
	}




	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof org.infoscoop.dao.model.GadgetInstance)) return false;
		else {
			org.infoscoop.dao.model.GadgetInstance gadgetInstance = (org.infoscoop.dao.model.GadgetInstance) obj;
			if (null == this.getId() || null == gadgetInstance.getId()) return false;
			else return (this.getId().equals(gadgetInstance.getId()));
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