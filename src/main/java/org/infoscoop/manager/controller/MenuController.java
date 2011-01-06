package org.infoscoop.manager.controller;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xpath.XPathAPI;
import org.infoscoop.dao.GadgetDAO;
import org.infoscoop.dao.GadgetInstanceDAO;
import org.infoscoop.dao.MenuItemDAO;
import org.infoscoop.dao.MenuTreeDAO;
import org.infoscoop.dao.RoleDAO;
import org.infoscoop.dao.TabTemplatePersonalizeGadgetDAO;
import org.infoscoop.dao.WidgetDAO;
import org.infoscoop.dao.model.Gadget;
import org.infoscoop.dao.model.GadgetInstance;
import org.infoscoop.dao.model.GadgetInstanceUserpref;
import org.infoscoop.dao.model.GadgetInstanceUserprefPK;
import org.infoscoop.dao.model.MenuItem;
import org.infoscoop.dao.model.MenuTree;
import org.infoscoop.dao.model.Role;
import org.infoscoop.dao.model.TabTemplate;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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
	@Autowired
	private TabTemplatePersonalizeGadgetDAO tabTemplatePersonalizeGadgetDAO;

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
	
	@RequestMapping(method = RequestMethod.GET)
	@Transactional
	public void editMenu(
			@RequestParam(value = "id", required = false) Integer id,
			Model model) throws Exception {
		model.addAttribute("menuId", id);
	}
	
	@RequestMapping
	@Transactional
	public String deleteMenu(@RequestParam("id") Integer id) throws Exception {
		MenuTree tree = menuTreeDAO.get(id);
		//remove all menu items and gadget instances which the specified menu tree contains.
		for (MenuItem item : tree.getMenuItems()) {
			removeGadgetInstance(item);
		}
		tree.setMenuItems(null);
		menuTreeDAO.delete(id);
		return "redirect:index";
	}

	@RequestMapping
	@Transactional
	public void tree(@RequestParam(value = "id", required = false) Integer id,
			Model model) throws Exception {
		if (id == null)
			return;
		MenuTree tree = menuTreeDAO.get(id);
		tree.setChildItems();
		model.addAttribute("tree", tree);
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
			@RequestParam(value = "id", required = false) Integer parentId,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "title", required = false) String title,
			Model model, Locale locale) throws Exception {
		MenuTree menu = menuTreeDAO.get(menuId);
		MenuItem item = new MenuItem();
		item.setFkMenuTree(menu);
		if (parentId != null) {
			MenuItem parentItem = menuItemDAO.get(parentId);
			item.setFkParent(parentItem);
		}
		item.setPublish(0);
		item.setMenuOrder(menuItemDAO.getMaxOrder(parentId) + 1);
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
		model.addAttribute(item);
	}

	@RequestMapping
	@Transactional
	public void showEditItem(
			@RequestParam("menuId") Integer menuId, 
			Model model,
			Locale locale) throws Exception {
		MenuItem item = menuItemDAO.get(menuId);
		model.addAttribute(item);
		Set<Role> roles = item.getRoles();
		if(roles == null)
			roles = new HashSet<Role>();
		model.addAttribute("roles", roles);

		if(item.getGadgetInstance() != null)
			model.addAttribute("conf", getGadgetConf(item.getGadgetInstance(), locale));
	}

	@RequestMapping(method = RequestMethod.POST)
	@Transactional
	public MenuItem addItem(MenuItem item) throws Exception {
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
	public String updateItem(
			@Valid MenuItem item,
			BindingResult result,
			@RequestParam(value = "roles.id", required = false) String[] roleIdList,
			Locale locale,
			Model model)
			throws Exception {
		if(result.hasErrors()){
			model.addAttribute(item);
			if(item.getGadgetInstance() != null)
				model.addAttribute("conf", getGadgetConf(item.getGadgetInstance(), locale));
			return "menu/showEditItem";
		}
		GadgetInstance gadget = item.getGadgetInstance();
		if (gadget != null) {
			String type = gadget.getType();
			if (type != null && type.length() > 0) {
				gadget.setTitle(item.getTitle());
				gadget.setHref(item.getHref());
			}
		}
		if (roleIdList != null) {
			for (int i = 0; i < roleIdList.length; i++) {
				Role role = RoleDAO.newInstance().get(roleIdList[i]);
				item.addToRoles(role);
			}
		}
		menuItemDAO.save(item);
		if(item.isApplyToUsersGadgets())
			WidgetDAO.newInstance().markMenuItemUpdated(item.getId());
		return "menu/updateItem";
	}

	@RequestMapping(method = RequestMethod.POST)
	@Transactional
	public String showEditInstance(@RequestParam("instanceId") int instanceId,
			@RequestParam("id") Integer parentId, Locale locale, Model model)
			throws Exception {
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
		item.setGadgetInstance(gadgetInstance);
		item.setTitle(gadgetInstance.getTitle());
		item.setHref(gadgetInstance.getHref());
		item.setMenuOrder(last != null ? last.getMenuOrder() + 1 : 0);

		model.addAttribute(item);
		model.addAttribute("conf", getGadgetConf(gadgetInstance,
				locale));
		return "menu/showAddItem";
	}
	
	@RequestMapping
	@Transactional
	public void showEditTree(
			@RequestParam(value = "id", required = false) Integer id,
			Model model) throws Exception {
		MenuTree tree = null;
		if (id == null) {
			tree = new MenuTree();
			tree.setPublish(0);
			tree.setOrderIndex(-1);//last
		} else {
			tree = menuTreeDAO.get(id);
		}
		model.addAttribute(tree);
		Set<Role> roles = tree.getRoles();
		if(roles == null)
			roles = new HashSet<Role>();
		model.addAttribute("roles", roles);
	}

	@RequestMapping(method = RequestMethod.POST)
	@Transactional
	public String updateTree(
			@Valid MenuTree tree,
			BindingResult result,
			@RequestParam(value = "roles.id", required = false) String[] roleIdList,
			Locale locale,
			Model model)
			throws Exception {
		if(result.hasErrors()){
			model.addAttribute(tree);
			return "menu/showEditTree";
		}
		if (roleIdList != null) {
			for (int i = 0; i < roleIdList.length; i++) {
				Role role = RoleDAO.newInstance().get(roleIdList[i]);
				tree.addToRoles(role);
			}
		}
		if(tree.getOrderIndex() == -1){
			tree.setOrderIndex(menuTreeDAO.getMaxOrderIndex() + 1);
		}
		menuTreeDAO.save(tree);
		return "redirect:index";
	}

	@RequestMapping(method = RequestMethod.POST)
	@Transactional
	public @ResponseBody
	String sort(@RequestParam("menuId") String[] menuIds, Model model) {
		int order = 0;
		for (String menuId : menuIds) {
			MenuTree tree = menuTreeDAO.get(Integer.parseInt(menuId));
			tree.setOrderIndex(order++);
			menuTreeDAO.save(tree);
		}
		return "success to sort Menu trees";
	}

	@RequestMapping(method = RequestMethod.POST)
	@Transactional
	public MenuItem togglePublish(@RequestParam("id") Integer id)
			throws Exception {
		MenuItem item = menuItemDAO.get(id);
		item.toggolePublish();
		menuItemDAO.save(item);
		return item;
	}

	@RequestMapping(method = RequestMethod.POST)
	@Transactional
	public MenuItem removeItem(@RequestParam("id") Integer id) throws Exception {
		MenuItem item = menuItemDAO.get(id);
		removeGadgetInstance(item);
		menuItemDAO.delete(item.getId());
		return item;
	}
	
	/**
	 * remove all gadget instances of the specified MenuItem's ancestors
	 * @param item
	 */
	private void removeGadgetInstance(MenuItem item) {
		if (item.getGadgetInstance() != null) {
			gadgetInstanceDAO.deleteById(item.getGadgetInstance().getId());
			tabTemplatePersonalizeGadgetDAO.deleteByGadgetInstanceId(item
					.getGadgetInstance().getId());
		}
		for (MenuItem m : item.getChildItems()) {
			removeGadgetInstance(m);
		}
	}

	@RequestMapping(method = RequestMethod.POST)
	@Transactional
	public MenuItem moveItem(@RequestParam("id") Integer id,
			@RequestParam("sibling[]") Integer[] siblings,
			@RequestParam(value = "parentId", required = false) Integer parentId)
			throws Exception {
		MenuItem item = menuItemDAO.get(id);
		if (parentId != null) {
			MenuItem parentItem = menuItemDAO.get(parentId);
			item.setFkParent(parentItem);
		} else {
			item.setFkParent(null);
		}
		int order = 0;
		for(Integer siblingId : siblings){
			MenuItem siblingItem = menuItemDAO.get(siblingId);
			siblingItem.setMenuOrder(order++);
			menuItemDAO.save(siblingItem);
		}
		return item;
	}

	/**
	 * 
	 * @param id copied menu item
	 * @param parentId parent menu item which above item is copied to.
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method = RequestMethod.POST)
	@Transactional
	public MenuItem copyItem(@RequestParam("id") Integer id,
			@RequestParam("parentId") Integer parentId) throws Exception {
		MenuItem parentItem = menuItemDAO.get(parentId);
		MenuItem item = menuItemDAO.get(id);

		MenuItem newItem = new MenuItem();
		newItem.setFkParent(parentItem);

		newItem.setTitle(item.getTitle());
		newItem.setHref(item.getHref());
		newItem.setAlert(item.getAlert());
		newItem.setMenuOrder(menuItemDAO.getMaxOrder(parentId) + 1);
		newItem.setPublish(item.getPublish());
		newItem.setFkMenuTree(item.getFkMenuTree());

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

	private Document getGadgetConf(GadgetInstance gadget, Locale locale) throws Exception {
		String type = gadget.getType();
		if (type != null && type.length() > 0) {
			gadget.getGadgetInstanceUserPrefs().size();
		}
		return getGadgetConf(gadget.getType(), locale);
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
}
