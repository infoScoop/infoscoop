package org.infoscoop.manager.controller;

import java.util.Date;
import java.util.List;

import org.infoscoop.dao.MenuItemDAO;
import org.infoscoop.dao.model.MenuItem;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MenuController {
	@RequestMapping
	public void index() throws Exception {
	}

	@RequestMapping
	public ModelAndView data(@RequestParam("id") String menuId)
			throws Exception {
		List<MenuItem> items = null;
		if (menuId.equals("0")) {
			items = MenuItemDAO.newInstance().getTopItems();
		} else {
			items = MenuItemDAO.newInstance().getChildItems(menuId);
		}
		// if (items.size() == 0)
		// throw new NotFountException();

		ModelAndView model = new ModelAndView();
		model.addObject("items", items);
		return model;
	}

	@RequestMapping
	public ModelAndView showAddItem(@RequestParam("id") String parentId)
			throws Exception {
		ModelAndView model = new ModelAndView();
		model.addObject("parentId", parentId);
		return model;
	}

	@RequestMapping
	public MenuItem addItem(@RequestParam("parentId") String parentId,
			@RequestParam("title") String title) throws Exception {
		MenuItem item = new MenuItem();
		item.setId("m_" + new Date().getTime());
		item.setTitle(title);
		item.setParentId(parentId);
		MenuItemDAO.newInstance().save(item);
		return item;
	}

	@RequestMapping
	public void removeItem(@RequestParam("id") String id) throws Exception {
		MenuItemDAO.newInstance().delete(id);
	}
}
