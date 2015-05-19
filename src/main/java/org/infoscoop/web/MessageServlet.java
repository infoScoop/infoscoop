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

package org.infoscoop.web;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.context.UserContext;
import org.infoscoop.dao.MessageDAO;
import org.infoscoop.dao.model.Message;
import org.infoscoop.service.MessageService;
import org.infoscoop.util.DateUtility;
import org.infoscoop.util.XmlUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MessageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final String DEFAULT_DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";
	private static int TITLE_MAX_LENGTH = 30;

	private Log log = LogFactory.getLog(this.getClass());

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String uid = (String) req.getSession().getAttribute("Uid");

		if (uid == null) {
			resp.sendError(403);
			return;
		}

		String command = req.getParameter("command");

		if (command == null) {
			resp.sendError(500);
			return;
		}

		String result = req.getParameter("result");

		String offsetStr = req.getParameter("offset");
		long offset = offsetStr != null ? Long.parseLong(offsetStr) : Long.MAX_VALUE;

		String limitStr = req.getParameter("limit");
		int limit = limitStr != null ? Integer.parseInt(limitStr) : 100;

		String retVal = null;
		try {
			if (command.equals("add")) {
				String message = req.getParameter("message");
				String to = req.getParameter("to");
				retVal = add(uid, message, to);
			} else if (command.equals("addlink")) {
				String message = req.getParameter("message");
				String to = req.getParameter("to");
				String title = req.getParameter("title");
				String url = req.getParameter("url");
				retVal = addlink(uid, message, to, title, url);
			} else if (command.equals("addbc")){
				String message = req.getParameter("message");
				retVal = addbc(uid, message);
			} else if (command.equals("mylist")) {
				retVal = mylist(result, offset, limit, uid);
			} else if (command.equals("recieved")) {
				retVal = recieved(result, offset, limit, uid);
			} else if (command.equals("list")) {
				String uids = req.getParameter("uids");
				String[] uidArr = uids.split(",");
				retVal = list(result, offset, limit, uid, uidArr);
			} else if (command.equals("all")) {
				retVal = all(result, offset, limit, uid);
			} else if (command.equals("broadcast")) {
				retVal = broadcast(result, offset, limit);
			} else if (command.equals("check")) {
				String lastviewtime = req.getParameter("lastviewtime");
				retVal = Boolean.toString(check(uid, lastviewtime));
			} else {
				log.error("\"" + command + "\" is invalid command");
				resp.sendError(500);
				return;
			}
		} catch (Exception e) {
			log.error("", e);
			resp.sendError(500);
			return;
		}

		if (retVal != null) {
			resp.setContentType("text/xml;charset=utf-8");
			resp.setHeader("Pragma", "no-cache");
			resp.setHeader("Cache-Control", "no-cache");
			resp.getWriter().write(retVal);
		} else {
			log.error("unexpected error occured.");
			resp.sendError(500);
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	private String add(String from, String body, String to) throws Exception {
		String[] toArray = to != null ? to.split(",") : null;
		if (toArray != null && toArray.length > 0)
			MessageService.getHandle().addDirectMessage(from, toArray, body);
		else
			MessageService.getHandle().addPublicMessage(from, body);
		return "";
	}

	private String addlink(String from, String body, String to,
			String linkTitle, String linkUrl) throws Exception {
		String[] toArray = to != null ? to.split(",") : null;
		if (toArray != null && toArray.length > 0)
			MessageService.getHandle().addDirectFYI(from, toArray, body,
					linkTitle, linkUrl);
		else
			MessageService.getHandle().addPublicFYI(from, body, linkTitle,
					linkUrl);
		return "";
	}

	private String addbc(String from, String body) {
		MessageService.getHandle().addBroadCastMessage(from, body);
		return "";
	}

	private String mylist(String result, long offset, int limit, String uid)
			throws Exception {
		List<Message> msgs = MessageDAO.newInstance().selectByUid(uid, offset,
				limit, UserContext.instance().getUserInfo().getCurrentSquareId());
		if (result != null && result.equals("json"))
			return listToJson(msgs);
		return listToRss("A list of My messages", msgs);
	}

	private String recieved(String result, long offset, int limit, String uid)
			throws Exception {
		List<Message> msgs = MessageDAO.newInstance().selectByTo(uid, offset,
				limit, UserContext.instance().getUserInfo().getCurrentSquareId());
		if (result != null && result.equals("json"))
			return listToJson(msgs);
		return listToRss("A list of received messages", msgs);
	}

	private String list(String result, long offset, int limit, String myuid,
			String[] uidArr) throws Exception {
		List<Message> msgs = MessageDAO.newInstance().selectByUids(myuid,
				uidArr, offset, limit, UserContext.instance().getUserInfo().getCurrentSquareId());
		if (result != null && result.equals("json"))
			return listToJson(msgs);
		return listToRss("A list of follow messages", msgs);
	}

	private String all(String result, long offset, int limit, String myuid)
			throws Exception {
		List<Message> msgs = MessageDAO.newInstance().selectAll(myuid, offset,
				limit, UserContext.instance().getUserInfo().getCurrentSquareId());
		if (result != null && result.equals("json"))
			return listToJson(msgs);
		return listToRss("A list of all messages", msgs);
	}

	private String broadcast(String result, long offset, int limit)
			throws Exception {
		List<Message> msgs = MessageDAO.newInstance().selectByType(
				Message.MESSAGE_BROADCAST, offset, limit, UserContext.instance().getUserInfo().getCurrentSquareId());
		if (result != null && result.equals("json"))
			return listToJson(msgs);
		return listToRss("An information", msgs);
	}

	private boolean check(String uid, String lastviewtime) throws Exception {
		List<Message> msgs = MessageDAO.newInstance().selectByTo(uid, Long.MAX_VALUE, 1, UserContext.instance().getUserInfo().getCurrentSquareId());
		if (msgs.size() == 0)
			return false;
		if (Long.parseLong(lastviewtime) < msgs.get(0).getPostedTime()
				.getTime())
			return true;
		return false;
	}

	private String listToJson(List<Message> msgs) throws JSONException {
		SimpleDateFormat df = UserContext.instance().getUserInfo().getClientDateFormat(DEFAULT_DATE_FORMAT);
		
		JSONArray result = new JSONArray();
		for (Message msg : msgs) {
			JSONObject obj = new JSONObject();
			obj.put("id",msg.getId());
			obj.put("from", msg.getFrom());
			obj.put("displayFrom", msg.getDisplayfrom());
			obj.put("body", msg.getHtmlBody());
			obj.put("postedtime", df.format(msg.getPostedTime()));
			if (msg.getTojson() != null) {
				obj.put("to", new JSONArray(msg.getTojson()));
			}
			if (msg.getOption() != null) {
				obj.put("option", msg.getOption());
			}
			obj.put("type", msg.getType());
			result.put(obj);
		}
		return result.toString();
	}

	private String listToRss(String title, List<Message> msgs) {
		StringBuffer xml = new StringBuffer();

		xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
		xml
				.append(
						"<rss version=\"2.0\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:rank=\"")
				.append(WidgetRankingServlet.XMLNS).append("\">\n");
		xml.append("<channel>\n");
		xml.append("<title>").append( XmlUtil.escapeXmlEntities( title )).append("</title>\n");
		xml.append("<rank:excepted>true</rank:excepted>\n");
		for (Message msg : msgs) {
			xml.append("<item>");
			xml.append("<title>");
			String body = msg.getBody();
			xml.append(XmlUtil
					.escapeXmlEntities(body.length() >= TITLE_MAX_LENGTH ? body
							.substring(0, TITLE_MAX_LENGTH)
							+ "..." : body));
			xml.append("</title>");
			xml.append("<pubDate>")
				.append( DateUtility.getW3CDTFDate( msg.getPostedTime()))
				.append("</pubDate>");
			xml.append("<dc:creator>")
				.append(XmlUtil.escapeXmlEntities( msg.getDisplayfrom()))
				.append("</dc:creator>");
			xml.append("<description>");
			xml.append("<![CDATA[");
			if (msg.getType().equals(Message.FYI_FROM)
					|| msg.getType().equals(Message.FYI_PUBLIC)
					|| msg.getType().equals(Message.FYI_TO)) {
				String option = msg.getOption();
				if (option != null) {
					try {
						JSONObject link = new JSONObject(option);
						String url = link.has("url")
								&& link.getString("url").length() > 0 ? link
								.getString("url") : null;
						if (url != null)
							xml.append("<a href=\"").append(
									link.getString("url")).append(
									"\" target=\"_blank\">");
						xml.append(XmlUtil.escapeXmlEntities(link
								.optString("title")));
						if (url != null)
							xml.append("</a>");
						xml.append("<br>");
					} catch (JSONException e) {
					}
				}
			}
			xml.append(body.replaceAll("\n", "<br>"));
			xml.append("]]>");
			xml.append("</description>");
			xml.append("</item>\n");
		}
		xml.append("</channel>\n</rss>\n");
		return xml.toString();
	}
}
