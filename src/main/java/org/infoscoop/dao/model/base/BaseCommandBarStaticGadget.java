package org.infoscoop.dao.model.base;

import java.io.Serializable;


/**
 * This is an object that contains data related to the is_command_bar_static_gadgets table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="is_command_bar_static_gadgets"
 */

public abstract class BaseCommandBarStaticGadget  implements Serializable {

	public static String REF = "CommandBarStaticGadget";
	public static String PROP_FK_COMMAND_BAR = "FkCommandBar";
	public static String PROP_GADGET_INSTANCE = "GadgetInstance";
	public static String PROP_ID = "Id";
	public static String PROP_CONTAINER_ID = "ContainerId";


	// constructors
	public BaseCommandBarStaticGadget () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseCommandBarStaticGadget (java.lang.Integer id) {
		this.setId(id);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BaseCommandBarStaticGadget (
		java.lang.Integer id,
		org.infoscoop.dao.model.GadgetInstance gadgetInstance,
		org.infoscoop.dao.model.CommandBar fkCommandBar,
		java.lang.String containerId) {

		this.setId(id);
		this.setGadgetInstance(gadgetInstance);
		this.setFkCommandBar(fkCommandBar);
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
	private org.infoscoop.dao.model.GadgetInstance gadgetInstance;
	private org.infoscoop.dao.model.CommandBar fkCommandBar;



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
	 * Return the value associated with the column: fk_command_bar_id
	 */
	public org.infoscoop.dao.model.CommandBar getFkCommandBar () {
		return fkCommandBar;
	}

	/**
	 * Set the value related to the column: fk_command_bar_id
	 * @param fkCommandBar the fk_command_bar_id value
	 */
	public void setFkCommandBar (org.infoscoop.dao.model.CommandBar fkCommandBar) {
		this.fkCommandBar = fkCommandBar;
	}




	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof org.infoscoop.dao.model.CommandBarStaticGadget)) return false;
		else {
			org.infoscoop.dao.model.CommandBarStaticGadget commandBarStaticGadget = (org.infoscoop.dao.model.CommandBarStaticGadget) obj;
			if (null == this.getId() || null == commandBarStaticGadget.getId()) return false;
			else return (this.getId().equals(commandBarStaticGadget.getId()));
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