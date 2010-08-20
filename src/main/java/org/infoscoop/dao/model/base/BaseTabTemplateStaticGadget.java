package org.infoscoop.dao.model.base;

import java.io.Serializable;


/**
 * This is an object that contains data related to the is_tabtemplate_static_gadgets table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="is_tabtemplate_static_gadgets"
 */

public abstract class BaseTabTemplateStaticGadget  implements Serializable {

	public static String REF = "TabTemplateStaticGadget";
	public static String PROP_ID = "Id";
	public static String PROP_CONTAINER_ID = "ContainerId";
	public static String PROP_FK_TAB_TEMPLATE = "FkTabTemplate";
	public static String PROP_FK_GADGET_INSTANCE = "FkGadgetInstance";


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
		org.infoscoop.dao.model.GadgetInstance fkGadgetInstance,
		java.lang.String containerId) {

		this.setId(id);
		this.setFkGadgetInstance(fkGadgetInstance);
		this.setContainerId(containerId);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private java.lang.Integer id;

	// fields
	private java.lang.String containerId;

	// many to one
	private org.infoscoop.dao.model.GadgetInstance fkGadgetInstance;
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
	 * Return the value associated with the column: fk_gadget_instance_id
	 */
	public org.infoscoop.dao.model.GadgetInstance getFkGadgetInstance () {
		return fkGadgetInstance;
	}

	/**
	 * Set the value related to the column: fk_gadget_instance_id
	 * @param fkGadgetInstance the fk_gadget_instance_id value
	 */
	public void setFkGadgetInstance (org.infoscoop.dao.model.GadgetInstance fkGadgetInstance) {
		this.fkGadgetInstance = fkGadgetInstance;
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