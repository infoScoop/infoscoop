package org.infoscoop.dao.model;

import java.util.ArrayList;
import java.util.List;

import org.infoscoop.account.DomainManager;
import org.infoscoop.dao.model.base.BaseMenuItem;

public class MenuItem extends BaseMenuItem {
	private static final long serialVersionUID = 1L;

	/*[CONSTRUCTOR MARKER BEGIN]*/
	public MenuItem () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public MenuItem (java.lang.Integer id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public MenuItem (
		java.lang.Integer id,
		org.infoscoop.dao.model.MenuTree fkMenuTree,
		java.lang.String title,
		java.lang.Integer menuOrder,
		java.lang.Integer publish,
		java.lang.Integer alert) {

		super (
			id,
			fkMenuTree,
			title,
			menuOrder,
			publish,
			alert);
	}

/*[CONSTRUCTOR MARKER END]*/

	protected void initialize (){
		super.setFkDomainId(DomainManager.getContextDomainId());
	}
	
	private List<MenuItem> childItems = new ArrayList<MenuItem>();

	public void setChildItems(List<MenuItem> childItems) {
		this.childItems = childItems;
	}

	public void addChildItem(MenuItem item) {
		this.childItems.add(item);
	}

	public List<MenuItem> getChildItems() {
		return this.childItems;
	}
	
	public boolean isPublishBool() {
		return super.getPublish() > 0;
	}

	public void setPublishBool(boolean publish) {
		super.setPublish(publish ? 1 : 0);
	}

	public int getAccessLevel(){
		return (super.getRoles()== null || super.getRoles().isEmpty()) ? 0 : 1;
	}

	public void setAccessLevel(String accessLevel){
		if(Integer.parseInt(accessLevel) == 0)
			super.getRoles().clear();
	}
	
	public void toggolePublish() {
		setPublishBool(!isPublishBool());
	}
	
	private boolean applyToUsersGadgets = false;

	public boolean isApplyToUsersGadgets() {
		return applyToUsersGadgets;
	}

	public void setApplyToUsersGadgets(boolean applyToUsersGadgets) {
		this.applyToUsersGadgets = applyToUsersGadgets;
	}

}