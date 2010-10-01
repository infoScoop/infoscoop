package org.infoscoop.manager.controller;

import java.util.List;

import org.infoscoop.dao.GroupDAO;
import org.infoscoop.dao.UserDAO;
import org.infoscoop.dao.model.Group;
import org.infoscoop.dao.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import sample.appsforyourdomain.AppsForYourDomainClient;

import com.google.gdata.data.Link;
import com.google.gdata.data.appsforyourdomain.EmailList;
import com.google.gdata.data.appsforyourdomain.provisioning.EmailListEntry;
import com.google.gdata.data.appsforyourdomain.provisioning.EmailListFeed;
import com.google.gdata.data.appsforyourdomain.provisioning.EmailListRecipientEntry;
import com.google.gdata.data.appsforyourdomain.provisioning.EmailListRecipientFeed;
import com.google.gdata.data.appsforyourdomain.provisioning.UserEntry;
import com.google.gdata.data.appsforyourdomain.provisioning.UserFeed;

@Controller
public class UserController {
	@RequestMapping(method=RequestMethod.GET)
	public void index(Model model) throws Exception {
	}

	@RequestMapping(method=RequestMethod.GET)
	public void sync(Model model) throws Exception {
		
		String adminAddress = "y_yoshida@beacon-it.co.jp";
		String domain = "beacon-it.co.jp";
		String password = "z1x2c3v4";
		AppsForYourDomainClient client = new AppsForYourDomainClient(adminAddress, password, domain);
		long start = System.currentTimeMillis();
		String firstName = null;
		int userCount = 0;
		int pageSize = 100;
		Link nextLink = null;
		UserDAO userDAO = UserDAO.newInstance();
		do {
			UserFeed users = client.retrievePageOfUsers(firstName);

			for (UserEntry entry : users.getEntries()) {
				String name = entry.getTitle().getPlainText();
				User user = userDAO.getByName(name);
				if(user == null)user = new User();
				String mail = name + "@" + domain;
				user.setEmail(mail);
				user.setName(name);
				firstName = name;
				userDAO.save(user);
			}
			nextLink = users.getNextLink();
			pageSize = users.getEntries().size();
			userCount += pageSize;
			if(nextLink != null)userCount--;
			
		}while(nextLink != null);
		
		model.addAttribute("userCount", userCount);

		int groupCount = 0;
		firstName = null;
		GroupDAO groupDAO = GroupDAO.newInstance();
		do {
			EmailListFeed groups = client.retrievePageOfEmailLists(firstName);
			for (EmailListEntry entry : groups.getEntries()) {
				String name = entry.getTitle().getPlainText();
				EmailList list = entry.getEmailList();
				Group group = groupDAO.getByName(name);
				if(group == null) group = new Group();
				group.setName(name);
				EmailListRecipientFeed recipients = client.retrieveAllRecipients(name);
				for(EmailListRecipientEntry recipient : recipients.getEntries()){
					String userName = recipient.getTitle().getPlainText().split("@")[0];
					User user = userDAO.getByName(userName);
					if(user != null)
						group.addToUsers(user);
					else
						System.out.println("User " + userName + " is missing.");
				}
				groupDAO.save(group);
				firstName = name;
			}
			nextLink = groups.getNextLink();
			pageSize = groups.getEntries().size();
			groupCount += pageSize;
			if(nextLink != null)userCount--;
		}while(nextLink != null);
		
		model.addAttribute("groupCount", groupCount);
		System.out.println(System.currentTimeMillis() - start);

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
	}
}