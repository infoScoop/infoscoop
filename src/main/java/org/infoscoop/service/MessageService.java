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


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.dao.MessageDAO;
import org.infoscoop.dao.model.Message;
import org.infoscoop.account.IAccount;
import org.infoscoop.account.SearchUserService;
import org.infoscoop.util.SpringUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

public class MessageService {
	private static Log log = LogFactory.getLog(MessageService.class);

	private MessageDAO messageDAO;

	public void setMessageDAO(MessageDAO messageDAO) {
		this.messageDAO = messageDAO;
	}

	public static MessageService getHandle() {
		return (MessageService) SpringUtil.getBean("MessageService");
	}

	public void addBroadCastMessage(String from, String body) {
		Message msg = new Message();
		msg.setFrom(from);
		msg.setDisplayfrom(getUserName(from));
		msg.setBody(body);
		msg.setType(Message.MESSAGE_BROADCAST);
		this.messageDAO.insert(msg);
	}

	public void addPublicMessage(String from, String body) {
		Message msg = new Message();
		msg.setFrom(from);
		msg.setDisplayfrom(getUserName(from));
		msg.setBody(body);
		msg.setType(Message.MESSAGE_PUBLIC);
		this.messageDAO.insert(msg);
	}

	public void addPublicFYI(String from, String body, String linkTitle,
			String linkUrl) throws Exception {
		Message msg = new Message();
		msg.setFrom(from);
		msg.setDisplayfrom(getUserName(from));
		msg.setBody(body);
		msg.setType(Message.FYI_PUBLIC);
		JSONObject link = new JSONObject();
		link.put("title", linkTitle);
		link.put("url", linkUrl);
		msg.setOption(link.toString());
		this.messageDAO.insert(msg);
	}

	public void addSystemMessage(String to, String body)
	throws Exception {
		String from = "admin";

		Message msgTo = new Message();
		msgTo.setFrom(from);
		msgTo.setDisplayfrom(getUserName(from));
		msgTo.setBody(body);
		msgTo.setType(Message.SYSTEM);
		msgTo.setTo(to);
		msgTo.setTojson("[" + to +"]");
		this.messageDAO.insert(msgTo);
	}

	public void addDirectMessage(String from, String[] toArray, String body)
			throws Exception {
		String toJson = buildToJSON(toArray);

		Message msgFrom = new Message();
		msgFrom.setFrom(from);
		msgFrom.setDisplayfrom(getUserName(from));
		msgFrom.setBody(body);
		msgFrom.setType(Message.MESSAGE_FROM);
		msgFrom.setTojson(toJson);
		this.messageDAO.insert(msgFrom);

		for (String to : toArray) {
			Message msgTo = new Message();
			msgTo.setFrom(from);
			msgTo.setDisplayfrom(getUserName(from));
			msgTo.setBody(body);
			msgTo.setType(Message.MESSAGE_TO);
			msgTo.setTo(to);
			msgTo.setTojson(toJson);
			this.messageDAO.insert(msgTo);
		}
	}

	public void addDirectFYI(String from, String[] toArray, String body,
			String linkTitle, String linkUrl) throws Exception {
		JSONObject link = new JSONObject();
		link.put("title", linkTitle);
		link.put("url", linkUrl);
		String toJson = buildToJSON(toArray);

		Message msgFrom = new Message();
		msgFrom.setFrom(from);
		msgFrom.setDisplayfrom(getUserName(from));
		msgFrom.setBody(body);
		msgFrom.setType(Message.FYI_FROM);
		msgFrom.setTojson(toJson);
		msgFrom.setOption(link.toString());
		this.messageDAO.insert(msgFrom);

		for (String to : toArray) {
			Message msgTo = new Message();
			msgTo.setFrom(from);
			msgTo.setDisplayfrom(getUserName(from));
			msgTo.setBody(body);
			msgTo.setType(Message.FYI_TO);
			msgTo.setTo(to);
			msgTo.setTojson(toJson);
			msgTo.setOption(link.toString());
			this.messageDAO.insert(msgTo);
		}
	}

	private String getUserName(String uid) {
		if (!SearchUserService.isAvailable())
			return uid;
		try {
			SearchUserService search = (SearchUserService) SpringUtil
					.getBean("searchUserService");
			IAccount user = search.getUser(uid);
			if (user != null) {
				return user.getName();
			}
		} catch (NoSuchBeanDefinitionException e) {
			log.warn("searchUserService not found.", e);
		} catch (Exception e) {
			log.warn("unexpected error occured in searchUserService.", e);
		}
		return uid;
	}

	private String buildToJSON(String[] toArray) throws Exception {
		JSONArray uids = new JSONArray();
		for (String to : toArray) {
			JSONObject uid = new JSONObject();
			uid.put("uid", to);
			uid.put("name", getUserName(to));
			uids.put(uid);
		}
		return uids.toString();
	}
}
