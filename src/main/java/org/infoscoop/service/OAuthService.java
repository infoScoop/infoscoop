package org.infoscoop.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.dao.OAuthCertificateDAO;
import org.infoscoop.dao.OAuth3LeggedConsumerDAO;
import org.infoscoop.dao.OAuthTokenDAO;
import org.infoscoop.dao.OAuth2LeggedConsumerDAO;
import org.infoscoop.dao.model.OAUTH_CONSUMER_PK;
import org.infoscoop.dao.model.OAuth3LeggedConsumer;
import org.infoscoop.dao.model.OAuthCertificate;
import org.infoscoop.dao.model.OAuth2LeggedConsumer;
import org.infoscoop.util.Crypt;
import org.infoscoop.util.SpringUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OAuthService {
	private static Log log = LogFactory.getLog(OAuthService.class);

	private OAuth3LeggedConsumerDAO oauth3LeggedConsumerDAO;

	private OAuthTokenDAO oauthTokenDAO;

	private OAuthCertificateDAO oauthCertificateDAO;
	
	private OAuth2LeggedConsumerDAO oauth2LeggedConsumerDAO;
	
	public static OAuthService getHandle() {
		return (OAuthService) SpringUtil.getBean("OAuthService");
	}
	
	/**
	 * @param portalAdminsDAO
	 */
	public void setOauth3LeggedConsumerDAO(OAuth3LeggedConsumerDAO oauth3LeggedConsumerDAO) {
		this.oauth3LeggedConsumerDAO = oauth3LeggedConsumerDAO;
	}

	public void setOauthTokenDAO(OAuthTokenDAO oauthTokenDAO){
		this.oauthTokenDAO = oauthTokenDAO;
	}
	
	public void setOauthCertificateDAO(OAuthCertificateDAO oauthCertificateDAO){
		this.oauthCertificateDAO = oauthCertificateDAO;
	}
	
	public void setOauth2LeggedConsumerDAO(OAuth2LeggedConsumerDAO oauth2LeggedConsumerDAO){
		this.oauth2LeggedConsumerDAO = oauth2LeggedConsumerDAO;
	}
	
	private String buildJsonArray(List<OAuth3LeggedConsumer> consumerPropList) throws JSONException{
		JSONArray consumerList = new JSONArray();
		for(OAuth3LeggedConsumer prop: consumerPropList){
			JSONObject obj = new JSONObject();
			obj.put("service_name", prop.getServiceName());
			obj.put("gadget_url", prop.getGadgetUrl());
			obj.put("consumer_key", prop.getConsumerKey());
			obj.put("consumer_secret", prop.getConsumerSecret());
			//obj.put("private_key", prop.getPrivateKey());
			obj.put("signature_method", prop.getSignatureMethod());
			consumerList.put(obj);
		}
		return consumerList.toString();
	}
	public String getOAuthConsumerListJson() throws Exception{
		return buildJsonArray( this.oauth3LeggedConsumerDAO.getConsumers() );
	}


	private String buildTwoLeggedJsonArray(List<OAuth2LeggedConsumer> consumerPropList) throws JSONException{
		JSONArray consumerList = new JSONArray();
		for(OAuth2LeggedConsumer prop: consumerPropList){
			JSONObject obj = new JSONObject();
			obj.put("service_name", prop.getServiceName());
			obj.put("consumer_key", prop.getConsumerKey());
			obj.put("consumer_secret", prop.getConsumerSecret());
			obj.put("signature_method", prop.getSignatureMethod());
			consumerList.put(obj);
		}
		return consumerList.toString();
	}
	
	public String getTwoLeggedOAuthConsumerListJson() throws Exception{
		return buildTwoLeggedJsonArray( this.oauth2LeggedConsumerDAO.all() );
	}
	
	public void saveTwoLeggedOAuthConsumerList(String saveArray) throws Exception{
		this.oauth2LeggedConsumerDAO.deleteAll();
		
		JSONArray consumerJsonList = new JSONArray(saveArray);

		List<OAuth2LeggedConsumer> consumers = new ArrayList<OAuth2LeggedConsumer>();
		for(int i = 0; i < consumerJsonList.length();i++){
			JSONObject obj = consumerJsonList.getJSONObject(i);
			OAuth2LeggedConsumer consumer = new OAuth2LeggedConsumer();
			consumer.setServiceName(obj.getString("serviceName"));
			consumer.setConsumerKey(obj.getString("consumerKey"));
			consumer.setConsumerSecret(obj.getString("consumerSecret"));
			consumer.setSignatureMethod(obj.getString("signatureMethod"));
			consumers.add(consumer);
		}

		this.oauth2LeggedConsumerDAO.saveConsumers(consumers);
	}

	
	public String getGetConsumerListJsonByUrl(String url) throws Exception{
		return buildJsonArray( this.oauth3LeggedConsumerDAO.getConsumersByUrl(url) );
	}
	public void saveOAuthConsumerList(String saveArray) throws Exception{
		this.oauth3LeggedConsumerDAO.deleteAll();
		
		JSONArray consumerJsonList = new JSONArray(saveArray);

		List<OAuth3LeggedConsumer> consumers = new ArrayList<OAuth3LeggedConsumer>();
		for(int i = 0; i < consumerJsonList.length();i++){
			JSONObject obj = consumerJsonList.getJSONObject(i);
			String gadgetUrl = obj.getString("gadgetUrl");
			OAuth3LeggedConsumer consumer = new OAuth3LeggedConsumer();
			consumer.setServiceName(obj.getString("serviceName"));
			consumer.setGadgetUrl(gadgetUrl);
			consumer.setConsumerKey(obj.getString("consumerKey"));
			consumer.setConsumerSecret(obj.getString("consumerSecret"));
			consumer.setSignatureMethod(obj.getString("signatureMethod"));
			consumer.setIsUpload(0);
			consumers.add(consumer);
		}

		this.oauth3LeggedConsumerDAO.saveConsumers(consumers);
	}
	
	public void saveOAuthToken(String uid, String gadgetUrl,
			String serviceName, String requestToken, String accessToken,
			String tokenSecret) {
		this.oauthTokenDAO.saveAccessToken(uid, gadgetUrl, serviceName,
				requestToken, accessToken, tokenSecret);
	}
	
	public void deleteOAuthToken(String uid, String gadgetUrl, String serviceName){
		this.oauthTokenDAO.deleteOAuthToken(this.oauthTokenDAO.getAccessToken(uid, gadgetUrl, serviceName));
	}
	public String getContainerCertificateJson()throws Exception{
		JSONObject obj = new JSONObject();
		OAuthCertificate cert = this.oauthCertificateDAO.get();
		obj.put("consumerKey", (cert != null ? cert.getConsumerKey() : ""));
		obj.put("privateKey", (cert != null ? new String(cert.getPrivateKey()) : ""));
		obj.put("certificate", (cert != null ? new String(cert.getCertificate()) : ""));
		return obj.toString();
	}
	
	public void saveContainerCertificate(String consumerKey, String privateKey, String certificate){
		OAuthCertificate cert = new OAuthCertificate(consumerKey);
		cert.setPrivateKey(privateKey);
		cert.setCertificate(certificate);
		this.oauthCertificateDAO.save(cert);
	}
		
}
