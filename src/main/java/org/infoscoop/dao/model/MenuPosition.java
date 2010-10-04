package org.infoscoop.dao.model;

import org.infoscoop.account.DomainManager;
import org.infoscoop.dao.model.base.BaseMenuPosition;



public class MenuPosition extends BaseMenuPosition {
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public MenuPosition () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public MenuPosition (org.infoscoop.dao.model.MENUPOSITIONPK id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public MenuPosition (
		org.infoscoop.dao.model.MENUPOSITIONPK id,
		org.infoscoop.dao.model.MenuTree fkMenuTree) {

		super (
			id,
			fkMenuTree);
	}

	public MenuPosition(String position) {
		this(new MENUPOSITIONPK(position, DomainManager.getContextDomainId()));
	}

/*[CONSTRUCTOR MARKER END]*/


}