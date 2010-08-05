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

package org.infoscoop.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.dao.I18NDAO;
import org.infoscoop.service.I18NService;

public class I18NUtil {
	public static final String TYPE_JS = "js";

	public static final String TYPE_ADMINJS = "adminjs";

	public static final String TYPE_MENU = "menu";

	public static final String TYPE_WIDGET = "widget";

	public static final String TYPE_SEARCH = "search";

	public static final String TYPE_LAYOUT = "layout";

	public static final String TYPE_PROPERTY = "property";

	private static final Pattern REPACE_PATTERN = Pattern
			.compile("[\\%\\!]\\{([a-zA-Z0-9\\.-_]+)\\}");
	private static final Pattern REPACE_PATTERN2 = Pattern
			.compile("\\!\\{([a-zA-Z0-9\\.-_]+)\\}");
	private static final Pattern ESCAPE_REPACEMENT_1 = Pattern.compile("\\\\");
	private static final Pattern ESCAPE_REPACEMENT_2 = Pattern.compile("\\$");
	
	private static Map i18nMap = new HashMap();

	private static Log log = LogFactory.getLog(I18NUtil.class);

	public static Map getResourceMap( String type,Locale locale ) {
		String country = locale.getCountry();
		String lang = locale.getLanguage();

		Map resMap = null;
		
		I18NDAO i18NDAO = I18NDAO.newInstance();
		
		String lastmodified = i18NDAO.getLastmodified(type);
		String key = type + "_" + country + "_" + lang;
		ResourceMap rm = (ResourceMap) i18nMap.get(key);
		if (rm != null) {
			String current = rm.getLastmodified();
			if (lastmodified == null
					|| (current != null && current.equals(lastmodified))) {
				resMap = rm.getResMap();
			}
		}
		if (resMap == null) {
			resMap = I18NService.getHandle().getResourceMap(type, country, lang);
			i18nMap.put(key, new ResourceMap(lastmodified, resMap));
		}
		
		return resMap;
	}
	/**
	 * If "onlySystem" is true, we substitute only "!{ID}",
	 * the case that it's false, we do "%{ID}" and "!{ID}".
	 * @param type
	 * @param str
	 * @param locale
	 * @param onlySystem
	 * @return
	 */
	public static String resolve(String type, String str, Locale locale,
			boolean onlySystem) {
		return replace(str, getResourceMap(type, locale), onlySystem, false);
	}
	public static String resolve(String type, String str, Locale locale) {
		return resolve(type, str, locale, false);
	}
	
	public static String resolveForXML(String type, String xml, Locale locale) {
		return replace(xml, getResourceMap(type, locale), false, true);
	}
	
	private static String replace(String str, Map resMap, boolean onlySystem, boolean escapeXML){
		StringBuffer sb = new StringBuffer();
		Matcher m = onlySystem ? REPACE_PATTERN2.matcher(str) : REPACE_PATTERN
				.matcher(str);
		while (m.find()) {
			String g = m.group(1);
			String replacement = (String) resMap.get(g);
			if (replacement == null)
				replacement = g;
			else
				replacement = escapeReplacement(escapeXML? XmlUtil.escapeXmlEntities(replacement) : replacement);

			m.appendReplacement(sb, replacement);
		}
		m.appendTail(sb);
		return sb.toString();
	}
	
	public static String replace(String str, Map resMap) {
		return replace(str, resMap, false, false);
	}
	
	@SuppressWarnings("unchecked")
	private Map currentResourceMap;

	public I18NUtil(String type, Locale locale) {
		this.currentResourceMap = getResourceMap(type, locale);
	}

	public String msg(String resourceId) {
		return (String) this.currentResourceMap.get(resourceId);
	}

	private static class ResourceMap {
		private String lastmodified;

		private Map resMap;

		public ResourceMap(String lastmodified, Map resMap) {
			this.lastmodified = lastmodified;
			this.resMap = resMap;
		}

		public String getLastmodified() {
			return lastmodified;
		}

		public void setLastmodified(String lastmodified) {
			this.lastmodified = lastmodified;
		}

		public Map getResMap() {
			return resMap;
		}

		public void setResMap(Map resMap) {
			this.resMap = resMap;
		}
	}
	
	/**
	 * substitute character string that need to escape by the regular expression.
	 * 
	 * @param replacement
	 * @return
	 */
	private static String escapeReplacement(String replacement){
		replacement = ESCAPE_REPACEMENT_1.matcher(replacement).replaceAll("\\\\\\\\");
		replacement = ESCAPE_REPACEMENT_2.matcher(replacement).replaceAll("\\\\\\$");
		return replacement;
	}
}
