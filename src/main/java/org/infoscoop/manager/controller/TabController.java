package org.infoscoop.manager.controller;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xpath.XPathAPI;
import org.infoscoop.account.DomainManager;
import org.infoscoop.command.XMLCommandProcessor;
import org.infoscoop.command.util.XMLCommandUtil;
import org.infoscoop.dao.GadgetDAO;
import org.infoscoop.dao.GadgetInstanceDAO;
import org.infoscoop.dao.MenuItemDAO;
import org.infoscoop.dao.RoleDAO;
import org.infoscoop.dao.TabDAO;
import org.infoscoop.dao.TabTemplateDAO;
import org.infoscoop.dao.TabTemplatePersonalizeGadgetDAO;
import org.infoscoop.dao.TabTemplateStaticGadgetDAO;
import org.infoscoop.dao.UserDAO;
import org.infoscoop.dao.WidgetDAO;
import org.infoscoop.dao.model.Gadget;
import org.infoscoop.dao.model.GadgetInstance;
import org.infoscoop.dao.model.MenuItem;
import org.infoscoop.dao.model.Role;
import org.infoscoop.dao.model.TabTemplate;
import org.infoscoop.dao.model.TabTemplatePersonalizeGadget;
import org.infoscoop.dao.model.TabTemplateStaticGadget;
import org.infoscoop.dao.model.User;
import org.infoscoop.dao.model.Widget;
import org.infoscoop.request.ProxyRequest;
import org.infoscoop.service.GadgetService;
import org.infoscoop.util.SpringUtil;
import org.infoscoop.util.XmlUtil;
import org.infoscoop.util.spring.TextView;
import org.infoscoop.util.spring.XmlView;
import org.infoscoop.web.ProxyServlet;
import org.infoscoop.widgetconf.I18NConverter;
import org.infoscoop.widgetconf.MessageBundle;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@Controller
public class TabController {
	private static Log log = LogFactory.getLog(TabController.class);
	
	private static final String LAYOUT_01 = "<table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr><td width=\"75%\"><table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr><td style=\"width:33%\"><div class=\"static_column\" style=\"width: 99%; height:82px; min-height: 1px;\"></div></td><td><div style=\"width:10px\">&nbsp;</div></td><td style=\"width:33%\"><div class=\"static_column\" style=\"width: 99%; height:82px; min-height: 1px;\"></div></td><td><div style=\"width:10px\">&nbsp;</div></td><td style=\"width:34%\"><div class=\"static_column\" style=\"width: 99%; height:82px; min-height: 1px;\"></div></td></tr></table></td></tr></table>";
	private static final String LAYOUT_2 = "<table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr><td style=\"width:33%\"><div class=\"static_column\" style=\"width: 99%; min-height: 1px;\"></div></td><td><div style=\"width:10px\">&nbsp;</div></td><td style=\"width:33%\"><div class=\"static_column\" style=\"width: 99%; min-height: 1px;\"></div></td><td><div style=\"width:10px\">&nbsp;</div></td><td style=\"width:34%\"><div class=\"static_column\" style=\"width: 99%; min-height: 1px;\"></div></td></tr></table>";
	
	@Autowired
	private TabTemplateDAO tabTemplateDAO;
	@Autowired
	private TabTemplateStaticGadgetDAO tabTemplateStaticGadgetDAO;
	@Autowired
	private UserDAO userDAO;
	@Autowired
	private TabTemplatePersonalizeGadgetDAO tabTemplatePersonalizeGadgetDAO; 
	
	@RequestMapping
	public void index(Model model)throws Exception {
		List<TabTemplate> tabs = tabTemplateDAO.all();
		List<TabTemplate> tabsTemp0 = new ArrayList<TabTemplate>();
		for(TabTemplate tab: tabs){
			if(tab.getStatus() == 0){
				tabsTemp0.add(tab);
			}
		}
		model.addAttribute("tabs", tabsTemp0);
	}
	
	@RequestMapping
	public void history(
			@RequestParam(value = "id", required = false) String tabId,
			Model model) throws Exception {
		List<TabTemplate> tabs = tabTemplateDAO.getHisotry(tabId);
		TabTemplate currentTab = tabTemplateDAO.getByTabId(tabId);
		tabs.add(0, currentTab);
		model.addAttribute("tabs", tabs);
		model.addAttribute("currentTab", currentTab);
	}

	@RequestMapping
	@Transactional
	public String newTab(HttpServletRequest request,
			@RequestParam(value = "id", required = false) String tabId,
			Model model) throws Exception {
		String uid = (String) request.getSession().getAttribute("Uid");
		TabTemplate tab = new TabTemplate();
		tab.setTabId("t_" + new Date().getTime());
		tab.setName("New Tab");
		tab.setOrderIndex(tabTemplateDAO.getMaxOrderIndex() + 1);
		tab.setStatus(1);
		Integer domainId = DomainManager.getContextDomainId();
		User loginUser = userDAO.getByEmail(uid, domainId);
		tab.setEditor(loginUser);
		tab.setLayout(LAYOUT_01);
		tabTemplateDAO.save(tab);
		model.addAttribute(tab);
		model.addAttribute("editors", "{}");
		return "tab/editTab";
	}
	
	@RequestMapping
	@Transactional
	public void editTab(HttpServletRequest request,
			@RequestParam(value = "id", required = false) String id, Model model)
			throws Exception {
		String uid = (String) request.getSession().getAttribute("Uid");
		TabTemplate tab = tabTemplateDAO.get(id);
		
		JSONArray editors = getEditors(tab.getTabId(), uid);
		
		TabTemplate tabCopy = tab.createTemp();
		Integer domainId = DomainManager.getContextDomainId();
		User loginUser = userDAO.getByEmail(uid, domainId);
		tabCopy.setEditor(loginUser);
		model.addAttribute(tabCopy);
		model.addAttribute("editors", editors.toString());

		Set<Role> roles = tab.getRoles();
		if(roles == null)
			roles = new HashSet<Role>();
		model.addAttribute("roles", roles);
	}
	
	@RequestMapping
	@Transactional
	public String editTemp(HttpServletRequest request,
			@RequestParam(value = "id", required = false) String id, Model model)
			throws Exception {
		String uid = (String) request.getSession().getAttribute("Uid");
		TabTemplate tab = tabTemplateDAO.get(id);
		JSONArray editors = getEditors(tab.getTabId(), uid);
		model.addAttribute(tab);
		model.addAttribute("editors", editors.toString());
		return "tab/editTab";
	}
	
	/**
	 * get users on editing 
	 * @param tabId
	 * @return user names
	 */
	private JSONArray getEditors(String tabId, String uid){
		List<TabTemplate> tempTabs = tabTemplateDAO.getTemp(tabId);
		JSONArray editors = new JSONArray();
		//A user is not on editing if not updating in this 5 minutes.
		Date fiveMinutesAgo = new Date(new Date().getTime() - 5 * 60 * 1000);
		for (TabTemplate tab : tempTabs) {
			if (tab.getUpdatedAt().after(fiveMinutesAgo)) {
				if (tab.getEditor().getEmail().equals(uid))
					continue;
				editors.put(tab.getEditor().getName());
			}
		}
		return editors;
	}

	@RequestMapping
	@Transactional
	public String deleteTempTab(
			@RequestParam("id") String id,
			Model model) throws Exception {
		TabTemplate tab = tabTemplateDAO.get(id);
		if(tab.getStatus() == 1){
			tabTemplateDAO.delete(tab);
		}
		return "redirect:index";
	}
	
	@RequestMapping
	@Transactional
	public String deleteTab(@RequestParam("id") String tabId,
			Model model) throws Exception {
		tabTemplateDAO.deleteByTabId(tabId);
		return "redirect:index";
	}
	
	@RequestMapping
	@Transactional
	public String deleteHistory(@RequestParam("id") String id, Model model)
			throws Exception {
		TabTemplate tab = tabTemplateDAO.get(id);
		tabTemplateDAO.delete(tab);
		return "redirect:history?id=" + tab.getTabId();
	}
	
	@RequestMapping
	public void selectGadgetType(
			HttpServletRequest request, 
			@RequestParam("tabId") String tabId,
			@RequestParam("containerId") String containerId,
			Model model) throws Exception {
		model.addAttribute("tabId", tabId);
		model.addAttribute("containerId", containerId);
		model.addAttribute("gadgetConfs", GadgetService.getHandle().getGadgetConfs(request.getLocale()));
	}
	
	@RequestMapping
	@Transactional
	public String newStaticGadget(
			HttpServletRequest request, 
			@RequestParam("type") String type,
			@RequestParam("tabId") String tabId,
			@RequestParam("containerId") String containerId,
			Locale locale,
			Model model)throws Exception {
		TabTemplateStaticGadget staticGadget = new TabTemplateStaticGadget();
		GadgetInstance gadgetInstance = new GadgetInstance();
		staticGadget.setGadgetInstance(gadgetInstance);
		staticGadget.getGadgetInstance().setType(type);
		staticGadget.setTabTemplateId(tabId);
		staticGadget.setContainerId(containerId);

		//TODO 国際化処理して言語ごとにDBにキャッシュとして保存する。そしてそれを取得する。
		Document gadgetConf = null;
		try{
			gadgetConf = getGadgetConf(type, locale);
		}catch(Exception e){
			model.addAttribute("tabId", tabId);
			model.addAttribute("containerId", containerId);
			model.addAttribute("gadgetConfs", GadgetService.getHandle().getGadgetConfs(request.getLocale()));
			model.addAttribute("error_message", e.getMessage());
			return "tab/selectGadgetType";
		}
		Node titleNode = XPathAPI.selectSingleNode(gadgetConf,
				"/Module/ModulePrefs/@title");
		if (titleNode != null) {
			String title = titleNode.getNodeValue();
			staticGadget.getGadgetInstance().setTitle(title);
		}
		
		model.addAttribute("conf", gadgetConf);
		model.addAttribute(staticGadget);
		return "tab/editStaticGadget";
	
	}

	
	@RequestMapping(value="/tab/editStaticGadget")
	@Transactional
	public String editStaticGadget(
			HttpServletRequest request,
			@RequestParam("tabId") String tabId,
			@RequestParam("containerId") String containerId,
			Locale locale,
			Model model)throws Exception {
		TabTemplateStaticGadget staticGadget = null;
		TabTemplate tab = tabTemplateDAO.get(tabId);
		
		staticGadget = tabTemplateStaticGadgetDAO.getByContainerId(containerId, tab);
		if(staticGadget == null){
			return "forward:selectGadgetType";
		}
		String instanceId = Integer.toString(staticGadget.getGadgetInstance().getId());
		
		staticGadget.setTabTemplateId(tabId);
		staticGadget.setInstanceId( instanceId );
		staticGadget.setContainerId(containerId);
		model.addAttribute("tabId", tabId);
		model.addAttribute("containerId", containerId);
		model.addAttribute("instanceId", instanceId);
		model.addAttribute("ignoreHeaderBool", staticGadget.isIgnoreHeaderBool());
		model.addAttribute("noBorderBool", staticGadget.isNoBorderBool());
		
		model.addAttribute(staticGadget);
		
		GadgetInstance gadget = staticGadget.getGadgetInstance();
		if (gadget != null) {
			model.addAttribute("conf", getGadgetConf(gadget, locale));
		}
		return "tab/editStaticGadget";
	}
	
	@RequestMapping
	public void listGadgetInstances(
			HttpServletRequest request, 
			@RequestParam("tabId") String tabId,
			@RequestParam("containerId") String containerId,
			Model model)throws Exception{
		
		GadgetInstanceDAO dao = GadgetInstanceDAO.newInstance();
		List<GadgetInstance> gadgetInstances = dao.all();
		model.addAttribute("instances", gadgetInstances);
		model.addAttribute("tabId", tabId);
		model.addAttribute("containerId", containerId);
		
	}
	
	@RequestMapping
	@Transactional
	public String editInstance(HttpServletRequest request, 
			@RequestParam("tabId") String tabId,
			@RequestParam("containerId") String containerId,
			@RequestParam("instanceId") int instanceId,
			Locale locale,
			Model model) throws Exception {
		TabTemplateStaticGadget staticGadget = null;
		TabTemplate tab = tabTemplateDAO.get(tabId);		
		staticGadget = tabTemplateStaticGadgetDAO.getByContainerId(containerId, tab);
		if(staticGadget == null){
			staticGadget = new TabTemplateStaticGadget();
			staticGadget.setContainerId(containerId);
			staticGadget.setFkTabTemplate(tab);
		}
		GadgetInstance gadget = GadgetInstanceDAO.newInstance().get(instanceId);
		// lazy=true, but get userprefs ahead of time. Can get ahead with calling any method.
		gadget.getGadgetInstanceUserPrefs().size();
		staticGadget.setGadgetInstance(gadget);
		staticGadget.setTabTemplateId(tabId);
		staticGadget.setInstanceId(Integer.toString(instanceId));
		//
		model.addAttribute(staticGadget);
		model.addAttribute("conf", getGadgetConf(gadget.getType(),locale));
		return "tab/editStaticGadget";
	}

	@RequestMapping(method = RequestMethod.POST)
	@Transactional
	public void clearStaticGadgets(@RequestParam("tabid") String tabId,
			@RequestParam("widgetids") List<String> removeIds) {
		this.tabTemplateStaticGadgetDAO.deleteByTabIdAndWidgetIds(Integer
				.valueOf(tabId), removeIds);
	}
		
	@RequestMapping(method = RequestMethod.POST)
	@Transactional
	public String submitGadgetSettings(
			@Valid TabTemplateStaticGadget staticGadget,
			BindingResult result,
			Locale locale,
			Model model)throws Exception {
		if(result.hasErrors()){
			GadgetInstance gadget = staticGadget.getGadgetInstance();
			if (gadget != null) {
				model.addAttribute("conf", getGadgetConf(gadget, locale));
			}
			model.addAttribute("gadget", staticGadget);
			return "tab/editStaticGadget";
		}
		
		TabTemplate tab = tabTemplateDAO.get(staticGadget.getTabTemplateId());
		staticGadget.setFkTabTemplate(tab);
		String containerId = staticGadget.getContainerId();
		String instanceId = staticGadget.getInstanceId();
		
		TabTemplateStaticGadget sg = 
			tabTemplateStaticGadgetDAO.getByContainerId(containerId, tab);
		
		if(sg == null){//new
			if(instanceId != ""){
				setGadgetInstance(staticGadget, instanceId);
			}
		}else{//edit
			if(instanceId != ""){
				sg.setGadgetInstance(staticGadget.getGadgetInstance());
				sg.setContainerId(staticGadget.getContainerId());
				sg.setFkTabTemplate(staticGadget.getFkTabTemplate());
				sg.setIgnoreHeader(staticGadget.getIgnoreHeader());
				sg.setNoBorder(staticGadget.getNoBorder());
			}else{
				sg.setGadgetInstance(staticGadget.getGadgetInstance());
				sg.setIgnoreHeader(staticGadget.getIgnoreHeader());
				sg.setNoBorder(staticGadget.getNoBorder());
			}
			staticGadget = sg;
		}
		tabTemplateStaticGadgetDAO.save(staticGadget);
		//gadgetInstanceDAO.save(staticGadget.getGadgetInstance());
		
		model.addAttribute("gadget", staticGadget);
		return "tab/submitGadgetSettings";
	}
	
	private void setGadgetInstance(TabTemplateStaticGadget staticGadget, String instanceId){
		GadgetInstance gadget = 
			GadgetInstanceDAO.newInstance().
				get(Integer.parseInt( instanceId ));
		if(gadget != null){
			staticGadget.setGadgetInstance(gadget);
			// lazy=true, but get userprefs ahead of time. Can get ahead with calling any method.
			gadget.getGadgetInstanceUserPrefs().size();
		}
	}

	@RequestMapping(method = RequestMethod.POST)
	@Transactional
	public void updateTab(TabTemplate formTab,
			@RequestParam("layoutModified") String layoutModified,
			@RequestParam(value = "roles.id", required = false) String[] roleIdList, 
			Model model)throws Exception {

		TabTemplate tabOriginal = tabTemplateDAO.getByTabId(formTab.getTabId());
		
		TabTemplate tab = 
			tabTemplateDAO.get(Integer.toString(formTab.getId()));
		
		if(tabOriginal != null){
			tab.setOrderIndex(tabOriginal.getOrderIndex());//copy order from current to temp
			tabOriginal.setStatus(2);//history
			tabTemplateDAO.save(tabOriginal);
			WidgetDAO widgetDAO = WidgetDAO.newInstance();
			if(Boolean.valueOf(layoutModified)){
				widgetDAO.deleteStaticWidgetByTabId(tab.getTabId());
			}
		}
		
		tab.setName(formTab.getName());
		tab.setLayout(formTab.getLayout());
		tab.setAreaType(formTab.getAreaType());
		tab.setStatus(0);//current
		tab.setNumberOfColumns(formTab.getNumberOfColumns());
		tab.setPublish(formTab.getPublish());
		tab.setAccessLevel(Integer.toString(formTab.getAccessLevel()));
		if (roleIdList != null) {
			for (int i = 0; i < roleIdList.length; i++) {
				Role role = RoleDAO.newInstance().get(roleIdList[i]);
				tab.addToRoles(role);
			}
		}
		tabTemplateDAO.save(tab);
		
		//history max count is 30
		List<TabTemplate> tabTemplates = tabTemplateDAO.findByTabId(formTab
				.getTabId());
		if (tabTemplates.size() > 30) {
			tabTemplateDAO.delete(tabTemplates.get(0));
		}
		
		model.addAttribute(tab);
	}

	@RequestMapping(method = RequestMethod.POST)
	@Transactional
	public String updateTemp(TabTemplate formTab,
			@RequestParam("layoutModified") String layoutModified,
			@RequestParam(value = "roles.id", required = false) String[] roleIdList, 
			Model model)throws Exception {
		TabTemplate tab = 
			tabTemplateDAO.get(Integer.toString(formTab.getId()));
		
		tab.setName(formTab.getName());
		if (tab.getAreaType() == 2 && formTab.getAreaType() < 2) {
			tab.setLayout(LAYOUT_01);
		} else if (formTab.getAreaType() == 2 && tab.getAreaType() < 2) {
			tab.setLayout(LAYOUT_2);
		} else {
			tab.setLayout(formTab.getLayout());
		}
		tab.setAreaType(formTab.getAreaType());
		tab.setStatus(1);//temp
		tab.setNumberOfColumns(formTab.getNumberOfColumns());
		tabTemplateDAO.save(tab);
		
		if (tab.getAreaType() != 0) {
			// delete personalized gadgets
			tabTemplatePersonalizeGadgetDAO.deleteByTabTemplateId(tab.getId());
		}
		
		model.addAttribute(tab);
		return "redirect:editTemp?id=" + tab.getId();
	}

	@RequestMapping(method = RequestMethod.POST)
	@Transactional
	public @ResponseBody
	String sort(@RequestParam("tabId") String[] tabIds, Model model) {
		int order = 0;
		for (String tabId : tabIds) {
			TabTemplate tab = tabTemplateDAO.get(tabId);
			tab.setOrderIndex(order++);
			tabTemplateDAO.save(tab);
		}
		return "success to sort tabs";
	}

	@RequestMapping(method = RequestMethod.POST)
	@Transactional
	public XmlView comsrv(HttpServletRequest request) throws Exception{
		String uid = (String) request.getSession().getAttribute("Uid");
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = builder.parse(request.getInputStream());
		Element root = doc.getDocumentElement();
		return this.executeCommand(uid, root);
	}
	
	@RequestMapping
	@Transactional
	public TextView widsrv(HttpServletRequest request,
			@RequestParam("tabId") String tabId, Model model)
			throws JSONException {
		TabTemplate tab = tabTemplateDAO.get(tabId);
		String uid = (String) request.getSession().getAttribute("Uid");
		model.addAttribute("uid", uid);
		model.addAttribute(tab);
		
		for(TabTemplateStaticGadget gadget : tab.getTabTemplateStaticGadgets())
			gadget.getGadgetInstance().getGadgetInstanceUserPrefs().size();
		
		
		Collection<TabTemplatePersonalizeGadget> firstOfColumn = new ArrayList<TabTemplatePersonalizeGadget>();
		Map<Integer, TabTemplatePersonalizeGadget> gadgetMap = new HashMap<Integer, TabTemplatePersonalizeGadget>();
		for(TabTemplatePersonalizeGadget gadget : tab.getTabTemplatePersonalizeGadgets()){
			gadget.getFkGadgetInstance().getGadgetInstanceUserPrefs().size();
			if(gadget.getSiblingId() == null){
				firstOfColumn.add(gadget);
			}else{
				gadgetMap.put(gadget.getSiblingId(), gadget);
			}
		}
		Collection<TabTemplatePersonalizeGadget> gadgets = new ArrayList<TabTemplatePersonalizeGadget>();
		for(TabTemplatePersonalizeGadget gadget : firstOfColumn){
			addNextSibling(gadget,gadgets,gadgetMap);
		}
		model.addAttribute("gadgets", gadgets);
		
		JSONArray tabsJson = new JSONArray();
		JSONObject tabJson = new JSONObject();
		tabsJson.put(tabJson);
		tabJson.put("uid", uid);
		tabJson.put("defaultUid", "default");
		tabJson.put("tabId", tab.getId());
		tabJson.put("tabName",tab.getName());
		tabJson.put("tabNumber", "0");
		tabJson.put("tabType","static");
		tabJson.put("widgetLastModified", "-");
		tabJson.put("disabledDynamicPanel", tab.getAreaType() != 0);
		tabJson.put("adjustStaticHeight", tab.getAreaType() == 2);
		JSONObject propertyJson = new JSONObject();
		propertyJson.put("numCol", tab.getNumberOfColumns());
		propertyJson.put("columnsWidth", tab.getColumnWidth());
		tabJson.put("property", propertyJson);
		JSONObject staticJson = new JSONObject();
		tabJson.put("staticPanel", staticJson);
		for(TabTemplateStaticGadget gadget : tab.getTabTemplateStaticGadgets()){
			JSONObject gadgetJson = new JSONObject();
			staticJson.put(gadget.getContainerId(), gadgetJson);
			gadgetJson.put("id", gadget.getContainerId());
			gadgetJson.put("ignoreHeader", gadget.isIgnoreHeaderBool());
			gadgetJson.put("noBorder", gadget.isNoBorderBool());
			gadgetJson.put("tabId", tab.getId());
			gadgetJson.put("href", gadget.getGadgetInstance().getHref());
			gadgetJson.put("title", gadget.getGadgetInstance().getTitle());
			gadgetJson.put("siblingId", "");
			gadgetJson.put("type", gadget.getGadgetInstance().getGadgetType());
			gadgetJson.put("iconUrl", gadget.getGadgetInstance().getIcon());
			JSONObject gadgetPropertyJson = new JSONObject();
			for(Entry<String, String> userPref : gadget.getGadgetInstance().getUserPrefs().entrySet()){
				gadgetPropertyJson.put(userPref.getKey(), userPref.getValue());
			}
			gadgetJson.put("property", gadgetPropertyJson);
		}
		tabJson.put("staticPanelLayout", tab.getLayout());
		JSONObject dynamicJson = new JSONObject();
		tabJson.put("dynamicPanel", dynamicJson);
		for(TabTemplatePersonalizeGadget gadget : gadgets){
			JSONObject gadgetJson = new JSONObject();
			dynamicJson.put(gadget.getWidgetId(), gadgetJson);
			gadgetJson.put("id", gadget.getWidgetId());
			gadgetJson.put("column", gadget.getColumnNum());
			gadgetJson.put("tabId", tab.getId());
			gadgetJson.put("href", gadget.getFkGadgetInstance().getHref());
			gadgetJson.put("title", gadget.getFkGadgetInstance().getTitle());
			gadgetJson.put("siblingId",gadget.getSiblingId());
			gadgetJson.put("type", gadget.getFkGadgetInstance().getGadgetType());
			gadgetJson.put("iconUrl", gadget.getFkGadgetInstance().getIcon());
			JSONObject gadgetPropertyJson = new JSONObject();
			for(Entry<String, String> userPref : gadget.getFkGadgetInstance().getUserPrefs().entrySet()){
				gadgetPropertyJson.put(userPref.getKey(), userPref.getValue());
			}
			gadgetJson.put("property", gadgetPropertyJson);
		}
		TextView view = new TextView();
		view.setResponseBody(tabsJson.toString());
		view.setContentType("application/json; charset=UTF-8");
		return view;
	}
	
	private void addNextSibling(TabTemplatePersonalizeGadget gadget, Collection<TabTemplatePersonalizeGadget> gadgets, Map<Integer, TabTemplatePersonalizeGadget> gadgetMap){
		gadgets.add(gadget);
		TabTemplatePersonalizeGadget nextSibling = gadgetMap.get(gadget.getId());
		if(nextSibling != null)
			addNextSibling(nextSibling,gadgets,gadgetMap);
	}
	
	@RequestMapping
	public void getGadgetConf(HttpServletRequest request, Model model)
			throws Exception {
		Locale locale = request.getLocale();
		String uploadGadgets = GadgetService.getHandle().getGadgetJson(locale,
				3000);
		model.addAttribute("builtin", "{}");
		model.addAttribute("upload", uploadGadgets);
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
			Document doc = getRemoteGadget(url,locale);
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
	
	private Document getRemoteGadget(String url, Locale locale) throws Exception {
		InputStream is = null;
		ProxyRequest proxyRequest = null;
		try {
			proxyRequest = new ProxyRequest(url, "XML");
			proxyRequest.setTimeout(ProxyServlet.DEFAULT_TIMEOUT);
			int statusCode = proxyRequest.executeGet();
			if (statusCode != 200){
				ResourceBundleMessageSource rb = (ResourceBundleMessageSource)SpringUtil.getBean("messageSource");
				throw new Exception(rb.getMessage("tab.newStaticGadget.retrieve_gadget_failed", new Object[]{proxyRequest.getProxy().getUrl(), statusCode}, locale));
			}
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
		/**
	 * Copy from CommandExecutionService
	 * @param uid
	 * @param commandXML
		 * @return 
	 * @throws Exception 
	 */
	private XmlView executeCommand(String uid, Element commandXML) throws Exception{
		NodeList commandList = commandXML.getElementsByTagName("command");
		
		XMLCommandProcessor[] commands = new XMLCommandProcessor[commandList.getLength()];
		
		for (int i = 0; i < commandList.getLength(); i++) {
			Element commandEl = (Element)commandList.item(i);

			if(log.isDebugEnabled())
				log.debug("Command Elememt: " + XmlUtil.xmlSerialize(commandEl));
			
			
			String type = commandEl.getAttribute("type");
			
			XMLCommandProcessor command;
			try{
				command = (XMLCommandProcessor)SpringUtil.getBean("manager" + type);
			}catch(BeansException e){
				log.error("Unexpected error occurred.", e);
				continue;
			}
			command.initialize(uid, commandEl);
			commands[i] = command;
		    //XMLCommandProcessor command = getCommand(context, type, resultList);
			if(log.isInfoEnabled())
				log.info("uid:[" + uid + "]: doPost: "
						+ command.getClass().getName());
			command.execute();
			
		}

		StringWriter writer = new StringWriter();
		writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
		writer.write("<responses>");
		for(int i = 0; i < commands.length; i++){
			if(commands[i] != null){
				if(log.isInfoEnabled() ){
					log.info("Command [" + commands[i].getCommandId() + "] result: " + commands[i].getResult());
				}
				writer.write(commands[i].getResult().toString());
			}
		}
		writer.write("</responses>");
		XmlView view = new XmlView();
		view.setResponseBody(writer.toString());
		return view;
	}
	
	public static class AddWidget extends XMLCommandProcessor{

		AddWidget() {
			super();
		}

		@Override
		public void execute() throws Exception {

	        String commandId = super.commandXml.getAttribute("id").trim();
	        String widgetId = super.commandXml.getAttribute("widgetId").trim();
	        String tabId = super.commandXml.getAttribute("tabId").trim();
	        String targetColumn = super.commandXml.getAttribute("targetColumn").trim();
	        String parent = super.commandXml.getAttribute("parent").trim();
	        String sibling = super.commandXml.getAttribute("sibling").trim();
	        String ginstid = super.commandXml.getAttribute("ginstid").trim();

	        if(log.isInfoEnabled()){
	        	log.info("uid:[" + uid + "]: processXML: widgetId:[" + widgetId
	                	+ "], tabId:[" + tabId + "], targetColumn:[" + targetColumn + 
	                	"], parent:[" + parent + "], sibling:[" + sibling + "]");
	        }

	        if (widgetId == null || widgetId == "") {
	            String reason = "It's an unjust widgetId. widgetId:[" + widgetId + "]";
	            log.error("Failed to execute the command of AddWidget:" + reason);
	            this.result = XMLCommandUtil.createResultElement(uid, "processXML",
	                    log, commandId, false, reason);
	            return;
	        }

	        if (targetColumn != null && !"".equals(targetColumn) && !XMLCommandUtil.isNumberValue(targetColumn)) {
	        	String reason = "It's an unjust value of column targetColumn:[" + targetColumn + "]";
	            log.error("Failed to execute the command of AddWidget" + reason);
	            this.result = XMLCommandUtil.createResultElement(uid, "processXML",
	                    log, commandId, false, reason);
	            return;
	        }
	        	    	
	    	try{
	    		TabTemplateDAO tabDAO = TabTemplateDAO.newInstance();
	    		TabTemplate tab = tabDAO.get(tabId);
	    		
	    		TabTemplatePersonalizeGadget gadget = new TabTemplatePersonalizeGadget();
	    			    		
	    		TabTemplatePersonalizeGadget nextSibling = null;
	        	if( parent != null && !"".equals( parent )) {
	        		//newNextSibling = tabDAO.getSubWidgetBySibling( uid,tabId,sibling,parent,widgetId );
	        		nextSibling = null;
	        	} else if(sibling != null && !"".equals(sibling)){
	        		log.info("Find sibling: "+sibling+" of "+targetColumn );
	        		nextSibling = tabDAO.getColumnWidgetBySibling( tabId,sibling,Integer.valueOf( targetColumn ) );
	        	}
	        	
	        	if(nextSibling != null){
	        		gadget.setSibling( nextSibling );
	        		log.info("Replace siblingId of [" + gadget.getSiblingId() + "] to " + widgetId );
	 //       		WidgetDAO.newInstance().updateWidget(uid, tabId, newNextSibling);
	        	}
	        	
	    		gadget.setWidgetId(widgetId);
	    		if(targetColumn != null && !"".equals(targetColumn)){
	    			gadget.setColumnNum(new Integer(targetColumn));
	    		}
	    		
	    		GadgetInstance ginst = GadgetInstanceDAO.newInstance().get(Integer.valueOf(ginstid));
	    		
	    		gadget.setFkGadgetInstance(ginst);
	    		
	    		tab.getTabTemplatePersonalizeGadgets().add(gadget);
	    		gadget.setFkTabTemplate(tab);
	    		tabDAO.save(tab);
	    	} catch (Exception e) {
	    		log.error("", e);
	            String reason = "Failed to save the widget.";
	            log.error("Failed to execute the command of AddWidget" + reason);
	            this.result = XMLCommandUtil.createResultElement(uid, "processXML",
	                    log, commandId, false, reason);
	            throw e;
			}

	        this.result = XMLCommandUtil.createResultElement(uid, "processXML",
	                log, commandId, true, null);
		}
		
	}
	
	public static class UpdateWidgetLocation extends XMLCommandProcessor {

	    private Log log = LogFactory.getLog(this.getClass());

	    /**
	     * create a new object of UpdateWidgetLocation.
	     */
	    public UpdateWidgetLocation() {

	    }

	    /**
	     * udpate the information of the widget's location.
	     * 
	     * @param uid
	     *            a userId that is target of operation.
	     * @param el
	     *             The element of request command. Attributes of "widgetId", "targetColumn", and "sibling" are necessary for the Element. <BR>
	     *            <BR>
	     *             example of input element：<BR>
	     * 
	     * <pre>
	     *  &lt;command type=&quot;UpdateWidgetLocation&quot; id=&quot;UpdateWidgetLocation_w_4&quot; widgetId=&quot;w_4&quot; targetColumn=&quot;3&quot; sibling=&quot;w_1&quot;/&gt;
	     * </pre>
	     */

		public void execute() {

	        String commandId = super.commandXml.getAttribute("id").trim();
	        String widgetId = super.commandXml.getAttribute("widgetId").trim();
	        String tabId = super.commandXml.getAttribute("tabId").trim();
	        String targetColumn = super.commandXml.getAttribute("targetColumn").trim();
	        String parentId = super.commandXml.getAttribute("parent").trim();
	        String siblingId = super.commandXml.getAttribute("sibling").trim();
	        
	        if(log.isInfoEnabled()){
	        	String logMsg = "uid:[" + uid + "]: processXML: tabId:[" + tabId
						+ "], widgetId:[" + widgetId + "], targetColumn:["
						+ targetColumn + "], parent:[" + parentId + "], sibling:[" + siblingId + "]";
	        	log.info(logMsg);
	        }
	        if (widgetId == null || "".endsWith(widgetId)) {
	            String reason = "It's an unjust widgetId．widgetId:[" + widgetId + "]";
	            this.result = XMLCommandUtil.createResultElement(uid, "processXML",
	                    log, commandId, false, reason);
	            return;
	        }

	        if (targetColumn != null && !"".equals(targetColumn) && !XMLCommandUtil.isNumberValue(targetColumn)) {
	            String reason = "The value of column is unjust．targetColumn:[" + targetColumn + "]";
	            this.result = XMLCommandUtil.createResultElement(uid, "processXML",
	                    log, commandId, false, reason);
	            return;
	        }

    		TabTemplateDAO tabDAO = TabTemplateDAO.newInstance();
    		TabTemplate tab = tabDAO.get(tabId);
    		
	        TabTemplatePersonalizeGadget gadget = tab.getPersonalizeGadgetByWidgetId(widgetId);
	        if (gadget == null) {
	            String reason = "Not found the information of the widget(wigetID) that is origin of movement．widgetId:["
	                    + widgetId + "]";
	            this.result =  XMLCommandUtil.createResultElement(uid, "processXML",
	                    log, commandId, false, reason);
	            return;
	        }
	        
	        TabTemplatePersonalizeGadget oldNextSibling = tab.getPersonalizeGadgetBySibling( gadget.getWidgetId());            	
	        if(oldNextSibling != null){
	        	oldNextSibling.setSibling(gadget.getSibling());
	        }

	        TabTemplatePersonalizeGadget newNextSibling;
	        if(parentId != null && !"".equals(parentId)){
				newNextSibling = tab.getSubWidgetBySibling( siblingId, parentId );
			} else {
				newNextSibling = tab.getNextSiblingOnColumn(siblingId,Integer.valueOf( targetColumn ) );
			}
	        
	        if(newNextSibling != null){
	        	newNextSibling.setSibling(gadget);
	        	log.info("Replace siblingId of [" + newNextSibling.getId() + "] to " + gadget.getId());
	        }
	        
	        TabTemplatePersonalizeGadget sibling = tab.getPersonalizeGadgetByWidgetId(siblingId);
	        if(sibling != null)
	        	gadget.setSibling(sibling);
	      
	        try{
	        	gadget.setColumnNum(new Integer(targetColumn));
	        }catch(NumberFormatException e){
	        	gadget.setColumnNum(null);
	        }

	        /* TODO:
	        if(parentId != null)
	        	gadget.setParent(parent);
	        */
	        this.result = XMLCommandUtil.createResultElement(uid, "processXML", log,
	                commandId, true, null);

		}
	}

	public static class RemoveWidget extends XMLCommandProcessor {

	    private Log log = LogFactory.getLog(this.getClass());

	    public RemoveWidget() {
	    }
	    
	    public void execute() throws Exception{
		 	
	    	String commandId = super.commandXml.getAttribute("id").trim();
	        String tabId = super.commandXml.getAttribute("tabId").trim();
	        String widgetId = super.commandXml.getAttribute("widgetId").trim();
	        String parent = super.commandXml.getAttribute("parent").trim();
	        
	        if(log.isInfoEnabled()){
	        	String logMsg = "uid:[" + uid + "]: processXML: tabId:[" + tabId + "], widgetId:[" + widgetId + "], parent:[" + parent + "]";
	        	log.info(logMsg);
	        }
	        if (widgetId == null || widgetId == "") {
	        	String reason = "It's an unjust widgetId．widgetId:[" + widgetId + "]";
	            this.result =  XMLCommandUtil.createResultElement(uid, "processXML",
	                    log, commandId, false, reason);
	            return;
	        }
	        
	        try{
	        	TabTemplateDAO tabDAO = TabTemplateDAO.newInstance();
	        	TabTemplatePersonalizeGadgetDAO tabTemplatePersonalizeGadgetDAO = TabTemplatePersonalizeGadgetDAO.newInstance();
	        	TabTemplate tab = tabDAO.get(tabId);
	        	TabTemplatePersonalizeGadget widget = tab.getPersonalizeGadgetByWidgetId(widgetId);
	        	tab.removeTabTemplatePersonalizeGadget(widget);
	        	
	        	if(widget == null ){
	                this.result = XMLCommandUtil.createResultElement(uid, "processXML",
	                        log, commandId, false, "Failed to delete the widget. Not found the widget to delete.");
	                return;
	        	}
	        	
	        	//TODO:check whether the widget is null or not;
	        	TabTemplatePersonalizeGadget nextSibling = tab.getNextSibling(widgetId);
	        	if(nextSibling != null){
	        		nextSibling.setSibling(widget.getSibling());
	        	}
				tabTemplatePersonalizeGadgetDAO.deleteById(widget.getId());
	        		        	
	        } catch (Exception e) {			
	            String reason = "Failed to delete the widget.";
	            log.error("Failed to execute the command of RemoveWidget", e);
	            this.result = XMLCommandUtil.createResultElement(uid, "processXML",
	                    log, commandId, false, reason);
	            
	            throw e;
			}
	        this.result = XMLCommandUtil.createResultElement(uid, "processXML",
	                log, commandId, true, null);
	    }
	    
	}
	
	public class AddMultiWidget extends XMLCommandProcessor{

	    private Log log = LogFactory.getLog(this.getClass());
	    
	    /**
	     * create a new object of AddMultiWidget.
	     * 
	     */
	    public AddMultiWidget(){
	    }
	    
		public void execute() throws Exception {
			
	        String commandId = super.commandXml.getAttribute("id").trim();
	        String parentWidgetId = super.commandXml.getAttribute("widgetId").trim();
	        String tabId = super.commandXml.getAttribute("tabId").trim();
	        String targetColumn = super.commandXml.getAttribute("targetColumn").trim();
	        String parent = super.commandXml.getAttribute("parent").trim();
	        String sibling = super.commandXml.getAttribute("sibling").trim();
	        String menuId = super.commandXml.getAttribute("menuId").trim();

	        if (log.isInfoEnabled()) {
				log.info("uid:[" + uid + "]: processXML: widgetId:["
						+ parentWidgetId + "], tabId:[" + tabId
						+ "], targetColumn:[" + targetColumn + "], parent:["
						+ parent + "], sibling:[" + sibling + "], menuId:["
						+ menuId + "]");
	        }

	        if (parentWidgetId == null || parentWidgetId == "") {
	            String reason = "It's an unjust widgetId．widgetId:[" + parentWidgetId + "]";
	            log.error("Failed to exexute the command of AddMultiWidget： " + reason);
	            this.result = XMLCommandUtil.createResultElement(uid, "processXML",
	                    log, commandId, false, reason);
	            return;
	        }

	        if (targetColumn != null && !"".equals(targetColumn) && !XMLCommandUtil.isNumberValue(targetColumn)) {
	        	String reason = "Value of column is unjust．targetColumn:[" + targetColumn + "]";
	            log.error("Failed to execute the command of AddMultiWidget： " + reason);
	            this.result = XMLCommandUtil.createResultElement(uid, "processXML",
	                    log, commandId, false, reason);
	            return;
	        }
	        
	        // convert JSON to XML
	        String parentConfJSONStr = super.commandXml.getAttribute("widgetConf");
	        String subWidgetConfJSONStr = super.commandXml.getAttribute("subWidgetConfList");
	        
	        JSONObject parentConfJSON = null;
	        JSONArray subWidgetConfJsonAry = null;
	        try {
	        	parentConfJSON = new JSONObject(parentConfJSONStr);
	    		subWidgetConfJsonAry = new JSONArray(subWidgetConfJSONStr);
	    	} catch (Exception e) {
	            String reason = "The infomation of Widget is unjust.";
	            log.error("Failed to execute the command of AddMultiWidget： " + reason, e);
	            this.result = XMLCommandUtil.createResultElement(uid, "processXML",
	                    log, commandId, false, reason);
	            throw e;
			}
	    	
	    	try{
	    		TabDAO tabDAO = TabDAO.newInstance();
	    		
	    		Widget newNextSibling = tabDAO.getColumnWidgetBySibling( uid,tabId,sibling,Integer.valueOf( targetColumn ),parentWidgetId );
	        	if(newNextSibling != null){
	        		newNextSibling.setSiblingid( parentWidgetId );
	        		log.info("Replace siblingId of [" + newNextSibling.getWidgetid() + "] to " + parentWidgetId );
	 //       		WidgetDAO.newInstance().updateWidget(uid, tabId, newNextSibling);
	        	}
	    		
	    		// insert parent
	    		Widget widget = 
	    			createWidget(tabId, parent, parentWidgetId, targetColumn, sibling, menuId, parentConfJSON);
	    		tabDAO.addDynamicWidget( uid,"defaultUid",tabId, widget);
	    		
	    		// insert subWidgets
				String subCategorySibling = "";
				String subWidgetId, subWidgetMenuId;
				for (int i = 0; i < subWidgetConfJsonAry.length(); i++) {
					JSONObject confJson = new JSONObject(subWidgetConfJsonAry
							.get(i).toString());
					subWidgetId = confJson.getString("id");
					subWidgetMenuId = confJson.getString("menuId");

					Widget subWidget = createWidget(tabId, parentWidgetId,
							subWidgetId, targetColumn, subCategorySibling,
							subWidgetMenuId, confJson);
					tabDAO.addDynamicWidget(uid, "defaultUid", tabId, subWidget);
	    			
	    			subCategorySibling = subWidgetId;
				}
	        	
//	    		WidgetDAO.newInstance().updateTab( tab );
	    	} catch (Exception e) {			
	            String reason = "Failed to save the infomaton of widget.";
				log.error("Failed to execute the command of AddMultiWidget： " + reason, e);
	            this.result = XMLCommandUtil.createResultElement(uid, "processXML",
	                    log, commandId, false, reason);
	            
	            throw e;
			}
	        this.result = XMLCommandUtil.createResultElement(uid, "processXML",
	                log, commandId, true, null);
		}
		
		private Widget createWidget(String tabId, String parentId, String widgetId,
				String targetColumn, String sibling, String menuId,
				JSONObject confJson) throws JSONException {
			Widget widget = new Widget();
			widget.setTabid(tabId);
			widget.setFkDomainId(DomainManager.getContextDomainId());
			widget.setWidgetid(widgetId);
			widget.setUid(uid);
			if(targetColumn != null && !"".equals(targetColumn)){
				widget.setColumn(new Integer(targetColumn));
			}
			widget.setSiblingid(sibling);
			widget.setParentid(parentId);
			
			if (menuId != null && menuId.length() > 0) {
				MenuItem menuItem = MenuItemDAO.newInstance().get(
						Integer.valueOf(menuId));
				widget.setMenuItem(menuItem);
			}
			
			if(confJson.has("title"))
				widget.setTitle(confJson.getString("title"));
			if(confJson.has("href"))
				widget.setHref(confJson.getString("href"));
			if(confJson.has("type"))
				widget.setType(confJson.getString("type"));
			if(confJson.has("property"))
				widget.setUserPrefsJSON(confJson.getJSONObject("property"));
			if (confJson.has("ignoreHeader"))
				widget.setIgnoreHeader(confJson.getBoolean("ignoreHeader"));
			if (confJson.has("noBorder"))
				widget.setIgnoreHeader(confJson.getBoolean("noBorder"));

			widget.setIsstatic(new Integer(0));
			return widget;
		}
	}

	public static class UpdateTabPreference extends XMLCommandProcessor {

		private Log log = LogFactory.getLog(this.getClass());

		public UpdateTabPreference() {
		}

		public void execute() throws Exception {
			String commandId = super.commandXml.getAttribute("id").trim();
			String tabTemplateId = super.commandXml.getAttribute("tabId")
					.trim();
			String field = super.commandXml.getAttribute("field");
			String value = super.commandXml.getAttribute("value");
			if (!field.equals("columnsWidth"))
				return;

			try {
				TabTemplateDAO tabDAO = TabTemplateDAO.newInstance();
				TabTemplate tab = tabDAO.get(tabTemplateId);
				tab.setColumnWidth(value);
				tabDAO.save(tab);
			} catch (Exception e) {
				String reason = "Failed to update tabTemplate column width.";
				log.error(
						"Failed to execute the command of UpdateTabPreference :"
								+ tabTemplateId, e);
				this.result = XMLCommandUtil.createResultElement(uid,
						"processXML", log, commandId, false, reason);

				throw e;
			}
			this.result = XMLCommandUtil.createResultElement(uid, "processXML",
					log, commandId, true, null);
		}

	}

	public static class UpdateTabTemplateTimestamp extends
			XMLCommandProcessor {

		private Log log = LogFactory.getLog(this.getClass());

		public UpdateTabTemplateTimestamp() {
		}

		public void execute() throws Exception {

			String commandId = super.commandXml.getAttribute("id").trim();
			String tabTemplateId = super.commandXml.getAttribute("tabId")
					.trim();

			try {
				TabTemplateDAO tabDAO = TabTemplateDAO.newInstance();
				TabTemplate tab = tabDAO.get(tabTemplateId);
				tab.setUpdatedAt(new Date());
				tabDAO.save(tab);
			} catch (Exception e) {
				String reason = "Failed to update tabTemplate timestamp.";
				log.error(
						"Failed to execute the command of UpdateTabTemplateTimestampWidget :"
								+ tabTemplateId, e);
				this.result = XMLCommandUtil.createResultElement(uid,
						"processXML", log, commandId, false, reason);

				throw e;
			}
			this.result = XMLCommandUtil.createResultElement(uid, "processXML",
					log, commandId, true, null);
		}

	}
}
