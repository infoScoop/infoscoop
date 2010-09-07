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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xpath.XPathAPI;
import org.infoscoop.account.IAccount;
import org.infoscoop.account.SearchUserService;
import org.infoscoop.acl.ISPrincipal;
import org.infoscoop.acl.SecurityController;
import org.infoscoop.admin.exception.MenusIllegalEditException;
import org.infoscoop.admin.exception.MenusTimeoutException;
import org.infoscoop.dao.MenuItemDAO;
import org.infoscoop.dao.SiteAggregationMenuDAO;
import org.infoscoop.dao.SiteAggregationMenuTempDAO;
import org.infoscoop.dao.WidgetDAO;
import org.infoscoop.dao.model.Adminrole;
import org.infoscoop.dao.model.GadgetInstance;
import org.infoscoop.dao.model.GadgetInstanceUserpref;
import org.infoscoop.dao.model.MenuItem;
import org.infoscoop.dao.model.Portaladmins;
import org.infoscoop.dao.model.SITEAGGREGATIONMENU_TEMPPK;
import org.infoscoop.dao.model.Siteaggregationmenu;
import org.infoscoop.dao.model.Siteaggregationmenu_temp;
import org.infoscoop.util.SpringUtil;
import org.infoscoop.util.StringUtil;
import org.infoscoop.util.XmlUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class SiteAggregationMenuService {

	private static Log log = LogFactory.getLog(SiteAggregationMenuService.class);
	private SiteAggregationMenuDAO siteAggregationMenuDAO;
	private SiteAggregationMenuTempDAO siteAggregationMenuTempDAO;
	
	public void setSiteAggregationMenuDAO(
			SiteAggregationMenuDAO siteAggregationMenuDAO) {
		this.siteAggregationMenuDAO = siteAggregationMenuDAO;
	}

	public void setSiteAggregationMenuTempDAO(
			SiteAggregationMenuTempDAO siteAggregationMenuTempDAO) {
		this.siteAggregationMenuTempDAO = siteAggregationMenuTempDAO;
	}

	/**
	 * create a temporary record of the ID that is target.
	 * 
	 * @param menuType
	 * @param targetSiteTopId
	 * @throws SAXException 
	 * @throws TransformerException 
	 */
	public String menuLock(String menuType, String targetSiteTopId, boolean isForce, List<String> editSitetopIdList) throws Exception{
		ISPrincipal p = SecurityController.getPrincipalByType("UIDPrincipal");
		String myUid = p.getName();

		// check whether it is already locked
		Siteaggregationmenu_temp tempMenu = siteAggregationMenuTempDAO.selectBySitetopId(menuType, targetSiteTopId);
		
		if(tempMenu != null){
			this.siteAggregationMenuTempDAO.evict(tempMenu);
			// check the timeout
			if(!deleteTimeoutTemp(tempMenu.getId().getType(), tempMenu.getWorkinguid())){
				// In case that it is already locked
				if(tempMenu.getWorkinguid().equals(myUid)){
					this.siteAggregationMenuTempDAO.delete(tempMenu);
				}
				
				if(isForce && PortalAdminsService.getHandle().isPermitted("menu")){
					// delete all the temporary which the competing person has.
					this.siteAggregationMenuTempDAO.deleteByTypeAndUser(menuType, tempMenu.getWorkinguid());
				}
				else{
					JSONObject conflictJson = new JSONObject();
					conflictJson.put("conflict", true);
					conflictJson.put("conflictUser", getUserName(tempMenu.getWorkinguid()));
					return conflictJson.toString();
				}
			}
		}
		
		// get siteTop of the targetID by the main body.
		Siteaggregationmenu entity = this.siteAggregationMenuDAO.select(menuType);
		Element menuEl = entity.getElement();
		
		String path = "site-top[@id=\"" + targetSiteTopId + "\"]";
		Element targetEl = (Element)XPathAPI.selectSingleNode(menuEl, path);
		
		if(targetEl == null)
			throw new Exception("element not found [" + path + "]");
		
		// register a record
		tempMenu = new Siteaggregationmenu_temp(new SITEAGGREGATIONMENU_TEMPPK(menuType, targetSiteTopId));
		tempMenu.setElement(targetEl);
		tempMenu.setWorkinguid(myUid);
		this.siteAggregationMenuTempDAO.update(tempMenu);
		
		checkError(this.siteAggregationMenuTempDAO.selectByTypeAndUser(menuType, myUid), editSitetopIdList, menuType);
		
		JSONObject json = new JSONObject();
		JSONObject menuItemsJson = new JSONObject();
		JSONObject mapJson = new JSONObject();
		
		mapJson.put(targetSiteTopId, getChildSiteArray(targetEl));
		menuItemsJson.put(targetSiteTopId, siteToJSON(targetSiteTopId, targetEl, true));
		addChildSiteJSON(targetSiteTopId, targetEl, menuItemsJson, mapJson, true);
		
		json.put("menuItems", menuItemsJson);
		json.put("mapJson", mapJson);
		return json.toString();
	}
	
	/**
	 * Create temp record for changing order
	 * 
	 * @param menuType
	 * @param isForce
	 * @return
	 * @throws Exception
	 */
	public String orderLock(String menuType, boolean isForce, List<String> editSitetopIdList) throws Exception{
		ISPrincipal p = SecurityController.getPrincipalByType("UIDPrincipal");
		String myUid = p.getName();

		// check whether it is already locked
		Siteaggregationmenu_temp tempMenu = siteAggregationMenuTempDAO.selectBySitetopId(menuType, SiteAggregationMenuTempDAO.SITEMENU_ORDER_TEMP_ID);
		if(tempMenu != null){
			this.siteAggregationMenuTempDAO.evict(tempMenu);
			// check the timeout
			if(!deleteTimeoutTemp(tempMenu.getId().getType(), tempMenu.getWorkinguid())){
				// In case that it is already locked
				if(tempMenu.getWorkinguid().equals(myUid) || (isForce && PortalAdminsService.getHandle().isPermitted("menu"))){
					this.siteAggregationMenuTempDAO.delete(tempMenu);
				}
				else{
					JSONObject conflictJson = new JSONObject();
					conflictJson.put("conflict", true);
					conflictJson.put("conflictUser", getUserName(tempMenu.getWorkinguid()));
					return conflictJson.toString();
				}
			}
		}
		
		// Obtain order of sitetop
		Siteaggregationmenu entity = this.siteAggregationMenuDAO.select(menuType);
		Element menuEl = entity.getElement();
		
		// Create order element
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		List<String> getSiteTopIdList = getSiteTopIdList(menuEl);
		Document orderDoc = builder.newDocument();
		Element orderRoot = orderDoc.createElement("sites");
		orderDoc.appendChild(orderRoot);
		for(String sitetopId : getSiteTopIdList){
			Element sitTop = orderDoc.createElement("site-top");
			sitTop.setAttribute("id", sitetopId);
			orderRoot.appendChild(sitTop);
		}
		
		// Entry record
		tempMenu = new Siteaggregationmenu_temp(new SITEAGGREGATIONMENU_TEMPPK(menuType, SiteAggregationMenuTempDAO.SITEMENU_ORDER_TEMP_ID));
		tempMenu.setElement(orderRoot);
		tempMenu.setWorkinguid(myUid);
		this.siteAggregationMenuTempDAO.update(tempMenu);
		
		checkError(this.siteAggregationMenuTempDAO.selectByTypeAndUser(menuType, myUid), editSitetopIdList, menuType);
		
		return "true";
	}
	
	/**
	 * Check if edit of existing editor is already time out.
	 * All temporary records of existing editor is deleted if the editor is time out.
	 * 
	 * @param tempEntity
	 * @return
	 * @throws Exception 
	 */
	private boolean deleteTimeoutTemp(String menuType, String workingUid) throws Exception{
		// Obtain the latest last modified date.
		Date latestLastModifiedTime = this.siteAggregationMenuTempDAO.findLatestLastModifiedTime(menuType, workingUid);

		if(latestLastModifiedTime == null)
			return true;
		
		Date now = new Date();
		
		long one_minute_time = 1000 * 60;
	    long diffMinute = (now.getTime() - latestLastModifiedTime.getTime()) / one_minute_time;
		
	    String menuLockTimeoutStr = PropertiesService.getHandle().getProperty("menuLockTimeout");
	    int menuLockTimeout = Integer.parseInt(menuLockTimeoutStr);
	    
	    if(diffMinute >= menuLockTimeout){
	    	// If time out
			log.info("Because the time that can be edited passes, temporary data is deleted. [" + menuLockTimeout + "min]");
	    	this.siteAggregationMenuTempDAO.deleteByTypeAndUser(menuType, workingUid);
	    	return true;
	    }
	    return false;
	}
	
	/**
	 * Obtain node of update/add subject 
	 * 
	 * @param menuType
	 * @param targetMenuId
	 * @return
	 * @throws Exception 
	 * @throws Exception
	 */
	private Element getTargetElement(Siteaggregationmenu_temp tempEntity, String targetMenuId) throws Exception{
		return getTargetElement(tempEntity, targetMenuId, false);
	}
	
	private Element getTargetElement(Siteaggregationmenu_temp tempEntity, String targetMenuId, boolean ignoreSiteTop) throws Exception{
		checkError(tempEntity);
		
		Element sitetopEl = tempEntity.getElement();
		String workingUid = tempEntity.getWorkinguid();
		
		return getTargetElement(sitetopEl, workingUid, targetMenuId, ignoreSiteTop);
	}
	
	private Element getTargetElement(Element sitetopEl, String workingUid, String targetMenuId, boolean ignoreSiteTop) throws Exception{
		if(sitetopEl == null)
			return null;
		
		ISPrincipal p = SecurityController.getPrincipalByType("UIDPrincipal");
		String myUid = p.getName();
		
		Document document = sitetopEl.getOwnerDocument();
		Node node = null;
		node = AdminServiceUtil.getNodeById(document, "//site", targetMenuId);
		
		String siteTopId = sitetopEl.getAttribute("id");
		if(!ignoreSiteTop && node == null){
			node = (siteTopId.equals(targetMenuId))? sitetopEl : null;
		}

		if(node == null)
			return null;
		
		if(workingUid != null && !workingUid.equals(myUid)){
			// If other user has edit control
			workingUid = getUserName(workingUid);
			throw new IllegalAccessException("The user \"" + workingUid + "\" is editing it.");
		}
		
		// If the user is not tree administrator
		boolean isMenuTreeRoleUser = PortalAdminsService.getHandle().isMenuTreeRoleUser();
		if(isMenuTreeRoleUser && !isMyRoleTree(siteToJSON(siteTopId, sitetopEl, false), myUid)){
			String siteTopTitle = sitetopEl.getAttribute("title");
			throw new IllegalAccessException("The edit has not been permitted.[" + siteTopTitle + "]");
		}
		
		return (Element)node;
	}
	
	/* (Not Javadoc)
	 * @see jp.co.beacon_it.msd.admin.web.IMenuAdminService#addMenuItem(java.lang.String, java.lang.String, java.lang.String, java.util.List)
	 */
	public synchronized String addTopMenuItem(
			String id, 
			String title, 
			String href, 
			String display,
			String serviceURL, 
			String serviceAuthType, 
			String alert,
			String menuType,
			Collection auths,
			Collection<String> menuTreeAdmins)
	throws Exception {
		ISPrincipal p = SecurityController.getPrincipalByType("UIDPrincipal");
		String myUid = p.getName();

		href = StringUtil.getTruncatedString(href, 1024, "UTF-8");
		
		if(log.isInfoEnabled()){
			log.info("AddTopMenuItem: title=" + title + ", href=" + href + ", display=" + display ); 
		}
		
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document document = builder.newDocument();

		// Create element to insert
		Element element;
		element = document.createElement("site-top");
		element.setAttribute("id", id);
		element.setAttribute("title", title);
		element.setAttribute("href", href);
		element.setAttribute("display", display);
		element.setAttribute("workingUid", myUid);
		if(serviceURL != null)
			element.setAttribute("serviceURL", serviceURL);
		if (serviceAuthType != null)
			element.setAttribute("serviceAuthType", serviceAuthType);

		if(alert != null && !"".equals(alert))
			element.setAttribute("alert", alert);
		
		if(auths != null){
			element.appendChild(MenuAuthorization.createAuthsElement(document, auths));
		}
		
		if(menuTreeAdmins != null){
			element.appendChild(createAdminsElement(document, menuTreeAdmins));
		}
		
		document.appendChild(element);
		
		Siteaggregationmenu_temp entity = new Siteaggregationmenu_temp(new SITEAGGREGATIONMENU_TEMPPK(menuType, id));
		entity.setElement(document.getDocumentElement());
		entity.setWorkinguid(myUid);
		
		// Update
		this.siteAggregationMenuTempDAO.update(entity);
		
		// Add newly if there is changing order temp.
		Siteaggregationmenu_temp orderTemp = this.siteAggregationMenuTempDAO.selectBySitetopId(menuType, SiteAggregationMenuTempDAO.SITEMENU_ORDER_TEMP_ID);
		if(orderTemp != null){
			Element orderEl = orderTemp.getElement();
			Element orderSiteTop = orderEl.getOwnerDocument().createElement("site-top");
			orderSiteTop.setAttribute("id", id);
			orderEl.appendChild(orderSiteTop);
			
			orderTemp.setElement(orderEl);
			this.siteAggregationMenuTempDAO.update(orderTemp);
		}
		
		return id;

	}
	
	/* (Not Javadoc)
	 * @see jp.co.beacon_it.msd.admin.web.IMenuAdminService#addMenuItem(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.util.Map, java.util.List)
	 */
	public synchronized void addMenuItem(String id, String parentId,
			String title, String href, String display, String type, Map props,
			String alert, String menuType, Collection auths,
			Boolean linkDisabled, String directoryTitle, String sitetopId,
			boolean multi ) throws Exception {

		href = StringUtil.getTruncatedString(href, 1024, "UTF-8");
		
		if(log.isInfoEnabled()){
			log.info("AddMenuItem: title=" + title + ", href=" + href + ", display=" + display + ", type=" + type+ ", alert" + alert+ ", properties=" + props ); 
		}
		// Obtain data and transfer the result to Document.
		Siteaggregationmenu_temp entity = this.siteAggregationMenuTempDAO.selectBySitetopId(menuType, sitetopId);

		Node node = getTargetElement(entity, parentId);
		
		// Error
		if(node == null)
			throw new Exception("element not found [//site],[//site-top]");

		Document document = node.getOwnerDocument();

		Element element;
		element = document.createElement("site");
		element.setAttribute("id", id);
		element.setAttribute("title", title);
		element.setAttribute("href", href);
		element.setAttribute("display", display);
		element.setAttribute("link_disabled", linkDisabled.toString());
		element.setAttribute("multi",String.valueOf( multi ));
		element.setAttribute("type", type);
		if(alert != null && !"".equals(alert))
			element.setAttribute("alert", alert);
		if(directoryTitle != null && !"".equals(directoryTitle))
			element.setAttribute("directory_title", directoryTitle);

		element.appendChild(recreatePropertiesNode(document, element, props));

		if(auths != null){
			element.appendChild(MenuAuthorization.createAuthsElement(document, auths));
		}

		// Added at last
		node.appendChild(element);

		// Update
		entity.setElement(document.getDocumentElement());
		this.siteAggregationMenuTempDAO.update(entity);
	}

	/* (Not Javadoc)
	 * @see jp.co.beacon_it.msd.admin.web.IMenuAdminService#updateMenuItem(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.util.Map, java.util.List)
	 */
	public synchronized void updateMenuItem(String menuId, String title, String href,
			String display, String type, String serviceURL, String serviceAuthType, Map props, 
			String alert, String menuType, Collection auths, Collection<String> menuTreeAdmins, Boolean linkDisabled, String directoryTitle, String sitetopId,
			boolean multi ) throws Exception {
		
		href = StringUtil.getTruncatedString(href, 1024, "UTF-8");
		
		if(log.isInfoEnabled()){
			log.info("UpdateMenuItem: menuId=" + menuId + ", title=" + title + ", " +
					title + ", href=" + href + ", display=" + display + ", alert" + alert+ ", properties=" + props);
		}
		// Obtain data and transfer the result to Document.
		Siteaggregationmenu_temp entity = this.siteAggregationMenuTempDAO.selectBySitetopId(menuType, sitetopId);
		Node node = getTargetElement(entity, menuId);
		
		// Error
		if(node == null)
			throw new Exception("element not found [//site],[//site-top]");
		
		Document document = node.getOwnerDocument();

		// Create element to be updated
		Element element;
		element = (Element) node;
		element.setAttribute("title", title);
		element.setAttribute("href", href);
		element.setAttribute("display", display);
		element.setAttribute("link_disabled", linkDisabled.toString());
		if(serviceURL != null)
			element.setAttribute("serviceURL", serviceURL);
		if(serviceAuthType != null)
			element.setAttribute("serviceAuthType", serviceAuthType);
		if(alert != null && !"".equals(alert))
			element.setAttribute("alert", alert);
		
		element.setAttribute("multi",String.valueOf( multi ) );

		element.setAttribute("type", type);
		
		if(directoryTitle != null && !"".equals(directoryTitle)) {
			element.setAttribute("directory_title", directoryTitle);
		} else if( element.hasAttribute("directory_title")){
			element.removeAttribute("directory_title");
		}

		element.insertBefore(recreatePropertiesNode(document, element, props),
				element.getFirstChild());

		Element oldAuths = getFirstChildElementByName(element, "auths");
		if(oldAuths != null){
			element.removeChild(oldAuths);
		}
		if(auths != null){
			element.insertBefore(MenuAuthorization.createAuthsElement(document, auths), getFirstChildElementByName(element, "site"));
		}
		
		NodeList oldAdmins = element.getElementsByTagName("menuTreeAdmins");
		if(oldAdmins != null){
			while(oldAdmins.getLength() != 0){
				oldAdmins.item(0).getParentNode().removeChild(oldAdmins.item(0));
			}
		}
		if(menuTreeAdmins != null){
			element.insertBefore(createAdminsElement(document, menuTreeAdmins), getFirstChildElementByName(element, "site"));
		}
		
		// Update
		entity.setElement(document.getDocumentElement());
		this.siteAggregationMenuTempDAO.update(entity);
	}

	public void updateMenuItemAttribute(String menuType, String menuId, String attrName, String value, String sitetopId) throws Exception{
		if(log.isInfoEnabled()){
			log.info("UpdateMenuItemAttr: menuType=" +  menuType + ", menuId=" + menuId + ", attr=" + attrName + ", value=" + value);
		}
		// Obtain data and transfer the result to Document.
		Siteaggregationmenu_temp entity = this.siteAggregationMenuTempDAO.selectBySitetopId(menuType, sitetopId);

		Node node = getTargetElement(entity, menuId);

		// Error
		if(node == null)
			throw new Exception("element not found [//site],[//site-top]");
		
		Document document = entity.getElement().getOwnerDocument();

		// Create element to be updated
		Element element;
		element = (Element) node;
		element.getAttributeNode(attrName).setValue(value);
		
		// Update
		entity.setElement(document.getDocumentElement());
		this.siteAggregationMenuTempDAO.update(entity);
	}
	
	/**
	 * @param ownerDocument
	 * @param parentNode
	 * @param propertiesMap
	 * @return
	 * @throws Exception
	 */
	private Node recreatePropertiesNode(Document ownerDocument, Node parentNode, Map propertiesMap)
		throws Exception {
		Node node = AdminServiceUtil.getNodeById(parentNode, "./properties", null);
		AdminServiceUtil.removeSelf(node);
		Element propertiesElement = ownerDocument.createElement("properties");
		for (Iterator it = propertiesMap.keySet().iterator(); it.hasNext();) {
			Element element = ownerDocument.createElement("property");
			String key = (String) it.next();
			element.setAttribute("name", key);
			element.appendChild(ownerDocument.createTextNode( propertiesMap.get(key).toString()) );
			propertiesElement.appendChild(element);
		}
		return propertiesElement;
	}
	
	/**
	 * 
	 * @param document
	 * @param treeMenuAdmins
	 * @return
	 */
	private Element createAdminsElement(Document document, Collection treeMenuAdmins){
		String adminUid;
		
		Element adminsEl = document.createElement("menuTreeAdmins");
		Element adminEl;
		for(Iterator<String> ite=treeMenuAdmins.iterator();ite.hasNext();){
			adminUid = ite.next();
			adminEl = document.createElement("admin");
			adminEl.appendChild(document.createTextNode(adminUid));
			adminsEl.appendChild(adminEl);
		}
		return adminsEl;
	}

	/* (Not Javadoc)
	 * @see jp.co.beacon_it.msd.admin.web.IMenuAdminService#removeMenuItem(java.lang.String)
	 */
	public synchronized void removeMenuItem(String menuType, String menuId, String sitetopId) throws Exception {
		
		if(log.isInfoEnabled()){
			log.info("RemoveMenuItem: menuId=" + menuId); 
		}
		// Obtain data and transfer the result to Document.
		Siteaggregationmenu_temp entity = this.siteAggregationMenuTempDAO.selectBySitetopId(menuType, sitetopId);
		Node node = getTargetElement(entity, menuId, true);

		// Error
		if(node == null)
			throw new Exception("element not found [//site],[//site-top]");

		Document document = node.getOwnerDocument();

		// Delete node matches menuId
		AdminServiceUtil.removeSelf(node);

		// Update
		entity.setElement(document.getDocumentElement());
		this.siteAggregationMenuTempDAO.update(entity);
	}
	
	public synchronized void removeTopMenuItem(String menuType, String menuId, String sitetopId) throws Exception {
		
		if(log.isInfoEnabled()){
			log.info("RemovTopMenuItem: menuId=" + menuId); 
		}
		// Obtain data and transfer the result to Document.
		Siteaggregationmenu_temp entity = this.siteAggregationMenuTempDAO.selectBySitetopId(menuType, sitetopId);
		Element siteTop = getTargetElement(entity, menuId);

		// Error
		if(siteTop == null)
			throw new Exception("element not found [//site],[//site-top]");

		// Make flag
		siteTop.setAttribute("deleteFlag", "1");

		// Update
		entity.setElement(siteTop.getOwnerDocument().getDocumentElement());
		this.siteAggregationMenuTempDAO.update(entity);
	}
	
	/**
	 * @param menuId
	 * @param siblingId
	 * @throws Exception 
	 */
	public synchronized void replaceOrder(String menuType, String menuId, String parentId, String siblingId, String fromSitetopId, String toSitetopId) throws Exception {
		if(log.isInfoEnabled()){
			log.info("MoveMenuItem: menuId=" + menuId + ", parentId=" + parentId + ", siblingId=" + siblingId + ", menuType=" + menuType + ", fromSitetopId=" + fromSitetopId + ", toSitetopId=" + toSitetopId); 
		}
		
		boolean isSameTree = fromSitetopId.equals(toSitetopId);
		
		// Obtain data and transfer the result to Document.
		Siteaggregationmenu_temp fromEntity = this.siteAggregationMenuTempDAO.selectBySitetopId(menuType, fromSitetopId);
		Siteaggregationmenu_temp toEntity = (isSameTree)? fromEntity : this.siteAggregationMenuTempDAO.selectBySitetopId(menuType, toSitetopId);

		Element menuNode = getTargetElement(fromEntity, menuId);
		
		// Error
		if(menuNode == null)
			throw new Exception("menuNode not found [//site],[//site-top]");


		Element fromMenuEl = menuNode.getOwnerDocument().getDocumentElement();
		Element toMenuEl = (isSameTree)? fromMenuEl : toEntity.getElement();
		Document document = toMenuEl.getOwnerDocument();

		// Search for node matches parentId from <site-top> or <site>
		Element parentNode = (Element)AdminServiceUtil.getNodeById(document, "//site", parentId);
		if(parentNode == null)
			parentNode = (Element)AdminServiceUtil.getNodeById(document, "//site-top", parentId);
		
		// Search for node matches siblingId from parentNode
		Element siblingNode = (Element)AdminServiceUtil.getNodeById(document, "//site", siblingId);
		
		Element moveMenuNodeCopy = (Element)document.importNode(menuNode, true);
		if(siblingNode == null){
			parentNode.appendChild(moveMenuNodeCopy);
		}else{
			parentNode.insertBefore(moveMenuNodeCopy, siblingNode);
		}

		// Delete the node moving from.
		AdminServiceUtil.removeSelf(menuNode);

		// Update the node moving to.
		toEntity.setElement(toMenuEl);
		this.siteAggregationMenuTempDAO.update(toEntity);
		
		// Update source entity if it is moved between tree.
		if(!isSameTree){
			fromEntity.setElement(fromMenuEl);
			this.siteAggregationMenuTempDAO.update(fromEntity);
		}
	}
	// alias
	public synchronized void replaceOrder(String menuType, String menuId, String siblingId, String fromSitetopId, String toSitetopId) throws Exception {
		replaceOrder( menuType, menuId, null, siblingId, fromSitetopId, toSitetopId );
	}
	
	public synchronized void replaceTopOrder(String menuType, String menuId, String siblingId) throws Exception {
		if(log.isInfoEnabled()){
			log.info("MoveMenuItem: menuId=" + menuId + ", siblingId=" + siblingId + ", menuType=" + menuType); 
		}
		
		// Obtain data and transfer the result to Document.
		Siteaggregationmenu_temp orderEntity = this.siteAggregationMenuTempDAO.selectBySitetopId(menuType, SiteAggregationMenuTempDAO.SITEMENU_ORDER_TEMP_ID);
		Element orderEl = orderEntity.getElement();
		Element siteTopEl = (Element)AdminServiceUtil.getNodeById(orderEl.getOwnerDocument(), "//site-top", menuId);
		
		Element targetEl = getTargetElement(siteTopEl, orderEntity.getWorkinguid(), menuId, false);
		
		// Error
		if(targetEl == null)
			throw new Exception("targetElement not found [//site],[//site-top]");

		// Search for node matches siblingId from <site-top>.
		Element siblingSiteTopEl = (Element)AdminServiceUtil.getNodeById(orderEl.getOwnerDocument(), "//site-top", siblingId);
		Element siblingEl = getTargetElement(siblingSiteTopEl, orderEntity.getWorkinguid(), siblingId, false); 
		
		if(siblingEl == null){
			targetEl.getParentNode().appendChild(targetEl);
		}else{
			targetEl.getParentNode().insertBefore(targetEl, siblingEl);
		}

		// Update
		orderEntity.setElement(orderEl);
		this.siteAggregationMenuTempDAO.update(orderEntity);
	}
	
	/**
	 * Return menu json on reference mode
	 * 
	 * @param menuType
	 * @return
	 * @throws Exception
	 */
	public String getMenuTree(String menuType) throws Exception {
		Siteaggregationmenu entity = getMenuEntity(menuType);
		if(entity == null) return "";
		
		return getMenuTree(entity, null);
	}
	
	/* (Not Javadoc)
	 * @see jp.co.beacon_it.msd.admin.web.IMenuAdminService#getMenuTree()
	 */
	private String getMenuTree(Siteaggregationmenu entity, List<String> targetMenuIdList) throws Exception {

		ISPrincipal p = SecurityController.getPrincipalByType("UIDPrincipal");
		String myUid = p.getName();
		
		// Menu tree administrator
		boolean isMenuTreeRoleUser = PortalAdminsService.getHandle().isMenuTreeRoleUser();
		
		// Transform result
		Element menuEl = entity.getElement();
		
		JSONObject json = new JSONObject();			// menuItem
		JSONArray siteTopArray = new JSONArray();	// siteTopID Array
		JSONObject mapJson = new JSONObject();		// parentId:[menuId] Map
		
		NodeList siteTopList = menuEl.getElementsByTagName("site-top");
		Element siteTop;
		for(int i=0;i<siteTopList.getLength();i++){
			siteTop = (Element)siteTopList.item(i);
			String siteTopId = siteTop.getAttribute("id");
			
			JSONObject topTreeMenuJson = siteToJSON(siteTopId, siteTop, false);
			
			if(!isMenuTreeRoleUser || (isMenuTreeRoleUser && isMyRoleTree(topTreeMenuJson, myUid))){
				if(targetMenuIdList != null && !targetMenuIdList.contains(siteTopId))
					continue;
				
				siteTopArray.put(siteTopId);
				mapJson.put(siteTopId, getChildSiteArray(siteTop));
				json.put(siteTopId, topTreeMenuJson);
				addChildSiteJSON(siteTopId, siteTop, json, mapJson, false);
			}
		}
		
		return "ISA_SiteAggregationMenu.setMenu(" + json.toString() + "," +  siteTopArray.toString() + "," + mapJson.toString() + ");";
	}
	
	/**
	 * Whether the id is administrator of tree or not
	 * 
	 * @param menuItemJson
	 * @param myUid
	 * @return <UL>
	 * 				<LI>true: The id is administrator of menu tree</LI>
	 * 				<LI>false:The id is not administrator of menu tree</LI>
	 * 			</UL>
	 * @throws JSONException
	 */
	private static boolean isMyRoleTree(JSONObject topTreeMenuJson, String myUid) throws JSONException{
		JSONArray menuTreeAdminsArray = topTreeMenuJson.getJSONArray("menuTreeAdmins");
		
		for(int i=0;i<menuTreeAdminsArray.length();i++){
			if(myUid.equals(menuTreeAdminsArray.get(i)))
				return true;
		}
		
		return false;
	}
	
	/**
	 * Create JSONObject for under the menu top
	 * 
	 * @param siteEl
	 * @param json
	 * @param mapJson
	 * @throws DOMException
	 * @throws JSONException
	 * @throws TransformerException
	 */
	private static void addChildSiteJSON(String sitetopId, Element siteEl, JSONObject json, JSONObject mapJson, boolean isEditMode) throws DOMException, JSONException, TransformerException{
		NodeList childs = siteEl.getChildNodes();
		
		for(int i=0;i<childs.getLength();i++){
			if(childs.item(i).getNodeType() != Node.ELEMENT_NODE)
				continue;
			
			Element childSite = (Element)childs.item(i);
			if("site".equalsIgnoreCase(childSite.getNodeName())){
				String siteId = childSite.getAttribute("id");
				
				json.put(siteId, siteToJSON(sitetopId, childSite, isEditMode));
				
				if(mapJson != null)
					mapJson.put(siteId, getChildSiteArray(childSite));
				
				addChildSiteJSON(sitetopId, childSite, json, mapJson, isEditMode);
			}
		}
	}
	
	/**
	 * Create JSONArray that is child site of site|site-top.
	 * 
	 * @param siteEl
	 * @return
	 */
	private static JSONArray getChildSiteArray(Element siteEl){
		JSONArray childSiteArray = new JSONArray();
		NodeList childs = siteEl.getChildNodes();
		
		for(int i=0;i<childs.getLength();i++){
			if(childs.item(i).getNodeType() != Node.ELEMENT_NODE)
				continue;
			
			Element childSite = (Element)childs.item(i);
			if("site".equalsIgnoreCase(childSite.getNodeName())){
				childSiteArray.put(childSite.getAttribute("id"));
			}
		}
		return childSiteArray;
	}
	
	/**
	 * Create JSONObject of site
	 * 
	 * @param siteEl
	 * @return
	 * @throws DOMException
	 * @throws JSONException
	 * @throws TransformerException
	 */
	private static JSONObject siteToJSON(String sitetopId, Element siteEl, boolean isEditMode) throws DOMException, JSONException, TransformerException{
		JSONObject json = new JSONObject();
		
		// attrs
		NamedNodeMap attMap = siteEl.getAttributes();
		Node attr;
		for(int i=0;i<attMap.getLength();i++){
			attr = attMap.item(i);
			if("link_disabled".equalsIgnoreCase(attr.getNodeName())){
				json.put("linkDisabled", new Boolean(attr.getNodeValue()));
			}
			else if("directory_title".equalsIgnoreCase(attr.getNodeName())){
				json.put("directoryTitle", attr.getNodeValue());
			}
			else{
				json.put(attr.getNodeName(), attr.getNodeValue());
			}
		}
		
		// parentId
		if(!sitetopId.equals(siteEl.getAttribute("id"))){
			Element parentEl = (Element)siteEl.getParentNode();
			if(parentEl != null && (parentEl.getNodeName().equalsIgnoreCase("site") || parentEl.getNodeName().equalsIgnoreCase("site-top"))){
			json.put("parentId", parentEl.getAttribute("id"));
			}
		}
		
		// properties
		JSONObject propsJson = new JSONObject();
		Element propEl = (Element)XPathAPI.selectSingleNode(siteEl, "properties");
		if(propEl != null){
			NodeList propertyList = propEl.getElementsByTagName("property");
			Element propertyEl;
			for(int i=0;i<propertyList.getLength();i++){
				propertyEl = (Element)propertyList.item(i);
				propsJson.put(propertyEl.getAttribute("name"), (propertyEl.getFirstChild() != null)? propertyEl.getFirstChild().getNodeValue() : "");
			}
		}
		json.put("properties", propsJson);
		
		// auths
		Element authsEl = (Element) XPathAPI.selectSingleNode(siteEl, "auths");
		if (authsEl != null) {
			json.put("auths", MenuAuthorization.createAuthsJson(authsEl));
		}
		
		// admins
		JSONArray adminsArray = new JSONArray();
		Element adminsEl = (Element)XPathAPI.selectSingleNode(siteEl, "menuTreeAdmins");
		if(adminsEl != null){
			NodeList adminList = adminsEl.getElementsByTagName("admin");
			Element adminEl;
			for(int i=0;i<adminList.getLength();i++){
				adminEl = (Element)adminList.item(i);
				if(adminEl.getFirstChild() != null){
					adminsArray.put(adminEl.getFirstChild().getNodeValue());
				}
			}
		}
		json.put("menuTreeAdmins", adminsArray);
		
		if(isEditMode)
			json.put("isEditMode", true);
		
		return json;
	}
	
	/**
	 * Delete all temporary data that the uid has.
	 * 
	 * @param uid
	 * @throws Exception 
	 */
	private void removeTempMenu(String uid) throws Exception{
		ISPrincipal p = SecurityController.getPrincipalByType("UIDPrincipal");
		String myUid = p.getName();
		this.siteAggregationMenuTempDAO.deleteByUser(myUid);
	}
	
	public synchronized void removeTempMenu() throws Exception {
		ISPrincipal p = SecurityController.getPrincipalByType("UIDPrincipal");
		
		removeTempMenu(p.getName());
	}
	
	public class ForceUpdateUserPref{
		private String name;
		private String value;
		private boolean implied;
		public ForceUpdateUserPref(String name){
			this.name = name;
		}
		public void setImplied(boolean implied){
			this.implied = implied;
		}
		public boolean isImplied(){
			return this.implied;
		}
		public String getValue(){
			return this.value;
		}
		public String getName() {
			return this.name;
		}
		
	}
	
	/**
	 * Commiting temporary data.
	 * @param menuType topmenu|sidemenu
	 * @param forceUpdateMap
	 * @throws Exception
	 */
	public synchronized void commitMenu(String menuType, Map<String, List<ForceUpdateUserPref>> forceUpdateMap, List<String> editSitetopIdList) throws Exception{
		ISPrincipal p = SecurityController.getPrincipalByType("UIDPrincipal");
		String myUid = p.getName();
		
		Siteaggregationmenu currentEntity = this.siteAggregationMenuDAO.select(menuType);

		Element currentMenuEl = currentEntity.getElement();
		Document currentDoc = currentMenuEl.getOwnerDocument();
		
		// Get the tree that the user edited
		List<Siteaggregationmenu_temp> myTempList = this.siteAggregationMenuTempDAO.selectByTypeAndUser(menuType, myUid);
		
		checkError(myTempList, editSitetopIdList, menuType);
		
		// Merge
		Element siteTop;
		WidgetDAO dao = WidgetDAO.newInstance();
		List<Element> deleteSiteTopList = new ArrayList<Element>();
		Siteaggregationmenu_temp orderTemp = null;
		
		for(Siteaggregationmenu_temp tempEntity : myTempList){
			if(tempEntity.getId().getSitetopid().equals(SiteAggregationMenuTempDAO.SITEMENU_ORDER_TEMP_ID)){
				orderTemp = tempEntity;
				continue;
			}
			
			// Update last modified time
			tempEntity.setLastmodified(new Date());
			this.siteAggregationMenuTempDAO.update(tempEntity);

			siteTop = tempEntity.getElement();
			String siteTopId = tempEntity.getId().getSitetopid();
			Node currentSiteTopEl = XPathAPI.selectSingleNode(currentMenuEl, "site-top[@id=\"" + siteTopId + "\"]");
			
			// Import
			Node mergeEl = currentDoc.importNode(siteTop.cloneNode(true), true);
			
			if(siteTop.getAttributeNode("deleteFlag") != null){
				// If the tree is deleted
				if(currentSiteTopEl != null)
					deleteSiteTopList.add((Element)currentSiteTopEl);
				continue;
			}
			else {
				if(currentSiteTopEl != null){
					currentDoc.getDocumentElement().replaceChild(mergeEl, currentSiteTopEl);
				}else{
					currentDoc.getDocumentElement().appendChild(mergeEl);
				}
			}
			
			// Properties should not be updated if existing data is not changed.
			if(currentSiteTopEl == null)
				continue;
			
			//Properties of url and authType in current menu is added to Map.
			NodeList sites = siteTop.getElementsByTagName("site");
			for (int i = 0; i < sites.getLength(); i++) {
				Element site = (Element) sites.item(i);
				String type = site.getAttribute("type");
				if (type == null)continue;

				String menuId = site.getAttribute("id");
				List<ForceUpdateUserPref> updatePropList = forceUpdateMap.get(menuId);
				if(updatePropList == null || updatePropList.isEmpty())continue;
				

				NodeList properties = XPathAPI.selectNodeList(site,
						"properties/property");
				
				Map<String, String> propMap = new HashMap<String, String>();
				for (int j = 0; j < properties.getLength(); j++) {
					Element property = (Element) properties.item(j);
					String name = property.getAttribute("name");
					String value = property.getFirstChild() != null ? property
							.getFirstChild().getNodeValue() : "";
					propMap.put(name, value);
				}
				
				String title = null;
				String href = null;
				Map<String, ForceUpdateUserPref> upPropMap = new HashMap<String, ForceUpdateUserPref>();
				Set<ForceUpdateUserPref> removePropNames = new HashSet<ForceUpdateUserPref>();
				for(ForceUpdateUserPref prop: updatePropList){
					if("__MENU_TITLE__".equals(prop.name)){
						title = site.getAttribute("title");
					}else if("__MENU_HREF__".equals(prop.name)){
						href = site.getAttribute("href");			
					}else{
						String value = propMap.get(prop.name);
						prop.value = value;
						if(value != null){
							upPropMap.put(prop.name, prop);
						}else{
							removePropNames.add(prop);
						}
					}
				}

				if(title != null || href != null || upPropMap.size() > 0 || removePropNames.size() > 0)
					dao.updateWidgetProperties(menuId, title, href, upPropMap, removePropNames);
			}
		}
		
		// Deleting tree
		for(Element deleteSiteTop : deleteSiteTopList){
			AdminServiceUtil.removeSelf(deleteSiteTop);
		}
		
		// Changing order of tree
		if(orderTemp != null){
			Element orderEl = orderTemp.getElement();
			NodeList orderSiteTopList = orderEl.getElementsByTagName("site-top");
			
			for(int i=0;i<orderSiteTopList.getLength();i++){
				Element orderSiteTop = (Element)orderSiteTopList.item(i);
				Element targetNode = (Element)XPathAPI.selectSingleNode(currentMenuEl, "site-top[@id=\"" + orderSiteTop.getAttribute("id") + "\"]");
				
				if(targetNode != null)
					targetNode.getParentNode().appendChild(targetNode);
			}
		}
		
		//Apply the change of menu to widget.
		currentEntity.setElement(currentMenuEl);
		this.siteAggregationMenuDAO.update(currentEntity);
	}

	/**
	 * Return menu entity
	 * 
	 * @param menuType
	 * @return
	 */
	public Siteaggregationmenu getMenuEntity(String menuType){
		// Obtain data
		Siteaggregationmenu entity = this.siteAggregationMenuDAO.select(menuType);
		if(log.isDebugEnabled())log.debug(entity.getData());
		if (entity == null) {
			log.error("siteaggregationmenu not found.");
			return null;
		}
		
		return entity;
	}
		
	public String getMenuTreeXml(String menuType, boolean ignoreAccessControl) throws Exception {
		try {
			List<MenuItem> items = MenuItemDAO.newInstance().getTree();
						
			StringBuffer buf = new StringBuffer();
			buf.append("<sites>\n");
			
			for(MenuItem item: items){
				buildAuthorizedMenuXml(item, buf, ignoreAccessControl );
			}
			buf.append("</sites>");
			
			return buf.toString();
		} catch (Exception e) {
			log.error("Unexpected error occurred.", e);
			throw e;
		}
	}
	
	private static void buildAuthorizedMenuXml(MenuItem menuItem, StringBuffer buf, boolean noAuth ) throws ClassNotFoundException{

		List<MenuItem> childItems = menuItem.getChildItems();
		Element propertiesNode = null;
		boolean accessible = true;
		
		//TODO:ACL
		/*
		for(MenuItem item: childItems){
			Node node = childItems.item(i);
			if("auths".equals(node.getNodeName()) && !noAuth ){
				accessible = false;
				Element rolesEl = (Element)node;
				NodeList roles = rolesEl.getElementsByTagName("auth");
				for(int j = 0; j < roles.getLength(); j++){
					Element auth = (Element)roles.item(j);
					String type = auth.getAttribute("type");
					String regx = auth.getAttribute("regx");
					if(RoleUtil.isPermitted(type, regx)){
						accessible = true;
					}
				}
			}
			if("site".equals(node.getNodeName())){
				childSites.add(node);
			}
			if("properties".equals(node.getNodeName())){
				propertiesNode = (Element)node;
			}
		}
		if(!accessible){
			return;
		}
		*/
		String menuElName = ( menuItem.getFkParent() == null ? "site-top" : "site" );
		buf.append("<" + menuElName);
		buf.append(" id=\"" + menuItem.getId() + "\"");
		buf.append(" title=\"" + menuItem.getTitle() + "\"");
		if(menuItem.getHref() != null)
			buf.append(" href=\"" + menuItem.getHref() + "\"");
		GadgetInstance gadgetInstance = menuItem.getFkGadgetInstance();
		boolean isRemoteGadget = false;
		if(gadgetInstance != null){
			buf.append(" ginstid=\"" + gadgetInstance.getId() + "\"");
			String type = gadgetInstance.getType();
			if(type.indexOf("http") == 0 ){
				buf.append(" type=\"Gadget\"");
				isRemoteGadget = true;
			}else if (type.indexOf("upload_") == 0){
				buf.append(" type=\"g_" + gadgetInstance.getType() + "/gadget\"");
			}else
				buf.append(" type=\"" + gadgetInstance.getType() + "\"");
		}
		buf.append(">\n");
		
		buf.append("<properties>\n");
		if(isRemoteGadget){
			buf.append("<property name=\"url\">");
			buf.append("g_" + gadgetInstance.getType() + "/gadget");
			buf.append("</property>");
		}
		if(gadgetInstance != null && !gadgetInstance.getGadgetInstanceUserPrefs().isEmpty()){
			for(GadgetInstanceUserpref userPref: gadgetInstance.getGadgetInstanceUserPrefs()){
				setElement2Buf(userPref, buf);
			}
		}
		buf.append("</properties>\n");
		
		for(MenuItem item: menuItem.getChildItems())
			buildAuthorizedMenuXml(item, buf, noAuth );
		
		buf.append("</").append(menuElName).append(">\n");
		
	}
	
	private static void setElement2Buf(GadgetInstanceUserpref userPref, StringBuffer buf){
		buf.append("<property name=\"" + userPref.getId().getName() + "\">");
		buf.append(userPref.getValue());
		buf.append("</property>");
	}
	
	private static Element getFirstChildElementByName(Element el, String name){
		if(el != null && name != null){
			NodeList childNodes = el.getChildNodes();
			for(int i = 0; i < childNodes.getLength(); i++){
				Node node = childNodes.item(i);
				if(node.getNodeName().equals(name)){
					return (Element)node;
				}
			}
		}
		return null;
		
	}
	
	public String getMenuTreeAdminUsers() throws Exception{
		List<Portaladmins> admins = PortalAdminsService.getHandle().getPortalAdmins();
		JSONArray treeAdminArray = new JSONArray();
		
		for(Portaladmins admin : admins){
			Adminrole adminRole = admin.getAdminrole();
			if(adminRole == null)
				continue;
			
			String permissionStr = adminRole.getPermission();
			JSONArray jArray = new JSONArray(permissionStr);
			for(int i=0;i<jArray.length();i++){
				if("menu_tree".equals(jArray.getString(i)))
					treeAdminArray.put(admin.getUid());
			}
		}
		
		return treeAdminArray.toString();
	}
	
	private static List<String> getSiteTopIdList(Element element){
		
		NodeList sitetopList = element.getElementsByTagName("site-top");
		List<String> siteTopIdList = new ArrayList();
		
		for(int i=0;i<sitetopList.getLength();i++){
			Element siteTop = (Element)sitetopList.item(i);
			siteTopIdList.add(siteTop.getAttribute("id"));
		}
		return siteTopIdList;
	}

	private static String getUserName(String workingUid){
		// If the other user has edit control
		if (!SearchUserService.isAvailable())
			return workingUid;
		try {
			SearchUserService search = (SearchUserService) SpringUtil
					.getBean("searchUserService");
			IAccount user = search.getUser(workingUid);
			if (user != null) {
				workingUid = user.getName();
				if (user.getGroupName() != null)
					workingUid += "/" + user.getGroupName();
			}
		} catch (NoSuchBeanDefinitionException e) {
			log.warn("searchUserService not found.", e);
		} catch (Exception e) {
			log.warn("unexpected error occured in searchUserService.", e);
		}
		return workingUid;
	}
	
	private void checkError(Siteaggregationmenu_temp tempEntity) throws Exception{
		if(tempEntity == null)
			throw new MenusTimeoutException();
		
		List<Siteaggregationmenu_temp> myTempList = new ArrayList<Siteaggregationmenu_temp>();
		myTempList.add(tempEntity);
		
		List<String> editSitetopIdList = new ArrayList<String>();
		editSitetopIdList.add(tempEntity.getId().getSitetopid());
		checkError(myTempList, editSitetopIdList, tempEntity.getId().getType());
	}
	
	private void checkError(List<Siteaggregationmenu_temp> myTempList, List<String> editSitetopIdList, String menuType) throws Exception{
		ISPrincipal p = SecurityController.getPrincipalByType("UIDPrincipal");
		String uid = p.getName();

		if(myTempList.size() == 0 || deleteTimeoutTemp(menuType, uid)){
			throw new MenusTimeoutException();
		}
		else{
			// When you contain the tree without the right of the edit
			List<String> tempSitetopIdList = new ArrayList<String>();
			for(Siteaggregationmenu_temp tempEntity : myTempList){
				tempSitetopIdList.add(tempEntity.getId().getSitetopid());
			}
			
			List<String> errorSitetopIdList = new ArrayList<String>();
			for(String editSitetopId : editSitetopIdList){
				if(!tempSitetopIdList.contains(editSitetopId))
					errorSitetopIdList.add(editSitetopId);
			}
			
			if(errorSitetopIdList.size() > 0){
				throw new MenusIllegalEditException(errorSitetopIdList);
			}
		}
	}
}
