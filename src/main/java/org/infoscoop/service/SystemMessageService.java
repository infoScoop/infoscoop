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


import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.context.UserContext;
import org.infoscoop.dao.SystemMessageDAO;
import org.infoscoop.dao.model.SystemMessage;
import org.infoscoop.util.SpringUtil;
import org.json.JSONArray;
import org.json.JSONObject;

public class SystemMessageService {
	private static Log log = LogFactory.getLog(SystemMessageService.class);

	private SystemMessageDAO systemMessageDAO;

	public void setSystemMessageDAO(SystemMessageDAO messageDAO) {
		this.systemMessageDAO = messageDAO;
	}

	public static SystemMessageService getHandle() {
		return (SystemMessageService) SpringUtil.getBean("SystemMessageService");
	}


	public String getNonReadMessagesJson(String uid) throws Exception{
		String squareid = UserContext.instance().getUserInfo().getCurrentSquareId();

		Collection<SystemMessage> msgs = this.systemMessageDAO.selectByToAndNoRead(uid, squareid);
		JSONArray msgJsons = new JSONArray();
		for(SystemMessage msg : msgs){
			JSONObject obj = new JSONObject();
			obj.put("body", msg.getBody());
			obj.put("resourceId", msg.getResourceId());
			obj.put("replaceValues", new JSONArray(msg.getReplaceValueCollection()));
			msg.setIsRead(Integer.valueOf(1));
			msgJsons.put(obj);
		}

		return msgJsons.toString();
	}
}
