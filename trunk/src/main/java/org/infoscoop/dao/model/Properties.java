package org.infoscoop.dao.model;

import org.infoscoop.dao.model.base.BaseProperties;
import org.infoscoop.util.StringUtil;




public class Properties extends BaseProperties {
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public Properties () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public Properties (java.lang.String id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public Properties (
		java.lang.String id,
		java.lang.Integer required) {

		super (
			id,
			required);
	}

/*[CONSTRUCTOR MARKER END]*/

	public String getValue() {
		return StringUtil.getNullSafe( super.getValue() ); 
	}
}