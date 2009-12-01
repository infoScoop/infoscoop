package org.infoscoop.service;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.dao.AccessLogDAO;
import org.infoscoop.dao.LogDAO;
import org.infoscoop.util.SpringUtil;

public class LogService {
	
	private static Log log = LogFactory.getLog(LogService.class);
	
	private LogDAO logDAO;
	private AccessLogDAO accessLogDAO;
	
	public static LogService getHandle() {
		return (LogService) SpringUtil.getBean("LogService");
	}

	public void setLogDAO(LogDAO logDAO) {
		this.logDAO = logDAO;
	}

	public void setAccessLogDAO(AccessLogDAO accessLogDAO) {
		this.accessLogDAO = accessLogDAO;
	}

	public void insertLog(String uid, String logType, String url,
			String rssUrl, String date) {
		logDAO.deleteOldLog();

		if (!logDAO.checkLog(uid, logType, url, rssUrl, date)) {
			logDAO.insertLog(uid, logType, url, rssUrl, date);
		}

		if (log.isInfoEnabled())
			log.info("param[]: Insert XML successfully.");

	}


	public void insertDailyAccessLog(String uid) {
		Date today = new Date();
		this.accessLogDAO.deleteOldLog();

		if (this.accessLogDAO.selectCountByDate(uid, today) == 0) {
			this.accessLogDAO.insert(uid, new Date());
		}
	}
}