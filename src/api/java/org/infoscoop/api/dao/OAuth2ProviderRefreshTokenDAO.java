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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.infoscoop.api.dao.model.OAuth2ProviderRefreshToken;
import org.infoscoop.util.SpringUtil;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class OAuth2ProviderRefreshTokenDAO extends HibernateDaoSupport {
	private static Log log = LogFactory.getLog(OAuth2ProviderRefreshTokenDAO.class);
	
	public static OAuth2ProviderRefreshTokenDAO newInstance(){
		return (OAuth2ProviderRefreshTokenDAO) SpringUtil.getContext().getBean("oauth2ProviderRefreshTokenDAO");
	}

	@SuppressWarnings("unchecked")
	public OAuth2ProviderRefreshToken getRefreshTokenById(String tokenId){
		if (tokenId == null) {
			throw new RuntimeException("tokenId must be set.");
		}
		
		Iterator<OAuth2ProviderRefreshToken> results = super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(OAuth2ProviderRefreshToken.class)
				.add(Restrictions.eq(OAuth2ProviderRefreshToken.PROP_ID, tokenId)))
				.iterator();

		if(results.hasNext()) {
			return (OAuth2ProviderRefreshToken)results.next();
		}
		
		return null;
	}
	
	public void saveRefreshToken(String tokenId, byte[] token, byte[] authentication, String squareId) {
		OAuth2ProviderRefreshToken refreshToken = getRefreshTokenById(tokenId);

		if(refreshToken == null){
			refreshToken = new OAuth2ProviderRefreshToken(tokenId, squareId);
		}
		refreshToken.setToken(token);
		refreshToken.setAuthentication(authentication);
		super.getHibernateTemplate().saveOrUpdate(refreshToken);
		super.getHibernateTemplate().flush();
	}

	public void deleteOAuth2ProviderRefreshToken(String tokenId) {
		OAuth2ProviderRefreshToken refreshToken = getRefreshTokenById(tokenId);
		
		if (refreshToken != null)
			super.getHibernateTemplate().delete(refreshToken);
	}
}
