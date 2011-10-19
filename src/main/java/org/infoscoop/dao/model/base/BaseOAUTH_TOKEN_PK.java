package org.infoscoop.dao.model.base;

import java.io.Serializable;


public abstract class BaseOAUTH_TOKEN_PK implements Serializable {

	protected int hashCode = Integer.MIN_VALUE;

	private java.lang.String uid;
	private java.lang.String fkOAuthId;


	public BaseOAUTH_TOKEN_PK () {}
	
	public BaseOAUTH_TOKEN_PK (
		java.lang.String uid,
		java.lang.String fkOAuthId) {

		this.setUid(uid);
		this.setFkOAuthId(fkOAuthId);
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
	 * Return the value associated with the column: gadget_url_key
	 */
	public java.lang.String getFkOAuthId () {
		return fkOAuthId;
	}

	/**
	 * Set the value related to the column: gadget_url_key
	 * @param gadgetUrlKey the gadget_url_key value
	 */
	public void setFkOAuthId (java.lang.String fkOAuthId) {
		this.fkOAuthId = fkOAuthId;
	}



	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof org.infoscoop.dao.model.OAUTH_TOKEN_PK)) return false;
		else {
			org.infoscoop.dao.model.OAUTH_TOKEN_PK mObj = (org.infoscoop.dao.model.OAUTH_TOKEN_PK) obj;
			if (null != this.getUid() && null != mObj.getUid()) {
				if (!this.getUid().equals(mObj.getUid())) {
					return false;
				}
			}
			else {
				return false;
			}

			if (null != this.getFkOAuthId() && null != mObj.getFkOAuthId()) {
				if (!this.getFkOAuthId().equals(mObj.getFkOAuthId())) {
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
			if (null != this.getUid()) {
				sb.append(this.getUid().hashCode());
				sb.append(":");
			}
			else {
				return super.hashCode();
			}
			if (null != this.getFkOAuthId()) {
				sb.append(this.getFkOAuthId().hashCode());
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