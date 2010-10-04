package org.infoscoop.dao.model;

import org.infoscoop.dao.model.base.BasePreferencesPK;

public class PreferencePK extends BasePreferencesPK {
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public PreferencePK () {}
	
	public PreferencePK (
		java.lang.Integer fkDomainOd,
		java.lang.String uid) {

		super (
			fkDomainOd,
			uid);
	}
/*[CONSTRUCTOR MARKER END]*/


}