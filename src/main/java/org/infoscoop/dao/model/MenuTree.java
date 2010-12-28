package org.infoscoop.dao.model;

import java.util.List;

import org.infoscoop.account.DomainManager;
import org.infoscoop.dao.MenuItemDAO;
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
		java.lang.String title,
		java.lang.Integer orderIndex,
		java.lang.Integer publish,
		java.lang.Integer alert,
		java.lang.String country,
		java.lang.String lang,
		java.lang.Integer top,
		java.lang.Integer side) {

		super (
			id,
			title,
			orderIndex,
			publish,
			alert,
			country,
			lang,
			top,
			side);
	}

/*[CONSTRUCTOR MARKER END]*/

	protected void initialize (){
		super.setFkDomainId(DomainManager.getContextDomainId());
	}

	public int getAccessLevel(){
		return (super.getRoles()== null || super.getRoles().isEmpty()) ? 0 : 1;
	}

	public void setAccessLevel(String accessLevel){
		if(Integer.parseInt(accessLevel) == 0)
			super.getRoles().clear();
	}
	
	public boolean isTopPos() {
		return super.getTop() != null && super.getTop() == 1;
	}

	public void setTopPos(boolean toppos) {
		super.setTop(toppos ? 1 : 0);
	}

	public boolean isSidePos() {
		return super.getSide() != null && super.getSide() == 1;
	}

	public void setSidePos(boolean sidepos) {
		super.setSide(sidepos ? 1 : 0);
	}
	
	private List<MenuItem> childItems;

	public List<MenuItem> getChildItems() {
		return childItems;
	}

	public void setChildItems() {
		// don't use super.getMenuItems to keep sort order
		childItems = MenuItemDAO.newInstance().getTree(this);
	}
}