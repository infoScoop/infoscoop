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

package org.infoscoop.properties;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class InfoScoopProperties {
	private static Log logger = LogFactory.getLog(InfoScoopProperties.class);
	private static InfoScoopProperties singleInstance = new InfoScoopProperties();
	private boolean useMultitenantMode = false;
	private ResourceBundle bundle;
	
	private InfoScoopProperties() {
		try {
			this.bundle = ResourceBundle.getBundle("infoscoop");
			String useMultitenantModeStr = bundle.getString("useMultitenantMode");
			this.useMultitenantMode = new Boolean(useMultitenantModeStr);
		} catch (Exception ex) {
			logger.error("Failed to load infoscoop.properties.", ex);
		}
	}

	public static InfoScoopProperties getInstance() {
		return singleInstance;
	}

	public String getProperty(String key) {
		try{
			return bundle.getString(key);
		} catch(MissingResourceException e) {
			return null;
		}
	}

	public int getIntProperty(String key) {
		String value = bundle.getString(key);
		int intValue;
		try{
			String expiredPeriodStr = InfoScoopProperties.getInstance().getProperty(key);
			intValue = Integer.parseInt(expiredPeriodStr);
		}catch(NumberFormatException e){
			throw new RuntimeException(e);
		}
		return intValue;
	}

	public boolean isUseMultitenantMode() {
		return useMultitenantMode;
	}
}