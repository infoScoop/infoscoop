package org.infoscoop.manager.controller;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.infoscoop.dao.GadgetDAO;
import org.infoscoop.dao.MenuItemDAO;
import org.infoscoop.dao.WidgetConfDAO;
import org.infoscoop.dao.model.GadgetInstance;
import org.infoscoop.dao.model.MenuItem;
import org.infoscoop.service.GadgetService;
import org.infoscoop.service.WidgetConfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@Controller
public class MenuController {
	@Autowired
	private MenuItemDAO menuItemDAO;
	
	@RequestMapping
	public void index() throws Exception {
	}

	@RequestMapping
	@Transactional
	public void tree(Model model) throws Exception {
		List<MenuItem> items = menuItemDAO.getTree();
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
		MenuItem parentItem = menuItemDAO.get(parentId);
		MenuItem item = new MenuItem();
		GadgetInstance gadget = new GadgetInstance();
		item.setFkParent(parentItem);
		item.setFkGadgetInstance(gadget);
		item.getFkGadgetInstance().setType(type);
		item.setMenuOrder(0);
		item.setPublish(0);
		model.addAttribute(item);

		model.addAttribute("conf", getGadgetConf(type));
	}

	@RequestMapping
	@Transactional
	public void showEditItem(@RequestParam("id") String id, Model model)
			throws Exception {
		MenuItem item = menuItemDAO.get(id);
		model.addAttribute(item);

		String type = item.getFkGadgetInstance().getType();
		//lazy=trueだが、Viewに渡すために事前に取得する。何かメソッド呼ぶと事前に取得できる。
		item.getFkGadgetInstance().getGadgetInstanceUserPrefs().size();

		model.addAttribute("conf", getGadgetConf(type));
	}

	@RequestMapping(method = RequestMethod.POST)
	@Transactional
	public MenuItem addItem(MenuItem item) throws Exception {
		item.setId("m_" + new Date().getTime());
		item.getFkGadgetInstance().setTitle(item.getTitle());
		item.getFkGadgetInstance().setHref(item.getHref());
		
		menuItemDAO.save(item);
		return item;
	}

	@RequestMapping(method = RequestMethod.POST)
	@Transactional
	public MenuItem updateItem(MenuItem item) throws Exception {
		item.getFkGadgetInstance().setTitle(item.getTitle());
		item.getFkGadgetInstance().setHref(item.getHref());
		menuItemDAO.save(item);
		//menuItemDAO.save(item);
		return item;
	}
	
	@RequestMapping(method = RequestMethod.POST)
	@Transactional
	public MenuItem togglePublish(@RequestParam("id") String id)
			throws Exception {
		MenuItem item = menuItemDAO.get(id);
		item.toggolePublish();
		menuItemDAO.save(item);
		return item;
	}

	@RequestMapping(method = RequestMethod.POST)
	@Transactional
	public void removeItem(@RequestParam("id") String id) throws Exception {
		menuItemDAO.delete(id);
	}

	@RequestMapping(method = RequestMethod.POST)
	@Transactional
	public MenuItem moveItem(@RequestParam("id") String id,
			@RequestParam("parentId") String parentId) throws Exception {
		MenuItem parentItem = menuItemDAO.get(parentId);
		MenuItem item = menuItemDAO.get(id);
		item.setFkParent(parentItem);
		menuItemDAO.save(item);
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
	
	private Document getGadgetConf(String type) {
		//TODO 国際化処理して言語ごとにDBにキャッシュとして保存する。そしてそれを取得する。
		Element conf = null;
		if (type.startsWith("upload__")) {
			conf = GadgetDAO.newInstance().getGadgetElement(type.substring(8));
		} else {
			conf = WidgetConfDAO.newInstance().getElement(type);
		}
		return conf.getOwnerDocument();
	}
}
