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
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.api.dao.OAuth2ProviderAccessTokenDAO;
import org.infoscoop.api.dao.OAuth2ProviderClientDetailDAO;
import org.infoscoop.api.dao.OAuth2ProviderRefreshTokenDAO;
import org.infoscoop.api.dao.model.OAuth2ProviderAccessToken;
import org.infoscoop.api.dao.model.OAuth2ProviderClientDetail;
import org.infoscoop.api.oauth2.provider.ISClientDetailsService;
import org.infoscoop.context.UserContext;
import org.infoscoop.util.SpringUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.util.UUID;

public class ExtAppsService {
	private OAuth2ProviderClientDetailDAO oauth2ProviderClientDetailDAO;
	private OAuth2ProviderAccessTokenDAO oauth2ProviderAccessTokenDAO;
	private OAuth2ProviderRefreshTokenDAO oauth2ProviderRefreshTokenDAO;
	
	private static Log log = LogFactory.getLog(ExtAppsService.class);
	
	private static final String GRANTTYPE_WEB = "web";
	private static final String GRANTTYPE_NATIVE = "native";
	private static final String GRANTTYPE_CODE = "authorization_code";
	private static final String GRANTTYPE_REFRESH = "refresh_token";
	private static final String GRANTTYPE_CLIENTCREDENTIALS = "client_credentials";
	private static final String GRANTTYPE_PASSWORD = "password";
	private static final String GRANTTYPE_IMPLICIT = "implicit";

	private static final String SCOPE_USERPROFILE = "USERPROFILE";
	
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
	
	public OAuth2ProviderAccessTokenDAO getOauth2ProviderAccessTokenDAO() {
		return oauth2ProviderAccessTokenDAO;
	}

	public void setOauth2ProviderAccessTokenDAO(
			OAuth2ProviderAccessTokenDAO oauth2ProviderAccessTokenDAO) {
		this.oauth2ProviderAccessTokenDAO = oauth2ProviderAccessTokenDAO;
	}

	public OAuth2ProviderRefreshTokenDAO getOauth2ProviderRefreshTokenDAO() {
		return oauth2ProviderRefreshTokenDAO;
	}

	public void setOauth2ProviderRefreshTokenDAO(
			OAuth2ProviderRefreshTokenDAO oauth2ProviderRefreshTokenDAO) {
		this.oauth2ProviderRefreshTokenDAO = oauth2ProviderRefreshTokenDAO;
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
			obj.put("deleteFlg", clientDetail.getDeleteFlg());
			arr.put(obj);
		}
		return arr.toString();
	}

	public String saveExtApps(String parameters) throws Exception {
		JSONObject obj = new JSONObject(parameters);
		
		String title = obj.getString("appName");
		String clientId = obj.getString("clientId");
		String secret = obj.getString("clientSecret");
		String grantType = decodeGrantTypes(obj.getString("grantType"));
		String redirectUrl = obj.getString("redirectUrl");
		String additionalInformation = obj.getString("explain");
		String squareid = UserContext.instance().getUserInfo().getCurrentSquareId();

		if(obj.getString("clientId").isEmpty()){
			// new
			clientId = createUUID();
			secret = createUUID();
		}

		oauth2ProviderClientDetailDAO.saveClientDetail(clientId, title, ISClientDetailsService.getResouceId(), secret, SCOPE_USERPROFILE, grantType, redirectUrl, null, null, null, null, additionalInformation, squareid);
		
		// response JSON
		JSONObject obj2 = new JSONObject();
		obj2.put("appName", title);
		obj2.put("clientId", clientId);
		obj2.put("clientSecret", secret);
		obj2.put("redirectUrl", redirectUrl);
		obj2.put("grantType", obj.getString("grantType"));
		obj2.put("explain", additionalInformation);
		obj2.put("deleteFlg", true);

		JSONObject resultObj = new JSONObject();
		resultObj.put("list", new JSONArray(getExtAppsList()));
		resultObj.put("self", obj2);
		
		return resultObj.toString();
	}
	
	public void deleteExtApps(String parameters) throws Exception{
		JSONObject obj = new JSONObject(parameters);
		String clientId = obj.getString("clientId");
		String squareid = UserContext.instance().getUserInfo().getCurrentSquareId();
		
		// delete client detail
		oauth2ProviderClientDetailDAO.deleteClientDetail(clientId, squareid);

		//delete access token and refresh token
		deleteTokens(clientId);
	}
	
	public String resetClientSecret(String parameters) throws Exception {
		JSONObject obj = new JSONObject(parameters);
		String clientId = obj.getString("clientId");
		String clientSecret = createUUID();
		String squareid = UserContext.instance().getUserInfo().getCurrentSquareId();

		// rewrite secret
		OAuth2ProviderClientDetail clientDetail = oauth2ProviderClientDetailDAO.getClientDetailById(clientId, squareid);
		clientDetail.setSecret(clientSecret);
		oauth2ProviderClientDetailDAO.saveClientDetail(clientDetail);

		// delete tokens
		deleteTokens(clientId);
		
		JSONObject resultObj = new JSONObject();
		resultObj.put("clientSecret", clientSecret);
		return resultObj.toString();
	}
	
	private void deleteTokens(String clientId) {
		String squareid = UserContext.instance().getUserInfo().getCurrentSquareId();
		List<OAuth2ProviderAccessToken> tokenList = oauth2ProviderAccessTokenDAO.getAccessTokenByClientId(clientId, squareid);
		for(OAuth2ProviderAccessToken token : tokenList){
			oauth2ProviderRefreshTokenDAO.deleteOAuth2ProviderRefreshToken(token.getRefreshToken(), squareid);
			oauth2ProviderAccessTokenDAO.deleteOAuth2ProviderAccessToken(token);
		}		
	}
	
	private String createUUID(){
		String uuid = new UUID().toString();
		return uuid.replaceAll("-", "");
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
}