package org.infoscoop.dao.model;

import org.infoscoop.dao.model.base.BaseI18n;



public class I18n extends BaseI18n {
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public I18n () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public I18n (org.infoscoop.dao.model.I18NPK id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public I18n (
		org.infoscoop.dao.model.I18NPK id,
		java.lang.String message) {

		super (
			id,
			message);
	}

/*[CONSTRUCTOR MARKER END]*/

	public String getType() { return super.getId().getType(); }
	public String getCountry() { return super.getId().getCountry(); }
	public String getLang() { return super.getId().getLang(); }
	
	public String toString() {
		return "type=" + getType() + ", id=" + getId().getId()
				+ ", country=" + getCountry() + ",lang=" + getLang() + ", message="
				+ getMessage();
	}
}