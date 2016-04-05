package org.infoscoop.dao.model;

import org.infoscoop.dao.model.base.BaseNotification;

public class Notification extends BaseNotification {
    private static final long serialVersionUID = 1L;

    public Notification() {
		// TODO Auto-generated constructor stub
	}
    
    Notification(String type) {
        super(type);
    }

}
