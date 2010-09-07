package org.infoscoop.dao.model.base;

import java.io.Serializable;


/**
 * This is an object that contains data related to the IS_MENU_TREES table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="IS_MENU_TREES"
 */

public abstract class BaseMenuTree  implements Serializable {

	public static String REF = "MenuTree";
	public static String PROP_ID = "Id";
	public static String PROP_TITLE = "Title";


	// constructors
	public BaseMenuTree () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseMenuTree (java.lang.Integer id) {
		this.setId(id);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BaseMenuTree (
		java.lang.Integer id,
		java.lang.String title) {

		this.setId(id);
		this.setTitle(title);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private java.lang.Integer id;

	// fields
	private java.lang.String title;

	// collections
	private java.util.Set<org.infoscoop.dao.model.MenuPosition> menuPositions;
	private java.util.Set<org.infoscoop.dao.model.MenuItem> menuItems;



	/**
	 * Return the unique identifier of this class
     * @hibernate.id
     *  generator-class="native"
     *  column="ID"
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
	 * Return the value associated with the column: MenuPositions
	 */
	public java.util.Set<org.infoscoop.dao.model.MenuPosition> getMenuPositions () {
		return menuPositions;
	}

	/**
	 * Set the value related to the column: MenuPositions
	 * @param menuPositions the MenuPositions value
	 */
	public void setMenuPositions (java.util.Set<org.infoscoop.dao.model.MenuPosition> menuPositions) {
		this.menuPositions = menuPositions;
	}

	public void addToMenuPositions (org.infoscoop.dao.model.MenuPosition menuPosition) {
		if (null == getMenuPositions()) setMenuPositions(new java.util.TreeSet<org.infoscoop.dao.model.MenuPosition>());
		getMenuPositions().add(menuPosition);
	}



	/**
	 * Return the value associated with the column: MenuItems
	 */
	public java.util.Set<org.infoscoop.dao.model.MenuItem> getMenuItems () {
		return menuItems;
	}

	/**
	 * Set the value related to the column: MenuItems
	 * @param menuItems the MenuItems value
	 */
	public void setMenuItems (java.util.Set<org.infoscoop.dao.model.MenuItem> menuItems) {
		this.menuItems = menuItems;
	}

	public void addToMenuItems (org.infoscoop.dao.model.MenuItem menuItem) {
		if (null == getMenuItems()) setMenuItems(new java.util.TreeSet<org.infoscoop.dao.model.MenuItem>());
		getMenuItems().add(menuItem);
	}




	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof org.infoscoop.dao.model.MenuTree)) return false;
		else {
			org.infoscoop.dao.model.MenuTree menuTree = (org.infoscoop.dao.model.MenuTree) obj;
			if (null == this.getId() || null == menuTree.getId()) return false;
			else return (this.getId().equals(menuTree.getId()));
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