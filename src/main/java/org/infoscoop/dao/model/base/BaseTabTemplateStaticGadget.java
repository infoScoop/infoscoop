package org.infoscoop.dao.model.base;

import java.io.Serializable;


/**
 * This is an object that contains data related to the IS_TAB_TEMPLATE_STATIC_GADGETS table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="IS_TAB_TEMPLATE_STATIC_GADGETS"
 */

public abstract class BaseTabTemplateStaticGadget  implements Serializable {

	public static String REF = "TabTemplateStaticGadget";
	public static String PROP_IGNORE_HEADER = "IgnoreHeader";
	public static String PROP_GADGET_INSTANCE = "GadgetInstance";
	public static String PROP_ID = "Id";
	public static String PROP_CONTAINER_ID = "ContainerId";
	public static String PROP_FK_TAB_TEMPLATE = "FkTabTemplate";
	public static String PROP_NO_BORDER = "NoBorder";


	// constructors
	public BaseTabTemplateStaticGadget () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseTabTemplateStaticGadget (java.lang.Integer id) {
		this.setId(id);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BaseTabTemplateStaticGadget (
		java.lang.Integer id,
		org.infoscoop.dao.model.GadgetInstance gadgetInstance,
		java.lang.String containerId) {

		this.setId(id);
		this.setGadgetInstance(gadgetInstance);
		this.setContainerId(containerId);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private java.lang.Integer id;

	// fields
	private java.lang.String containerId;
	private java.lang.Integer ignoreHeader;
	private java.lang.Integer noBorder;

	// many to one
	private org.infoscoop.dao.model.GadgetInstance gadgetInstance;
	private org.infoscoop.dao.model.TabTemplate fkTabTemplate;



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
	 * Return the value associated with the column: container_id
	 */
	public java.lang.String getContainerId () {
		return containerId;
	}

	/**
	 * Set the value related to the column: container_id
	 * @param containerId the container_id value
	 */
	public void setContainerId (java.lang.String containerId) {
		this.containerId = containerId;
	}



	/**
	 * Return the value associated with the column: IGNOREHEADER
	 */
	public java.lang.Integer getIgnoreHeader () {
		return ignoreHeader;
	}

	/**
	 * Set the value related to the column: IGNOREHEADER
	 * @param ignoreHeader the IGNOREHEADER value
	 */
	public void setIgnoreHeader (java.lang.Integer ignoreHeader) {
		this.ignoreHeader = ignoreHeader;
	}



	/**
	 * Return the value associated with the column: NOBORDER
	 */
	public java.lang.Integer getNoBorder () {
		return noBorder;
	}

	/**
	 * Set the value related to the column: NOBORDER
	 * @param noBorder the NOBORDER value
	 */
	public void setNoBorder (java.lang.Integer noBorder) {
		this.noBorder = noBorder;
	}



	/**
	 * Return the value associated with the column: fk_gadget_instance_id
	 */
	public org.infoscoop.dao.model.GadgetInstance getGadgetInstance () {
		return gadgetInstance;
	}

	/**
	 * Set the value related to the column: fk_gadget_instance_id
	 * @param gadgetInstance the fk_gadget_instance_id value
	 */
	public void setGadgetInstance (org.infoscoop.dao.model.GadgetInstance gadgetInstance) {
		this.gadgetInstance = gadgetInstance;
	}



	/**
	 * Return the value associated with the column: fk_tabtemplate_id
	 */
	public org.infoscoop.dao.model.TabTemplate getFkTabTemplate () {
		return fkTabTemplate;
	}

	/**
	 * Set the value related to the column: fk_tabtemplate_id
	 * @param fkTabTemplate the fk_tabtemplate_id value
	 */
	public void setFkTabTemplate (org.infoscoop.dao.model.TabTemplate fkTabTemplate) {
		this.fkTabTemplate = fkTabTemplate;
	}




	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof org.infoscoop.dao.model.TabTemplateStaticGadget)) return false;
		else {
			org.infoscoop.dao.model.TabTemplateStaticGadget tabTemplateStaticGadget = (org.infoscoop.dao.model.TabTemplateStaticGadget) obj;
			if (null == this.getId() || null == tabTemplateStaticGadget.getId()) return false;
			else return (this.getId().equals(tabTemplateStaticGadget.getId()));
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