package org.infoscoop.dao.model.base;

import java.io.Serializable;

/**
 * This is an object that contains data related to the IS_TAB_ADMINS table. Do
 * not modify this class because it will be overwritten if the configuration
 * file related to this class is modified.
 * 
 * @hibernate.class table="IS_TAB_ADMINS"
 */

public abstract class BaseTabAdminPK implements Serializable {

	public static String PROP_ID = "Tabid";
	public static String REF = "TabAdmin";
	public static String PROP_UID = "Uid";

	// constructors
	public BaseTabAdminPK() {
	}

	/**
	 * Constructor for primary key
	 */
	public BaseTabAdminPK(java.lang.String tabId, java.lang.String uid) {
		this.setTabid(tabId);
		this.setUid(uid);
	}

	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private java.lang.String tabId;

	// fields
	private java.lang.String uid;

	/**
	 * Return the unique identifier of this class
	 * 
	 * @hibernate.id column="id"
	 */
	public java.lang.String getTabid() {
		return tabId;
	}

	/**
	 * Set the unique identifier of this class
	 * 
	 * @param id
	 *            the new ID
	 */
	public void setTabid(java.lang.String tabId) {
		this.tabId = tabId;
		this.hashCode = Integer.MIN_VALUE;
	}

	/**
	 * Return the value associated with the column: UID
	 */
	public java.lang.String getUid() {
		return uid;
	}

	/**
	 * Set the value related to the column: UID
	 * 
	 * @param uid
	 *            the UID value
	 */
	public void setUid(java.lang.String uid) {
		this.uid = uid;
	}

	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof org.infoscoop.dao.model.TabAdminPK)) return false;
		else {
			org.infoscoop.dao.model.TabAdminPK mObj = (org.infoscoop.dao.model.TabAdminPK) obj;
			if (null != this.getUid() && null != mObj.getUid()) {
				if (!this.getUid().equals(mObj.getUid())) {
					return false;
				}
			}
			else {
				return false;
			}
			if (null != this.getTabid() && null != mObj.getTabid()) {
				if (!this.getTabid().equals(mObj.getTabid())) {
					return false;
				}
			}
			else {
				return false;
			}
			return true;
		}
	}
	
	public int hashCode() {
		if (Integer.MIN_VALUE == this.hashCode) {
			if (null == this.getTabid())
				return super.hashCode();
			else {
				String hashStr = this.getClass().getName() + ":"
						+ this.getTabid().hashCode();
				this.hashCode = hashStr.hashCode();
			}
		}
		return this.hashCode;
	}

	public String toString() {
		return super.toString();
	}

}