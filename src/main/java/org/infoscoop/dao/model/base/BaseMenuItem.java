package org.infoscoop.dao.model.base;

import java.io.Serializable;


/**
 * This is an object that contains data related to the IS_MENU_ITEMS table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="IS_MENU_ITEMS"
 */

public abstract class BaseMenuItem  implements Serializable {

	public static String REF = "MenuItem";
	public static String PROP_PUBLISH = "Publish";
	public static String PROP_ALERT = "Alert";
	public static String PROP_GADGET_INSTANCE = "GadgetInstance";
	public static String PROP_FK_MENU_TREE = "FkMenuTree";
	public static String PROP_MENU_ORDER = "MenuOrder";
	public static String PROP_ID = "Id";
	public static String PROP_HREF = "Href";
	public static String PROP_FK_PARENT = "FkParent";
	public static String PROP_MENU_ID = "MenuId";
	public static String PROP_TITLE = "Title";
	public static String PROP_FK_DOMAIN_ID = "FkDomainId";


	// constructors
	public BaseMenuItem () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseMenuItem (java.lang.Integer id) {
		this.setId(id);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BaseMenuItem (
		java.lang.Integer id,
		org.infoscoop.dao.model.MenuTree fkMenuTree,
		java.lang.String menuId,
		java.lang.String title,
		java.lang.Integer menuOrder,
		java.lang.Integer publish,
		java.lang.Integer alert) {

		this.setId(id);
		this.setFkMenuTree(fkMenuTree);
		this.setMenuId(menuId);
		this.setTitle(title);
		this.setMenuOrder(menuOrder);
		this.setPublish(publish);
		this.setAlert(alert);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private java.lang.Integer id;

	// fields
	private java.lang.String menuId;
	private java.lang.Integer fkDomainId;
	private java.lang.String title;
	private java.lang.Integer menuOrder;
	private java.lang.String href;
	private java.lang.Integer publish;
	private java.lang.Integer alert;

	// many to one
	private org.infoscoop.dao.model.MenuItem fkParent;
	private org.infoscoop.dao.model.GadgetInstance gadgetInstance;
	private org.infoscoop.dao.model.MenuTree fkMenuTree;

	// collections
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
	 * Return the value associated with the column: menu_id
	 */
	public java.lang.String getMenuId () {
		return menuId;
	}

	/**
	 * Set the value related to the column: menu_id
	 * @param menuId the menu_id value
	 */
	public void setMenuId (java.lang.String menuId) {
		this.menuId = menuId;
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
	 * Return the value associated with the column: menu_order
	 */
	public java.lang.Integer getMenuOrder () {
		return menuOrder;
	}

	/**
	 * Set the value related to the column: menu_order
	 * @param menuOrder the menu_order value
	 */
	public void setMenuOrder (java.lang.Integer menuOrder) {
		this.menuOrder = menuOrder;
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
	 * Return the value associated with the column: publish
	 */
	public java.lang.Integer getPublish () {
		return publish;
	}

	/**
	 * Set the value related to the column: publish
	 * @param publish the publish value
	 */
	public void setPublish (java.lang.Integer publish) {
		this.publish = publish;
	}



	/**
	 * Return the value associated with the column: alert
	 */
	public java.lang.Integer getAlert () {
		return alert;
	}

	/**
	 * Set the value related to the column: alert
	 * @param alert the alert value
	 */
	public void setAlert (java.lang.Integer alert) {
		this.alert = alert;
	}



	/**
	 * Return the value associated with the column: fk_parent_id
	 */
	public org.infoscoop.dao.model.MenuItem getFkParent () {
		return fkParent;
	}

	/**
	 * Set the value related to the column: fk_parent_id
	 * @param fkParent the fk_parent_id value
	 */
	public void setFkParent (org.infoscoop.dao.model.MenuItem fkParent) {
		this.fkParent = fkParent;
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
		if (!(obj instanceof org.infoscoop.dao.model.MenuItem)) return false;
		else {
			org.infoscoop.dao.model.MenuItem menuItem = (org.infoscoop.dao.model.MenuItem) obj;
			if (null == this.getId() || null == menuItem.getId()) return false;
			else return (this.getId().equals(menuItem.getId()));
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