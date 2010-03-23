package org.infoscoop.service;


import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

		Collection<SystemMessage> msgs = this.systemMessageDAO.selectByToAndNoRead(uid);
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