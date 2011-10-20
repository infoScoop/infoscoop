package org.infoscoop.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.dao.OAuthCertificateDAO;
import org.infoscoop.dao.OAuthConsumerDAO;
import org.infoscoop.dao.OAuthGadgetUrlDAO;
import org.infoscoop.dao.OAuthTokenDAO;
import org.infoscoop.dao.model.OAuthCertificate;
import org.infoscoop.dao.model.OAuthConsumerProp;
import org.infoscoop.dao.model.OAuthGadgetUrl;
import org.infoscoop.dao.model.OAuthToken;
import org.infoscoop.util.SpringUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.util.UUID;

public class OAuthService {
	private static Log log = LogFactory.getLog(OAuthService.class);

	private OAuthConsumerDAO oauthConsumerDAO;
	private OAuthGadgetUrlDAO oauthGadgetUrlDAO;
	private OAuthTokenDAO oauthTokenDAO;
	private OAuthCertificateDAO oauthCertificateDAO;

	public static OAuthService getHandle() {
		return (OAuthService) SpringUtil.getBean("OAuthService");
	}
	
	/**
	 * @param portalAdminsDAO
	 */
	public void setOauthConsumerDAO(OAuthConsumerDAO oauthConsumerDAO) {
		this.oauthConsumerDAO = oauthConsumerDAO;
	}

	public void setOauthGadgetUrlDAO(OAuthGadgetUrlDAO oauthGadgetUrlDAO){
		this.oauthGadgetUrlDAO = oauthGadgetUrlDAO;
	}

	public void setOauthTokenDAO(OAuthTokenDAO oauthTokenDAO){
		this.oauthTokenDAO = oauthTokenDAO;
	}
	
	public void setOauthCertificateDAO(OAuthCertificateDAO oauthCertificateDAO){
		this.oauthCertificateDAO = oauthCertificateDAO;
	}
	
	private String buildJsonArray(List<OAuthConsumerProp> consumerPropList) throws JSONException{
		JSONArray cunsumerList = new JSONArray();
		ArrayList<String> idList = new ArrayList<String>();
		for(OAuthConsumerProp prop: consumerPropList){
			String id = prop.getId();
			if(!idList.contains(id)){
				JSONObject obj = new JSONObject();
				obj.put("id", id);
				obj.put("service_name", prop.getServiceName());
				JSONArray gadgetUrlList = new JSONArray();
				for(Iterator<OAuthGadgetUrl> i = prop.getOAuthGadgetUrl().iterator(); i.hasNext();){
					OAuthGadgetUrl url = (OAuthGadgetUrl)i.next();
					gadgetUrlList.put(url.getGadgetUrl());
				}
				obj.put("gadget_url", gadgetUrlList);
				obj.put("consumer_key", prop.getConsumerKey());
				obj.put("consumer_secret", prop.getConsumerSecret());
				obj.put("signature_method", prop.getSignatureMethod());
				obj.put("description", prop.getDescription());
				cunsumerList.put(obj);
				idList.add(id);
			}
		}
		return cunsumerList.toString();
	}
	public String getOAuthConsumerListJson() throws Exception{
		return buildJsonArray( this.oauthConsumerDAO.getConsumersJoinGadgetUrl() );
	}

	public void saveOAuthConsumerList(String saveArray) throws Exception{
		JSONArray consumerJsonList = new JSONArray(saveArray);

		List<OAuthConsumerProp> consumers = new ArrayList<OAuthConsumerProp>();
		List<String> idList = new ArrayList<String>();
		for(int i = 0; i < consumerJsonList.length();i++){
			Set<OAuthGadgetUrl> gadgetUrlSet = new TreeSet<OAuthGadgetUrl>();
			JSONObject obj = consumerJsonList.getJSONObject(i);
			OAuthConsumerProp consumer = new OAuthConsumerProp();
			String id = obj.getString("id");
			if(id.length()==0){
				id = new UUID().toString();
			}
			idList.add(id);
			consumer.setId(id);
			consumer.setServiceName(obj.getString("serviceName"));
			consumer.setConsumerKey(obj.getString("consumerKey"));
			consumer.setConsumerSecret(obj.getString("consumerSecret"));
			consumer.setSignatureMethod(obj.getString("signatureMethod"));
			consumer.setDescription(obj.getString("description"));
			
			JSONArray gadgetUrlArr = obj.getJSONArray("gadgetUrl");
			for(int j=0;j<gadgetUrlArr.length();j++){
				OAuthGadgetUrl gadgetUrl = new OAuthGadgetUrl();
				gadgetUrl.setFkOauthId(id);
				gadgetUrl.setGadgetUrl(gadgetUrlArr.getString(j));
				gadgetUrlSet.add(gadgetUrl);
			}
			
			consumer.setOAuthGadgetUrl(gadgetUrlSet);
			consumers.add(consumer);
		}
		if(idList.isEmpty())
			idList.add("");
		this.oauthConsumerDAO.deleteUpdate(idList);
		this.oauthConsumerDAO.saveConsumers(consumers);
	}

	public void saveOAuthGadgetUrl(String fkOauthId, String gadgetUrl) {
		this.oauthGadgetUrlDAO.saveGadgetUrl(fkOauthId, gadgetUrl);
	}
	
	public void deleteOAuthGadgetUrl(String fkOAuthId, String serviceName){
		this.oauthGadgetUrlDAO.deleteGadgetUrl(this.oauthGadgetUrlDAO.getGadgetUrl(fkOAuthId, serviceName));
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

	public void deleteOAuthTokens(String uid, String serviceName){
		List<OAuthToken> tokens = this.oauthTokenDAO.getAccessTokens(uid, serviceName);
		if(tokens.size() > 0)
			this.oauthTokenDAO.deleteOAuthToken(tokens);
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
