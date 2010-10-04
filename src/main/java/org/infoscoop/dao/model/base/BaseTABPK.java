package org.infoscoop.dao.model.base;

import java.io.Serializable;


public abstract class BaseTABPK implements Serializable {

	protected int hashCode = Integer.MIN_VALUE;

	private java.lang.Integer fkDomainId;
	private java.lang.String uid;
	private java.lang.String id;


	public BaseTABPK () {}
	
	public BaseTABPK (
		java.lang.Integer fkDomainId,
		java.lang.String uid,
		java.lang.String id) {

		this.setFkDomainId(fkDomainId);
		this.setUid(uid);
		this.setId(id);
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
	 * Return the value associated with the column: `UID`
	 */
	public java.lang.String getUid () {
		return uid;
	}

	/**
	 * Set the value related to the column: `UID`
	 * @param uid the `UID` value
	 */
	public void setUid (java.lang.String uid) {
		this.uid = uid;
	}



	/**
	 * Return the value associated with the column: ID
	 */
	public java.lang.String getId () {
		return id;
	}

	/**
	 * Set the value related to the column: ID
	 * @param id the ID value
	 */
	public void setId (java.lang.String id) {
		this.id = id;
	}




	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof org.infoscoop.dao.model.TABPK)) return false;
		else {
			org.infoscoop.dao.model.TABPK mObj = (org.infoscoop.dao.model.TABPK) obj;
			if (null != this.getFkDomainId() && null != mObj.getFkDomainId()) {
				if (!this.getFkDomainId().equals(mObj.getFkDomainId())) {
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
			if (null != this.getId() && null != mObj.getId()) {
				if (!this.getId().equals(mObj.getId())) {
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
			if (null != this.getFkDomainId()) {
				sb.append(this.getFkDomainId().hashCode());
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
			if (null != this.getId()) {
				sb.append(this.getId().hashCode());
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