package org.infoscoop.dao.model;

import org.infoscoop.dao.model.base.BaseKeyword;



public class Keyword extends BaseKeyword {
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public Keyword () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public Keyword (java.lang.Long id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public Keyword (
		java.lang.Long id,
		java.lang.String uid,
		java.lang.Integer type,
		java.lang.String keyword,
		java.lang.String date) {

		super (
			id,
			uid,
			type,
			keyword,
			date);
	}

/*[CONSTRUCTOR MARKER END]*/


}