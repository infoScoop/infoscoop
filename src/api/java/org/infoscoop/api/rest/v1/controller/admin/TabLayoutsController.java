/* infoScoop OpenSource
 * Copyright (C) 2010 Beacon IT Inc.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0-standalone.html>.
 */

package org.infoscoop.api.rest.v1.controller.admin;

import java.io.File;
import java.io.StringReader;
import java.io.Writer;
import java.net.URL;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.api.ISAPIException;
import org.infoscoop.api.rest.v1.controller.BaseController;
import org.infoscoop.api.rest.v1.response.TabLayoutsResponse;
import org.infoscoop.dao.TabLayoutDAO;
import org.infoscoop.dao.model.StaticTab;
import org.infoscoop.dao.model.TABLAYOUTPK;
import org.infoscoop.dao.model.TabAdmin;
import org.infoscoop.dao.model.TabLayout;
import org.infoscoop.dao.model.base.BaseStaticTab;
import org.infoscoop.service.StaticTabService;
import org.infoscoop.util.StringUtil;
import org.infoscoop.util.XmlUtil;
import org.infoscoop.util.spring.TextView;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.NodeIterator;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.sun.org.apache.xpath.internal.XPathAPI;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;

@Controller
@RequestMapping("/v1/admin/tablayouts")
public class TabLayoutsController extends BaseController {
	private static Log log = LogFactory.getLog(TabLayoutsController.class);
	private static List<String> singletonTabIdList
		= Arrays.asList(StaticTab.COMMANDBAR_TAB_ID, StaticTab.PORTALHEADER_TAB_ID);

	private static final String ROLE_ORDER_DEFAULT = "default";
	private static final String ROLE_ORDER_LAST = "last";
	
	/**
	 * Get tabLayoutXML by tabId
	 * 
	 * @return
	 */
	@RequestMapping(value = "/{tabId}", method = RequestMethod.GET)
	public TextView getTabLayout(@PathVariable("tabId") String tabId) throws Exception {
		StaticTab currentStaticTab = getStaticTab(tabId);
		TextView view = createTabLayoutResponseView(Arrays.asList(currentStaticTab));
		return view;
	}

	/**
	 * Get tabLayoutXML(Role) by tabId and roleOrder
	 * 
	 * @return
	 */
	@RequestMapping(value = "/{tabId}/{roleOrder}", method = RequestMethod.GET)
	public TextView getTabLayoutRole(@PathVariable("tabId") String tabId, @PathVariable("roleOrder") String roleOrderStr) throws Exception {
		StaticTab currentStaticTab = getStaticTab(tabId);
		
		Integer roleOrder = getRoleOrder(currentStaticTab, roleOrderStr);
		
		TextView view = createTabLayoutResponseView(Arrays.asList(currentStaticTab), roleOrder);
		return view;
	}

	/**
	 * Update tabLayout by tabId
	 * 
	 * @return
	 */
	@RequestMapping(value = "/{tabId}", method = RequestMethod.PUT)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void updateTabLayout(@PathVariable("tabId") String targetTabId, @RequestBody String requestBody) throws Exception {
		StaticTab currentStaticTab = getStaticTab(targetTabId);
		requestBody = URLDecoder.decode(requestBody, "UTF-8");
		
		Document doc = parseTabLayoutsXML(requestBody);
		List<StaticTab> staticTabList = toTabList(doc);
		
		StaticTabService service = StaticTabService.getHandle();
		if(staticTabList.size() > 0){
			StaticTab staticTab = staticTabList.get(0);
			service.replaceStaticTab(currentStaticTab, staticTab);
			// Update last modified date of tab0 if it is commandbar
			if (StaticTab.COMMANDBAR_TAB_ID.equals(targetTabId)) {
				service.updateLastmodifiedByTabId(StaticTab.TABID_HOME);
			}
		}
	}
	
	/**
	 * Update tabLayoutRole by tabId and roleOrder
	 * 
	 * @return
	 */
	@RequestMapping(value = "/{tabId}/{roleOrder}", method = RequestMethod.PUT)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void updateTabLayoutRole(@PathVariable("tabId") String targetTabId, @PathVariable("roleOrder") String roleOrderStr, @RequestBody String requestBody) throws Exception {
		StaticTab currentStaticTab = getStaticTab(targetTabId);
		requestBody = URLDecoder.decode(requestBody, "UTF-8");
		
		Document doc = parseTabLayoutsXML(requestBody, "role.xsd");
		TabLayout tabLayout = toTabLayout(doc.getDocumentElement(), targetTabId);
		
		Integer roleOrder = getRoleOrder(currentStaticTab, roleOrderStr);
		
		// update target Tablayout
		TabLayoutDAO dao = TabLayoutDAO.newInstance();
		TabLayout targetTabLayout = dao.get(new TABLAYOUTPK(targetTabId, roleOrder, TabLayout.TEMP_FALSE));
		
		if(!ROLE_ORDER_DEFAULT.equalsIgnoreCase(roleOrderStr)){
			targetTabLayout.setRole(tabLayout.getRole());
			targetTabLayout.setRolename(tabLayout.getRolename());
			targetTabLayout.setDefaultuid(tabLayout.getDefaultuid());
		}
		targetTabLayout.setLayout(tabLayout.getLayout());
		targetTabLayout.setPrincipaltype(tabLayout.getPrincipaltype());
		targetTabLayout.setWidgets(tabLayout.getWidgets());
		targetTabLayout.setWidgetslastmodified(tabLayout.getWidgetslastmodified());
		
		dao.update(targetTabLayout);
		
		StaticTabService service = StaticTabService.getHandle();
		// Update last modified date of tab0 if it is commandbar
		if (StaticTab.COMMANDBAR_TAB_ID.equals(targetTabId)) {
			service.updateLastmodifiedByTabId(StaticTab.TABID_HOME);
		}
	}
	
	/**
	 * Add tabLayoutRole by tabId and roleOrder
	 * 
	 * @return
	 */
	@RequestMapping(value = "/{tabId}/{roleOrder}", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void addTabLayoutRole(@PathVariable("tabId") String targetTabId, @PathVariable("roleOrder") String roleOrderStr, @RequestBody String requestBody) throws Exception {
		StaticTab currentStaticTab = getStaticTab(targetTabId);
		requestBody = URLDecoder.decode(requestBody, "UTF-8");
		
		Document doc = parseTabLayoutsXML(requestBody, "role.xsd");
		TabLayout newTabLayout = toTabLayout(doc.getDocumentElement(), targetTabId);
		
		Integer roleOrder = getRoleOrder(currentStaticTab, roleOrderStr);
		
		StaticTabService service = StaticTabService.getHandle();
		service.insertTabLayout(currentStaticTab, newTabLayout, roleOrder);
		
		// Update last modified date of tab0 if it is commandbar
		if (StaticTab.COMMANDBAR_TAB_ID.equals(targetTabId)) {
			service.updateLastmodifiedByTabId(StaticTab.TABID_HOME);
		}
	}
	
	/**
	 * delete tabLayout by tabId
	 * 
	 * @return
	 */
	@RequestMapping(value = "/{tabId}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteTabLayout(@PathVariable("tabId") String tabId) throws Exception {
		if(singletonTabIdList.contains(tabId) || StaticTab.TABID_HOME.equals(tabId))
			throw new ISAPIException("tabId=" + tabId + " cannot be deleted.");

		// check exists
		getStaticTab(tabId);
		StaticTabService.getHandle().deleteTabs(Arrays.asList(tabId));
	}

	/**
	 * create new tabLayout 
	 * 
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	public void createTabLayout(@RequestBody String requestBody) throws Exception {
		requestBody = URLDecoder.decode(requestBody, "UTF-8");
		
		Document doc = parseTabLayoutsXML(requestBody);
		List<StaticTab> staticTabList = toTabList(doc);
		
		if(staticTabList.size() > 0){
			StaticTabService service = StaticTabService.getHandle();
			StaticTab staticTab = staticTabList.get(0);
			
			if(singletonTabIdList.contains(staticTab.getTabid()))
				throw new ISAPIException("tabId=" + staticTab.getTabid() + " cannot be deleted.");
			
			String tabId = service.getNewTabId();
			staticTab.setTabid(tabId);
			staticTab.setTabnumber(service.getNextTabNumber());
			StaticTabService.getHandle().saveStaticTab(tabId, staticTab);
		}
	}
	
	/**
	 * 削除フラグの立っていない全てのタブレイアウトを返します
	 * 
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public TextView getTabLayouts() throws Exception {
		List<StaticTab> staticTabList = StaticTabService.getHandle().getStaticTabList();
		TextView view = createTabLayoutResponseView(staticTabList);
		
		return view;
	}

	/**
	 * タブレイアウトの全置換を行います 
	 * 
	 * @return
	 */
	@RequestMapping(method = RequestMethod.PUT)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void replaceTabLayouts(@RequestBody String requestBody) throws Exception {
		requestBody = URLDecoder.decode(requestBody, "UTF-8");
		
		Document doc = parseTabLayoutsXML(requestBody);
		List<StaticTab> staticTabList = toTabList(doc);
		
		StaticTabService.getHandle().replaceAllTabs(staticTabList);
	}
	
	private TextView createTabLayoutResponseView(List<StaticTab> tabList) throws SAXException, XPathExpressionException, ISAPIException{
		return createTabLayoutResponseView(tabList, null);
	}
	
	private TextView createTabLayoutResponseView(List<StaticTab> tabList, Integer roleOrder) throws SAXException, XPathExpressionException, ISAPIException{
		// change the null property to a blank for output.
		String tabId = null;
		for(StaticTab staticTab : tabList){
			tabId = staticTab.getTabid();
			if(staticTab.getTabdesc() == null)
				staticTab.setTabdesc("");
			Set<TabLayout> tabLayouts =  staticTab.getTabLayout();
			for(TabLayout tabLayout : tabLayouts){
				if(tabLayout.getLayout() == null)
					tabLayout.setLayout("");
			}
		}
		
		TabLayoutsResponse response = new TabLayoutsResponse(tabList);
		
		TabLayoutXppDriver xppDriver = new TabLayoutXppDriver();
		XStream xs = new XStream(xppDriver);
		xs.processAnnotations(TabLayoutsResponse.class);
		xs.processAnnotations(BaseStaticTab.class);
		
		String header = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";
		String xml = xs.toXML(response);
		
		xml = header + "\r\n" + xml;
		
		if(roleOrder != null){
			Document doc = parseTabLayoutsXML(xml);
			Node roleNode = getRoleNode(doc, roleOrder);
			
			if(roleNode == null)
				throw new ISAPIException("role data [tabId=" + tabId + ", roleOrder=" + roleOrder + "] is not found.");
			
			xml = XmlUtil.dom2String(roleNode);
		}

		return createXmlResponseView(xml);
	}
	
	/**
	 * validatation and parse XMLString for tabLayouts(role).
	 * @param xml
	 * @return
	 */
	private Document parseTabLayoutsXML(String xml) {
		return parseTabLayoutsXML(xml, "tabLayouts.xsd");
	}
	
	/**
	 * validatation and parse XMLString for tabLayouts.
	 * @param xml
	 * @return
	 */
	private Document parseTabLayoutsXML(String xml, String schemaName) {
		try {
			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			URL path = Thread.currentThread().getContextClassLoader().getResource(schemaName);
			File f = new File(path.toURI());
			Schema schema = factory.newSchema(f);
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setSchema(schema);
			dbf.setNamespaceAware(true);
			
			DocumentBuilder parser = dbf.newDocumentBuilder();
			Document doc = parser.parse(new InputSource(new StringReader(xml)));

			Validator validator = schema.newValidator();
			validator.validate(new DOMSource(doc));
			
			return doc;
		} catch (SAXException e) {
			// instance document is invalid
			throw new RuntimeException(e);
	    } catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@SuppressWarnings("unused")
	private List<StaticTab> toTabList(Document doc) throws TransformerException, SAXException{
		List<StaticTab> staticTabList = new ArrayList<StaticTab>();
		NodeList tabs = doc.getElementsByTagName("tab");
		NodeIterator tabIte = XPathAPI.selectNodeIterator(doc, "//tab");
		Element tab;
		while((tab = (Element)tabIte.nextNode()) != null){
			String tabId = tab.getAttribute("tabId");
			String disableDefault = tab.getAttribute("disableDefault");
			String tabNumber = tab.getAttribute("tabNumber");
			String tabDesc = XPathAPI.selectSingleNode(tab, "tabDesc").getTextContent();
			
			StaticTab staticTab = new StaticTab(tabId);
			staticTab.setTabdesc(StringUtil.getNullSafe(tabDesc));
			staticTab.setDeleteflag(StaticTab.DELETEFLAG_FALSE);
			staticTab.setDisabledefault(new Integer(disableDefault));
			staticTab.setTabnumber((tabNumber!=null && tabNumber.length() > 0)? new Integer(tabNumber) : null);
			
			// TabAdmins
			NodeIterator tabAdminsIte = XPathAPI.selectNodeIterator(tab, "./admin");
			Element tabAdmin;
			staticTab.setTabAdmin(new HashSet<TabAdmin>());
			while((tabAdmin = (Element)tabAdminsIte.nextNode()) != null){
				String adminUid = tabAdmin.getAttribute("uid");
				staticTab.getTabAdmin().add(new TabAdmin(tabId, adminUid));
			}
			
			// TabLayouts
			NodeIterator roleIte = XPathAPI.selectNodeIterator(tab, ".//role");
			Element role;
			staticTab.setTabLayout(new HashSet<TabLayout>());
			while((role = (Element)roleIte.nextNode()) != null){
				/*
				String roleOrder = role.getAttribute("roleOrder");
				String defaultuid = role.getAttribute("defaultuid");
				String roleRegx = XPathAPI.selectSingleNode(role, "roleRegx").getTextContent();
				String rolename = XPathAPI.selectSingleNode(role, "rolename").getTextContent();
				String principaltype = XPathAPI.selectSingleNode(role, "principaltype").getTextContent();
				String widgets = XPathAPI.selectSingleNode(role, "widgets").getTextContent();
				String layout = XPathAPI.selectSingleNode(role, "layout").getTextContent();

				TABLAYOUTPK tabLayoutPK = new TABLAYOUTPK(tabId, new Integer(roleOrder), TabLayout.TEMP_FALSE);
				TabLayout tabLayout = new TabLayout(tabLayoutPK);
				String widgetsLastmodified = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());

				tabLayout.setLayout(layout);
				tabLayout.setDefaultuid(defaultuid);
				tabLayout.setPrincipaltype(principaltype);
				tabLayout.setRole(roleRegx);
				tabLayout.setRolename(rolename);
				tabLayout.setWidgets(widgets);
				tabLayout.setWidgetslastmodified(widgetsLastmodified);
				
				// widgetsに追記
				String disabled = XPathAPI.selectSingleNode(role, "disabledDynamicPanel").getTextContent(); 
				String numCol = XPathAPI.selectSingleNode(role, "numCol").getTextContent(); 
				String tabName = XPathAPI.selectSingleNode(role, "tabName").getTextContent(); 
				String adjustToWindowHeight = XPathAPI.selectSingleNode(role, "adjustToWindowHeight").getTextContent(); 
				
				Element widgetEl = tabLayout.getElement();
				widgetEl.setAttribute("numCol", numCol);
				widgetEl.setAttribute("tabName", tabName);
				
				Element staticPanel = (Element)XPathAPI.selectSingleNode(widgetEl, "//panel[@type='StaticPanel']");
				if(staticPanel != null){
					staticPanel.setAttribute("disabled", disabled);
					staticPanel.setAttribute("adjustToWindowHeight", adjustToWindowHeight);
				}
				
				tabLayout.setElement(widgetEl);
				*/
				TabLayout tabLayout = toTabLayout(role, tabId);
				staticTab.getTabLayout().add(tabLayout);
			}
			
			staticTabList.add(staticTab);
		}
		
		return staticTabList;
	}
	
	private TabLayout toTabLayout(Element role, String tabId) throws TransformerException, SAXException{
		String roleOrder = role.getAttribute("roleOrder");
		String defaultuid = role.getAttribute("defaultuid");
		String roleRegx = XPathAPI.selectSingleNode(role, "roleRegx").getTextContent();
		String rolename = XPathAPI.selectSingleNode(role, "rolename").getTextContent();
		String principaltype = XPathAPI.selectSingleNode(role, "principaltype").getTextContent();
		String widgets = XPathAPI.selectSingleNode(role, "widgets").getTextContent();
		String layout = XPathAPI.selectSingleNode(role, "layout").getTextContent();

		TABLAYOUTPK tabLayoutPK = new TABLAYOUTPK(tabId, new Integer(roleOrder), TabLayout.TEMP_FALSE);
		TabLayout tabLayout = new TabLayout(tabLayoutPK);
		String widgetsLastmodified = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());

		tabLayout.setLayout(layout);
		tabLayout.setDefaultuid(defaultuid);
		tabLayout.setPrincipaltype(principaltype);
		tabLayout.setRole(roleRegx);
		tabLayout.setRolename(rolename);
		tabLayout.setWidgets(widgets);
		tabLayout.setWidgetslastmodified(widgetsLastmodified);
		
		// widgetsに追記
		String disabled = XPathAPI.selectSingleNode(role, "disabledDynamicPanel").getTextContent(); 
		String numCol = XPathAPI.selectSingleNode(role, "numCol").getTextContent(); 
		String tabName = XPathAPI.selectSingleNode(role, "tabName").getTextContent(); 
		String adjustToWindowHeight = XPathAPI.selectSingleNode(role, "adjustToWindowHeight").getTextContent(); 
		
		Element widgetEl = tabLayout.getElement();
		widgetEl.setAttribute("numCol", numCol);
		widgetEl.setAttribute("tabName", tabName);
		
		Element staticPanel = (Element)XPathAPI.selectSingleNode(widgetEl, "//panel[@type='StaticPanel']");
		if(staticPanel != null){
			staticPanel.setAttribute("disabled", disabled);
			staticPanel.setAttribute("adjustToWindowHeight", adjustToWindowHeight);
		}
		
		tabLayout.setElement(widgetEl);
		
		return tabLayout;
	}
	
	/**
	 * get StatiTab and check exists
	 * @param tabId
	 * @return
	 * @throws ISAPIException
	 */
	private StaticTab getStaticTab(String tabId) throws ISAPIException{
		StaticTab staticTab = StaticTabService.getHandle().getStaticTab(tabId);
		if(staticTab == null)
			throw new ISAPIException("tabId[" + tabId + "] is not found.");
		
		return staticTab;
	}
	
	class TabLayoutXppDriver extends XppDriver {
		private final List<String> CDATA_NODE_LIST = Arrays.asList("tabDesc",
				"layout", "tabName", "roleRegx", "rolename");
		
		public HierarchicalStreamWriter createWriter(Writer out) {

			return new PrettyPrintWriter(out) {
				boolean isCdataSection = false;

				@Override
				public void startNode(String name) {
					super.startNode(name);
					
					if (CDATA_NODE_LIST.contains(name))
						isCdataSection = true;
				}

				protected void writeText(QuickWriter writer, String text) {
					if(isCdataSection){
						writer.write("<![CDATA[");
						writer.write(text);
						writer.write("]]>");
					}else{
						super.writeText(writer, text);
					}
				}

				@Override
				protected void writeAttributeValue(QuickWriter writer,
						String text) {
					super.writeAttributeValue(writer, text);
				}
				
				@Override
				public void endNode() {
					super.endNode();
					isCdataSection = false;
				}
			};
		}
	}
	
	private Node getRoleNode(Document doc, Integer roleOrder) throws XPathExpressionException{
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		XPathExpression expr = xpath.compile("//role[@roleOrder='" + roleOrder + "']");
	    Node roleNode = (Node)expr.evaluate(doc, XPathConstants.NODE);
	    return roleNode;
	}
	
	private Integer getRoleOrder(StaticTab currentStaticTab, String roleOrderStr) throws ISAPIException{
		Integer roleOrder = null;
		
		int defaultRoleRoleOrder = 0;
		Set<TabLayout> tabLayouts = currentStaticTab.getTabLayout();
		for(Iterator<TabLayout> ite = tabLayouts.iterator();ite.hasNext();){
			defaultRoleRoleOrder = Math.max(defaultRoleRoleOrder, ite.next().getId().getRoleorder());
		}
		
		if(ROLE_ORDER_DEFAULT.equalsIgnoreCase(roleOrderStr)){
			return defaultRoleRoleOrder;
		}
		else if(ROLE_ORDER_LAST.equalsIgnoreCase(roleOrderStr)){
			return defaultRoleRoleOrder - 1;
		}else{
			try{
				roleOrder = Integer.parseInt(roleOrderStr);
			}catch(NumberFormatException e){
				throw new ISAPIException("roleOrder=" + roleOrderStr + " is not a number.");
			}
		}
		// can't be bigger than defaultRoleOrder
		if(roleOrder >= defaultRoleRoleOrder || roleOrder < 0){
			throw new ISAPIException("roleOrder=" + roleOrder + " is not found.");
		}
		
		return roleOrder;
	}
}
