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
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MenuAuthorization{
	private static Log log = LogFactory.getLog(MenuAuthorization.class);
	
	String principalClass;
	String regx;
	String[] actions;
	public MenuAuthorization(String principalClassName, String regx, String[] actions) throws ClassNotFoundException{
		this.principalClass = principalClassName;
		this.regx = regx;
		this.actions = actions;
	}
	
	public static MenuAuthorization newMenuAuthorization( Object obj ) throws ClassNotFoundException {
		if( obj instanceof MenuAuthorization )
			return ( MenuAuthorization )obj;
		
		Map map = ( Map )obj;
		String type = ( String )map.get("type");
		String regx = ( String )map.get("regx");
		
		Object[] actions = (( Collection )map.get("actions")).toArray();
		String[] strActions = new String[ actions.length ];
		for( int i=0;i<actions.length;i++ )
			strActions[i] = actions[i].toString();
		
		try {
			return new MenuAuthorization( type,regx,strActions );
		} catch (ClassNotFoundException ex) {
			if (log.isErrorEnabled())
				log.error(
						"Principal class for Authorization is not found. This is bug case. "
								+ ex.getMessage(), ex);
			throw new IllegalArgumentException(
					"Principal class for Authorization is not found. This is bug case. "/*,ex */);
		}
	}

	
	/**
	 * @param document
	 * @param auths
	 * @return
	 * @throws Exception
	 */
	public static Element createAuthsElement(Document document,
			Collection auths) throws Exception {
		Element authsEl = document.createElement("auths");
		for( Iterator ite=auths.iterator();ite.hasNext();) {
			MenuAuthorization auth = newMenuAuthorization( ite.next());
			
			Element element = document.createElement("auth");
			element.setAttribute("type", auth.principalClass);
			element.setAttribute("regx", auth.regx);
			if (auth.actions != null && auth.actions.length > 0) {
				StringBuffer actionsStr = new StringBuffer();
				actionsStr.append("[");
				for (int j = 0; j < auth.actions.length; j++) {
					actionsStr.append("'").append(auth.actions[j]).append(
							"'");
				}
				actionsStr.append("]");
				element.setAttribute("actions", actionsStr.toString());
			}
			authsEl.appendChild(element);
		}
		return authsEl;
	}
	
	public static JSONArray createAuthsJson(Element authsEl)
			throws DOMException, JSONException {
		if (authsEl == null)
			return null;
		JSONArray authArray = new JSONArray();
		NodeList authList = authsEl.getElementsByTagName("auth");
		for (int i = 0; i < authList.getLength(); i++) {
			JSONObject authJson = new JSONObject();
			Element authEl = (Element) authList.item(i);
			NamedNodeMap attMap = authEl.getAttributes();
			for (int j = 0; j < attMap.getLength(); j++) {
				Node attr = attMap.item(j);
				if ("actions".equals(attr.getNodeName())) {
					authJson.put(attr.getNodeName(), new JSONArray(attr
							.getNodeValue()));
				} else {
					authJson.put(attr.getNodeName(), attr.getNodeValue());
				}
			}
			authArray.put(authJson);
		}
		return authArray;
	}
}
