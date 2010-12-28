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
	public static String PROP_LANG = "Lang";
	public static String PROP_PUBLISH = "Publish";
	public static String PROP_TOP = "Top";
	public static String PROP_DESCRIPTION = "Description";
	public static String PROP_ALERT = "Alert";
	public static String PROP_ORDER_INDEX = "OrderIndex";
	public static String PROP_SIDE = "Side";
	public static String PROP_ID = "Id";
	public static String PROP_HREF = "Href";
	public static String PROP_COUNTRY = "Country";
	public static String PROP_TITLE = "Title";
	public static String PROP_FK_DOMAIN_ID = "FkDomainId";


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
		java.lang.String title,
		java.lang.Integer orderIndex,
		java.lang.Integer publish,
		java.lang.Integer alert,
		java.lang.String country,
		java.lang.String lang,
		java.lang.Integer top,
		java.lang.Integer side) {

		this.setId(id);
		this.setTitle(title);
		this.setOrderIndex(orderIndex);
		this.setPublish(publish);
		this.setAlert(alert);
		this.setCountry(country);
		this.setLang(lang);
		this.setTop(top);
		this.setSide(side);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private java.lang.Integer id;

	// fields
	private java.lang.Integer fkDomainId;
	private java.lang.String title;
	private java.lang.String description;
	private java.lang.Integer orderIndex;
	private java.lang.String href;
	private java.lang.Integer publish;
	private java.lang.Integer alert;
	private java.lang.String country;
	private java.lang.String lang;
	private java.lang.Integer top;
	private java.lang.Integer side;

	// collections
	private java.util.Set<org.infoscoop.dao.model.MenuItem> menuItems;
	private java.util.Set<org.infoscoop.dao.model.Role> roles;



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
	 * Return the value associated with the column: description
	 */
	public java.lang.String getDescription () {
		return description;
	}

	/**
	 * Set the value related to the column: description
	 * @param description the description value
	 */
	public void setDescription (java.lang.String description) {
		this.description = description;
	}



	/**
	 * Return the value associated with the column: order_index
	 */
	public java.lang.Integer getOrderIndex () {
		return orderIndex;
	}

	/**
	 * Set the value related to the column: order_index
	 * @param orderIndex the order_index value
	 */
	public void setOrderIndex (java.lang.Integer orderIndex) {
		this.orderIndex = orderIndex;
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
	 * Return the value associated with the column: country
	 */
	public java.lang.String getCountry () {
		return country;
	}

	/**
	 * Set the value related to the column: country
	 * @param country the country value
	 */
	public void setCountry (java.lang.String country) {
		this.country = country;
	}



	/**
	 * Return the value associated with the column: lang
	 */
	public java.lang.String getLang () {
		return lang;
	}

	/**
	 * Set the value related to the column: lang
	 * @param lang the lang value
	 */
	public void setLang (java.lang.String lang) {
		this.lang = lang;
	}



	/**
	 * Return the value associated with the column: top
	 */
	public java.lang.Integer getTop () {
		return top;
	}

	/**
	 * Set the value related to the column: top
	 * @param top the top value
	 */
	public void setTop (java.lang.Integer top) {
		this.top = top;
	}



	/**
	 * Return the value associated with the column: side
	 */
	public java.lang.Integer getSide () {
		return side;
	}

	/**
	 * Set the value related to the column: side
	 * @param side the side value
	 */
	public void setSide (java.lang.Integer side) {
		this.side = side;
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



	/**
	 * Return the value associated with the column: Roles
	 */
	public java.util.Set<org.infoscoop.dao.model.Role> getRoles () {
		return roles;
	}

	/**
	 * Set the value related to the column: Roles
	 * @param roles the Roles value
	 */
	public void setRoles (java.util.Set<org.infoscoop.dao.model.Role> roles) {
		this.roles = roles;
	}

	public void addToRoles (org.infoscoop.dao.model.Role role) {
		if (null == getRoles()) setRoles(new java.util.TreeSet<org.infoscoop.dao.model.Role>());
		getRoles().add(role);
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