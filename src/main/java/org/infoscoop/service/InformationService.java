/* infoScoop OpenSource
 * Copyright (C) 2010 Beacon IT Inc.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0-standalone.html>.
 */

package org.infoscoop.service;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.dao.AccessLogDAO;
import org.infoscoop.dao.PreferenceDAO;
import org.infoscoop.dao.SessionDAO;
import org.infoscoop.util.SpringUtil;
import org.json.JSONObject;

public class InformationService {
	private static Log log = LogFactory.getLog(InformationService.class);
	
	private SessionDAO sessionDAO;
	private PreferenceDAO preferenceDAO;
	private AccessLogDAO accessLogDAO;
	
	public void setSessionDAO(SessionDAO sessionDAO) {
		this.sessionDAO = sessionDAO;
	}

	public void setPreferenceDAO(PreferenceDAO preferenceDAO) {
		this.preferenceDAO = preferenceDAO;
	}

	public void setAccessLogDAO(AccessLogDAO accessLogDAO) {
		this.accessLogDAO = accessLogDAO;
	}
	
	public static InformationService getHandle() {
		return (InformationService) SpringUtil.getBean("InformationService");
	}

	public int getTodayAccessCount() {
		return accessLogDAO.getAccessCountByDate(new Date());
	}
	
	public String getUserCountListJSON() throws Exception {
		try {
			int activeUsersCount = sessionDAO.getActiveSessionsCount();
			int totalUsersCount = preferenceDAO.getTotalUsersCount();
			int todayAccessCount = getTodayAccessCount();
			
			JSONObject json = new JSONObject();
			json.put("activeUsersCount", String.valueOf(activeUsersCount));
			json.put("todayAccessCount", String.valueOf(todayAccessCount));
			json.put("totalUsersCount", String.valueOf(totalUsersCount));
			
			return json.toString(1);
		} catch (Exception e) {
			log.error("Unexpected error occurred.", e);
			throw e;
		}
	}

}
