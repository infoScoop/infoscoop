package org.infoscoop.manager.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.account.DomainManager;
import org.infoscoop.dao.DomainDAO;
import org.infoscoop.dao.GroupDAO;
import org.infoscoop.dao.PropertiesDAO;
import org.infoscoop.dao.UserDAO;
import org.infoscoop.dao.model.Domain;
import org.infoscoop.dao.model.Group;
import org.infoscoop.dao.model.Properties;
import org.infoscoop.dao.model.User;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {
	private static Log log = LogFactory.getLog(UserController.class);

	@RequestMapping(method = RequestMethod.GET)
	public void index(Model model) throws Exception {
	}

	@RequestMapping(method = RequestMethod.GET)
	public void sync(Model model,
			@RequestParam(value = "acl", required = false) boolean acl,
			HttpServletRequest request) throws Exception {
		log.info("UserController.sync is called.");
		Integer domainId = DomainManager.getContextDomainId();
		Domain domain = DomainDAO.newInstance().get(domainId);
		String domainName = domain.getName();
		String uid = (String) request.getSession().getAttribute("Uid");

		long start = System.currentTimeMillis();

		HttpClient http = new HttpClient();
		Properties property = PropertiesDAO.newInstance().findProperty("appsServiceURL");
		String url = property.getValue();
		if (!url.endsWith("/"))
			url += "/";
		GetMethod method = new GetMethod(url + "usergroup?domain=" + domainName
				+ "&u=" + uid);
		http.executeMethod(method);
		if (method.getStatusCode() >= 300) {
			// TODO handle error
			throw new Exception("Failed to retrieve users and groups. "
					+ method.getStatusCode() + " - " + method.getStatusText());
		}

		String response = method.getResponseBodyAsString();
		JSONObject json = new JSONObject(response);

		log.info("start to insert users.");
		UserDAO userDAO = UserDAO.newInstance();
		JSONArray usersJ = json.getJSONArray("users");
		int user_count = 0;
		for (int i = 0; i < usersJ.length(); i++) {
			JSONObject userJ = usersJ.getJSONObject(i);
			String account = userJ.getString("login");
			String suspended = userJ.getString("suspended");
			String admin = userJ.getString("admin");
			String fname = userJ.getString("familyName");
			String gname = userJ.getString("givenName");
			String name = fname + " " + gname;
			String email = account + "@" + domainName;
			User user = userDAO.getByEmail(email, domainId);
			if (suspended == "false"){
				if (user == null)
					user = new User();
				user.setEmail(email);
				user.setName(name);
				if (admin == "true")
					user.setAdmin(1);
				else
					user.setAdmin(0);
				userDAO.save(user);
				log.info(user.getEmail() + " is saved.");
				user_count ++;
			} else {
				if (user != null)
					userDAO.delete(user);
			}
		}
		model.addAttribute("userCount", user_count);

		log.info("start to insert groups.");
		GroupDAO groupDAO = GroupDAO.newInstance();
		JSONArray groupsJ = json.getJSONArray("groups");
		for (int i = 0; i < groupsJ.length(); i++) {
			JSONObject groupJ = groupsJ.getJSONObject(i);
			String email = groupJ.getString("groupId");
			String name = groupJ.getString("groupName");
			String description = groupJ.getString("description");
			Group group = groupDAO.getByEmail(email);
			if (group == null)
				group = new Group();
			group.setEmail(email);
			group.setName(name);
			group.setDescription(description);

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
				User user = userDAO.getByEmail(memberId, domainId);
				if (user != null)
					group.addToUsers(user);
				//else
				//	System.out.println("User " + memberId.split("@")[0] + " is missing.");
			}
			groupDAO.save(group);
			log.info(group.getEmail() + " is saved.");
		}
		model.addAttribute("groupCount", groupsJ.length());

		long time = System.currentTimeMillis() - start;
		log.info(usersJ.length() + " users and " + groupsJ.length()
				+ " groups have been synchronized. It took " + time
				+ " millisecond.");
		
		// create and share special docs.
		method = new GetMethod(url + "usergroup?domain=" + domainName + "&u="
				+ uid + "&mode=docs" + (acl ? "&acl=true" : ""));
		http.executeMethod(method);
		if (method.getStatusCode() >= 300) {
			// TODO handle error
			throw new Exception("Failed to create and share special docs."
					+ method.getStatusCode() + " - " + method.getStatusText());
		}
		log.info("The special docs have been created"
				+ (acl ? " and shared." : "."));
	}
}