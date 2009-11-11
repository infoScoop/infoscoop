package org.infoscoop.service;

import java.util.Calendar;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.dao.LogDAO;
import org.infoscoop.util.SpringUtil;

public class LogService {
	
	private static Log log = LogFactory.getLog(LogService.class);
	
	private LogDAO logDAO;
	
	public static LogService getHandle() {
		return (LogService) SpringUtil.getBean("LogService");
	}

	public void setLogDAO(LogDAO logDAO) {
		this.logDAO = logDAO;
	}
	
	public void insertLog(String uid, String logType, String url,
			String rssUrl, String date) throws Exception {
		try {
				logDAO.deleteOldLog();

			if (!logDAO.checkLog(uid, logType, url, rssUrl, date)) {
				logDAO.insertLog(uid, logType, url, rssUrl, date);
			}

			if (log.isInfoEnabled())
				log.info("param[]: Insert XML successfully.");
		} catch (Exception e) {
			log.error("Unexpected exception occurred.", e);
			throw e;
		}
	}

	
}