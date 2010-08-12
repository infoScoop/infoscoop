package org.infoscoop.dao.model.base;

import java.io.Serializable;


/**
 * This is an object that contains data related to the is_menu_items table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="is_menu_items"
 */

public abstract class BaseMenuItem  implements Serializable {

	public static String REF = "MenuItem";
	public static String PROP_PUBLISH = "Publish";
	public static String PROP_PARENT_ID = "ParentId";
	public static String PROP_ALERT = "Alert";
	public static String PROP_TYPE = "Type";
	public static String PROP_ORDER = "Order";
	public static String PROP_ID = "Id";
	public static String PROP_HREF = "Href";
	public static String PROP_TITLE = "Title";


	// constructors
	public BaseMenuItem () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseMenuItem (java.lang.String id) {
		this.setId(id);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BaseMenuItem (
		java.lang.String id,
		java.lang.String title,
		java.lang.Integer order,
		java.lang.Integer publish,
		java.lang.Integer alert) {

		this.setId(id);
		this.setTitle(title);
		this.setOrder(order);
		this.setPublish(publish);
		this.setAlert(alert);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private java.lang.String id;

	// fields
	private java.lang.String title;
	private java.lang.String parentId;
	private java.lang.Integer order;
	private java.lang.String type;
	private java.lang.String href;
	private java.lang.Integer publish;
	private java.lang.Integer alert;



	/**
	 * Return the unique identifier of this class
     * @hibernate.id
     *  column="id"
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
	 * Return the value associated with the column: parent_id
	 */
	public java.lang.String getParentId () {
		return parentId;
	}

	/**
	 * Set the value related to the column: parent_id
	 * @param parentId the parent_id value
	 */
	public void setParentId (java.lang.String parentId) {
		this.parentId = parentId;
	}



	/**
	 * Return the value associated with the column: `order`
	 */
	public java.lang.Integer getOrder () {
		return order;
	}

	/**
	 * Set the value related to the column: `order`
	 * @param order the `order` value
	 */
	public void setOrder (java.lang.Integer order) {
		this.order = order;
	}



	/**
	 * Return the value associated with the column: `type`
	 */
	public java.lang.String getType () {
		return type;
	}

	/**
	 * Set the value related to the column: `type`
	 * @param type the `type` value
	 */
	public void setType (java.lang.String type) {
		this.type = type;
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