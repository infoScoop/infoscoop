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

package org.infoscoop.service;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xpath.XPathAPI;
import org.infoscoop.context.UserContext;
import org.infoscoop.dao.SearchEngineDAO;
import org.infoscoop.dao.model.Searchengine;
import org.infoscoop.dao.model.SearchenginePK;
import org.infoscoop.util.RoleUtil;
import org.infoscoop.util.XmlUtil;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
			String title, String retrieveUrl, String encoding, boolean defaultSelected) throws Exception {
		if (log.isInfoEnabled()) {
			log.info("addSearchEngine: id=" + id + ", title=" + title
				+ ", retrieveUrl=" + retrieveUrl + ", encoding="
				+ encoding);
		}
		String squareid = UserContext.instance().getUserInfo().getCurrentSquareId();

		// Obtain data and transfer the result to Document.
		Searchengine temp = (Searchengine)this.searchEngineDAO.selectTemp(squareid);
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
		element.setAttribute("defaultSelected", String.valueOf(defaultSelected));
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
	 * @throws Exception
	 */
	public synchronized void updateSearchEngineAttr(String name, String value) throws Exception {
		if (log.isInfoEnabled()) {
			log.info("updateSearchEngineAttr: name=" + name + ", value=" + value);
		}

		String squareid = UserContext.instance().getUserInfo().getCurrentSquareId();
		// Obtain data and transfer the result to Document.
		Searchengine temp = (Searchengine)this.searchEngineDAO.selectTemp(squareid);
		Document document = temp.getDocument();

		// Search for node matches engineId
		Element element = document.getDocumentElement();
		element.setAttribute(name, value);
		temp.setDocument( document );
		// Update
		this.searchEngineDAO.update(temp);

	}

	/**
	 * @param engineId
	 * @param itemsMap
	 * @param childTag
	 * @throws Exception 
	 */
	public synchronized void updateSearchEngineItem(String engineId, Map itemsMap, String childTag) throws Exception {
		String squareid = UserContext.instance().getUserInfo().getCurrentSquareId();

		// Obtain data and transfer the result to Document.
		Searchengine temp = (Searchengine)this.searchEngineDAO.selectTemp(squareid);
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
		Element childElement = null;
		if (childTag.equals("auths")) {
			Collection auths = (Collection) itemsMap.get("auths");
			if (auths != null)
				childElement = MenuAuthorization.createAuthsElement(document,
						auths);
		}else if (!"rssPattern".equals(childTag)) {
			childElement = document.createElement(childTag);
			for (Iterator it = itemsMap.entrySet().iterator(); it.hasNext();) {
				Map.Entry pattern = (Map.Entry)it.next();
				String key = (String)pattern.getKey() ;
				String value = (String)pattern.getValue() ;
				childElement.setAttribute(key, value);
			}
		} else {
			childElement = document.createElement(childTag);
			String key = (String) itemsMap.keySet().iterator().next();
			childElement.appendChild(document.createTextNode((String) itemsMap.get(key)));
		}

//		parentNode.appendChild(childElement);
		if (childElement != null)
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
		String squareid = UserContext.instance().getUserInfo().getCurrentSquareId();

		// Obtain data and transfer the result to Document.
		Searchengine temp = (Searchengine)this.searchEngineDAO.selectTemp(squareid);
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
		String squareid = UserContext.instance().getUserInfo().getCurrentSquareId();
		Searchengine temp = (Searchengine)this.searchEngineDAO.selectTemp(squareid);
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
		String squareid = UserContext.instance().getUserInfo().getCurrentSquareId();

		// Obtain data and transfer the result to Document.
		Searchengine temp = (Searchengine)this.searchEngineDAO.selectTemp(squareid);
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
	 * 
	 * @return String
	 * @throws Exception
	 */
	public String getSearchEngineJson() throws Exception {
		String squareid = UserContext.instance().getUserInfo().getCurrentSquareId();
		// Obtain data
		Searchengine entity =  this.searchEngineDAO.select(SearchEngineDAO.SEARCHENGINE_FLAG_NOT_TEMP, squareid);
		if (entity== null) {
			log.error("searchengine not found.");
			return "";
		}
		// Overwrite to temporary
		Searchengine tempEntity =  this.searchEngineDAO.select(SearchEngineDAO.SEARCHENGINE_FLAG_TEMP,squareid);
		if(tempEntity == null){
			Integer temp = new Integer(SearchEngineDAO.SEARCHENGINE_FLAG_TEMP);
			SearchenginePK pk = new SearchenginePK(temp, squareid);
			
			tempEntity = new Searchengine(pk, entity.getData());
		}
		else
			tempEntity.setData(entity.getData());
		this.searchEngineDAO.update(tempEntity);

		StringBuffer jsonString = new StringBuffer();
		jsonString.append("ISA_SearchEngine.setSearchEngine(");
		Document doc = entity.getDocument();
		String newwindow = doc.getDocumentElement().getAttribute("newwindow");
		jsonString.append( ("true".equalsIgnoreCase(newwindow) ? "true" : "false" ) ).append(",");
		Element defaultSearch = (Element) XPathAPI.selectSingleNode(doc,
				"/searchEngines/defaultSearch");
		if (defaultSearch != null)
			jsonString.append(makeJSON(defaultSearch));
		jsonString.append(",");
		Element rssSearch = (Element) XPathAPI.selectSingleNode(doc,
				"/searchEngines/rssSearch");
		if (defaultSearch != null)
			jsonString.append(makeJSON(rssSearch));
		jsonString.append(");");
		return jsonString.toString();
	}
	
	public String getSearchEngineXmlWithAcl() throws Exception {
		String squareid = UserContext.instance().getUserInfo().getCurrentSquareId();
		Searchengine entity = this.searchEngineDAO
				.select(SearchEngineDAO.SEARCHENGINE_FLAG_NOT_TEMP,squareid);
		if (entity == null) {
			log.error("searchengine not found.");
			return "";
		}
		Document doc = entity.getDocument();
		Element root = doc.getDocumentElement();
		Node newRoot = root.cloneNode(true);
		Element defaultSearch = (Element) XPathAPI.selectSingleNode(newRoot,
				"defaultSearch");
		checkAcl(defaultSearch);
		Element rssSearch = (Element) XPathAPI.selectSingleNode(newRoot,
				"rssSearch");
		checkAcl(rssSearch);
		return XmlUtil.dom2String(newRoot);
	}

	/**
	 * Committing temporary data 
	 * 
	 * @throws Exception
	 */
	public void commitSearch() throws Exception {
		String squareid = UserContext.instance().getUserInfo().getCurrentSquareId();

		// Obtain data
		Searchengine tempEntity =  this.searchEngineDAO.selectTemp(squareid);
		Searchengine entity = this.searchEngineDAO.selectEntity(squareid);
		if (tempEntity == null) {
			log.error("searchengine not found.");
		}

		// Overwrite actual data from temporary
		entity.setData(tempEntity.getData());
		this.searchEngineDAO.update(entity);
	}

	private JSONObject makeJSON(Element searchEngine)
			throws Exception {
		NodeList searchEngines = searchEngine
				.getElementsByTagName("searchEngine");
		JSONObject json = new JSONObject();
		for (int i = 0; i < searchEngines.getLength(); i++) {
			Element searchEl = (Element) searchEngines.item(i);
			
			String id = searchEl.getAttribute("id");
			JSONObject searchObj = new JSONObject();
			searchObj.put("id", id);
			searchObj.put("title", searchEl.getAttribute("title"));
			searchObj.put("retrieveUrl", searchEl.getAttribute("retrieveUrl"));
			String defaultSelected = searchEl.getAttribute("defaultSelected");
			searchObj.put("defaultSelected", (defaultSelected != null ? "TRUE".equalsIgnoreCase(defaultSelected) : false) );
			searchObj.put("encoding", searchEl.getAttribute("encoding"));
			
			// rssPattern
			Element rssPattern = (Element) XPathAPI.selectSingleNode(searchEl,"rssPattern");
			if(rssPattern != null){
				searchObj.put("rssPattern", rssPattern.getTextContent());
			}

			// countRule
			Element countRule = (Element) XPathAPI.selectSingleNode(searchEl,
					"countRule");
			if(countRule != null){
				JSONObject countJson = new JSONObject();
				countJson.put("method", countRule.getAttribute("method"));
				countJson.put("value", countRule.getAttribute("value"));
				String useCache = countRule.getAttribute("useCache");
				countJson.put("useCache", (useCache !=null  ? "TRUE".equalsIgnoreCase(useCache) : false) );
				searchObj.put("countRule", countJson);
			}
			// auths
			Element authsEl = (Element) XPathAPI.selectSingleNode(searchEl,
					"auths");
			if (authsEl != null) {
				searchObj.put("auths", MenuAuthorization
						.createAuthsJson(authsEl));
			}

			json.put(id, searchObj);
		}
		return json;
	}
	
	private void checkAcl(Element searchEngine) throws Exception {
		if (searchEngine == null)
			return;
		NodeList searchEngines = searchEngine
				.getElementsByTagName("searchEngine");
		for (int i = 0; i < searchEngines.getLength(); i++) {
			Element searchEl = (Element) searchEngines.item(i);
			
			String retrieveUrl = searchEl.getAttribute("retrieveUrl");
			
			Element authsEl = (Element) XPathAPI.selectSingleNode(searchEl,	"auths");
			if(authsEl != null){
				NodeList roles = authsEl.getElementsByTagName("auth");
				boolean isPermitted = false;
				for (int j = 0; j < roles.getLength(); j++) {
					Element auth = (Element) roles.item(j);
					String type = auth.getAttribute("type");
					String regx = auth.getAttribute("regx");
					List<String> matchStrList = RoleUtil.getPermittedMatchList(type, regx);
					if(matchStrList != null){
						AdminServiceUtil.removeSelf(authsEl);
						for(int k = 0; k < matchStrList.size(); k++){
							retrieveUrl = retrieveUrl.replaceAll("%\\{" + type + "(\\[" + k + "\\])?\\}", matchStrList.get(k));
						}
						if(!matchStrList.isEmpty()){
							searchEl.setAttribute("retrieveUrl", retrieveUrl);
						}
						isPermitted = true;
						break;
					}
				}
				if (!isPermitted){
					AdminServiceUtil.removeSelf(searchEl); // searchEngines.getLength() is reduced.
					i--;
				}
			}
		}
	}
	
}
