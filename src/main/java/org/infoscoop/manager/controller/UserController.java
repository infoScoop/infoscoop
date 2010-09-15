package org.infoscoop.manager.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.infoscoop.dao.UserDAO;
import org.infoscoop.dao.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import sample.appsforyourdomain.AppsForYourDomainClient;
import com.google.gdata.data.appsforyourdomain.provisioning.UserEntry;
import com.google.gdata.data.appsforyourdomain.provisioning.UserFeed;

@Controller
public class UserController {
	@RequestMapping(method=RequestMethod.GET)
	public void index(Model model) throws Exception {
		List<User> users = UserDAO.newInstance().all();
		System.out.print(users);
		model.addAttribute("users", users);
	}

	@RequestMapping(method=RequestMethod.GET)
	public String save(Model model) throws Exception {
		String adminAddress = "y_yoshida@beacon-it.co.jp";
		String domain = "beacon-it.co.jp";
		String password = "z1x2c3v4";
		AppsForYourDomainClient client = new AppsForYourDomainClient(adminAddress, password, domain);
		int size = client.retrieveAllUsers().getEntries().size();
//		UserFeed hoge = client.retrieveAllUsers();

		int max = 100;
		String firstName = null;
		for (int i = 0; i < size/max+1; i++) {
			UserFeed users = client.retrievePageOfUsers(firstName);
			for (UserEntry entry : users.getEntries()) {
				User user = new User();
				String name = entry.getTitle().getPlainText();
				String mail = name + "@" + domain;
				user.setEmail(mail);
				user.setName(name);
				firstName = name;
				UserDAO.newInstance().save(user);
			}

		}

/*
		URL feedUrl = new URL("http://www.google.com/m8/feeds/profiles/domain/beacon-it.co.jp/full");
		ContactsService service = new ContactsService("Google-contactsExampleApp-3");
		service.setUserCredentials("y_yoshida@beacon-it.co.jp", "z1x2c3v4");

		Query myQuery = new Query(feedUrl);
		myQuery.setMaxResults(300);
		ContactFeed resultFeed = service.query(myQuery, ContactFeed.class);

// -----
		for (ContactEntry entry : resultFeed.getEntries()) {
			User user = new User();
			String[] id = entry.getId().split("/");
			String mail = id[id.length-1]+"@"+id[id.length-3];
			user.setEmail(mail);
//			user.setEmail(entry.getEmailAddresses().get(0).getAddress());
			user.setName(entry.getTitle().getPlainText());
			UserDAO.newInstance().save(user);
		}
//------
*/

/*
		int length = resultFeed.getEntries().size();
		int max = 100;
		for (int i = 0; i < length/max; i++) {
			Query query = new Query(feedUrl);
			query.setMaxResults(max);
			query.setStartIndex(i*max+1);

			ContactFeed feed = service.query(query, ContactFeed.class);
			for (ContactEntry entry : feed.getEntries()) {
				User user = new User();
				String[] id = entry.getId().split("/");
				String mail = id[id.length-1]+"@"+id[id.length-3];
				user.setEmail(mail);
				user.setName(entry.getTitle().getPlainText());
				UserDAO.newInstance().save(user);
			}

		}
*/
		return "redirect:index";
	}
}