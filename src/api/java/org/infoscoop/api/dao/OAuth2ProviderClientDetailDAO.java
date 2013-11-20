package org.infoscoop.api.dao;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.infoscoop.api.dao.model.OAuth2ProviderClientDetail;
import org.infoscoop.util.SpringUtil;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class OAuth2ProviderClientDetailDAO extends HibernateDaoSupport {
	private static Log log = LogFactory.getLog(OAuth2ProviderClientDetailDAO.class);
	
	public static OAuth2ProviderClientDetailDAO newInstance() {
		return (OAuth2ProviderClientDetailDAO) SpringUtil.getContext().getBean("oauth2ProviderClientDetailDAO");
	}

	@SuppressWarnings("unchecked")
	public Collection<OAuth2ProviderClientDetail> getClientDetails(){
		List<OAuth2ProviderClientDetail> results = super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(OAuth2ProviderClientDetail.class,"ocd"));
		
		return results; 
	}
	
	@SuppressWarnings("unchecked")
	public OAuth2ProviderClientDetail getClientDetailById(String clientId){
		if (clientId == null) {
			throw new RuntimeException("clientId must be set.");
		}
		
		Iterator<OAuth2ProviderClientDetail> results = super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(OAuth2ProviderClientDetail.class,"ocd")
				.add(Restrictions.eq("ocd.id", clientId)))
				.iterator();

		if(results.hasNext()) {
			return (OAuth2ProviderClientDetail)results.next();
		}
		
		return null; 
	}
	
	public void saveClientDetail(OAuth2ProviderClientDetail clientDetail) {
		if(clientDetail != null)
			super.getHibernateTemplate().saveOrUpdate(clientDetail);
	}

	public void saveClientDetail(String clientId, String title, String resourceIds, String secret, String scope, String grantType, String redirectUrl, String authorities, int accessTokenValidity, int refreshTokenValidity, String additionalInformation) {
		OAuth2ProviderClientDetail clientDetail = getClientDetailById(clientId);
		
		if(clientDetail == null){
			clientDetail = new OAuth2ProviderClientDetail(clientId);
		}
		
		clientDetail.setTitle(title);
		clientDetail.setSecret(secret);
		clientDetail.setScope(scope);
		clientDetail.setResourceIds(resourceIds);
		clientDetail.setGrantTypes(grantType);
		clientDetail.setRedirectUrl(redirectUrl);
		clientDetail.setAuthorities(authorities);
		clientDetail.setAccessTokenValidity(accessTokenValidity);
		clientDetail.setRefreshTokenValidity(refreshTokenValidity);
		clientDetail.setAdditionalInformation(additionalInformation);
		
		super.getHibernateTemplate().saveOrUpdate(clientDetail);
	}

	public void deleteClientDetail(String clientId) {
		OAuth2ProviderClientDetail clientDetail = getClientDetailById(clientId);
		
		if(clientDetail != null)
			super.getHibernateTemplate().delete(clientDetail);
	}
}
