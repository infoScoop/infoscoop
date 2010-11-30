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

import java.util.List;
import java.util.Set;

import javax.security.auth.Subject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.acl.ISPrincipal;
import org.infoscoop.acl.SecurityController;
import org.infoscoop.dao.MenuItemDAO;
import org.infoscoop.dao.MenuTreeDAO;
import org.infoscoop.dao.model.GadgetInstance;
import org.infoscoop.dao.model.GadgetInstanceUserpref;
import org.infoscoop.dao.model.MenuItem;
import org.infoscoop.dao.model.MenuTree;
import org.infoscoop.dao.model.Role;
import org.infoscoop.dao.model.RolePrincipal;
import org.infoscoop.util.XmlUtil;

public class SiteAggregationMenuService {

	private static Log log = LogFactory.getLog(SiteAggregationMenuService.class);
	
	public String getMenuTreeXml(String menuType, boolean ignoreAccessControl)
			throws Exception {
		try {
			MenuTree tree = MenuTreeDAO.newInstance().getByPosition(
					menuType.equals("topmenu") ? "top" : "side");
			List<MenuItem> items = MenuItemDAO.newInstance().getTree(tree);

			StringBuffer buf = new StringBuffer();
			buf.append("<sites>\n");

			for (MenuItem item : items) {
				buildAuthorizedMenuXml(item, buf, ignoreAccessControl);
			}
			buf.append("</sites>");

			return buf.toString();
		} catch (Exception e) {
			log.error("Unexpected error occurred.", e);
			throw e;
		}
	}
	
	private static void buildAuthorizedMenuXml(MenuItem menuItem, StringBuffer buf, boolean noAuth ) throws ClassNotFoundException{
		if(menuItem.isPrivateBool())
			return;
		//TODO: make following block to function.
		if(menuItem.isSpecialAccess()){
			boolean canDisplay = false;
			Set<Role> roles = menuItem.getRoles();
			for(Role role: roles){
				for(RolePrincipal p: role.getRolePrincipals()){
					Subject loginUser = SecurityController.getContextSubject();
					for(ISPrincipal principal : loginUser.getPrincipals(ISPrincipal.class)){
						if(p.getType().equalsIgnoreCase(principal.getType()) && 
								p.getName().equalsIgnoreCase(principal.getName())){
							canDisplay = true;
							break;
						}
					}
				}
			}
			if(!canDisplay)return;
		}
			
		
		String menuElName = ( menuItem.getFkParent() == null ? "site-top" : "site" );
		buf.append("<" + menuElName);
		buf.append(" id=\"" + menuItem.getId() + "\"");
		buf.append(" title=\"" + XmlUtil.escapeXmlEntities(menuItem.getTitle())
				+ "\"");
		if(menuItem.getHref() != null)
			buf.append(" href=\""
					+ XmlUtil.escapeXmlEntities(menuItem.getHref()) + "\"");
		GadgetInstance gadgetInstance = menuItem.getGadgetInstance();
		boolean isRemoteGadget = false;
		if (gadgetInstance != null) {
			buf.append(" ginstid=\"" + gadgetInstance.getId() + "\"");
			String type = gadgetInstance.getType();
			if (type.indexOf("http") == 0) {
				buf.append(" type=\"Gadget\"");
				isRemoteGadget = true;
			} else if (type.indexOf("upload_") == 0) {
				buf.append(" type=\"g_" + XmlUtil.escapeXmlEntities(type)
						+ "/gadget\"");
			} else
				buf.append(" type=\"" + XmlUtil.escapeXmlEntities(type) + "\"");
		}
		buf.append(" alert=\"").append(menuItem.getAlert()).append("\"");
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
	
}
