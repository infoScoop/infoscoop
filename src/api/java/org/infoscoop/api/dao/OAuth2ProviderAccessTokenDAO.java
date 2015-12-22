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

package org.infoscoop.api.dao;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.infoscoop.api.dao.model.OAuth2ProviderAccessToken;
import org.infoscoop.api.dao.model.OAuth2ProviderAccessTokenPK;
import org.infoscoop.util.SpringUtil;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class OAuth2ProviderAccessTokenDAO extends HibernateDaoSupport {
	private static Log log = LogFactory.getLog(OAuth2ProviderAccessTokenDAO.class);
	
	public static OAuth2ProviderAccessTokenDAO newInstance(){
		return (OAuth2ProviderAccessTokenDAO) SpringUtil.getContext().getBean("oauth2ProviderAccessTokenDAO");
	}
	
	@SuppressWarnings("unchecked")
	public OAuth2ProviderAccessToken getAccessTokenById(String tokenId, String squareId){
		if (tokenId == null) {
			throw new RuntimeException("tokenId must be set.");
		}
		
		Iterator<OAuth2ProviderAccessToken> results = super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(OAuth2ProviderAccessToken.class)
						.add(Restrictions.eq(OAuth2ProviderAccessToken.PROP_ID, tokenId))
						.add(Restrictions.eq(OAuth2ProviderAccessToken.PROP_SQUARE_ID, squareId)))
				.iterator();

		if(results.hasNext()) {
			return (OAuth2ProviderAccessToken)results.next();
		}
		
		return null;
	}

	@SuppressWarnings("unchecked")
	public OAuth2ProviderAccessToken getAccessTokenByAuthenticationId(String authenticationId, String squareId){
		if (authenticationId == null) {
			throw new RuntimeException("authenticationId must be set.");
		}
		
		Iterator<OAuth2ProviderAccessToken> results = super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(OAuth2ProviderAccessToken.class)
						.add(Restrictions.eq(OAuth2ProviderAccessToken.PROP_AUTHENTICATION_ID, authenticationId))
						.add(Restrictions.eq(OAuth2ProviderAccessToken.PROP_SQUARE_ID, squareId)))
				.iterator();

		if(results.hasNext()) {
			return (OAuth2ProviderAccessToken)results.next();
		}
		
		return null;
	}

	@SuppressWarnings("unchecked")
	public OAuth2ProviderAccessToken getAccessTokenByRefreshToken(String refreshToken, String squareId){
		if (refreshToken == null) {
			throw new RuntimeException("refreshToken must be set.");
		}
		
		Iterator<OAuth2ProviderAccessToken> results = super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(OAuth2ProviderAccessToken.class)
						.add(Restrictions.eq(OAuth2ProviderAccessToken.PROP_REFRESH_TOKEN, refreshToken))
						.add(Restrictions.eq(OAuth2ProviderAccessToken.PROP_SQUARE_ID, squareId)))
				.iterator();

		if(results.hasNext()) {
			return (OAuth2ProviderAccessToken)results.next();
		}
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<OAuth2ProviderAccessToken> getAccessTokenByUserId(String userId, String squareId){
		if (userId == null) {
			throw new RuntimeException("userId must be set.");
		}
		
		List<OAuth2ProviderAccessToken> results = super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(OAuth2ProviderAccessToken.class)
				.add(Restrictions.eq(OAuth2ProviderAccessToken.PROP_USER_ID, userId))
				.add(Restrictions.eq(OAuth2ProviderAccessToken.PROP_SQUARE_ID, squareId)));
		
		return results;
	}

	@SuppressWarnings("unchecked")
	public List<OAuth2ProviderAccessToken> getAccessTokenByClientId(String clientId, String squareId){
		if (clientId == null) {
			throw new RuntimeException("clientId must be set.");
		}
		
		List<OAuth2ProviderAccessToken> results = super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(OAuth2ProviderAccessToken.class)
				.add(Restrictions.eq(OAuth2ProviderAccessToken.PROP_CLIENT_ID, clientId))
				.add(Restrictions.eq(OAuth2ProviderAccessToken.PROP_SQUARE_ID, squareId)));
		
		return results;
	}
	
	public void saveAccessToken(String tokenId, byte[] token, String authenticationId, String userId, String clientId, byte[] authentication, String refreshToken, String squareId) {
		OAuth2ProviderAccessToken accessToken = getAccessTokenById(tokenId, squareId);
	
		if(accessToken == null){
			accessToken = new OAuth2ProviderAccessToken(tokenId, squareId);
		}
		accessToken.setToken(token);
		accessToken.setAuthenticationId(authenticationId);
		accessToken.setUserId(userId);
		accessToken.setClientId(clientId);
		accessToken.setAuthentication(authentication);
		accessToken.setRefreshToken(refreshToken);
		super.getHibernateTemplate().saveOrUpdate(accessToken);
		super.getHibernateTemplate().flush();
	}

	public void deleteOAuth2ProviderAccessToken(OAuth2ProviderAccessToken tokenObj) {
		if(tokenObj!=null)
			super.getHibernateTemplate().delete(tokenObj);
	}
	
	public void deleteOAuth2ProviderAccessToken(String tokenId, String squareId) {
		OAuth2ProviderAccessToken accessToken = getAccessTokenById(tokenId, squareId);
		
		if (accessToken != null)
			super.getHibernateTemplate().delete(accessToken);
	}

	public void deleteOAuth2ProviderAccessTokenByRefreshToken(String refreshToken, String squareId) {
		OAuth2ProviderAccessToken accessToken = getAccessTokenByRefreshToken(refreshToken, squareId);

		if (accessToken != null)
			super.getHibernateTemplate().delete(accessToken);
	}
}
