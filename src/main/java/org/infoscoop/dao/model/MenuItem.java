package org.infoscoop.dao.model;

import java.util.ArrayList;
import java.util.List;

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
	public MenuItem (java.lang.String id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public MenuItem (
		java.lang.String id,
		java.lang.String title,
		java.lang.Integer menuOrder,
		java.lang.Integer publish,
		java.lang.Integer alert) {

		super (
			id,
			title,
			menuOrder,
			publish,
			alert);
	}

/*[CONSTRUCTOR MARKER END]*/

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
		return super.getPublish() == 1;
	}

	public void setPublishBool(boolean publish) {
		super.setPublish(publish ? 1 : 0);
	}

	public void toggolePublish() {
		setPublishBool(!isPublishBool());
	}
}