package org.infoscoop.dao.model;

import org.infoscoop.dao.model.base.BaseI18NPK;

public class I18NPK extends BaseI18NPK {
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public I18NPK () {}
	
	public I18NPK (
		java.lang.String country,
		java.lang.String id,
		java.lang.String lang,
		java.lang.String type) {

		super (
			country,
			id,
			lang,
			type);
	}
/*[CONSTRUCTOR MARKER END]*/
	
	@Override
	public String toString() {
		return getId()+","+getType()+","+getCountry()+","+getLang();
	}
}