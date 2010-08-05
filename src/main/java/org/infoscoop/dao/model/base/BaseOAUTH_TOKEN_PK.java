package org.infoscoop.dao.model.base;

import java.io.Serializable;


public abstract class BaseOAUTH_TOKEN_PK implements Serializable {

	protected int hashCode = Integer.MIN_VALUE;

	private java.lang.String uid;
	private java.lang.String gadgetUrlKey;
	private java.lang.String serviceName;


	public BaseOAUTH_TOKEN_PK () {}
	
	public BaseOAUTH_TOKEN_PK (
		java.lang.String uid,
		java.lang.String gadgetUrlKey,
		java.lang.String serviceName) {

		this.setUid(uid);
		this.setGadgetUrlKey(gadgetUrlKey);
		this.setServiceName(serviceName);
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
	public java.lang.String getGadgetUrlKey () {
		return gadgetUrlKey;
	}

	/**
	 * Set the value related to the column: gadget_url_key
	 * @param gadgetUrlKey the gadget_url_key value
	 */
	public void setGadgetUrlKey (java.lang.String gadgetUrlKey) {
		this.gadgetUrlKey = gadgetUrlKey;
	}



	/**
	 * Return the value associated with the column: service_name
	 */
	public java.lang.String getServiceName () {
		return serviceName;
	}

	/**
	 * Set the value related to the column: service_name
	 * @param serviceName the service_name value
	 */
	public void setServiceName (java.lang.String serviceName) {
		this.serviceName = serviceName;
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
			if (null != this.getGadgetUrlKey() && null != mObj.getGadgetUrlKey()) {
				if (!this.getGadgetUrlKey().equals(mObj.getGadgetUrlKey())) {
					return false;
				}
			}
			else {
				return false;
			}
			if (null != this.getServiceName() && null != mObj.getServiceName()) {
				if (!this.getServiceName().equals(mObj.getServiceName())) {
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
			if (null != this.getGadgetUrlKey()) {
				sb.append(this.getGadgetUrlKey().hashCode());
				sb.append(":");
			}
			else {
				return super.hashCode();
			}
			if (null != this.getServiceName()) {
				sb.append(this.getServiceName().hashCode());
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