package org.infoscoop.dao.model;

import java.util.ArrayList;
import java.util.List;

import org.infoscoop.account.DomainManager;
import org.infoscoop.dao.model.base.BaseMenuTree;



public class MenuTree extends BaseMenuTree {
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public MenuTree () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public MenuTree (java.lang.Integer id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public MenuTree (
		java.lang.Integer id,
		java.lang.String title) {

		super (
			id,
			title);
	}

/*[CONSTRUCTOR MARKER END]*/

	protected void initialize (){
		super.setFkDomainId(DomainManager.getContextDomainId());
	}
	
	private List<String> positions;
	
	public void addPosition(String position) {
		if (positions == null)
			positions = new ArrayList<String>();
		positions.add(position);
	}

	public boolean isTop() {
		return hasPosition("top");
	}

	public boolean isSide() {
		return hasPosition("side");
	}

	public boolean hasPosition(String position) {
		if (positions == null)
			return false;
		for (String pos : positions) {
			if (pos.equals(position))
				return true;
		}
		return false;
	}
}