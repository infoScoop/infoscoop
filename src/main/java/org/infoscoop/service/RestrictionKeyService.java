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

import java.util.Calendar;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.dao.RestrictionKeyDAO;
import org.infoscoop.dao.model.RestrictionKey;
import org.infoscoop.properties.InfoScoopProperties;
import org.infoscoop.util.SpringUtil;

public class RestrictionKeyService {
	private RestrictionKeyDAO restrictionKeyDAO;
	private static Log log = LogFactory.getLog(RestrictionKeyService.class);
	private final String PROPERTY_KEY_EXPIRED = "restrictioinkey.expired.period";
	private final int KEY_EXPIRED_DEFAULT = 60;

	public static RestrictionKeyService getHandle() {
		return (RestrictionKeyService) SpringUtil.getBean("RestrictionKeyService");
	}

	public RestrictionKeyDAO getRestrictionKeyDAO() {
		return restrictionKeyDAO;
	}

	public void setRestrictionKeyDAO(RestrictionKeyDAO restrictionKeyDAO) {
		this.restrictionKeyDAO = restrictionKeyDAO;
	}
	
	public String createRestrictionKey(String uid){
		String key = RandomStringUtils.randomAlphabetic(60);
		RestrictionKey entity = this.restrictionKeyDAO.getById(key);
		int expiredPeriod = KEY_EXPIRED_DEFAULT;
		
		try{
			String expiredPeriodStr = InfoScoopProperties.getInstance().getProperty(PROPERTY_KEY_EXPIRED);
			expiredPeriod = Integer.parseInt(expiredPeriodStr);
		}catch(NumberFormatException e){
			// ignore
		}
		
		if(entity == null){
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.MINUTE, expiredPeriod);
			
			this.restrictionKeyDAO.save(key, calendar.getTime(), uid);
			return key;
		}
		
		return createRestrictionKey(uid);
	}
	
	public RestrictionKey getRestrictionEntity(String key){
		return this.restrictionKeyDAO.getById(key);
	}

	public void deleteRestrictionEntity(RestrictionKey entity){
		this.restrictionKeyDAO.delete(entity);
	}
}
