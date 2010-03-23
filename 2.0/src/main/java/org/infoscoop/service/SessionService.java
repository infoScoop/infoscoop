package org.infoscoop.service;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.dao.SessionDAO;
import org.infoscoop.util.SpringUtil;

public class SessionService {
	private static Log log = LogFactory.getLog(SessionService.class);
	SessionDAO sessionDAO = null;

	public void setSessionDAO(SessionDAO sessionDAO) {
		this.sessionDAO = sessionDAO;
	}


	public static SessionService getHandle() {
		return (SessionService) SpringUtil.getBean("SessionService");
	}


	public String newSessionId(String uid) throws Exception{

		return this.sessionDAO.newSessionId(uid);
	}
}