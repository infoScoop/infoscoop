package org.infoscoop.dao.model.base;

import java.io.Serializable;


public abstract class BasePreferencesPK implements Serializable {

	protected int hashCode = Integer.MIN_VALUE;

	private java.lang.Integer fkDomainOd;
	private java.lang.String uid;


	public BasePreferencesPK () {}
	
	public BasePreferencesPK (
		java.lang.Integer fkDomainOd,
		java.lang.String uid) {

		this.setFkDomainOd(fkDomainOd);
		this.setUid(uid);
	}


	/**
	 * Return the value associated with the column: fk_domain_id
	 */
	public java.lang.Integer getFkDomainOd () {
		return fkDomainOd;
	}

	/**
	 * Set the value related to the column: fk_domain_id
	 * @param fkDomainOd the fk_domain_id value
	 */
	public void setFkDomainOd (java.lang.Integer fkDomainOd) {
		this.fkDomainOd = fkDomainOd;
	}



	/**
	 * Return the value associated with the column: UID
	 */
	public java.lang.String getUid () {
		return uid;
	}

	/**
	 * Set the value related to the column: UID
	 * @param uid the UID value
	 */
	public void setUid (java.lang.String uid) {
		this.uid = uid;
	}




	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof org.infoscoop.dao.model.PreferencePK)) return false;
		else {
			org.infoscoop.dao.model.PreferencePK mObj = (org.infoscoop.dao.model.PreferencePK) obj;
			if (null != this.getFkDomainOd() && null != mObj.getFkDomainOd()) {
				if (!this.getFkDomainOd().equals(mObj.getFkDomainOd())) {
					return false;
				}
			}
			else {
				return false;
			}
			if (null != this.getUid() && null != mObj.getUid()) {
				if (!this.getUid().equals(mObj.getUid())) {
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
			if (null != this.getFkDomainOd()) {
				sb.append(this.getFkDomainOd().hashCode());
				sb.append(":");
			}
			else {
				return super.hashCode();
			}
			if (null != this.getUid()) {
				sb.append(this.getUid().hashCode());
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