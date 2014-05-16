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

package org.infoscoop.api.oauth2.provider;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.api.dao.OAuth2ProviderAccessTokenDAO;
import org.infoscoop.api.dao.OAuth2ProviderRefreshTokenDAO;
import org.infoscoop.api.dao.model.OAuth2ProviderAccessToken;
import org.infoscoop.api.dao.model.OAuth2ProviderRefreshToken;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.common.util.SerializationUtils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.TokenStore;

public class ISTokenStore implements TokenStore {
	private static Log log = LogFactory.getLog(ISTokenStore.class);

	private AuthenticationKeyGenerator authenticationKeyGenerator = new DefaultAuthenticationKeyGenerator();
	
	@Override
	public OAuth2Authentication readAuthentication(OAuth2AccessToken token) {
		return readAuthentication(token.getValue());
	}

	@Override
	public OAuth2Authentication readAuthentication(String tokenValue) {
//		private String selectAccessTokenAuthenticationSql = "select token_id, authentication from oauth_access_token where token_id = ?";
		OAuth2Authentication authentication = null;

		try {
			OAuth2ProviderAccessTokenDAO providerDao = OAuth2ProviderAccessTokenDAO.newInstance();
			OAuth2ProviderAccessToken accessToken = providerDao.getAccessTokenById(extractTokenKey(tokenValue));
			if(accessToken!=null)
				authentication = deserializeAuthentication(accessToken.getAuthentication());
		} catch (IllegalArgumentException e) {
			log.warn("Failed to deserialize authentication for " + tokenValue);
			removeAccessToken(tokenValue);
		}

		return authentication;
	}

	@Override
	public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
		String refreshToken = null;
		if (token.getRefreshToken() != null) {
			refreshToken = token.getRefreshToken().getValue();
		}
		
		OAuth2ProviderAccessTokenDAO providerDao = OAuth2ProviderAccessTokenDAO.newInstance();
		providerDao.saveAccessToken(extractTokenKey(token.getValue()),
									serializeAccessToken(token),
									authenticationKeyGenerator.extractKey(authentication),
									authentication.isClientOnly() ? null : authentication.getName(),
									authentication.getAuthorizationRequest().getClientId(),
									serializeAuthentication(authentication),
									extractTokenKey(refreshToken));
	}

	@Override
	public OAuth2AccessToken readAccessToken(String tokenValue) {
		OAuth2AccessToken accessToken = null;

		try {
//			private String selectAccessTokenSql = "select token_id, token from oauth_access_token where token_id = ?";;
			OAuth2ProviderAccessTokenDAO providerDao = OAuth2ProviderAccessTokenDAO.newInstance();
			OAuth2ProviderAccessToken oat = providerDao.getAccessTokenById(extractTokenKey(tokenValue));
			if(oat!=null)
				accessToken = deserializeAccessToken(oat.getToken());
		} catch (IllegalArgumentException e) {
			log.warn("Failed to deserialize access token for " + tokenValue);
			removeAccessToken(tokenValue);
		}

		return accessToken;
	}

	@Override
	public void removeAccessToken(OAuth2AccessToken token) {
		removeAccessToken(token.getValue());
	}
	
	public void removeAccessToken(String tokenValue) {
//		private String deleteAccessTokenSql = "delete from oauth_access_token where token_id = ?";
		OAuth2ProviderAccessTokenDAO providerDao = OAuth2ProviderAccessTokenDAO.newInstance();
		providerDao.deleteOAuth2ProviderAccessToken(extractTokenKey(tokenValue));
	}

	@Override
	public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken) {
		removeAccessTokenUsingRefreshToken(refreshToken.getValue());
	}
	
	public void removeAccessTokenUsingRefreshToken(String refreshToken) {
//		private String deleteAccessTokenFromRefreshTokenSql = "delete from oauth_access_token where refresh_token = ?";
		OAuth2ProviderAccessTokenDAO providerDao = OAuth2ProviderAccessTokenDAO.newInstance();
		providerDao.deleteOAuth2ProviderAccessTokenByRefreshToken(extractTokenKey(refreshToken));
	}
	
	@Override
	public OAuth2RefreshToken readRefreshToken(String tokenValue) {
		OAuth2RefreshToken refreshToken = null;

		try {
//			private String selectRefreshTokenSql = "select token_id, token from oauth_refresh_token where token_id = ?";
			OAuth2ProviderRefreshTokenDAO providerDao = OAuth2ProviderRefreshTokenDAO.newInstance();
			OAuth2ProviderRefreshToken ort = providerDao.getRefreshTokenById(extractTokenKey(tokenValue));
			if(ort!=null)
				refreshToken = deserializeRefreshToken(ort.getToken());
		} catch (IllegalArgumentException e) {
			log.warn("Failed to deserialize refresh token for token " + tokenValue);
			removeRefreshToken(tokenValue);
		}

		return refreshToken;
	}

	@Override
	public OAuth2Authentication readAuthenticationForRefreshToken(OAuth2RefreshToken token) {
		return readAuthenticationForRefreshToken(token.getValue());
	}
	
	public OAuth2Authentication readAuthenticationForRefreshToken(String tokenValue) {
		OAuth2Authentication authentication = null;

		try {
//			private String selectRefreshTokenAuthenticationSql = "select token_id, authentication from oauth_refresh_token where token_id = ?";
			OAuth2ProviderRefreshTokenDAO providerDao = OAuth2ProviderRefreshTokenDAO.newInstance();
			OAuth2ProviderRefreshToken ort = providerDao.getRefreshTokenById(extractTokenKey(tokenValue));
			if(ort!=null)
				authentication = deserializeAuthentication(ort.getAuthentication());
		} catch (IllegalArgumentException e) {
			log.warn("Failed to deserialize access token for " + tokenValue);
			removeRefreshToken(tokenValue);
		}
	
		return authentication;
	}
	
	@Override
	public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
//		private String insertRefreshTokenSql = "insert into oauth_refresh_token (token_id, token, authentication) values (?, ?, ?)";
		OAuth2ProviderRefreshTokenDAO providerDao = OAuth2ProviderRefreshTokenDAO.newInstance();
		providerDao.saveRefreshToken(extractTokenKey(refreshToken.getValue()),
									serializeRefreshToken(refreshToken),
									serializeAuthentication(authentication));
	}

	@Override
	public void removeRefreshToken(OAuth2RefreshToken token) {
		removeRefreshToken(token.getValue());
	}
	
	public void removeRefreshToken(String tokenValue) {
//		private String deleteAccessTokenSql = "delete from oauth_access_token where token_id = ?";
		OAuth2ProviderRefreshTokenDAO providerDao = OAuth2ProviderRefreshTokenDAO.newInstance();
		providerDao.deleteOAuth2ProviderRefreshToken(extractTokenKey(tokenValue));
	}
	
	@Override
	public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
		OAuth2AccessToken accessToken = null;
		String key = authenticationKeyGenerator.extractKey(authentication);

		try {
			OAuth2ProviderAccessTokenDAO providerDao = OAuth2ProviderAccessTokenDAO.newInstance();
			OAuth2ProviderAccessToken oat = providerDao.getAccessTokenByAuthenticationId(key);
			if(oat != null)
				accessToken = deserializeAccessToken(oat.getToken());
		} catch (IllegalArgumentException e) {
			log.error("Could not extract access token for authentication " + authentication);
		}

		if (accessToken != null
				&& !key.equals(authenticationKeyGenerator.extractKey(readAuthentication(accessToken.getValue())))) {
			storeAccessToken(accessToken, authentication);
		}

		return accessToken;
	}

	@Override
	public Collection<OAuth2AccessToken> findTokensByUserName(String userId) {
		List<OAuth2AccessToken> accessTokens = new ArrayList<OAuth2AccessToken>();

		try {
//			private String selectAccessTokensFromUserNameSql = "select token_id, token from oauth_access_token where user_name = ?";
			OAuth2ProviderAccessTokenDAO providerDao = OAuth2ProviderAccessTokenDAO.newInstance();
			List<OAuth2ProviderAccessToken> oat = providerDao.getAccessTokenByUserId(userId);
			
			if(oat!=null && oat.size()>0){
				for(Iterator<OAuth2ProviderAccessToken> itr = oat.iterator();itr.hasNext();){
					accessTokens.add(deserializeAccessToken(itr.next().getToken()));
				}
			}
		} catch (EmptyResultDataAccessException e) {
			log.info("Failed to find access token for userId " + userId);
		}
		accessTokens = removeNulls(accessTokens);
		return accessTokens;
	}

	@Override
	public Collection<OAuth2AccessToken> findTokensByClientId(String clientId) {
		List<OAuth2AccessToken> accessTokens = new ArrayList<OAuth2AccessToken>();

		try {
//			private String selectAccessTokensFromClientIdSql = "select token_id, token from oauth_access_token where client_id = ?";
			OAuth2ProviderAccessTokenDAO providerDao = OAuth2ProviderAccessTokenDAO.newInstance();
			List<OAuth2ProviderAccessToken> oat = providerDao.getAccessTokenByClientId(clientId);
			
			if(oat!=null && oat.size()>0){
				for(Iterator<OAuth2ProviderAccessToken> itr = oat.iterator();itr.hasNext();){
					accessTokens.add(deserializeAccessToken(itr.next().getToken()));
				}
			}
		} catch (EmptyResultDataAccessException e) {
			log.info("Failed to find access token for clientId " + clientId);
		}
		accessTokens = removeNulls(accessTokens);
		return accessTokens;
	}

	private List<OAuth2AccessToken> removeNulls(List<OAuth2AccessToken> accessTokens) {
		List<OAuth2AccessToken> tokens = new ArrayList<OAuth2AccessToken>();
		for (OAuth2AccessToken token : accessTokens) {
			if (token != null) {
				tokens.add(token);
			}
		}
		return tokens;
	}

	protected String extractTokenKey(String value) {
		if (value == null) {
			return null;
		}
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("MD5 algorithm not available.  Fatal (should be in the JDK).");
		}

		try {
			byte[] bytes = digest.digest(value.getBytes("UTF-8"));
			return String.format("%032x", new BigInteger(1, bytes));
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("UTF-8 encoding not available.  Fatal (should be in the JDK).");
		}
	}
	
	protected byte[] serializeAccessToken(OAuth2AccessToken token) {
		return SerializationUtils.serialize(token);
	}

	protected byte[] serializeRefreshToken(OAuth2RefreshToken token) {
		return SerializationUtils.serialize(token);
	}

	protected byte[] serializeAuthentication(OAuth2Authentication authentication) {
		return SerializationUtils.serialize(authentication);
	}
	
	protected OAuth2AccessToken deserializeAccessToken(byte[] token) {
		return SerializationUtils.deserialize(token);
	}

	protected OAuth2RefreshToken deserializeRefreshToken(byte[] token) {
		return SerializationUtils.deserialize(token);
	}

	protected OAuth2Authentication deserializeAuthentication(byte[] authentication) {
		return SerializationUtils.deserialize(authentication);
	}
}
