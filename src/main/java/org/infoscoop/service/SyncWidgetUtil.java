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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Define which property reflect on each user's widget when a menu and default setting were changed. I .
 * @author a-kimura
 *
 */
public class SyncWidgetUtil {
	//The definition that which property reflect on a widget when a menu of any type was changed.
	private static Map syncProperties = new HashMap();
	static {
		syncProperties.put("RssReader", Arrays.asList(new String[] { "url",
				"authType" }));
		syncProperties.put("MultiRssReader", Arrays.asList(new String[] {
				"url", "authType" }));
		syncProperties.put("MiniBrowser", Arrays.asList(new String[] { "url",
				"height" }));
		syncProperties.put("FragmentMiniBrowser", Arrays.asList(new String[] {
				"url", "height", "xPath", "cacheLifeTime", "charset", "cacheID", "authType" }));
		syncProperties.put("Gadget", Arrays.asList(new String[] { "url" }));
	}

	private static List getPropertyNameList(String type) {
		return (List) syncProperties.get(type);
	}

	public static boolean hasType(String type) {
		return getPropertyNameList(type) != null;
	}

	public static Iterator getPropertyNames(String type) {
		return getPropertyNameList(type).iterator();
	}

	public static boolean hasProperty(String type, String propName) {
		return getPropertyNameList(type).contains(propName);
	}
}
