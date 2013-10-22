package org.infoscoop.api.dao;

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.infoscoop.api.dao.model.OAuth2ProviderAccessToken;
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
				DetachedCriteria.forClass(OAuth2ProviderRefreshToken.class,"ort")
				.add(Restrictions.eq("ort.id", tokenId)))
				.iterator();

		if(results.hasNext()) {
			return (OAuth2ProviderRefreshToken)results.next();
		}
		
		return null;
	}
	
	public void saveRefreshToken(String tokenId, byte[] token, byte[] authentication) {
		OAuth2ProviderRefreshToken refreshToken = getRefreshTokenById(tokenId);
	
		if(refreshToken == null){
			refreshToken = new OAuth2ProviderRefreshToken(tokenId);
		}
		refreshToken.setToken(token);
		refreshToken.setAuthentication(authentication);
		super.getHibernateTemplate().saveOrUpdate(refreshToken);
	}

	public void deleteOAuth2ProviderRefreshToken(String tokenId) {
		OAuth2ProviderRefreshToken refreshToken = getRefreshTokenById(tokenId);
		
		if (refreshToken != null)
			super.getHibernateTemplate().delete(refreshToken);
	}
}
