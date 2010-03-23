package org.infoscoop.dao.model.base;

import java.io.Serializable;


/**
 * This is an object that contains data related to the IS_GADGET_ICONS table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="IS_GADGET_ICONS"
 */

public abstract class BaseGadgetIcon  implements Serializable {

	public static String REF = "GadgetIcon";
	public static String PROP_URL = "Url";
	public static String PROP_TYPE = "Type";


	// constructors
	public BaseGadgetIcon () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseGadgetIcon (java.lang.String type) {
		this.setType(type);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BaseGadgetIcon (
		java.lang.String type,
		java.lang.String url) {

		this.setType(type);
		this.setUrl(url);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private java.lang.String type;

	// fields
	private java.lang.String url;



	/**
	 * Return the unique identifier of this class
     * @hibernate.id
     *  generator-class="assigned"
     *  column="TYPE"
     */
	public java.lang.String getType () {
		return type;
	}

	/**
	 * Set the unique identifier of this class
	 * @param type the new ID
	 */
	public void setType (java.lang.String type) {
		this.type = type;
		this.hashCode = Integer.MIN_VALUE;
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




	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof org.infoscoop.dao.model.GadgetIcon)) return false;
		else {
			org.infoscoop.dao.model.GadgetIcon gadgetIcon = (org.infoscoop.dao.model.GadgetIcon) obj;
			if (null == this.getType() || null == gadgetIcon.getType()) return false;
			else return (this.getType().equals(gadgetIcon.getType()));
		}
	}

	public int hashCode () {
		if (Integer.MIN_VALUE == this.hashCode) {
			if (null == this.getType()) return super.hashCode();
			else {
				String hashStr = this.getClass().getName() + ":" + this.getType().hashCode();
				this.hashCode = hashStr.hashCode();
			}
		}
		return this.hashCode;
	}


	public String toString () {
		return super.toString();
	}


}