package org.infoscoop.manager.controller;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.command.XMLCommandProcessor;
import org.infoscoop.command.util.XMLCommandUtil;
import org.infoscoop.dao.GadgetDAO;
import org.infoscoop.dao.GadgetInstanceDAO;
import org.infoscoop.dao.TabDAO;
import org.infoscoop.dao.TabTemplateDAO;
import org.infoscoop.dao.TabTemplateStaticGadgetDAO;
import org.infoscoop.dao.WidgetConfDAO;
import org.infoscoop.dao.model.GadgetInstance;
import org.infoscoop.dao.model.TabTemplate;
import org.infoscoop.dao.model.TabTemplatePersonalizeGadget;
import org.infoscoop.dao.model.TabTemplateStaticGadget;
import org.infoscoop.dao.model.Widget;
import org.infoscoop.service.GadgetService;
import org.infoscoop.service.WidgetConfService;
import org.infoscoop.util.SpringUtil;
import org.infoscoop.util.XmlUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.AbstractView;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

@Controller
public class TabController {
	private static Log log = LogFactory.getLog(TabController.class);
	
	@Autowired
	private TabTemplateDAO tabTemplateDAO;
	@Autowired
	private TabTemplateStaticGadgetDAO tabTemplateStaticGadgetDAO;
	@Autowired
	private GadgetInstanceDAO gadgetInstanceDAO;
	
	@RequestMapping
	public void index(Model model)throws Exception {
		List<TabTemplate> tabs = tabTemplateDAO.all();
		List<TabTemplate> tabsTemp0 = new ArrayList<TabTemplate>();
		for(TabTemplate tab: tabs){
			if(tab.getTemp() == 0){
				tabsTemp0.add(tab);
			}
		}
		model.addAttribute("tabs", tabsTemp0);
	}

	@RequestMapping
	@Transactional
	public void editTab(@RequestParam(value="id", required=false) String tabId, Model model)
			throws Exception {
		TabTemplate tab;
		if(tabId != null){
			tab = tabTemplateDAO.get(tabId);
		}else{
			tab = new TabTemplate();
			tab.setName("New Tab");
			tab.setPublished(0);
			tab.setTemp(1);
			tab.setLayout("<table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">	<tr>		<td width=\"75%\">			<table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">				<tr>					<td style=\"width:33%\">						<div class=\"static_column\" style=\"width: 99%; height:82px; min-height: 1px;\"></div>					</td>					<td>						<div style=\"width:10px\">&nbsp;</div>					</td>					<td style=\"width:33%\">						<div class=\"static_column\" style=\"width: 99%; height:82px; min-height: 1px;\"></div>					</td>					<td>						<div style=\"width:10px\">&nbsp;</div>					</td>					<td style=\"width:34%\">						<div class=\"static_column\" style=\"width: 99%; height:82px; min-height: 1px;\"></div>					</td>				</tr>			</table>		</td>	</tr></table>");
			tabTemplateDAO.save(tab);
		}
		model.addAttribute(tab);
	}

	@RequestMapping
	@Transactional
	public String deleteTempTab(
			@RequestParam("id") String tabId,
			Model model) throws Exception {
		TabTemplate tab = TabTemplateDAO.newInstance().get(tabId);
		if(tab.getTemp() == 1){
			Set<TabTemplateStaticGadget> sgs = 
				tab.getTabTemplateStaticGadgets();
			for(TabTemplateStaticGadget s: sgs){
					tabTemplateStaticGadgetDAO.delete(s);
					gadgetInstanceDAO.delete(s.getFkGadgetInstance());
			}
			tabTemplateDAO.delete(tab);
		}
		return "redirect:index";
	}
	
	@RequestMapping
	@Transactional
	public String deleteTab(@RequestParam("id") String tabId,
			Model model) throws Exception {
		TabTemplate tab = tabTemplateDAO.get(tabId);
		tabTemplateDAO.delete(tab);
		return "redirect:index";
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
	public void showGadgetDialog(
			HttpServletRequest request, 
			@RequestParam("type") String type,
			@RequestParam("tabId") String tabId,
			@RequestParam("containerId") String containerId,
			Model model)throws Exception {
		TabTemplateStaticGadget staticGadget = new TabTemplateStaticGadget();
		GadgetInstance gadgetInstance = new GadgetInstance();
		staticGadget.setFkGadgetInstance(gadgetInstance);
		staticGadget.getFkGadgetInstance().setType(type);
		staticGadget.setTabTemplateId(tabId);
		staticGadget.setContainerId(containerId);
		model.addAttribute("tabTemplateStaticGadget", staticGadget);
		
		//TODO 国際化処理して言語ごとにDBにキャッシュとして保存する。そしてそれを取得する。
		model.addAttribute("conf", getGadgetConf(type));
	
	}

	@RequestMapping
	public void listGadgetInstances(){
	}

	@RequestMapping(method = RequestMethod.POST)
	@Transactional
	public void submitGadgetSettings(
			TabTemplateStaticGadget staticGadget,
			Model model)throws Exception {
		TabTemplate tab = tabTemplateDAO.get(staticGadget.getTabTemplateId());
		staticGadget.setFkTabTemplate(tab);
		tabTemplateStaticGadgetDAO.save(staticGadget);
		model.addAttribute(staticGadget);
		
		//This is not needed any more.
		//TabLayoutService.getHandle().insertStaticGadget("temp", staticGadget.getFkGadgetInstance());
	}
	
	@RequestMapping(method = RequestMethod.POST)
	@Transactional
	public void addTab(TabTemplate tab, Model model)throws Exception {		
		tab.setTemp(0);
		tabTemplateDAO.save(tab);
		model.addAttribute(tab);
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
	public void widsrv(
			HttpServletRequest request, 
			@RequestParam("tabId") String tabId, 
			Model model){
		TabTemplate tab = tabTemplateDAO.get(tabId);
		String uid = (String) request.getSession().getAttribute("Uid");
		model.addAttribute("uid", uid);
		model.addAttribute(tab);
		
		for(TabTemplateStaticGadget gadget : tab.getTabTemplateStaticGadgets())
			gadget.getFkGadgetInstance().getGadgetInstanceUserPrefs().size();
		
		
		Collection<TabTemplatePersonalizeGadget> firstOfColumn = new ArrayList<TabTemplatePersonalizeGadget>();
		Map<Integer, TabTemplatePersonalizeGadget> gadgetMap = new HashMap<Integer, TabTemplatePersonalizeGadget>();
		for(TabTemplatePersonalizeGadget gadget : tab.getTabTemplatePersonalizeGadgets()){
			gadget.getFkGadgetInstance().getGadgetInstanceUserPrefs().size();
			if(gadget.getSibling() == null){
				firstOfColumn.add(gadget);
			}else{
				gadgetMap.put(gadget.getSibling().getId(), gadget);
			}
		}
		Collection<TabTemplatePersonalizeGadget> gadgets = new ArrayList<TabTemplatePersonalizeGadget>();
		for(TabTemplatePersonalizeGadget gadget : firstOfColumn){
			addNextSibling(gadget,gadgets,gadgetMap);
		}
		model.addAttribute("gadgets", gadgets);	
			
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
		String buildinGadgets = WidgetConfService.getHandle()
				.getWidgetConfsJson(locale);
		String uploadGadgets = GadgetService.getHandle().getGadgetJson(locale,
				3000);
		model.addAttribute("buildin", buildinGadgets);
		model.addAttribute("upload", uploadGadgets);
	}
	
	private Document getGadgetConf(String type) {
		Element conf = null;
		if (type.startsWith("upload__")) {
			conf = GadgetDAO.newInstance().getGadgetElement(type.substring(8));
		} else {
			conf = WidgetConfDAO.newInstance().getElement(type);
		}
		return conf.getOwnerDocument();
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
			if (command != null) {
				if(log.isInfoEnabled())
					log.info("uid:[" + uid + "]: doPost: "
							+ command.getClass().getName());
				command.execute();
			}else{
				log.error("Command " + type + " is not exist.");
			}
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
		view.setXmlString(writer.toString());
		return view;
	}
	
	public static class XmlView extends AbstractView{

		private String xmlString;
		
		void setXmlString(String xmlStr){
			this.xmlString = xmlStr;
		}
		
		public String getContentType(){
			return "text/xml; charset=UTF-8" ;
		}

		@Override
		protected void renderMergedOutputModel(Map<String, Object> map,
				HttpServletRequest request, HttpServletResponse response)
				throws Exception {
					        
			response.setContentType( "text/xml; charset=UTF-8" );
			response.getWriter().write( xmlString );
		}
		
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
	        		log.info("Replace siblingId of [" + gadget.getSibling() + "] to " + widgetId );
	 //       		WidgetDAO.newInstance().updateWidget(uid, tabId, newNextSibling);
	        	}
	        	
	    		gadget.setWidgetId(widgetId);
	    		if(targetColumn != null && !"".equals(targetColumn)){
	    			gadget.setColumnNum(new Integer(targetColumn));
	    		}
	    		gadget.setSibling(nextSibling);
	    		
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
	        if (widgetId == null || widgetId == "") {
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
	        	tabDAO.deleteParsonalizeGadget(widget.getId());
	        		        	
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
			Widget widget = new Widget(tabId, new Long(0), widgetId, uid);
			if(targetColumn != null && !"".equals(targetColumn)){
				widget.setColumn(new Integer(targetColumn));
			}
			widget.setSiblingid(sibling);
			widget.setParentid(parentId);
			widget.setMenuid(menuId);
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
}
