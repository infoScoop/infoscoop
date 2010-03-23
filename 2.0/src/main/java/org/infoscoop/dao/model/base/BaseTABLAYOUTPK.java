package org.infoscoop.dao.model.base;

import java.io.Serializable;


public abstract class BaseTABLAYOUTPK implements Serializable {

	protected int hashCode = Integer.MIN_VALUE;

	private java.lang.String tabid;
	private java.lang.Integer roleorder;
	private java.lang.Integer temp;


	public BaseTABLAYOUTPK () {}
	
	public BaseTABLAYOUTPK (
		java.lang.String tabid,
		java.lang.Integer roleorder,
		java.lang.Integer temp) {

		this.setTabid(tabid);
		this.setRoleorder(roleorder);
		this.setTemp(temp);
	}


	/**
	 * Return the value associated with the column: TABID
	 */
	public java.lang.String getTabid () {
		return tabid;
	}

	/**
	 * Set the value related to the column: TABID
	 * @param tabid the TABID value
	 */
	public void setTabid (java.lang.String tabid) {
		this.tabid = tabid;
	}



	/**
	 * Return the value associated with the column: ROLEORDER
	 */
	public java.lang.Integer getRoleorder () {
		return roleorder;
	}

	/**
	 * Set the value related to the column: ROLEORDER
	 * @param roleorder the ROLEORDER value
	 */
	public void setRoleorder (java.lang.Integer roleorder) {
		this.roleorder = roleorder;
	}



	/**
	 * Return the value associated with the column: TEMP
	 */
	public java.lang.Integer getTemp () {
		return temp;
	}

	/**
	 * Set the value related to the column: TEMP
	 * @param deleteflag the TEMP value
	 */
	public void setTemp (java.lang.Integer temp) {
		this.temp = temp;
	}




	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof org.infoscoop.dao.model.TABLAYOUTPK)) return false;
		else {
			org.infoscoop.dao.model.TABLAYOUTPK mObj = (org.infoscoop.dao.model.TABLAYOUTPK) obj;
			if (null != this.getTabid() && null != mObj.getTabid()) {
				if (!this.getTabid().equals(mObj.getTabid())) {
					return false;
				}
			}
			else {
				return false;
			}
			if (null != this.getRoleorder() && null != mObj.getRoleorder()) {
				if (!this.getRoleorder().equals(mObj.getRoleorder())) {
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
			StringBuffer sb = new StringBuffer();
			if (null != this.getTabid()) {
				sb.append(this.getTabid().hashCode());
				sb.append(":");
			}
			else {
				return super.hashCode();
			}
			if (null != this.getRoleorder()) {
				sb.append(this.getRoleorder().hashCode());
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