package org.infoscoop.service;

import org.infoscoop.dao.UserDAO;
import org.infoscoop.dao.model.Group;
import org.infoscoop.dao.model.User;
import org.infoscoop.util.SpringUtil;

public class UserService {
	private UserDAO userDAO;
	
	public void setUserDAO(UserDAO userDAO){
		this.userDAO = userDAO;
	}

	public static UserService getHandle() {
		return (UserService) SpringUtil.getBean("UserService");
	}

	public User getUser(String email, Integer domainId) {
		User user = userDAO.getByEmail(email, domainId);
		user.getGroups().size();
		return user;
	}
}
