package org.infoscoop.manager.controller;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.command.XMLCommandProcessor;
import org.infoscoop.command.util.XMLCommandUtil;
import org.infoscoop.dao.GadgetDAO;
import org.infoscoop.dao.TabTemplateDAO;
import org.infoscoop.dao.TabTemplateStaticGadgetDAO;
import org.infoscoop.dao.WidgetConfDAO;
import org.infoscoop.dao.model.GadgetInstance;
import org.infoscoop.dao.model.TabTemplate;
import org.infoscoop.dao.model.TabTemplateParsonalizeGadget;
import org.infoscoop.dao.model.TabTemplateStaticGadget;
import org.infoscoop.service.GadgetService;
import org.infoscoop.service.TabLayoutService;
import org.infoscoop.service.WidgetConfService;
import org.infoscoop.util.SpringUtil;
import org.infoscoop.util.XmlUtil;
import org.json.JSONObject;
import org.springframework.beans.BeansException;
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
	
	@RequestMapping
	public void index(Model model)throws Exception {
		List<TabTemplate> tabs = TabTemplateDAO.newInstance().all();
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
			tab = TabTemplateDAO.newInstance().get(tabId);
		}else{
			tab = new TabTemplate();
			tab.setName("New Tab");
			tab.setPublished(0);
			tab.setTemp(1);
			tab.setLayout("<table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">	<tr>		<td width=\"75%\">			<table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">				<tr>					<td style=\"width:33%\">						<div class=\"static_column\" style=\"width: 99%; height:82px; min-height: 1px;\"></div>					</td>					<td>						<div style=\"width:10px\">&nbsp;</div>					</td>					<td style=\"width:33%\">						<div class=\"static_column\" style=\"width: 99%; height:82px; min-height: 1px;\"></div>					</td>					<td>						<div style=\"width:10px\">&nbsp;</div>					</td>					<td style=\"width:34%\">						<div class=\"static_column\" style=\"width: 99%; height:82px; min-height: 1px;\"></div>					</td>				</tr>			</table>		</td>	</tr></table>");
			TabTemplateDAO.newInstance().save(tab);
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
			TabTemplateDAO.newInstance().delete(tab);
		}
		return "redirect:index";
	}
	
	@RequestMapping
	@Transactional
	public String deleteTab(@RequestParam("id") String tabId,
			Model model) throws Exception {
		TabTemplate tab = TabTemplateDAO.newInstance().get(tabId);
		TabTemplateDAO.newInstance().delete(tab);
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
		TabTemplate tab = TabTemplateDAO.newInstance().get(staticGadget.getTabTemplateId());
		staticGadget.setFkTabTemplate(tab);
		TabTemplateStaticGadgetDAO.newInstance().save(staticGadget);
		model.addAttribute(staticGadget);
		
		//This is not needed any more.
		//TabLayoutService.getHandle().insertStaticGadget("temp", staticGadget.getFkGadgetInstance());
	}
	
	@RequestMapping(method = RequestMethod.POST)
	@Transactional
	public void addTab(TabTemplate tab, Model model)throws Exception {		
		tab.setTemp(0);
		TabTemplateDAO.newInstance().save(tab);
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
	public void widsrv(
			HttpServletRequest request, 
			@RequestParam("tabId") String tabId, 
			Model model){
		TabTemplate tab = TabTemplateDAO.newInstance().get(tabId);
		String uid = (String) request.getSession().getAttribute("Uid");
		model.addAttribute("uid", uid);
		model.addAttribute(tab);
		model.addAttribute("parsonarizeGadgets", tab.getTabTemplateParsonalizeGadgets());
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
	        String menuid = super.commandXml.getAttribute("menuId").trim();

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
	        
	        
	        // convert the JSON to XML.
	        String confJSONStr = super.commandXml.getAttribute("widgetConf");
	        JSONObject confJson = null;
	        try {
	    		confJson = new JSONObject(confJSONStr);
	    	} catch (Exception e) {
	    		log.error("", e);
	            String reason = "The information of widget is unjust.";
	            log.error("Failed to execute the command of AddWidget" + reason);
	            this.result = XMLCommandUtil.createResultElement(uid, "processXML",
	                    log, commandId, false, reason);
	            throw e;
			}
	    	
	    	try{
	    		GadgetInstance ginst = new GadgetInstance();
	    		TabTemplateParsonalizeGadget gadget = new TabTemplateParsonalizeGadget();
	    		
	    		TabTemplateDAO tabDAO = TabTemplateDAO.newInstance();
	    		TabTemplate tab = tabDAO.get(tabId);
	    		
	    		TabTemplateParsonalizeGadget nextSibling = null;
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
	        	
	    		
	    		if(targetColumn != null && !"".equals(targetColumn)){
	    			gadget.setColumnNum(new Integer(targetColumn));
	    		}
	    		gadget.setSibling(nextSibling);;
	    		if(confJson.has("title"))
	    			ginst.setTitle(confJson.getString("title"));
	    		if(confJson.has("href"))
	    			ginst.setHref(confJson.getString("href"));
	    		if(confJson.has("type"))
	    			ginst.setType(confJson.getString("type"));
	    		/*
	    		if(confJson.has("property"))
	    			widget.setUserPrefsJSON(confJson.getJSONObject("property"));
	    		if (confJson.has("ignoreHeader"))
	    			widget.setIgnoreHeader(confJson.getBoolean("ignoreHeader"));
	    		if (confJson.has("noBorder"))
	    			widget.setIgnoreHeader(confJson.getBoolean("noBorder"));
				*/
	    		gadget.setFkGadgetInstance(ginst);
	    		//GadgetInstanceDAO.newInstance().save(ginst);
	    		tab.getTabTemplateParsonalizeGadgets().add(gadget);
	    		gadget.setFkTabTemplate(tab);
	    		TabTemplateDAO.newInstance().save(tab);
	    	} catch (Exception e) {
	    		log.error("", e);
	            String reason = "Failed to save the widget.";
	            log.error("Failed to execute the command of AddWidget��" + reason);
	            this.result = XMLCommandUtil.createResultElement(uid, "processXML",
	                    log, commandId, false, reason);
	            throw e;
			}
	    	 


	        this.result = XMLCommandUtil.createResultElement(uid, "processXML",
	                log, commandId, true, null);
		}
		
	}
}
