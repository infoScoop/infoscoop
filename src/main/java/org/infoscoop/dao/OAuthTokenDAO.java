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
import org.infoscoop.dao.model.OAUTH_TOKEN_PK;
import org.infoscoop.dao.model.OAuthConsumerProp;
import org.infoscoop.dao.model.OAuthToken;
import org.infoscoop.util.Crypt;
import org.infoscoop.util.SpringUtil;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class OAuthTokenDAO extends HibernateDaoSupport {

	private static Log log = LogFactory.getLog(OAuthTokenDAO.class);

	public static OAuthTokenDAO newInstance() {
		return (OAuthTokenDAO) SpringUtil.getContext().getBean("oauthTokenDAO");
	}

	@SuppressWarnings("unchecked")
	public OAuthToken getAccessToken(String uid, String gadgetUrl, String serviceName) {
		if (uid == null || gadgetUrl == null || serviceName == null) {
			throw new RuntimeException(
					"uid, gadgetUrl and serviceName must be set.");
		}
		//String gadgetUrlKey = Crypt.getHash(gadgetUrl);
		Iterator results = super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(OAuthConsumerProp.class,"ocp")
				.createAlias("OAuthGadgetUrl", "ogu", CriteriaSpecification.LEFT_JOIN)
				.createAlias("OAuthToken", "ot", CriteriaSpecification.LEFT_JOIN)
				.add(Restrictions.conjunction()
					.add(Restrictions.eq("ot.Id.Uid", uid))
					.add(Restrictions.eq("ocp.ServiceName", serviceName))
					.add(Restrictions.eq("ogu.GadgetUrlKey", Crypt.getHash(gadgetUrl)))))
				.iterator();
		
		if (results.hasNext()) {
			OAuthConsumerProp o = (OAuthConsumerProp)results.next();
			Set<OAuthToken> s = o.getOAuthToken();
			Iterator i = s.iterator();
			if(i.hasNext())
				return (OAuthToken)i.next();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<OAuthToken> getAccessTokens(String uid, String serviceName) {
		if (uid == null || serviceName == null) {
			throw new RuntimeException(
					"uid and serviceName must be set.");
		}
		Iterator<OAuthConsumerProp> results = super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(OAuthConsumerProp.class,"ocp")
				.createAlias("OAuthGadgetUrl", "ogu", CriteriaSpecification.LEFT_JOIN)
				.createAlias("OAuthToken", "ot", CriteriaSpecification.LEFT_JOIN)
				.add(Restrictions.conjunction()
					.add(Restrictions.eq("ot.Id.Uid", uid))
					.add(Restrictions.eq("ocp.ServiceName", serviceName))))
				.iterator();
		
		if (results.hasNext()) {
			OAuthConsumerProp o = (OAuthConsumerProp)results.next();
			List l = new ArrayList();
			l.addAll(o.getOAuthToken());
			return  l;
		}
		return null;
	}
	
	public void saveAccessToken(String uid, String gadgetUrl,
			String serviceName, String requestToken, String accessToken,
			String tokenSecret) {
		OAuthToken token = getAccessToken(uid, gadgetUrl, serviceName);
		if (token == null) {
			OAuthConsumerProp oauthConsumer = OAuthConsumerDAO.newInstance().getConsumer(gadgetUrl, serviceName);
			oauthConsumer.getId();
			token = new OAuthToken(new OAUTH_TOKEN_PK(uid, oauthConsumer.getId()));
		}
		token.setRequestToken(requestToken);
		token.setAccessToken(accessToken);
		token.setTokenSecret(tokenSecret);
		super.getHibernateTemplate().saveOrUpdate(token);
	}

	public void deleteOAuthToken(OAuthToken token) {
		if (token != null)
			super.getHibernateTemplate().delete(token);
	}

	public void deleteOAuthToken(List<OAuthToken> tokens) {
		for (OAuthToken token : tokens) {
			super.getHibernateTemplate().delete(token);
		}
	}
}
