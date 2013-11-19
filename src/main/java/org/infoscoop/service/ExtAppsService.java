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
import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.api.dao.OAuth2ProviderClientDetailDAO;
import org.infoscoop.api.dao.model.OAuth2ProviderClientDetail;
import org.infoscoop.util.SpringUtil;
import org.json.JSONArray;
import org.json.JSONObject;

public class ExtAppsService {
	private OAuth2ProviderClientDetailDAO oauth2ProviderClientDetailDAO;
	
	private static Log log = LogFactory.getLog(ExtAppsService.class);
	
	private static final String GRANTTYPE_WEB = "web";
	private static final String GRANTTYPE_NATIVE = "native";
	private static final String GRANTTYPE_CODE = "authorization_code";
	private static final String GRANTTYPE_REFRESH = "refresh_token";
	private static final String GRANTTYPE_CLIENTCREDENTIALS = "client_credentials";
	private static final String GRANTTYPE_PASSWORD = "password";
	private static final String GRANTTYPE_IMPLICIT = "implicit";	

	
	public ExtAppsService(){}

	public static ExtAppsService getHandle(){
		return (ExtAppsService)SpringUtil.getBean("ExtAppsService");
	}
	
	public OAuth2ProviderClientDetailDAO getOauth2ProviderClientDetailDAO() {
		return oauth2ProviderClientDetailDAO;
	}

	public void setOauth2ProviderClientDetailDAO(
			OAuth2ProviderClientDetailDAO oauth2ProviderClientDetailDAO) {
		this.oauth2ProviderClientDetailDAO = oauth2ProviderClientDetailDAO;
	}
	
	public String getExtAppsList() throws Exception {
		ArrayList<OAuth2ProviderClientDetail> clientDetailList = (ArrayList<OAuth2ProviderClientDetail>)oauth2ProviderClientDetailDAO.getClientDetails();
		
		JSONArray arr = new JSONArray();
		for(OAuth2ProviderClientDetail clientDetail : clientDetailList){
			JSONObject obj = new JSONObject();
			obj.put("appName", clientDetail.getTitle());
			obj.put("clientId", clientDetail.getId());
			obj.put("clientSecret", clientDetail.getSecret());
			obj.put("redirectUrl", clientDetail.getRedirectUrl());
			obj.put("grantType", encodeGrantTypes(clientDetail.getGrantTypes()));
			obj.put("explain", clientDetail.getAdditionalInformation());
			arr.put(obj);
		}
		return arr.toString();
	}

	private String decodeGrantTypes(String grantTypes){
		String type = "";
		if(GRANTTYPE_WEB.equals(grantTypes)){
			type = GRANTTYPE_CODE+","+GRANTTYPE_REFRESH;
		}
		if(GRANTTYPE_NATIVE.equals(grantTypes)){
			type = GRANTTYPE_CLIENTCREDENTIALS;
		}
		
		return type;
	}
	
	private String encodeGrantTypes(String grantTypes){
		String[] typeList = grantTypes.split(",");
		String type = GRANTTYPE_WEB;
		if(Arrays.asList(typeList).contains(GRANTTYPE_CLIENTCREDENTIALS))
			type = GRANTTYPE_NATIVE;
		return type;
	}
	
//	public JSONObject getExtApps() throws Exception{
//		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
//		builderFactory.setValidating(false);
//
//		DocumentBuilder builder = builderFactory.newDocumentBuilder();
//		builder.setEntityResolver(NoOpEntityResolver.getInstance());
//		
//		Gadget gadget = gadgetDAO.select(type);
//		Document gadgetDoc = builder.parse(new ByteArrayInputStream(gadget.getData()));
//		Element gadgetEl = gadgetDoc.getDocumentElement();
//		JSONObject confJson = WidgetConfUtil.gadget2JSONObject( gadgetEl,null );
//		JSONObject obj = new JSONObject();
//		obj.put("hogehoge","hugahuga");
//		return obj;
//	}

}
