package org.infoscoop.dao.model.base;

import java.io.Serializable;


/**
 * This is an object that contains data related to the IS_COMMAND_BARS table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="IS_COMMAND_BARS"
 */

public abstract class BaseCommandBar  implements Serializable {

	public static String REF = "CommandBar";
	public static String PROP_ACCESS_LEVEL = "AccessLevel";
	public static String PROP_DISPLAY_ORDER = "DisplayOrder";
	public static String PROP_ID = "Id";
	public static String PROP_FK_DOMAIN_ID = "FkDomainId";


	// constructors
	public BaseCommandBar () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseCommandBar (java.lang.Integer id) {
		this.setId(id);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BaseCommandBar (
		java.lang.Integer id,
		java.lang.Integer displayOrder,
		java.lang.String accessLevel) {

		this.setId(id);
		this.setDisplayOrder(displayOrder);
		this.setAccessLevel(accessLevel);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private java.lang.Integer id;

	// fields
	private java.lang.Integer fkDomainId;
	private java.lang.Integer displayOrder;
	private java.lang.String accessLevel;

	// collections
	private java.util.Set<org.infoscoop.dao.model.CommandBarStaticGadget> commandBarStaticGadgets;



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
	 * Return the value associated with the column: fk_domain_id
	 */
	public java.lang.Integer getFkDomainId () {
		return fkDomainId;
	}

	/**
	 * Set the value related to the column: fk_domain_id
	 * @param fkDomainId the fk_domain_id value
	 */
	public void setFkDomainId (java.lang.Integer fkDomainId) {
		this.fkDomainId = fkDomainId;
	}



	/**
	 * Return the value associated with the column: display_order
	 */
	public java.lang.Integer getDisplayOrder () {
		return displayOrder;
	}

	/**
	 * Set the value related to the column: display_order
	 * @param displayOrder the display_order value
	 */
	public void setDisplayOrder (java.lang.Integer displayOrder) {
		this.displayOrder = displayOrder;
	}



	/**
	 * Return the value associated with the column: access_level
	 */
	public java.lang.String getAccessLevel () {
		return accessLevel;
	}

	/**
	 * Set the value related to the column: access_level
	 * @param accessLevel the access_level value
	 */
	public void setAccessLevel (java.lang.String accessLevel) {
		this.accessLevel = accessLevel;
	}



	/**
	 * Return the value associated with the column: CommandBarStaticGadgets
	 */
	public java.util.Set<org.infoscoop.dao.model.CommandBarStaticGadget> getCommandBarStaticGadgets () {
		return commandBarStaticGadgets;
	}

	/**
	 * Set the value related to the column: CommandBarStaticGadgets
	 * @param commandBarStaticGadgets the CommandBarStaticGadgets value
	 */
	public void setCommandBarStaticGadgets (java.util.Set<org.infoscoop.dao.model.CommandBarStaticGadget> commandBarStaticGadgets) {
		this.commandBarStaticGadgets = commandBarStaticGadgets;
	}

	public void addToCommandBarStaticGadgets (org.infoscoop.dao.model.CommandBarStaticGadget commandBarStaticGadget) {
		if (null == getCommandBarStaticGadgets()) setCommandBarStaticGadgets(new java.util.TreeSet<org.infoscoop.dao.model.CommandBarStaticGadget>());
		getCommandBarStaticGadgets().add(commandBarStaticGadget);
	}




	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof org.infoscoop.dao.model.CommandBar)) return false;
		else {
			org.infoscoop.dao.model.CommandBar commandBar = (org.infoscoop.dao.model.CommandBar) obj;
			if (null == this.getId() || null == commandBar.getId()) return false;
			else return (this.getId().equals(commandBar.getId()));
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