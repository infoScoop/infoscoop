package org.infoscoop.dao.model.base;

import java.io.Serializable;


public abstract class BaseHOLIDAYSPK implements Serializable {

	protected int hashCode = Integer.MIN_VALUE;

	private java.lang.String country;
	private java.lang.String lang;


	public BaseHOLIDAYSPK () {}
	
	public BaseHOLIDAYSPK ( java.lang.String lang,java.lang.String country ) {

		this.setCountry(country);
		this.setLang(lang);
	}


	/**
	 * Return the value associated with the column: COUNTRY
	 */
	public java.lang.String getCountry () {
		return country;
	}

	/**
	 * Set the value related to the column: COUNTRY
	 * @param country the COUNTRY value
	 */
	public void setCountry (java.lang.String country) {
		this.country = country;
	}

	/**
	 * Return the value associated with the column: LANG
	 */
	public java.lang.String getLang () {
		return lang;
	}

	/**
	 * Set the value related to the column: LANG
	 * @param lang the LANG value
	 */
	public void setLang (java.lang.String lang) {
		this.lang = lang;
	}

	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof org.infoscoop.dao.model.I18NPK)) return false;
		else {
			org.infoscoop.dao.model.I18NPK mObj = (org.infoscoop.dao.model.I18NPK) obj;
			if (null != this.getCountry() && null != mObj.getCountry()) {
				if (!this.getCountry().equals(mObj.getCountry())) {
					return false;
				}
			}
			else {
				return false;
			}
			if (null != this.getLang() && null != mObj.getLang()) {
				if (!this.getLang().equals(mObj.getLang())) {
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
			if (null != this.getCountry()) {
				sb.append(this.getCountry().hashCode());
				sb.append(":");
			}
			else {
				return super.hashCode();
			}
			if (null != this.getLang()) {
				sb.append(this.getLang().hashCode());
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