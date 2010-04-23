package org.infoscoop.service;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.collections.MultiHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.dao.AuthCredentialDAO;
import org.infoscoop.dao.model.AuthCredential;
import org.infoscoop.request.ProxyAuthenticationException;
import org.infoscoop.request.ProxyRequest;
import org.infoscoop.request.proxy.Proxy;
import org.infoscoop.request.proxy.ProxyConfig;
import org.infoscoop.util.RSAKeyManager;
import org.infoscoop.util.SpringUtil;

public class AuthCredentialService {

	private static Log log = LogFactory.getLog(AuthCredentialService.class);

	private AuthCredentialDAO authCredentialDAO;

	/**
	 * Constructor
	 */
	public AuthCredentialService() {
	}

	/**
	 * @return
	 */
	public static AuthCredentialService getHandle() {
		return (AuthCredentialService) SpringUtil.getBean("AuthCredentialService");
	}

	/**
	 * @param portalAdminsDAO
	 */
	public void setAuthCredentialDAO(AuthCredentialDAO authCredentialDAO) {
		this.authCredentialDAO = authCredentialDAO;
	}

	public AuthCredential getCredential(
			String uid,
			String id) throws ProxyAuthenticationException{
		AuthCredential c = this.authCredentialDAO.get(new Long(id));
		if(c == null || c.getUid().equals(uid)){
			return c;
		}else{
			throw new ProxyAuthenticationException("invalid access from " + uid);
		}
	}
	
	public AuthCredential getLoginCredential(
			String uid) throws ProxyAuthenticationException{
		return authCredentialDAO.select(uid, AuthCredential.LOGIN_AUTH_CREDENTIAL);
	}

	public void addLoginCredential(String uid, String authType, String authPasswd, String authDomain) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalStateException, IllegalBlockSizeException, BadPaddingException{
		String authUid = uid;
		if(authType != null && "ntlm".equalsIgnoreCase(authType))
			authUid = authUid.toLowerCase();

		authPasswd = RSAKeyManager.getInstance().encrypt(authPasswd);

		AuthCredential c = authCredentialDAO.select(uid, AuthCredential.LOGIN_AUTH_CREDENTIAL);

		if(c != null && !c.getAuthType().equals(authType)){
			c.setSysNum(AuthCredential.COMMON_AUTH_CREDENTIAL);
			authCredentialDAO.update(c);
			c = null;
		}

		if(c == null){
			c = new AuthCredential();
			c.setUid(uid);
			c.setSysNum(AuthCredential.LOGIN_AUTH_CREDENTIAL);
			c.setAuthType(authType);
			c.setAuthUid(authUid);
			c.setAuthPasswd(authPasswd);
			c.setAuthDomain(authDomain);

			authCredentialDAO.add(c);
		}else{
			c.setUid(uid);
			c.setSysNum(AuthCredential.LOGIN_AUTH_CREDENTIAL);
			c.setAuthType(authType);
			c.setAuthUid(authUid);
			c.setAuthPasswd(authPasswd);
			c.setAuthDomain(authDomain);

			authCredentialDAO.update(c);
		}
	}

	public String addCredential(
			String uid,
			String authType,
			String authUid,
			String authPasswd,
			String authDomain,
			String targetUrl,
			MultiHashMap headerMap){
		if(authType != null && "ntlm".equalsIgnoreCase(authType))
			authUid = authUid.toLowerCase();


		ProxyRequest proxy = new ProxyRequest(targetUrl, "NoOperation");
		proxy.setPortalUid(uid);
		Set<String> keys = headerMap.keySet();
		for (String key : keys) {
			Collection<String> headers = (Collection<String>) headerMap
					.get(key);
			for (String header : headers) {
				proxy.putRequestHeader(key, header);
			}
		}
		proxy.putRequestHeader("authType", authType);
		proxy.putRequestHeader("authUserid", authUid);
		proxy.putRequestHeader("authpassword", authPasswd);
		int status = 0;
		try {
			if(authType != null && authType.indexOf("post") == 0){
				status = proxy.executePost();
			}else{
				status = proxy.executeGet();
			}
		} catch (Exception e) {
			log.warn(e.getMessage());
		}
		if(status == 401){
			return null;
		} else if (status < 200 || status >= 300) {
			throw new RuntimeException("Status " + status + " returned from "
					+ targetUrl + ".");
		}

		AuthCredential c = new AuthCredential();
		c.setUid(uid);
		c.setAuthType(authType);
		c.setAuthUid(authUid);
		c.setAuthPasswd(authPasswd);
		c.setAuthDomain(authDomain);

		return authCredentialDAO.add(c).toString();
	}

	public String detectCredential(
			String uid,
			String authType,
			String authUrl) throws Exception{
		List credentialList = authCredentialDAO.select(uid);
		if(log.isInfoEnabled())
			log.info("Try valid authentication information for " + authType + " and " + authUrl + ".");


		ProxyConfig proxyConfig = ProxyConfig.getInstance();
		Proxy proxy =  proxyConfig.resolve( authUrl );
		if(!proxy.isIntranet())
			return null;

		for(Iterator it = credentialList.iterator(); it.hasNext();){
			AuthCredential c = (AuthCredential)it.next();
			if(c.getSysNum().equals(AuthCredential.COMMON_AUTH_CREDENTIAL) ||
					authType != null && authType.length() > 0
					&& !authType.equalsIgnoreCase(c.getAuthType()))
				continue;

			String authCredentialId = c.getId().toString();
			ProxyRequest proxyRequest = new ProxyRequest(authUrl, "NoOperation");
			proxyRequest.setPortalUid(uid);
			proxyRequest.putRequestHeader("authCredentialId", authCredentialId);
			int status;
			if(authType != null && authType.indexOf("post") == 0){
				status = proxyRequest.executePost();
			}else{
				status = proxyRequest.executeGet();
			}
			if(status == 200){
				return authCredentialId;
			}
		}
		return null;
	}

	public void removeCredential(String uid, String credentialId) throws ProxyAuthenticationException{
		AuthCredential c = authCredentialDAO.get(new Long(credentialId));
		if(c != null && c.getSysNum().equals(AuthCredential.COMMON_AUTH_CREDENTIAL)){
			if(!c.getUid().equals(uid)){
				throw new ProxyAuthenticationException("invalid access from " + uid);
			}
			authCredentialDAO.delete(c);
			if(log.isInfoEnabled())
				log.info("Success delete auth credential setting of uid=[" + uid + "]");
		}
	}

	public void removeCredential(AuthCredential c) {
		authCredentialDAO.delete(c);
	}

	/**
	 * @param uid
	 * @param credentialId
	 * @param password
	 * @param urlList A list of charcter string of the URL.
	 * @return
	 * @throws AuthenticationException
	 */
	public Set resetPassword(String uid, String credentialId, String password, String[] urlList) throws ProxyAuthenticationException{
		AuthCredential c = authCredentialDAO.get(new Long(credentialId));
		if(!c.getUid().equals(uid)){
			throw new ProxyAuthenticationException("invalid access from " + uid);
		}
		Set errorUrls = new HashSet();
		for(int i = 0; i < urlList.length; i++){
			ProxyRequest proxy = new ProxyRequest(urlList[i], "NoOperation");
			proxy.setPortalUid(uid);
			proxy.putRequestHeader("authType", c.getAuthType());
			proxy.putRequestHeader("authUserid", c.getAuthUid());
			proxy.putRequestHeader("authpassword", password);
			try {
				String authType = c.getAuthType();
				int status;
				if(authType != null && authType.indexOf("post") == 0){
					status = proxy.executePost();
				}else{
					status = proxy.executeGet();
				}
				if(status != 200){
					errorUrls.add(urlList[i]);
				}
			} catch (Exception e) {
				errorUrls.add(urlList[i]);
			}
		}
		if(errorUrls.isEmpty()){
			c.setAuthPasswd(password);
			authCredentialDAO.update(c);
			if(log.isInfoEnabled())
				log.info("Success reset password of uid=[" + uid + "]");
		}
		return errorUrls;
	}

	public void forceResetPassword(String uid, String credentialId,
			String authPasswd) throws ProxyAuthenticationException {
		AuthCredential c = authCredentialDAO.get(new Long(credentialId));
		if(!c.getUid().equals(uid)){
			throw new ProxyAuthenticationException("invalid access from " + uid);
		}
		c.setAuthPasswd(authPasswd);
		authCredentialDAO.update(c);
		if(log.isInfoEnabled())
			log.info("Success reset password of uid=[" + uid + "]");
	}

}
