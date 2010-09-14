package org.infoscoop.manager.controller;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.infoscoop.dao.GadgetDAO;
import org.infoscoop.dao.GadgetInstanceDAO;
import org.infoscoop.dao.MenuItemDAO;
import org.infoscoop.dao.MenuTreeDAO;
import org.infoscoop.dao.WidgetConfDAO;
import org.infoscoop.dao.model.Gadget;
import org.infoscoop.dao.model.GadgetInstance;
import org.infoscoop.dao.model.GadgetInstanceUserpref;
import org.infoscoop.dao.model.GadgetInstanceUserprefPK;
import org.infoscoop.dao.model.MenuItem;
import org.infoscoop.dao.model.MenuTree;
import org.infoscoop.service.GadgetService;
import org.infoscoop.service.WidgetConfService;
import org.infoscoop.util.I18NUtil;
import org.infoscoop.util.XmlUtil;
import org.infoscoop.widgetconf.I18NConverter;
import org.infoscoop.widgetconf.MessageBundle;
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
	@Autowired
	private MenuTreeDAO menuTreeDAO;

	@RequestMapping
	@Transactional
	public void index(Model model) throws Exception {
		List<MenuTree> menus = menuTreeDAO.all();
		model.addAttribute("menus", menus);
	}

	@RequestMapping(method = RequestMethod.POST)
	@Transactional
	public void newMenu(Model model) throws Exception {
		MenuTree menu = new MenuTree();
		menu.setTitle("untitled");
		menuTreeDAO.save(menu);
		model.addAttribute("menu", menu);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	@Transactional
	public MenuTree changePosition(@RequestParam("id") Integer id,
			@RequestParam("position") String position) throws Exception {
		MenuTree menu = menuTreeDAO.get(id);
		menuTreeDAO.updatePosition(menu, position);
		return menu;
	}

	@RequestMapping(method = RequestMethod.POST)
	@Transactional
	public MenuTree saveTitle(@RequestParam("id") Integer id,
			@RequestParam("title") String title) throws Exception {
		MenuTree menu = menuTreeDAO.get(id);
		menu.setTitle(title);
		return menu;
	}
	
	@RequestMapping
	@Transactional
	public void editMenu(
			@RequestParam(value = "id", required = false) Integer id,
			Model model) throws Exception {
		model.addAttribute("menuId", id);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	@Transactional
	public void deleteMenu(@RequestParam("id") Integer id) throws Exception {
		menuTreeDAO.delete(id);
	}

	@RequestMapping
	@Transactional
	public void tree(@RequestParam(value = "id", required = false) Integer id,
			Model model) throws Exception {
		if (id == null)
			return;
		List<MenuItem> items = menuItemDAO.getTree(id);
		model.addAttribute("items", items);
	}

	@RequestMapping
	public void selectGadgetInstance(@RequestParam("id") String parentId,
			Model model) throws Exception {
		GadgetInstanceDAO dao = GadgetInstanceDAO.newInstance();
		List<GadgetInstance> gadgetInstances = dao.all();
		model.addAttribute("instances", gadgetInstances);
		model.addAttribute("parentId", parentId);
	}

	@RequestMapping
	public void selectGadgetType(@RequestParam("id") String parentId,
			Model model) throws Exception {
		model.addAttribute("parentId", parentId);
	}

	@RequestMapping
	@Transactional
	public void showAddItem(@RequestParam("menuId") int menuId,
			@RequestParam("id") String parentId,
			@RequestParam(value = "type", required = false) String type,
			Model model, Locale locale) throws Exception {
		MenuTree menu = menuTreeDAO.get(menuId);
		MenuItem parentItem = menuItemDAO.get(parentId);
		MenuItem last =null;
		if (parentId.length() > 0) {
			last = menuItemDAO.getLastChild(parentId);
		} else {
			List<MenuItem> tops = menuItemDAO.getTops(menu);
			if (tops != null && tops.size() > 0)
				last = tops.get(tops.size() - 1);
		}
		MenuItem item = new MenuItem();
		item.setFkMenuTree(menu);
		item.setFkParent(parentItem);
		item.setMenuOrder(last != null ? last.getMenuOrder() + 1 : 0);
		item.setPublish(0);
		if (type != null && type.length() > 0) {
			GadgetInstance gadget = new GadgetInstance();
			item.setFkGadgetInstance(gadget);
			item.getFkGadgetInstance().setType(type);
			model.addAttribute("conf", getGadgetConf(type, locale));
		}
		model.addAttribute(item);
	}

	@RequestMapping
	@Transactional
	public void showEditItem(@RequestParam("id") String id, Model model,
			Locale locale) throws Exception {
		MenuItem item = menuItemDAO.get(id);
		model.addAttribute(item);

		GadgetInstance gadget = item.getFkGadgetInstance();
		if (gadget != null) {
			String type = gadget.getType();
			if (type != null && type.length() > 0) {
				// lazy=trueだが、Viewに渡すために事前に取得する。何かメソッド呼ぶと事前に取得できる。
				item.getFkGadgetInstance().getGadgetInstanceUserPrefs().size();
				model.addAttribute("conf", getGadgetConf(type, locale));
			}
		}
	}

	@RequestMapping(method = RequestMethod.POST)
	@Transactional
	public MenuItem addItem(MenuItem item) throws Exception {
		item.setId("m_" + new Date().getTime());
		GadgetInstance gadget = item.getFkGadgetInstance();
		if (gadget != null) {
			String type = gadget.getType();
			if (type != null && type.length() > 0) {
				gadget.setTitle(item.getTitle());
				gadget.setHref(item.getHref());
			}
		}
		menuItemDAO.save(item);
		return item;
	}

	@RequestMapping(method = RequestMethod.POST)
	@Transactional
	public MenuItem updateItem(MenuItem item) throws Exception {
		GadgetInstance gadget = item.getFkGadgetInstance();
		if (gadget != null) {
			String type = gadget.getType();
			if (type != null && type.length() > 0) {
				gadget.setTitle(item.getTitle());
				gadget.setHref(item.getHref());
			}
		}
		menuItemDAO.save(item);
		return item;
	}

	@RequestMapping(method = RequestMethod.POST)
	@Transactional
	public String showEditInstance(@RequestParam("instanceId") int instanceId,
			@RequestParam("id") String parentId, Locale locale, Model model)
			throws Exception {
		if(parentId.length() == 0)
			throw new Exception("parentId is required.");
		MenuItem item = new MenuItem();
		MenuItem parentItem = menuItemDAO.get(parentId);
		if (parentItem == null)
			throw new Exception("A menu item which id is \"" + parentId
					+ "\" is not found.");
		item.setFkParent(parentItem);
		item.setFkMenuTree(parentItem.getFkMenuTree());
		MenuItem last =menuItemDAO.getLastChild(parentId);
		GadgetInstanceDAO dao = GadgetInstanceDAO.newInstance();
		GadgetInstance gadgetInstance = dao.get(instanceId);
		// lazy=true, but get userprefs ahead of time. Can get ahead with calling any method.
		gadgetInstance.getGadgetInstanceUserPrefs().size();
		item.setFkGadgetInstance(gadgetInstance);
		item.setTitle(gadgetInstance.getTitle());
		item.setHref(gadgetInstance.getHref());
		item.setMenuOrder(last != null ? last.getMenuOrder() + 1 : 0);
		item.setPublish(0);

		model.addAttribute(item);
		model.addAttribute("conf", getGadgetConf(gadgetInstance.getType(),
				locale));
		return "menu/showAddItem";
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
			@RequestParam("parentId") String parentId,
			@RequestParam("refId") String refId,
			@RequestParam("position") String position) throws Exception {
		MenuItem item = menuItemDAO.get(id);
		if (parentId.length() > 0) {
			MenuItem parentItem = menuItemDAO.get(parentId);
			item.setFkParent(parentItem);
		}
		if (position.equals("last")) {
			MenuItem last = menuItemDAO.getLastChild(parentId);
			item.setMenuOrder(last != null ? last.getMenuOrder() + 1 : 0);
			menuItemDAO.save(item);
		} else {
			//loop all sibling items and renumber them.
			List<MenuItem> siblings = parentId.length() > 0 ? menuItemDAO
					.getByParentId(parentId) : menuItemDAO.getTops(item
					.getFkMenuTree());
			int nextOrder = 0;
			if (siblings != null) {
				for (MenuItem sibling : siblings) {
					if (sibling.getId().equals(refId)) {
						if (position.equals("before")) {
							item.setMenuOrder(nextOrder);
							sibling.setMenuOrder(++nextOrder);
							menuItemDAO.save(item);
							menuItemDAO.save(sibling);
						} else {
							sibling.setMenuOrder(nextOrder);
							item.setMenuOrder(++nextOrder);
							menuItemDAO.save(sibling);
							menuItemDAO.save(item);
						}
						nextOrder++;
					} else if (!sibling.getId().equals(id)) {
						sibling.setMenuOrder(nextOrder);
						menuItemDAO.save(sibling);
						nextOrder++;
					}
				}
			}
		}
		return item;
	}

	@RequestMapping(method = RequestMethod.POST)
	@Transactional
	public MenuItem copyItem(@RequestParam("id") String id,
			@RequestParam("parentId") String parentId) throws Exception {
		MenuItem parentItem = menuItemDAO.get(parentId);
		MenuItem item = menuItemDAO.get(id);

		MenuItem newItem = new MenuItem();

		newItem.setId("m_" + new Date().getTime());
		newItem.setFkParent(parentItem);

		newItem.setTitle(item.getTitle());
		newItem.setHref(item.getHref());
		newItem.setAlert(item.getAlert());
		newItem.setMenuOrder(item.getMenuOrder());
		newItem.setPublish(item.getPublish());

		GadgetInstance gadget = item.getFkGadgetInstance();
		GadgetInstance newGadget = new GadgetInstance();
		newGadget.setTitle(gadget.getTitle());
		newGadget.setType(gadget.getType());
		newGadget.setHref(gadget.getHref());
		Set<GadgetInstanceUserpref> newUps = new HashSet<GadgetInstanceUserpref>();
		Set<GadgetInstanceUserpref> ups = gadget.getGadgetInstanceUserPrefs();
		if (ups != null) {
			for (GadgetInstanceUserpref up : ups) {
				GadgetInstanceUserpref newUp = new GadgetInstanceUserpref(
						new GadgetInstanceUserprefPK(newGadget, up.getId()
								.getName()));
				newUp.setValue(up.getValue());
				newUps.add(newUp);
			}
		}
		newGadget.setGadgetInstanceUserPrefs(newUps);
		newItem.setFkGadgetInstance(newGadget);
		menuItemDAO.save(newItem);
		return newItem;
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

	private Document getGadgetConf(String type, Locale locale) throws Exception {
		// TODO 言語ごとにDBにキャッシュとして保存する。
		Document conf = null;
		if (type.startsWith("upload__")) {
			String realType = type.substring(8);// upload__を除く
			Gadget gadget = GadgetDAO.newInstance().select(realType);
			String gadgetXml = new String(gadget.getData(), "UTF-8");
			Document gadgetDoc = (Document) XmlUtil.string2Dom(gadgetXml);
			I18NConverter i18n = new I18NConverter(locale,
					new MessageBundle.Factory.Upload(0, realType)
							.createBundles(gadgetDoc));
			gadgetXml = i18n.replace(gadgetXml);
			conf = (Document) XmlUtil.string2Dom(gadgetXml);
		} else {
			Element widgetConfElm = WidgetConfDAO.newInstance()
					.getElement(type);
			String widgetXml = XmlUtil.dom2String(widgetConfElm);
			widgetXml = I18NUtil.resolveForXML(I18NUtil.TYPE_WIDGET, widgetXml,
					locale);
			conf = (Document) XmlUtil.string2Dom(widgetXml);
		}
		return conf;
	}
}
