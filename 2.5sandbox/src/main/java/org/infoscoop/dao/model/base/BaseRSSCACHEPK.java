package org.infoscoop.dao.model.base;

import java.io.Serializable;


public abstract class BaseRSSCACHEPK implements Serializable {

	protected int hashCode = Integer.MIN_VALUE;

	private java.lang.String urlKey;
	private java.lang.String uid;
	private java.lang.Integer pagenum;


	public BaseRSSCACHEPK () {}
	
	public BaseRSSCACHEPK (
		java.lang.String urlKey,
		java.lang.String uid,
		java.lang.Integer pagenum) {

		this.setUrlKey(urlKey);
		this.setUid(uid);
		this.setPagenum(pagenum);
	}


	/**
	 * Return the value associated with the column: URL_KEY
	 */
	public java.lang.String getUrlKey () {
		return urlKey;
	}

	/**
	 * Set the value related to the column: URL_KEY
	 * @param urlKey the URL_KEY value
	 */
	public void setUrlKey (java.lang.String urlKey) {
		this.urlKey = urlKey;
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



	/**
	 * Return the value associated with the column: PAGENUM
	 */
	public java.lang.Integer getPagenum () {
		return pagenum;
	}

	/**
	 * Set the value related to the column: PAGENUM
	 * @param pagenum the PAGENUM value
	 */
	public void setPagenum (java.lang.Integer pagenum) {
		this.pagenum = pagenum;
	}




	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof org.infoscoop.dao.model.RSSCACHEPK)) return false;
		else {
			org.infoscoop.dao.model.RSSCACHEPK mObj = (org.infoscoop.dao.model.RSSCACHEPK) obj;
			if (null != this.getUrlKey() && null != mObj.getUrlKey()) {
				if (!this.getUrlKey().equals(mObj.getUrlKey())) {
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
			if (null != this.getPagenum() && null != mObj.getPagenum()) {
				if (!this.getPagenum().equals(mObj.getPagenum())) {
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
			if (null != this.getUrlKey()) {
				sb.append(this.getUrlKey().hashCode());
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
			if (null != this.getPagenum()) {
				sb.append(this.getPagenum().hashCode());
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