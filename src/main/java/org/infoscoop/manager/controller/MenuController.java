package org.infoscoop.manager.controller;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.infoscoop.dao.GadgetDAO;
import org.infoscoop.dao.MenuItemDAO;
import org.infoscoop.dao.WidgetConfDAO;
import org.infoscoop.dao.model.MenuItem;
import org.infoscoop.service.GadgetService;
import org.infoscoop.service.WidgetConfService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.w3c.dom.Element;

@Controller
public class MenuController {
	@RequestMapping
	public void index() throws Exception {
	}

	@RequestMapping
	public void tree(Model model) throws Exception {
		List<MenuItem> items = MenuItemDAO.newInstance().getTree();
		model.addAttribute("items", items);
	}

	@RequestMapping
	public void selectGadgetType(@RequestParam("id") String parentId,
			Model model) throws Exception {
		model.addAttribute("parentId", parentId);
	}

	@RequestMapping
	public void showAddItem(@RequestParam("id") String parentId,
			@RequestParam("type") String type, Model model) throws Exception {
		MenuItem item = new MenuItem();
		item.setParentId(parentId);
		item.setType(type);
		item.setOrder(0);
		item.setPublish(0);
		model.addAttribute(item);

		//TODO 国際化処理して言語ごとにDBにキャッシュとして保存する。そしてそれを取得する。
		Element conf = null;
		if (type.startsWith("upload__")) {
			conf = GadgetDAO.newInstance().getGadgetElement(type.substring(8));
		} else {
			conf = WidgetConfDAO.newInstance().getElement(type);
		}
		model.addAttribute("conf", conf.getOwnerDocument());
	}

	@RequestMapping
	public void showEditItem(@RequestParam("id") String id, Model model)
			throws Exception {
		MenuItem item = MenuItemDAO.newInstance().get(id);
		model.addAttribute(item);

		String type = item.getType();
		Element conf = WidgetConfDAO.newInstance().getElement(type);
		if (conf == null)
			conf = GadgetDAO.newInstance().getGadgetElement(type);
		model.addAttribute("conf", conf.getOwnerDocument());
	}

	@RequestMapping(method = RequestMethod.POST)
	public MenuItem addItem(MenuItem item) throws Exception {
		item.setId("m_" + new Date().getTime());
		MenuItemDAO.newInstance().save(item);
		return item;
	}

	@RequestMapping(method = RequestMethod.POST)
	public MenuItem updateItem(MenuItem item) throws Exception {
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

	@RequestMapping
	public void getGadgetConf(HttpServletRequest request, Model model)
			throws Exception {
		Locale locale = request.getLocale();
		String buildinGadgets = WidgetConfService.getHandle()
				.getWidgetConfsJson(locale);
		String uploadGadgets = GadgetService.getHandle().getGadgetJson(locale,
				3000);
		model.addAttribute("buildin", buildinGadgets);
		model.addAttribute("upload", uploadGadgets);
	}
}
