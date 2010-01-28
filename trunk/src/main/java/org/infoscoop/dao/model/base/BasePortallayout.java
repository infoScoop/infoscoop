package org.infoscoop.dao.model.base;

import java.io.Serializable;


/**
 * This is an object that contains data related to the PORTALLAYOUT table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="PORTALLAYOUT"
 */

public abstract class BasePortallayout  implements Serializable {

	public static String REF = "Portallayout";
	public static String PROP_NAME = "Name";
	public static String PROP_LAYOUT = "Layout";


	// constructors
	public BasePortallayout () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BasePortallayout (java.lang.String name) {
		this.setName(name);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BasePortallayout (
		java.lang.String name,
		java.lang.String layout) {

		this.setName(name);
		this.setLayout(layout);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private java.lang.String name;

	// fields
	private java.lang.String layout;



	/**
	 * Return the unique identifier of this class
     * @hibernate.id
     *  generator-class="sequence"
     *  column="NAME"
     */
	public java.lang.String getName () {
		return name;
	}

	/**
	 * Set the unique identifier of this class
	 * @param name the new ID
	 */
	public void setName (java.lang.String name) {
		this.name = name;
		this.hashCode = Integer.MIN_VALUE;
	}




	/**
	 * Return the value associated with the column: LAYOUT
	 */
	public java.lang.String getLayout () {
		return layout;
	}

	/**
	 * Set the value related to the column: LAYOUT
	 * @param layout the LAYOUT value
	 */
	public void setLayout (java.lang.String layout) {
		this.layout = layout;
	}




	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof org.infoscoop.dao.model.Portallayout)) return false;
		else {
			org.infoscoop.dao.model.Portallayout portallayout = (org.infoscoop.dao.model.Portallayout) obj;
			if (null == this.getName() || null == portallayout.getName()) return false;
			else return (this.getName().equals(portallayout.getName()));
		}
	}

	public int hashCode () {
		if (Integer.MIN_VALUE == this.hashCode) {
			if (null == this.getName()) return super.hashCode();
			else {
				String hashStr = this.getClass().getName() + ":" + this.getName().hashCode();
				this.hashCode = hashStr.hashCode();
			}
		}
		return this.hashCode;
	}


	public String toString () {
		return super.toString();
	}


}