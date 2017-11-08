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

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.admin.exception.I18NImportException;
import org.infoscoop.admin.web.I18NImport;
import org.infoscoop.context.UserContext;
import org.infoscoop.dao.I18NDAO;
import org.infoscoop.dao.model.I18n;
import org.infoscoop.dao.model.I18nlocale;
import org.infoscoop.util.SpringUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import au.com.bytecode.opencsv.CSVReader;

public class I18NService {
	private static Log log = LogFactory.getLog(I18NService.class);

	private I18NDAO i18NDAO;

	public static I18NService getHandle() {
		return (I18NService) SpringUtil.getBean("I18NService");
		// return m_service;
	}

	public void setI18NDAO(I18NDAO i18NDAO) {
		this.i18NDAO = i18NDAO;
	}

	public String getLocales() throws Exception {
		String squareid = UserContext.instance().getUserInfo().getCurrentSquareId();
		List locales = i18NDAO.selectLocales(squareid);
		JSONObject localesJson = new JSONObject();
		JSONArray localeArray = null;
		String type = null;
		for (Iterator it = locales.iterator(); it.hasNext();) {
			I18nlocale locale = (I18nlocale) it.next();
			if (type == null || !type.equals(locale.getType())) {
				type = locale.getType();
				localeArray = new JSONArray();
				localesJson.put(type, localeArray);
			}
			JSONObject obj = new JSONObject();
			obj.put("country", locale.getCountry());
			obj.put("lang", locale.getLang());
			localeArray.put(obj);
		}
		return localesJson.toString();
	}

	public String getJson(String type) throws Exception {
		String squareid = UserContext.instance().getUserInfo().getCurrentSquareId();
		try {
			List locales = i18NDAO.selectLocales(type);
			JSONArray localeArray = new JSONArray();
			for (Iterator it = locales.iterator(); it.hasNext();) {
				I18nlocale locale = (I18nlocale) it.next();
				JSONObject obj = new JSONObject();
				obj.put("country", locale.getCountry());
				obj.put("lang", locale.getLang());
				localeArray.put(obj);
			}

			List msgs = i18NDAO.selectByType(type, squareid);
			JSONArray jsonArray = new JSONArray();
			JSONObject preObj = null;
			for (Iterator it = msgs.iterator(); it.hasNext();) {
				I18n msg = (I18n) it.next();
				JSONObject obj = null;
				JSONArray msgarray = null;
				if (preObj != null) {
					String preType = preObj.getString("type");
					String preId = preObj.getString("id");
					if (preType.equals(msg.getType())
							&& preId.equals(msg.getId())) {
						obj = preObj;
						msgarray = obj.getJSONArray("msgs");
					}
				}
				if (obj == null) {
					obj = new JSONObject();
					obj.put("type", msg.getType());
					obj.put("id", msg.getId());
					msgarray = new JSONArray();
					obj.put("msgs", msgarray);
					preObj = obj;
					jsonArray.put(obj);
				}
				JSONObject msgObj = new JSONObject();
				msgObj.put("country", msg.getCountry());
				msgObj.put("lang", msg.getLang());
				msgObj.put("message", msg.getMessage());
				msgarray.put(msgObj);
			}
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("locales", localeArray);
			jsonObj.put("msgs", jsonArray);
			return jsonObj.toString();
		} catch (Exception e) {
			log.error("Unexpected error occurred.", e);
			throw e;
		}
	}


	public Map getResourceMap(String type, String country, String lang, String squareId) {
		Map resMap = new HashMap();
		resMap.putAll(findI18nAsMap(type, "ALL", "ALL", squareId));

		if (!lang.equals("ALL"))
			resMap.putAll(findI18nAsMap(type, "ALL", lang, squareId));

		if (!country.equals("ALL"))
			resMap.putAll(findI18nAsMap(type, country, lang, squareId));

		return resMap;
	}

	private Map findI18nAsMap(String type, String country, String lang, String squareid) {
		if(StringUtils.isEmpty(squareid)) squareid = SquareService.SQUARE_ID_DEFAULT;

		List resourceMapI18n = i18NDAO.findI18n(type, country, lang, squareid);
		Map resourceMap = new HashMap();
		for (int i = 0; i < resourceMapI18n.size(); i++) {
			I18n i18n = (I18n) resourceMapI18n.get(i);

			resourceMap.put(i18n.getId().getId(), i18n.getMessage());
		}

		return resourceMap;
	}

	public void insertI18nLocale(String type, String country, String lang) {
		String squareid = UserContext.instance().getUserInfo().getCurrentSquareId();
		i18NDAO.insertLocale(type, country, lang, squareid);
	}

	public void removeI18nLocale(String type, String country, String lang) {
		String squareid = UserContext.instance().getUserInfo().getCurrentSquareId();
		i18NDAO.deleteI18NLocale(type, country, lang, squareid);
		
		// fix #56 delete the related i18n date.
		i18NDAO.deleteI18NByLocale(type, country, lang, squareid);
	}

	/**
	 * When we import a CSV file of globalization.
	 * 
	 * @param type
	 * @param country
	 * @param lang
	 */
	public void replaceI18nByLocale(String type, String country, String lang,
			FileItem csvFile, String mode, Map countMap, List errorList, List defaultIdList) throws Exception {
		CSVReader csvReader = null;
		InputStreamReader is;
		String squareid = UserContext.instance().getUserInfo().getCurrentSquareId();
		boolean isSuccess = true;

		int insertCount = 0;
		int updateCount = 0;

		// In the case of all substitution, we delete all once.
		if (mode.equalsIgnoreCase("replace")) {
			this.i18NDAO.deleteI18NByLocale(type, country, lang, squareid);
		}

		try {
			is = new InputStreamReader(csvFile.getInputStream(), "Windows-31J");

			csvReader = new CSVReader(is);

			String[] nextLine = null;
			int lineNumber = 0;
			I18NImport ir;
			while ((nextLine = csvReader.readNext()) != null) {
				ir = new I18NImport(++lineNumber, nextLine, type, country,
						lang, defaultIdList);
				if (!ir.isError()) {
					// If it isn't error record, we register it.
					if (ir.execInsertUpdate(this.i18NDAO)) {
						updateCount++;
					} else {
						insertCount++;
					}
					defaultIdList.remove(ir.getId());
				} else {
					errorList.add(ir);
					isSuccess = false;
				}
			}
		} finally {
			if (csvReader != null)
				csvReader.close();
		}

		if (!isSuccess) {
			// rollback
			throw new I18NImportException(
					"Because illegal data existed, the rollback is done.");
		}
		
		if(mode.equalsIgnoreCase("replace") && "ALL".equals(country) && "ALL".equals(lang)){
			// fix #796 When there is the default ID that lost after substitution, we delete the other locale in the ID.
			String removeID;
			for(Iterator ite=defaultIdList.iterator();ite.hasNext();){
				 removeID = (String)ite.next();
				 i18NDAO.deleteI18NByIDWithoutDefault(type, removeID, squareid);
			}
		}
		
		this.i18NDAO.updateLastmodified(type, squareid);
		countMap.put("insertCount", new Integer(insertCount));
		countMap.put("updateCount", new Integer(updateCount));
	}

	/**
	 * Whether there is an appointed ID in "ALL_ALL" of the appointed TYPE or not.
	 * 
	 * @param type
	 * @return
	 */
	/*
	public boolean isDefaultExists(String type, String id) {
		return i18NDAO.selectByPK(new I18NPK("ALL", id, "ALL", type)) != null;
	}
	*/
}
