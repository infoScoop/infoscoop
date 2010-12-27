package org.infoscoop.dao.model.base;

import java.io.Serializable;


public abstract class BasePortallayoutsPK implements Serializable {

	protected int hashCode = Integer.MIN_VALUE;

	private java.lang.String name;
	private java.lang.String lang;
	private java.lang.String country;


	public BasePortallayoutsPK () {}
	
	public BasePortallayoutsPK (
		java.lang.String name,
		java.lang.String lang,
		java.lang.String country) {

		this.setName(name);
		this.setLang(lang);
		this.setCountry(country);
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



	/**
	 * Return the value associated with the column: lang
	 */
	public java.lang.String getLang () {
		return lang;
	}

	/**
	 * Set the value related to the column: lang
	 * @param lang the lang value
	 */
	public void setLang (java.lang.String lang) {
		this.lang = lang;
	}



	/**
	 * Return the value associated with the column: country
	 */
	public java.lang.String getCountry () {
		return country;
	}

	/**
	 * Set the value related to the column: country
	 * @param country the country value
	 */
	public void setCountry (java.lang.String country) {
		this.country = country;
	}




	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof org.infoscoop.dao.model.PortallayoutsPK)) return false;
		else {
			org.infoscoop.dao.model.PortallayoutsPK mObj = (org.infoscoop.dao.model.PortallayoutsPK) obj;
			if (null != this.getName() && null != mObj.getName()) {
				if (!this.getName().equals(mObj.getName())) {
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
			if (null != this.getCountry() && null != mObj.getCountry()) {
				if (!this.getCountry().equals(mObj.getCountry())) {
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
			if (null != this.getName()) {
				sb.append(this.getName().hashCode());
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
			if (null != this.getCountry()) {
				sb.append(this.getCountry().hashCode());
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