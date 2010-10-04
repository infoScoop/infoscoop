package org.infoscoop.dao.model.base;

import java.io.Serializable;


public abstract class BaseMENUPOSITIONPK implements Serializable {

	protected int hashCode = Integer.MIN_VALUE;

	private java.lang.String id;
	private java.lang.Integer fkDomainId;


	public BaseMENUPOSITIONPK () {}
	
	public BaseMENUPOSITIONPK (
		java.lang.String id,
		java.lang.Integer fkDomainId) {

		this.setId(id);
		this.setFkDomainId(fkDomainId);
	}


	/**
	 * Return the value associated with the column: id
	 */
	public java.lang.String getId () {
		return id;
	}

	/**
	 * Set the value related to the column: id
	 * @param id the id value
	 */
	public void setId (java.lang.String id) {
		this.id = id;
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




	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof org.infoscoop.dao.model.MENUPOSITIONPK)) return false;
		else {
			org.infoscoop.dao.model.MENUPOSITIONPK mObj = (org.infoscoop.dao.model.MENUPOSITIONPK) obj;
			if (null != this.getId() && null != mObj.getId()) {
				if (!this.getId().equals(mObj.getId())) {
					return false;
				}
			}
			else {
				return false;
			}
			if (null != this.getFkDomainId() && null != mObj.getFkDomainId()) {
				if (!this.getFkDomainId().equals(mObj.getFkDomainId())) {
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
			if (null != this.getId()) {
				sb.append(this.getId().hashCode());
				sb.append(":");
			}
			else {
				return super.hashCode();
			}
			if (null != this.getFkDomainId()) {
				sb.append(this.getFkDomainId().hashCode());
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