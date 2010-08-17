package org.infoscoop.dao.model.base;

import java.io.Serializable;


public abstract class BaseMenuUserprefsPK implements Serializable {

	protected int hashCode = Integer.MIN_VALUE;

	private java.lang.String menuItemId;
	private java.lang.String name;


	public BaseMenuUserprefsPK () {}
	
	public BaseMenuUserprefsPK (
		java.lang.String menuItemId,
		java.lang.String name) {

		this.setMenuItemId(menuItemId);
		this.setName(name);
	}


	/**
	 * Return the value associated with the column: fk_menu_item_id
	 */
	public java.lang.String getMenuItemId () {
		return menuItemId;
	}

	/**
	 * Set the value related to the column: fk_menu_item_id
	 * @param menuItemId the fk_menu_item_id value
	 */
	public void setMenuItemId (java.lang.String menuItemId) {
		this.menuItemId = menuItemId;
	}



	/**
	 * Return the value associated with the column: name
	 */
	public java.lang.String getName () {
		return name;
	}

	/**
	 * Set the value related to the column: name
	 * @param name the name value
	 */
	public void setName (java.lang.String name) {
		this.name = name;
	}




	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof org.infoscoop.dao.model.MenuUserprefsPK)) return false;
		else {
			org.infoscoop.dao.model.MenuUserprefsPK mObj = (org.infoscoop.dao.model.MenuUserprefsPK) obj;
			if (null != this.getMenuItemId() && null != mObj.getMenuItemId()) {
				if (!this.getMenuItemId().equals(mObj.getMenuItemId())) {
					return false;
				}
			}
			else {
				return false;
			}
			if (null != this.getName() && null != mObj.getName()) {
				if (!this.getName().equals(mObj.getName())) {
					return false;
				}
			}
			else {
				return false;
			}
			return true;
		}
	}

	public int hashCode () {
		if (Integer.MIN_VALUE == this.hashCode) {
			StringBuilder sb = new StringBuilder();
			if (null != this.getMenuItemId()) {
				sb.append(this.getMenuItemId().hashCode());
				sb.append(":");
			}
			else {
				return super.hashCode();
			}
			if (null != this.getName()) {
				sb.append(this.getName().hashCode());
				sb.append(":");
			}
			else {
				return super.hashCode();
			}
			this.hashCode = sb.toString().hashCode();
		}
		return this.hashCode;
	}


}