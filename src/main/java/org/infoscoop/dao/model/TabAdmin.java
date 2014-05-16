package org.infoscoop.dao.model;

import org.infoscoop.dao.model.base.BaseTabAdmin;
import org.infoscoop.dao.model.base.BaseTabAdminPK;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;



public class TabAdmin extends BaseTabAdmin {
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public TabAdmin () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public TabAdmin (java.lang.String tabId, java.lang.String uid) {
		super(tabId, uid);
		this.uid = uid;
	}

/*[CONSTRUCTOR MARKER END]*/
	@XStreamAsAttribute
	private String uid;
	
	public String getUid() {
		return uid;
	}
	
	@Override
	public void setId(BaseTabAdminPK id) {
		super.setId(id);
		this.uid = id.getUid();
	}
}