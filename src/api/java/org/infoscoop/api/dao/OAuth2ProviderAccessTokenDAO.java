package org.infoscoop.api.dao;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.infoscoop.api.dao.model.OAuth2ProviderAccessToken;
import org.infoscoop.util.SpringUtil;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class OAuth2ProviderAccessTokenDAO extends HibernateDaoSupport {
	private static Log log = LogFactory.getLog(OAuth2ProviderAccessTokenDAO.class);
	
	public static OAuth2ProviderAccessTokenDAO newInstance(){
		return (OAuth2ProviderAccessTokenDAO) SpringUtil.getContext().getBean("oauth2ProviderAccessTokenDAO");
	}
	
	@SuppressWarnings("unchecked")
	public OAuth2ProviderAccessToken getAccessTokenById(String tokenId){
		if (tokenId == null) {
			throw new RuntimeException("tokenId must be set.");
		}
		
		Iterator<OAuth2ProviderAccessToken> results = super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(OAuth2ProviderAccessToken.class,"oat")
				.add(Restrictions.eq("oat.id", tokenId)))
				.iterator();

		if(results.hasNext()) {
			return (OAuth2ProviderAccessToken)results.next();
		}
		
		return null;
	}

	@SuppressWarnings("unchecked")
	public OAuth2ProviderAccessToken getAccessTokenByAuthenticationId(String authenticationId){
		if (authenticationId == null) {
			throw new RuntimeException("authenticationId must be set.");
		}
		
		Iterator<OAuth2ProviderAccessToken> results = super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(OAuth2ProviderAccessToken.class,"oat")
				.add(Restrictions.eq("oat.authenticationId", authenticationId)))
				.iterator();

		if(results.hasNext()) {
			return (OAuth2ProviderAccessToken)results.next();
		}
		
		return null;
	}

	@SuppressWarnings("unchecked")
	public OAuth2ProviderAccessToken getAccessTokenByRefreshToken(String refreshToken){
		if (refreshToken == null) {
			throw new RuntimeException("refreshToken must be set.");
		}
		
		Iterator<OAuth2ProviderAccessToken> results = super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(OAuth2ProviderAccessToken.class,"oat")
				.add(Restrictions.eq("oat.refreshToken", refreshToken)))
				.iterator();

		if(results.hasNext()) {
			return (OAuth2ProviderAccessToken)results.next();
		}
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<OAuth2ProviderAccessToken> getAccessTokenByUserId(String userId){
		if (userId == null) {
			throw new RuntimeException("userId must be set.");
		}
		
		List<OAuth2ProviderAccessToken> results = super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(OAuth2ProviderAccessToken.class,"oat")
				.add(Restrictions.eq("oat.userId", userId)));
		
		return results;
	}

	@SuppressWarnings("unchecked")
	public List<OAuth2ProviderAccessToken> getAccessTokenByClientId(String clientId){
		if (clientId == null) {
			throw new RuntimeException("clientId must be set.");
		}
		
		List<OAuth2ProviderAccessToken> results = super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(OAuth2ProviderAccessToken.class,"oat")
				.add(Restrictions.eq("oat.clientId", clientId)));
		
		return results;
	}
	
	public void saveAccessToken(String tokenId, byte[] token, String authenticationId, String userId, String clientId, byte[] authentication, String refreshToken) {
		OAuth2ProviderAccessToken accessToken = getAccessTokenById(tokenId);
	
		if(accessToken == null){
			accessToken = new OAuth2ProviderAccessToken(tokenId);
		}
		accessToken.setToken(token);
		accessToken.setAuthenticationId(authenticationId);
		accessToken.setUserId(userId);
		accessToken.setClientId(clientId);
		accessToken.setAuthentication(authentication);
		accessToken.setRefreshToken(refreshToken);
		super.getHibernateTemplate().saveOrUpdate(accessToken);
	}

	public void deleteOAuth2ProviderAccessToken(String tokenId) {
		OAuth2ProviderAccessToken accessToken = getAccessTokenById(tokenId);
		
		if (accessToken != null)
			super.getHibernateTemplate().delete(accessToken);
	}

	public void deleteOAuth2ProviderAccessTokenByRefreshToken(String refreshToken) {
		OAuth2ProviderAccessToken accessToken = getAccessTokenByRefreshToken(refreshToken);
		
		if (accessToken != null)
			super.getHibernateTemplate().delete(accessToken);
	}
//	@SuppressWarnings("unchecked")
//	public OAuth2ProviderAccessToken getAccessToken(String uid, String gadgetUrl, String serviceName) {
//		if (uid == null || gadgetUrl == null || serviceName == null) {
//			throw new RuntimeException(
//					"uid, gadgetUrl and serviceName must be set.");
//		}
//		Iterator results = super.getHibernateTemplate().findByCriteria(
//				DetachedCriteria.forClass(OAuthConsumerProp.class,"ocp")
//				.createAlias("OAuthGadgetUrl", "ogu", CriteriaSpecification.LEFT_JOIN)
//				.createAlias("OAuth2Token", "ot", CriteriaSpecification.LEFT_JOIN)
//				.add(Restrictions.conjunction()
//					.add(Restrictions.eq("ot.Id.Uid", uid))
//					.add(Restrictions.eq("ocp.ServiceName", serviceName))
//					.add(Restrictions.eq("ogu.GadgetUrlKey", Crypt.getHash(gadgetUrl)))))
//				.iterator();
//		
//		if (results.hasNext()) {
//			OAuthConsumerProp o = (OAuthConsumerProp)results.next();
//			Set<OAuth2Token> s = o.getOAuth2Token();
//			Iterator i = s.iterator();
//			if(i.hasNext())
//				return (OAuth2ProviderAccessToken)i.next();
//		}
//		return null;
//	}
//	
//	@SuppressWarnings("unchecked")
//	public List<OAuth2ProviderAccessToken> getAccessTokens(String uid, String serviceName) {
//		if (uid == null || serviceName == null) {
//			throw new RuntimeException(
//					"uid and serviceName must be set.");
//		}
//		Iterator<OAuthConsumerProp> results = super.getHibernateTemplate().findByCriteria(
//				DetachedCriteria.forClass(OAuthConsumerProp.class,"ocp")
//				.createAlias("OAuthGadgetUrl", "ogu", CriteriaSpecification.LEFT_JOIN)
//				.createAlias("OAuth2Token", "ot", CriteriaSpecification.LEFT_JOIN)
//				.add(Restrictions.conjunction()
//					.add(Restrictions.eq("ot.Id.Uid", uid))
//					.add(Restrictions.eq("ocp.ServiceName", serviceName))))
//				.iterator();
//		
//		if (results.hasNext()) {
//			OAuthConsumerProp o = (OAuthConsumerProp)results.next();
//			List l = new ArrayList();
//			l.addAll(o.getOAuth2Token());
//			return  l;
//		}
//		return null;
//	}
}
