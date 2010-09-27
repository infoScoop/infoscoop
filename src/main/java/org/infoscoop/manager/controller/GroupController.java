package org.infoscoop.manager.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.infoscoop.dao.GroupDAO;
import org.infoscoop.dao.UserDAO;
import org.infoscoop.dao.model.User;
import org.infoscoop.dao.model.Group;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import sample.appsforyourdomain.AppsForYourDomainClient;

import com.google.gdata.data.appsforyourdomain.Email;
import com.google.gdata.data.appsforyourdomain.provisioning.EmailListEntry;
import com.google.gdata.data.appsforyourdomain.provisioning.EmailListFeed;
import com.google.gdata.data.appsforyourdomain.provisioning.UserEntry;
import com.google.gdata.data.appsforyourdomain.provisioning.UserFeed;


@Controller
public class GroupController {
	@RequestMapping(method=RequestMethod.GET)
	public void index(Model model) throws Exception {
		List<Group> groups = GroupDAO.newInstance().all();
		System.out.print(groups);
		model.addAttribute("groups", groups);
	}

	@RequestMapping(method=RequestMethod.GET)
	public String save(Model model) throws Exception {

		String adminAddress = "y_yoshida@beacon-it.co.jp";
		String domain = "beacon-it.co.jp";
		String password = "z1x2c3v4";
		AppsForYourDomainClient client = new AppsForYourDomainClient(adminAddress, password, domain);

		int size = client.retrieveAllEmailLists().getEntries().size();
		int max = 100;
		String firstName = null;
		for (int i = 0; i < size/max+1; i++) {
			System.out.println("firstName ="+ firstName);
			EmailListFeed groups = client.retrievePageOfEmailLists(firstName);
			for (EmailListEntry entry : groups.getEntries()) {
				String name = entry.getTitle().getPlainText();
				Group group = new Group();
				group.setName(name);
				GroupDAO.newInstance().save(group);
				firstName = name;
			}
		}

		return "redirect:index";
	}
}