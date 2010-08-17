package org.infoscoop.dao.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.infoscoop.dao.MenuItemDAO;
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
		java.lang.Integer order,
		java.lang.Integer publish,
		java.lang.Integer alert) {

		super (
			id,
			title,
			order,
			publish,
			alert);
	}

/*[CONSTRUCTOR MARKER END]*/

	private List<MenuItem> childItems = new ArrayList<MenuItem>();
	
	public boolean hasChild() {
		return this.childItems.size() > 0;
	}

	public void setChildItems(List<MenuItem> childItems) {
		this.childItems = childItems;
	}

	public void addChildItem(MenuItem item) {
		this.childItems.add(item);
	}

	public List<MenuItem> getChildItems() {
		return this.childItems;
	}
	
	private Map<String, String> userPref;
	
	public Map<String, String> getUserPref() {
		if (userPref == null) {
			userPref = new HashMap<String, String>();
			if (getId() != null)
				userPref
						.putAll(MenuItemDAO.newInstance().getUserPrefs(getId()));
		}
		return this.userPref;
	}
}