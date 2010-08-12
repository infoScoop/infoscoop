package org.infoscoop.manager.controller;

import java.util.Date;
import java.util.List;

import org.infoscoop.dao.MenuItemDAO;
import org.infoscoop.dao.model.MenuItem;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MenuController {
	@RequestMapping
	public void index() throws Exception {
	}

	@RequestMapping
	public ModelAndView tree() throws Exception {
		List<MenuItem> items = MenuItemDAO.newInstance().getTree();
		ModelAndView model = new ModelAndView();
		model.addObject("items", items);
		return model;
	}

	@RequestMapping
	public void showAddItem(@RequestParam("id") String parentId, Model model)
			throws Exception {
		MenuItem item = new MenuItem();
		item.setParentId(parentId);
		item.setOrder(0);
		model.addAttribute(item);
	}

	@RequestMapping(method = RequestMethod.POST)
	public MenuItem addItem(MenuItem item) throws Exception {
		item.setId("m_" + new Date().getTime());
		MenuItemDAO.newInstance().save(item);
		return item;
	}

	@RequestMapping(method = RequestMethod.POST)
	public void removeItem(@RequestParam("id") String id) throws Exception {
		MenuItemDAO.newInstance().delete(id);
	}

	@RequestMapping(method = RequestMethod.POST)
	public MenuItem moveItem(@RequestParam("id") String id,
			@RequestParam("parentId") String parentId) throws Exception {
		MenuItemDAO dao = MenuItemDAO.newInstance();
		MenuItem item = dao.get(id);
		item.setParentId(parentId);
		dao.save(item);
		return item;
	}
}
