package org.infoscoop.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.infoscoop.dao.model.OAUTH2_TOKEN_PK;
import org.infoscoop.dao.model.OAuthConsumerProp;
import org.infoscoop.dao.model.OAuth2Token;
import org.infoscoop.util.Crypt;
import org.infoscoop.util.SpringUtil;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class OAuth2TokenDAO extends HibernateDaoSupport {

	private static Log log = LogFactory.getLog(OAuth2TokenDAO.class);

	public static OAuth2TokenDAO newInstance() {
		return (OAuth2TokenDAO) SpringUtil.getContext().getBean("oauth2TokenDAO");
	}

	@SuppressWarnings("unchecked")
	public OAuth2Token getAccessToken(String uid, String gadgetUrl, String serviceName) {
		if (uid == null || gadgetUrl == null || serviceName == null) {
			throw new RuntimeException(
					"uid, gadgetUrl and serviceName must be set.");
		}
		Iterator results = super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(OAuthConsumerProp.class,"ocp")
				.createAlias("OAuthGadgetUrl", "ogu", CriteriaSpecification.LEFT_JOIN)
				.createAlias("OAuth2Token", "ot", CriteriaSpecification.LEFT_JOIN)
				.add(Restrictions.conjunction()
					.add(Restrictions.eq("ot.Id.Uid", uid))
					.add(Restrictions.eq("ocp.ServiceName", serviceName))
					.add(Restrictions.eq("ogu.GadgetUrlKey", Crypt.getHash(gadgetUrl)))))
				.iterator();
		
		if (results.hasNext()) {
			OAuthConsumerProp o = (OAuthConsumerProp)results.next();
			Set<OAuth2Token> s = o.getOAuth2Token();
			Iterator i = s.iterator();
			if(i.hasNext())
				return (OAuth2Token)i.next();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<OAuth2Token> getAccessTokens(String uid, String serviceName) {
		if (uid == null || serviceName == null) {
			throw new RuntimeException(
					"uid and serviceName must be set.");
		}
		Iterator<OAuthConsumerProp> results = super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(OAuthConsumerProp.class,"ocp")
				.createAlias("OAuthGadgetUrl", "ogu", CriteriaSpecification.LEFT_JOIN)
				.createAlias("OAuth2Token", "ot", CriteriaSpecification.LEFT_JOIN)
				.add(Restrictions.conjunction()
					.add(Restrictions.eq("ot.Id.Uid", uid))
					.add(Restrictions.eq("ocp.ServiceName", serviceName))))
				.iterator();
		
		if (results.hasNext()) {
			OAuthConsumerProp o = (OAuthConsumerProp)results.next();
			List l = new ArrayList();
			l.addAll(o.getOAuth2Token());
			return  l;
		}
		return null;
	}
	
	public void saveAccessToken(String uid, String gadgetUrl,
			String serviceName, String tokenType, String authCode, String accessToken,
			String refreshToken, Long validityPeriodUTC, String squareid) {
		OAuth2Token token = getAccessToken(uid, gadgetUrl, serviceName);
		if (token == null) {
			OAuthConsumerProp oauthConsumer = OAuthConsumerDAO.newInstance().getConsumer(gadgetUrl, serviceName, squareid);
			oauthConsumer.getId();
			token = new OAuth2Token(new OAUTH2_TOKEN_PK(uid, oauthConsumer.getId().getId()));
			token.setSquareid(squareid);
		}
		token.setTokenType(tokenType);
		token.setAuthCode(authCode);
		token.setAccessToken(accessToken);
		token.setRefreshToken(refreshToken);
		token.setValidityPeriodUTC(validityPeriodUTC);
		super.getHibernateTemplate().saveOrUpdate(token);
	}

	public void deleteOAuth2Token(OAuth2Token token) {
		if (token != null)
			super.getHibernateTemplate().delete(token);
	}

	public void deleteOAuth2Token(List<OAuth2Token> tokens) {
		for (OAuth2Token token : tokens) {
			super.getHibernateTemplate().delete(token);
		}
	}
}