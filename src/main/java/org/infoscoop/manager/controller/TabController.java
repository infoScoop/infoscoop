package org.infoscoop.manager.controller;

import java.util.Date;
import java.util.List;

import org.infoscoop.dao.TabTemplateDAO;
import org.infoscoop.dao.model.TabTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TabController {
	@RequestMapping
	public void index(Model model)throws Exception {
		List<TabTemplate> tabs = TabTemplateDAO.newInstance().all();
		System.out.print(tabs);
		model.addAttribute("tabs", tabs);
	}
	
	@RequestMapping
	public void showAddTab(Model model)
			throws Exception {
		TabTemplate tab = new TabTemplate();
		model.addAttribute(tab);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public void addTab(TabTemplate tab)throws Exception {
		tab.setId("m_" + new Date().getTime());
		TabTemplateDAO.newInstance().save(tab);
	}
}
