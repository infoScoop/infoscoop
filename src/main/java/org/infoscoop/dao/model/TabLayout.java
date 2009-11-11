package org.infoscoop.dao.model;

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.dao.model.base.BaseTablayout;
import org.infoscoop.util.StringUtil;
import org.infoscoop.util.XmlUtil;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;



public class TabLayout extends BaseTablayout {
	private static final long serialVersionUID = 1L;

	private static Log log = LogFactory.getLog(TabLayout.class);
	
	public static final Integer DELETEFLAG_TRUE = new Integer(1);
	public static final Integer DELETEFLAG_FALSE = new Integer(0);
	
	public static final Integer TEMP_TRUE = new Integer(1);
	public static final Integer TEMP_FALSE = new Integer(0);
	
/*[CONSTRUCTOR MARKER BEGIN]*/
	public TabLayout () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public TabLayout (org.infoscoop.dao.model.TABLAYOUTPK id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public TabLayout (
		org.infoscoop.dao.model.TABLAYOUTPK id,
		java.lang.String role,
		java.lang.String rolename,
		java.lang.String principaltype,
		java.lang.String widgets,
		java.lang.String layout,
		java.lang.Integer deleteflag,
		java.lang.String workinguid) {

		super (
			id,
			role,
			rolename,
			principaltype,
			widgets,
			layout,
			deleteflag,
			workinguid);
		
	}

/*[CONSTRUCTOR MARKER END]*/
	private String staticPanel;
	private String dynamicPanel;
	private JSONObject staticPanelJson;
	private JSONObject dynamicPanelJson;
	private String tabName;
	private String columnsWidth;
	private String numCol;
	
	public String getLayout() {
		return StringUtil.getNullSafe( super.getLayout() );
	}
	
	public void setWidgets(String widgets) throws RuntimeException{
		super.setWidgets(widgets);
		
		try{
			//TODO: It can be regular expression
			DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document widgetsDoc = docBuilder.parse(new InputSource(new StringReader(super.getWidgets())));
			Element widgetsEl = widgetsDoc.getDocumentElement();
			this.tabName = widgetsEl.getAttribute("tabName");
			this.columnsWidth = widgetsEl.getAttribute("columnsWidth");
			this.numCol = widgetsEl.getAttribute("numCol");
			NodeList panels = widgetsEl.getElementsByTagName("panel");
			Element staticPanel =(Element)panels.item(0);
			if("StaticPanel".equals(staticPanel.getAttribute("type"))){
				NodeList list = staticPanel.getElementsByTagName("widget");
				this.staticPanel = getNodeListString(list);
				this.staticPanelJson = getPanelJson(list);
			}
			if(panels.getLength() > 1){
				Element daynamicPanel =(Element)panels.item(1);
				if("DynamicPanel".equals(daynamicPanel.getAttribute("type"))){
					NodeList list = daynamicPanel.getElementsByTagName("widget");
					this.dynamicPanel = getNodeListString(list);
					this.dynamicPanelJson = getPanelJson(list);
				}
			}
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	
	private String getNodeListString(NodeList list) throws TransformerFactoryConfigurationError, TransformerException{
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<list.getLength();i++){
			Node widget = list.item(i);
			StringWriter buf = new StringWriter();
			Transformer tf = TransformerFactory.newInstance().newTransformer();
			tf.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			tf.transform(new DOMSource(widget), new StreamResult(buf));
			
			sb.append(buf + "\n");
		}
		
		return sb.toString();
	}
	
	private JSONObject getPanelJson(NodeList widgetsList) throws JSONException {
		JSONObject panelJson = new JSONObject();
		for (int i = 0; i < widgetsList.getLength(); i++) {
			if (widgetsList.item(i).getNodeType() != Node.ELEMENT_NODE)
				continue;
			JSONObject widgetJson = new JSONObject();
			Element widget = (Element) widgetsList.item(i);
			String id = widget.getAttribute("id");
			if (id != null)
				widgetJson.put("id", id);
			String href = widget.getAttribute("href");
			if (href != null)
				widgetJson.put("href", href);
			String title = widget.getAttribute("title");
			if (title != null)
				widgetJson.put("title", title);
			String type = widget.getAttribute("type");
			if (type != null)
				widgetJson.put("type", type);
			String column = widget.getAttribute("column");
			if (column != null)
				widgetJson.put("column", column);
			String ignoreHeader = widget.getAttribute("ignoreHeader");
			if (ignoreHeader != null)
				widgetJson.put("ignoreHeader", new Boolean(ignoreHeader)
						.booleanValue());

			String disabled = widget.getAttribute("disabled");
			if (disabled != null)
				widgetJson.put("disabled", new Boolean(disabled)
						.booleanValue());
			
			
			NodeList propertiesList = widget
					.getElementsByTagName("property");
			JSONObject propertyJson = new JSONObject();
			for (int j = 0; j < propertiesList.getLength(); j++) {
				if (propertiesList.item(j).getNodeType() != Node.ELEMENT_NODE)
					continue;
				Element property = (Element) propertiesList.item(j);
				String name = property.getAttribute("name");
				
				String value = "";
				if(property.getFirstChild() != null){
					value = property.getFirstChild().getNodeValue();
				}
				propertyJson.put(name, value);
			}
			widgetJson.put("properties", propertyJson);
			panelJson.put(id, widgetJson);
		}
		
		return panelJson;
	}
	
	public String getStaticPanel(){
		return this.staticPanel;
	}
	public JSONObject getStaticPanelJson(){
		return this.staticPanelJson;
	}
	
	public JSONObject getStaticPanelJsonWithComment() throws Exception {
		DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document widgetsDoc = docBuilder.parse(new InputSource(new StringReader(super.getWidgets())));
		Element widgetsEl = widgetsDoc.getDocumentElement();
		NodeList panels = widgetsEl.getElementsByTagName("panel");
		Element staticPanel =(Element)panels.item(0);
		if("StaticPanel".equals(staticPanel.getAttribute("type"))){
			
			NodeList staticWidgetlist = staticPanel.getChildNodes();
			
			int nodeCount = staticWidgetlist.getLength();
			Comment c;
			String nodeStr;
			Document commentDoc;
			Node commentNode;
			for (int i = 0; i < nodeCount; i++) {
				Node node = staticWidgetlist.item(i);
				if(node.getNodeType() == Element.COMMENT_NODE){
					c = (Comment)node;
					nodeStr = "<" + c.getNodeValue().trim() + ">";
					commentDoc = docBuilder.parse(new InputSource(new StringReader(nodeStr)));
					commentDoc.getDocumentElement().setAttribute("disabled", "true");
					commentNode = staticPanel.getOwnerDocument().importNode(commentDoc.getDocumentElement(), true);
					staticPanel.appendChild(commentNode);
				}
			}
			
			NodeList list = staticPanel.getElementsByTagName("widget");
			return getPanelJson(list);
		}
		return null;
	}
	
	public String getDynamicPanel(){
		return this.dynamicPanel;
	}
	public JSONObject getDynamicPanelJson(){
		return this.dynamicPanelJson;
	}
	
	public Element getElement() throws SAXException {
		Document doc = (Document) XmlUtil.string2Dom(super.getWidgets());
		return doc.getDocumentElement();
	}

	public void setElement(Element conf) {
		super.setWidgets(XmlUtil.dom2String(conf));
	}
	public String getTabName() {
		return tabName;
	}

	public String getColumnsWidth() {
		return columnsWidth;
	}

	public String getNumCol() {
		return numCol;
	}
	

	public Tab toTab(String uid){
		String tabId = super.getId().getTabid();
		Tab tab = new Tab(new TABPK(uid, tabId));
		tab.setDefaultuid(super.getDefaultuid());
		tab.setWidgetlastmodified(super.getWidgetslastmodified());
		tab.setOrder( super.getTabnumber() );
		tab.setName( this.getTabName());
		tab.setType("static");
		tab.setProperty("numCol", this.getNumCol());
		
		return tab;
	}
	
	public Collection getDynamicPanelXmlWidgets( String uid ) throws Exception {
		return getPanelXmlWidgets( uid,super.getId().getTabid(),getDynamicPanel(),false );
	}
	public Collection getStaticPanelXmlWidgets( String uid ) throws Exception {
		return getPanelXmlWidgets( uid,super.getId().getTabid(),getStaticPanel(),true );
	}
	private Collection getPanelXmlWidgets(String uid, String tabId,
			String panelXml, boolean isStatic) throws Exception {
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer;
		
		Collection widgetList = new ArrayList();
		if (log.isDebugEnabled())
			log.debug("--"
					+ Thread.currentThread().getContextClassLoader());
		InputStream xsl = Thread.currentThread().getContextClassLoader().getResourceAsStream("widget_xml2object.xsl");
		transformer = factory.newTransformer(new StreamSource(xsl));
		
		DOMResult result = new DOMResult();
		transformer.transform(new StreamSource(new StringReader("<widgets>" + panelXml + "</widgets>")),
				result);
		
		//Store widgets at the end of each line
		Map siblingMap = new HashMap();
		
		Document widgets = (Document)result.getNode();
		NodeList widgetNodeList = widgets.getElementsByTagName("widget");
		for(int i = 0; i < widgetNodeList.getLength(); i++){
			Element widgetEl = (Element)widgetNodeList.item(i);
			Widget widget = new Widget();
			widget.setTabid( tabId );
			widget.setDeletedate(new Long(0));
			widget.setWidgetid(widgetEl.getAttribute("widgetId"));
			widget.setUid( uid );
			//widget.setWidgetId(widgetEl.getAttribute("widgetId"));
			widget.setType(widgetEl.getAttribute("type"));
			String column = widgetEl.getAttribute("colnum");
			if(column != null || !"".equals(column)){
				try{
					widget.setColumn(Integer.valueOf(widgetEl.getAttribute("colnum")));
				}catch(NumberFormatException e){
					widget.setColumn(new Integer(0));
				}
			}
			if(isStatic){
				widget.setSiblingid(widgetEl.getAttribute("siblingId"));
			}else{
				String siblingId = (String)siblingMap.get(widget.getColumn());
				if(siblingId != null){
					widget.setSiblingid(siblingId);
				}
				siblingMap.put(widget.getColumn(), widget.getWidgetid());
			}
			widget.setMenuid(isStatic ? "" : widget.getWidgetid().substring(2));
			widget.setParentid(widgetEl.getAttribute("parentId"));
			widget.setTitle(widgetEl.getAttribute("title"));
			widget.setHref(widgetEl.getAttribute("href"));
			widget.setIgnoreHeader(new Boolean(widgetEl
					.getAttribute("ignoreHeader")).booleanValue());
			Element data = (Element)widgetEl.getElementsByTagName("data").item(0);
			NodeList propertyNodes = data.getElementsByTagName("property");
			for(int k = 0; k < propertyNodes.getLength(); k++){
				Element propEl = (Element)propertyNodes.item(k);
				
				widget.setUserPref( propEl.getAttribute("name"),getText(propEl) );
			}
			
			if(isStatic){
				widget.setIsstatic(new Integer(1));
			}else{
				widget.setIsstatic(new Integer(0));
			}
			
			widgetList.add( widget );
		}
		
		return widgetList;
	}
	
	private String getText(Element element) {
		StringBuffer text = new StringBuffer();
		NodeList childNodes = element.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node child = childNodes.item(i);
			text.append(child.getNodeValue());
		}
		return text.toString();
	}
}