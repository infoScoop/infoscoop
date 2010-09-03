package org.infoscoop.dao.model.base;

import java.io.Serializable;


/**
 * This is an object that contains data related to the is_gadget_instance_userprefs table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="is_gadget_instance_userprefs"
 */

public abstract class BaseGadgetInstanceUserpref  implements Serializable,Comparable{

	public static String REF = "GadgetInstanceUserpref";
	public static String PROP_VALUE = "Value";
	public static String PROP_LONG_VALUE = "LongValue";
	public static String PROP_ID = "Id";


	// constructors
	public BaseGadgetInstanceUserpref () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseGadgetInstanceUserpref (org.infoscoop.dao.model.GadgetInstanceUserprefPK id) {
		this.setId(id);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private org.infoscoop.dao.model.GadgetInstanceUserprefPK id;

	// fields
	private java.lang.String value;
	private java.lang.String longValue;



	/**
	 * Return the unique identifier of this class
     * @hibernate.id
     */
	public org.infoscoop.dao.model.GadgetInstanceUserprefPK getId () {
		return id;
	}

	/**
	 * Set the unique identifier of this class
	 * @param id the new ID
	 */
	public void setId (org.infoscoop.dao.model.GadgetInstanceUserprefPK id) {
		this.id = id;
		this.hashCode = Integer.MIN_VALUE;
	}




	/**
	 * Return the value associated with the column: value
	 */
	public java.lang.String getValue () {
		return value;
	}

	/**
	 * Set the value related to the column: value
	 * @param value the value value
	 */
	public void setValue (java.lang.String value) {
		this.value = value;
	}



	/**
	 * Return the value associated with the column: long_value
	 */
	public java.lang.String getLongValue () {
		return longValue;
	}

	/**
	 * Set the value related to the column: long_value
	 * @param longValue the long_value value
	 */
	public void setLongValue (java.lang.String longValue) {
		this.longValue = longValue;
	}




	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof org.infoscoop.dao.model.GadgetInstanceUserpref)) return false;
		else {
			org.infoscoop.dao.model.GadgetInstanceUserpref gadgetInstanceUserpref = (org.infoscoop.dao.model.GadgetInstanceUserpref) obj;
			if (null == this.getId() || null == gadgetInstanceUserpref.getId()) return false;
			else return (this.getId().equals(gadgetInstanceUserpref.getId()));
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


	@Override
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		return arg0.hashCode() - this.hashCode();
	}

	public String toString () {
		return super.toString();
	}


}