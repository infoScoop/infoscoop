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

package org.infoscoop.command;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.infoscoop.dao.GlobalPreferenceDAO;
import org.infoscoop.dao.model.GlobalPreference;

public class UpdateNoticeConfirmDate extends XMLCommandProcessor {

	public void execute() {
		String field = super.commandXml.getAttribute("field").trim();
		SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		String value = sdf.format(new Date());
		
		GlobalPreferenceDAO globalPreferenceDAO = GlobalPreferenceDAO.newInstance();
   		GlobalPreference globalPreference = globalPreferenceDAO.getByUidKey(uid, field);
   		if(globalPreference != null){
   			globalPreference.setValue(value);
   			globalPreferenceDAO.update(globalPreference);
   		}else{
   			globalPreferenceDAO.insert(uid, field, value);
   		}
	}
	
}
