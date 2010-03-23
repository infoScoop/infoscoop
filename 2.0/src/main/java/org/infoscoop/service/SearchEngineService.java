package org.infoscoop.service;

import java.io.CharArrayWriter;
import java.io.StringReader;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.dao.SearchEngineDAO;
import org.infoscoop.dao.model.Searchengine;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SearchEngineService {

	private static Log log = LogFactory.getLog(SearchEngineService.class);
	private SearchEngineDAO searchEngineDAO;


	public SearchEngineService() {
	}

	public void setSearchEngineDAO(SearchEngineDAO searchEngineDAO) {
		this.searchEngineDAO = searchEngineDAO;
	}
	
	/**
	 * @param parent
	 * @param id
	 * @param title
	 * @param retrieveUrl
	 * @param encoding
	 * @return
	 * @throws Exception
	 */
	public synchronized String addSearchEngine(String parent, String id,
			String title, String retrieveUrl, String encoding) throws Exception {
		if (log.isInfoEnabled()) {
			log.info("addSearchEngine: id=" + id + ", title=" + title
				+ ", retrieveUrl=" + retrieveUrl + ", encoding="
				+ encoding);
		}

		// Obtain data and transfer the result to Document.
		Searchengine temp = (Searchengine)this.searchEngineDAO.selectTemp();
		Document document = temp.getDocument();

		// Search for child node.
		Node node = AdminServiceUtil.getNodeById(document, "/searchEngines/" + parent, null);

		// Error
		if(node == null)
			throw new Exception("element not found [/searchEngines/" + parent + "]");

		// Create element to insert.
		if(id == null || id.length() == 0)
			id = String.valueOf(System.currentTimeMillis());
		Element element;
		element = document.createElement("searchEngine");
		element.setAttribute("id", id);
		element.setAttribute("title", title);
		element.setAttribute("retrieveUrl", retrieveUrl);
		if (encoding != null && encoding.length() > 0)
			element.setAttribute("encoding", encoding);
		
		// Added to the end of node
		node.appendChild(element);

		temp.setDocument( document );
		// Update
		this.searchEngineDAO.update(temp);

		return id;
	}

	/**
	 * @param engineId
	 * @param itemsMap
	 * @param childTag
	 * @throws Exception 
	 */
	public synchronized void updateSearchEngineItem(String engineId, Map itemsMap, String childTag) throws Exception {

		// Obtain data and transfer the result to Document.
		Searchengine temp = (Searchengine)this.searchEngineDAO.selectTemp();
		Document document = temp.getDocument();

		// Search for node matches engineId
		Node parentNode = null;
		parentNode = AdminServiceUtil.getNodeById(document, "//searchEngine", engineId);

		// Error
		if(parentNode == null)
			throw new Exception("element not found [//searchEngine]");


		// Search for Node matches childTag
		Node node = AdminServiceUtil.getNodeById(parentNode, "./" + childTag, null);

		// Delete itself as it is created again
		AdminServiceUtil.removeSelf(node);

		// Create element to be updated
		Element childElement = document.createElement(childTag);
		if (!"rssPattern".equals(childTag)) {
			for (Iterator it = itemsMap.entrySet().iterator(); it.hasNext();) {
				Map.Entry pattern = (Map.Entry)it.next();
				String key = (String)pattern.getKey() ;
				String value = (String)pattern.getValue() ;
				childElement.setAttribute(key, value);
			}
		} else {
			String key = (String) itemsMap.keySet().iterator().next();
			childElement.appendChild(document.createTextNode((String) itemsMap.get(key)));
		}

//		parentNode.appendChild(childElement);
		parentNode.insertBefore(childElement, parentNode.getFirstChild());

		temp.setDocument( document );
		// Update
		this.searchEngineDAO.update(temp);

	}

	/**
	 * @param engineId
	 * @param itemsMap
	 * @throws Exception
	 */
	public synchronized void updateSearchEngineItem(String engineId, Map itemsMap) throws Exception {
		if (log.isInfoEnabled()) {
			log.info("updateSearchEngineItem: engineId=" + engineId
					+ ", itemsMap=" + itemsMap);
		}

		// Obtain data and transfer the result to Document.
		Searchengine temp = (Searchengine)this.searchEngineDAO.selectTemp();
		Document document = temp.getDocument();

		// Search for node matches engineId
		Node node = AdminServiceUtil.getNodeById(document, "//searchEngine", engineId);

		// Error 
		if(node == null)
			throw new Exception("element not found [//searchEngine]");

		// Create element to be updated
		Element element = (Element) node;
		for (Iterator it = itemsMap.entrySet().iterator(); it.hasNext();) {
			Map.Entry pattern = (Map.Entry)it.next();
			String key = (String)pattern.getKey() ;
			String value = (String)pattern.getValue() ;
			element.setAttribute(key, value);
		}
		temp.setDocument( document );
		// Update
		this.searchEngineDAO.update(temp);

	}

	/**
	 * @param engineId
	 * @throws Exception
	 */
	public synchronized void removeSearchEngine(String engineId) throws Exception {
		// Obtain data and transfer the result to Document.
		Searchengine temp = (Searchengine)this.searchEngineDAO.selectTemp();
		Document document = temp.getDocument();

		// Search for node matches engineId
		Node node = AdminServiceUtil.getNodeById(document, "//searchEngine", engineId);

		// Error 
		if(node == null)
			throw new Exception("element not found [//searchEngine]");

		// Delete node matches engineId
		AdminServiceUtil.removeSelf(node);

		temp.setDocument( document );
		// Update
		this.searchEngineDAO.update(temp);

	}

	/**
	 * @param engineId
	 * @param siblingId
	 * @throws Exception
	 */
	public synchronized void replaceSort(String engineId, String siblingId)
	throws Exception {

		// Obtain data and transfer the result to Document.
		Searchengine temp = (Searchengine)this.searchEngineDAO.selectTemp();
		Document document = temp.getDocument();

		// Search for node matches engineId
		Node engineNode = AdminServiceUtil.getNodeById(document, "//searchEngine", engineId);

		// Error
		if(engineNode == null)
			throw new Exception("engineNode not found [//searchEngine]");

		if (siblingId != null && siblingId.length() != 0) {
			// Search for node matches siblingId
			Node siblingNode = AdminServiceUtil.getNodeById(document, "//searchEngine", siblingId);

			// Error
			if (siblingNode == null)
				throw new Exception("siblingNode not found [//searchEngine]");

			// Insert before siblingNode
			siblingNode.getParentNode().insertBefore(engineNode, siblingNode);

		} else {
			engineNode.getParentNode().appendChild(engineNode);
		}

		temp.setDocument( document );
		// Update
		this.searchEngineDAO.update(temp);

	}

	/**
	 * For Management Page
	 * @return String
	 * @throws Exception
	 */
	public String getSearchEngineJson() throws Exception {
		// Obtain data
		Searchengine entity =  this.searchEngineDAO.select(SearchEngineDAO.SEARCHENGINE_FLAG_NOT_TEMP);
		if (entity== null) {
			log.error("searchengine not found.");
			return "";
		}
		// Overwrite to temporary
		Searchengine tempEntity =  this.searchEngineDAO.select(SearchEngineDAO.SEARCHENGINE_FLAG_TEMP);
		if(tempEntity == null)
			tempEntity = new Searchengine(new Integer(
					SearchEngineDAO.SEARCHENGINE_FLAG_TEMP), entity.getData());
		else
			tempEntity.setData(entity.getData());
		this.searchEngineDAO.update(tempEntity);
		
		SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
		MakeSearchEngineHandler handler = new MakeSearchEngineHandler();
		parser.parse(new InputSource(new StringReader(entity.getData())), handler);
		return handler.getJSONPString();
	}

	/**
	 * @return String
	 * @throws Exception 
	 */
	public String getSearchEngine() throws Exception {
		// Obtain data
		Searchengine entity =  this.searchEngineDAO.select(SearchEngineDAO.SEARCHENGINE_FLAG_NOT_TEMP);
		if (entity== null) {
			log.error("searchengine not found.");
			return "";
		}

		return entity.getData();

	}

	/**
	 * Committing temporary data 
	 * 
	 * @throws Exception
	 */
	public void commitSearch() throws Exception {

		// Obtain data
		Searchengine tempEntity =  this.searchEngineDAO.selectTemp();
		Searchengine entity = this.searchEngineDAO.selectEntity();
		if (tempEntity == null) {
			log.error("searchengine not found.");
		}

		// Overwrite actual data from temporary
		entity.setData(tempEntity.getData());
		this.searchEngineDAO.update(entity);
	}
	
	/**
	 * InnerClass
	 */
	private static class MakeSearchEngineHandler extends DefaultHandler{
		private CharArrayWriter buf = new CharArrayWriter();
		private StringBuffer defaultSearchArray = new StringBuffer();
		private StringBuffer rssSearchArray = new StringBuffer();
		boolean firstEngineElement = true;
		boolean endEngineElement = false;
		boolean close = false;
		Stack idStack = new Stack(); 
		long start = System.currentTimeMillis();
		public void startDocument() throws SAXException {
			defaultSearchArray.append("{");
			rssSearchArray.append("{");
		}
		
		public String getJSONPString(){
			return "ISA_SearchEngine.setSearchEngine("
					+ defaultSearchArray.toString() + ","
					+ rssSearchArray.toString() + ");";
		}
		public void endDocument() throws SAXException {
			defaultSearchArray.append("}");
			rssSearchArray.append("}");
		}
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			StringBuffer stringbuffer = new StringBuffer();
			StringBuffer stringbufferId = new StringBuffer();

			buf.reset();
			endEngineElement = false;
			if(qName.equals("defaultSearch")||qName.equals("rssSearch")){
				idStack.push(qName);
				firstEngineElement = true;
			}else if(qName.equals("searchEngine")){
				String engineId = attributes.getValue("id");

				if(!firstEngineElement){
					if(!close){
						stringbuffer.append("}");
					}
					stringbuffer.append(",");
					stringbufferId.append(",");
				}
				
				close = false;
				firstEngineElement = false;
				
				stringbuffer.append(engineId).append(":");
				stringbuffer.append("{");
				stringbuffer.append("id:").append(JSONObject.quote(engineId));
				stringbuffer.append(",title:").append(JSONObject.quote(attributes.getValue("title")));
				String retrieveUrl = attributes.getValue("retrieveUrl");
				if(retrieveUrl != null){
					stringbuffer.append(",retrieveUrl:").append(JSONObject.quote(retrieveUrl));
				}
				String encoding = attributes.getValue("encoding");
				if(encoding != null){
					stringbuffer.append(",encoding:").append(JSONObject.quote(encoding));
				}
				appendDivision(stringbuffer);
			}else if(qName.equals("countRule")){
				boolean firstAttribute = true;
				stringbuffer.append(",countRule:{");
				String method = attributes.getValue("method");
				if(method != null){
					if(!firstAttribute)
						stringbuffer.append(",");
					stringbuffer.append("method:").append(JSONObject.quote(method));
					firstAttribute = false;
				}
				String value = attributes.getValue("value");
				if(value != null){
					if(!firstAttribute)
						stringbuffer.append(",");
					stringbuffer.append("value:").append(JSONObject.quote(value));
					firstAttribute = false;
				}
				appendDivision(stringbuffer);
			}else if(qName.equals("rssPattern")){
				stringbuffer.append(",rssPattern:");
				appendDivision(stringbuffer);
			}
		}
		public void characters(char[] ch, int start, int length) throws SAXException {
			buf.write(ch, start, length);
		}
		
		public void endElement(String uri, String localName, String qName) throws SAXException {
			if(qName.equals("defaultSearch")||qName.equals("rssSearch")){
				if (!firstEngineElement) {
					appendDivision("}");
				}
				idStack.clear();
			}else if(qName.equals("searchEngine")){
				if(endEngineElement && !close){
					appendDivision("}");
					close = true;
				}
				endEngineElement = true;
			}else if(qName.equals("countRule")){
				appendDivision("}");
			}else if(qName.equals("rssPattern")){
				appendDivision(JSONObject.quote(buf.toString()));
			}
			buf.reset();
		}
		
		/**
		 * @param string
		 */
		private void appendDivision(String string){
			appendDivision(new StringBuffer(string));
		}

		/**
		 * @param stringbuffer
		 */
		private void appendDivision(StringBuffer stringbuffer){
			String peek = "";
			if(!idStack.isEmpty())
				peek = idStack.peek().toString();
			
			if("defaultSearch".equals(peek)){
				defaultSearchArray.append(stringbuffer);
			}else if("rssSearch".equals(peek)){
				rssSearchArray.append(stringbuffer);
			}
		}

	}
}