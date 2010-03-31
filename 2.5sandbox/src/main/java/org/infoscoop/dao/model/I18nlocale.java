package org.infoscoop.dao.model;

import org.infoscoop.dao.model.base.BaseI18nlocale;



public class I18nlocale extends BaseI18nlocale {
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public I18nlocale () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public I18nlocale (java.lang.Long id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public I18nlocale (
		java.lang.Long id,
		java.lang.String type,
		java.lang.String country,
		java.lang.String lang) {

		super (
			id,
			type,
			country,
			lang);
	}

/*[CONSTRUCTOR MARKER END]*/


}