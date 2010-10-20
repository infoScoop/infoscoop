package org.infoscoop.manager.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.dao.GroupDAO;
import org.infoscoop.dao.UserDAO;
import org.infoscoop.dao.model.Group;
import org.infoscoop.dao.model.User;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class UserController {
	private static Log log = LogFactory.getLog(UserController.class);

	@RequestMapping(method = RequestMethod.GET)
	public void index(Model model) throws Exception {
	}

	@RequestMapping(method = RequestMethod.GET)
	public void sync(Model model, HttpServletRequest request) throws Exception {
		String domain = "infoscoop.org";

		long start = System.currentTimeMillis();

		HttpClient http = new HttpClient();
		GetMethod method = new GetMethod(
				"http://localhost:8080/infoscoop4g/usergroup?domain=" + domain);
		http.executeMethod(method);
		if (method.getStatusCode() >= 300) {
			// TODO handle error
			throw new Exception("Failed to retrieve users and groups. "
					+ method.getStatusCode() + " - " + method.getStatusText());
		}

		String response = method.getResponseBodyAsString();
		JSONObject json = new JSONObject(response);

		UserDAO userDAO = UserDAO.newInstance();
		JSONArray usersJ = json.getJSONArray("users");
		for (int i = 0; i < usersJ.length(); i++) {
			JSONObject userJ = usersJ.getJSONObject(i);
			String name = userJ.getString("login");
			User user = userDAO.getByName(name);
			if (user == null)
				user = new User();
			String mail = name + "@" + domain;
			System.out.println(mail);
			user.setEmail(mail);
			user.setName(name);
			userDAO.save(user);
		}
		model.addAttribute("userCount", usersJ.length());

		GroupDAO groupDAO = GroupDAO.newInstance();
		JSONArray groupsJ = json.getJSONArray("groups");
		for (int i = 0; i < groupsJ.length(); i++) {
			JSONObject groupJ = groupsJ.getJSONObject(i);
			String name = groupJ.getString("groupId");
			Group group = groupDAO.getByName(name);
			if (group == null)
				group = new Group();
			group.setName(name);

			JSONArray membersJ = groupJ.getJSONArray("members");
			for (int j = 0; j < membersJ.length(); j++) {
				JSONObject memberJ = membersJ.getJSONObject(j);
				String memberId = memberJ.getString("memberId");
				if (memberId.equals("*")) {
					List<User> allusers = userDAO.all();
					for (User u : allusers) {
						group.addToUsers(u);
					}
					break;
				}
				String userName = memberId.split("@")[0];
				User user = userDAO.getByName(userName);
				if (user != null)
					group.addToUsers(user);
				else
					System.out.println("User " + userName + " is missing.");
			}
			groupDAO.save(group);
		}
		model.addAttribute("groupCount", groupsJ.length());

		long time = System.currentTimeMillis() - start;
		log.info(usersJ.length() + " users and " + groupsJ.length()
				+ " groups have been synchronized. It took " + time
				+ " millisecond.");
	}
}