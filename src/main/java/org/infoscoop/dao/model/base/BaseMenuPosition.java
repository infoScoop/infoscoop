package org.infoscoop.dao.model.base;

import java.io.Serializable;


/**
 * This is an object that contains data related to the IS_MENU_POSITIONS table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="IS_MENU_POSITIONS"
 */

public abstract class BaseMenuPosition  implements Serializable {

	public static String REF = "MenuPosition";
	public static String PROP_FK_MENU_TREE = "FkMenuTree";
	public static String PROP_ID = "Id";


	// constructors
	public BaseMenuPosition () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseMenuPosition (java.lang.String id) {
		this.setId(id);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BaseMenuPosition (
		java.lang.String id,
		org.infoscoop.dao.model.MenuTree fkMenuTree) {

		this.setId(id);
		this.setFkMenuTree(fkMenuTree);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private java.lang.String id;

	// many to one
	private org.infoscoop.dao.model.MenuTree fkMenuTree;



	/**
	 * Return the unique identifier of this class
     * @hibernate.id
     *  column="position"
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
	 * Return the value associated with the column: fk_menu_tree_id
	 */
	public org.infoscoop.dao.model.MenuTree getFkMenuTree () {
		return fkMenuTree;
	}

	/**
	 * Set the value related to the column: fk_menu_tree_id
	 * @param fkMenuTree the fk_menu_tree_id value
	 */
	public void setFkMenuTree (org.infoscoop.dao.model.MenuTree fkMenuTree) {
		this.fkMenuTree = fkMenuTree;
	}




	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof org.infoscoop.dao.model.MenuPosition)) return false;
		else {
			org.infoscoop.dao.model.MenuPosition menuPosition = (org.infoscoop.dao.model.MenuPosition) obj;
			if (null == this.getId() || null == menuPosition.getId()) return false;
			else return (this.getId().equals(menuPosition.getId()));
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