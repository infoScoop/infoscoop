package org.infoscoop.manager.controller;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xpath.XPathAPI;
import org.infoscoop.account.DomainManager;
import org.infoscoop.dao.GadgetDAO;
import org.infoscoop.dao.GadgetInstanceDAO;
import org.infoscoop.dao.MenuItemDAO;
import org.infoscoop.dao.MenuTreeDAO;
import org.infoscoop.dao.RoleDAO;
import org.infoscoop.dao.model.Gadget;
import org.infoscoop.dao.model.GadgetInstance;
import org.infoscoop.dao.model.GadgetInstanceUserpref;
import org.infoscoop.dao.model.GadgetInstanceUserprefPK;
import org.infoscoop.dao.model.MenuItem;
import org.infoscoop.dao.model.MenuTree;
import org.infoscoop.dao.model.Role;
import org.infoscoop.request.ProxyRequest;
import org.infoscoop.service.GadgetService;
import org.infoscoop.util.XmlUtil;
import org.infoscoop.web.ProxyServlet;
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
import org.w3c.dom.Node;

@Controller
public class MenuController {
	private static Log log = LogFactory.getLog(MenuController.class);

	@Autowired
	private MenuItemDAO menuItemDAO;
	@Autowired
	private MenuTreeDAO menuTreeDAO;
	@Autowired
	private GadgetInstanceDAO gadgetInstanceDAO;

	@RequestMapping(method=RequestMethod.GET)
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
			@RequestParam(value = "title", required = false) String title,
			Model model, Locale locale) throws Exception {
		MenuTree menu = menuTreeDAO.get(menuId);
		MenuItem parentItem = menuItemDAO.getByMenuId(parentId);
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
		item.setAccessLevel(0);
		if (type != null && type.length() > 0) {
			GadgetInstance gadget = new GadgetInstance();
			item.setGadgetInstance(gadget);
			item.getGadgetInstance().setType(type);
			Document gadgetConf = getGadgetConf(type, locale);
			if (title == null || title.length() == 0) {
				Node titleNode = XPathAPI.selectSingleNode(gadgetConf,
						"/Module/ModulePrefs/@title");
				if (titleNode != null)
					title = titleNode.getNodeValue();
			}
			model.addAttribute("conf", gadgetConf);
		}
		item.setTitle(title);
		model.addAttribute("gadget", item);
	}

	@RequestMapping
	@Transactional
	public void showEditItem(@RequestParam("menuId") String menuId, Model model,
			Locale locale) throws Exception {
		MenuItem item = menuItemDAO.getByMenuId(menuId);
		model.addAttribute("gadget", item);
		model.addAttribute("roles", item.getRoles());

		GadgetInstance gadget = item.getGadgetInstance();
		if (gadget != null) {
			String type = gadget.getType();
			if (type != null && type.length() > 0) {
				// lazy=true, but get userprefs ahead of time. Can get ahead with calling any method.
				item.getGadgetInstance().getGadgetInstanceUserPrefs().size();
				model.addAttribute("conf", getGadgetConf(type, locale));
			}
		}
	}

	@RequestMapping(method = RequestMethod.POST)
	@Transactional
	public MenuItem addItem(MenuItem item) throws Exception {
		item.setMenuId("m_" + new Date().getTime());
		GadgetInstance gadget = item.getGadgetInstance();
		if (gadget != null) {
			String type = gadget.getType();
			if (type != null && type.length() > 0) {
				gadget.setTitle(item.getTitle());
				gadget.setHref(item.getHref());
			}
		}
		if(item.getFkParent()!=null)
			item.setFkParent(menuItemDAO.get(item.getFkParent().getId()));
		menuItemDAO.save(item);
		return item;
	}

	@RequestMapping(method = RequestMethod.POST)
	@Transactional
	public MenuItem updateItem(
			MenuItem item,
			@RequestParam(value = "roles.id", required = false) String[] roleIdList)
			throws Exception {
		GadgetInstance gadget = item.getGadgetInstance();
		if (gadget != null) {
			String type = gadget.getType();
			if (type != null && type.length() > 0) {
				gadget.setTitle(item.getTitle());
				gadget.setHref(item.getHref());
			}
		}
		//TODO: edit roles collection, and remove a role in roles collection.
		if (roleIdList != null) {
			for (int i = 0; i < roleIdList.length; i++) {
				Role role = RoleDAO.newInstance().get(roleIdList[i]);
				item.addToRoles(role);
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
		MenuItem parentItem = menuItemDAO.getByMenuId(parentId);
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
		item.setGadgetInstance(gadgetInstance);
		item.setTitle(gadgetInstance.getTitle());
		item.setHref(gadgetInstance.getHref());
		item.setMenuOrder(last != null ? last.getMenuOrder() + 1 : 0);
		item.setAccessLevel(0);

		model.addAttribute(item);
		model.addAttribute("conf", getGadgetConf(gadgetInstance.getType(),
				locale));
		return "menu/showAddItem";
	}

	@RequestMapping(method = RequestMethod.POST)
	@Transactional
	public MenuItem togglePublish(@RequestParam("id") String id)
			throws Exception {
		MenuItem item = menuItemDAO.getByMenuId(id);
		item.toggolePublish();
		menuItemDAO.save(item);
		return item;
	}

	@RequestMapping(method = RequestMethod.POST)
	@Transactional
	public void removeItem(@RequestParam("id") String id) throws Exception {
		MenuItem item = menuItemDAO.getByMenuId(id);
		if (item.getGadgetInstance() != null)
			gadgetInstanceDAO.deleteById(item.getGadgetInstance().getId());
		menuItemDAO.delete(item.getId());
	}

	@RequestMapping(method = RequestMethod.POST)
	@Transactional
	public MenuItem moveItem(@RequestParam("id") String id,
			@RequestParam("parentId") String parentId,
			@RequestParam("refId") String refId,
			@RequestParam("position") String position) throws Exception {
		MenuItem item = menuItemDAO.getByMenuId(id);
		if (parentId.length() > 0) {
			MenuItem parentItem = menuItemDAO.getByMenuId(parentId);
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
		MenuItem parentItem = menuItemDAO.getByMenuId(parentId);
		MenuItem item = menuItemDAO.getByMenuId(id);

		MenuItem newItem = new MenuItem();
		newItem.setMenuId("m_" + new Date().getTime());
		newItem.setFkParent(parentItem);

		newItem.setTitle(item.getTitle());
		newItem.setHref(item.getHref());
		newItem.setAlert(item.getAlert());
		newItem.setMenuOrder(item.getMenuOrder());
		newItem.setAccessLevel(item.getAccessLevel());

		GadgetInstance gadget = item.getGadgetInstance();
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
		newItem.setGadgetInstance(newGadget);
		menuItemDAO.save(newItem);
		return newItem;
	}

	@RequestMapping
	public void getGadgetConf(HttpServletRequest request, Model model)
			throws Exception {
		Locale locale = request.getLocale();
		String uploadGadgets = GadgetService.getHandle().getGadgetJson(locale,
				3000);
		model.addAttribute("gadgets", uploadGadgets);
	}

	private Document getGadgetConf(String type, Locale locale) throws Exception {
		// TODO 言語ごとにDBにキャッシュとして保存する。
		if (type.startsWith("g_")) {

			String url = type.substring(2);
			Document doc = getRemoteGadget(url);
			I18NConverter i18n = new I18NConverter(locale,
					new MessageBundle.Factory.URL(-1, url).createBundles(doc));
			// TODO It's a little dangerous.
			String gadgetXml = i18n.replace(XmlUtil.dom2String(doc), true);
			return XmlUtil.string2Dom(gadgetXml);
		} else {
			Gadget gadget = GadgetDAO.newInstance().select(type);
			String gadgetXml = new String(gadget.getData(), "UTF-8");
			Document gadgetDoc = XmlUtil.string2Dom(gadgetXml);
			I18NConverter i18n = new I18NConverter(locale,
					new MessageBundle.Factory.Upload(0, type)
							.createBundles(gadgetDoc));
			// TODO It's a little dangerous.
			gadgetXml = i18n.replace(gadgetXml, true);
			return XmlUtil.string2Dom(gadgetXml);
		}
	}
	
	private Document getRemoteGadget(String url) throws Exception {
		InputStream is = null;
		ProxyRequest proxyRequest = null;
		try {
			proxyRequest = new ProxyRequest(url, "XML");
			proxyRequest.setTimeout(ProxyServlet.DEFAULT_TIMEOUT);
			int statusCode = proxyRequest.executeGet();
			if (statusCode != 200)
				throw new Exception("gadget url="
						+ proxyRequest.getProxy().getUrl() + ", statucCode="
						+ statusCode);
			if (log.isInfoEnabled())
				log.info("gadget url : " + proxyRequest.getProxy().getUrl());

			is = proxyRequest.getResponseBody();
			is = new BufferedInputStream(is);
		} finally {
			if (proxyRequest != null)
				proxyRequest.close();
		}

		return XmlUtil.stream2Dom(is);
	}
	
	@RequestMapping(method=RequestMethod.GET)
	public void selectRole( Model model){
		List<Role> roles = RoleDAO.newInstance().all();
		model.addAttribute("roles", roles);
	}
}
