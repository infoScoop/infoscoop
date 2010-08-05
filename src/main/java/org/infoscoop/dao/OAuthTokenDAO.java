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

import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.infoscoop.dao.model.OAUTH_TOKEN_PK;
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
	public OAuthToken getAccessToken(String uid, String gadgetUrl,
			String serviceName) {
		if (uid == null || gadgetUrl == null || serviceName == null) {
			throw new RuntimeException(
					"uid, gadgetUrl and serviceName must be set.");
		}
		String gadgetUrlKey = Crypt.getHash(gadgetUrl);
		Iterator results = super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(OAuthToken.class).add(
						Expression.eq("Id.Uid", uid)).add(
						Expression.eq("Id.GadgetUrlKey", gadgetUrlKey)).add(
						Expression.eq("Id.ServiceName", serviceName)))
				.iterator();
		if (results.hasNext()) {
			return (OAuthToken) results.next();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<OAuthToken> getAccessTokens(String uid, String gadgetUrl) {
		String gadgetUrlKey = Crypt.getHash(gadgetUrl);
		return super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(OAuthToken.class).add(
						Expression.eq("Id.Uid", uid)).add(
						Expression.eq("Id.GadgetUrlKey", gadgetUrlKey)));
	}

	public void saveAccessToken(String uid, String gadgetUrl,
			String serviceName, String requestToken, String accessToken,
			String tokenSecret) {
		OAuthToken token = getAccessToken(uid, gadgetUrl, serviceName);
		if (token == null) {
			String gadgetUrlKey = Crypt.getHash(gadgetUrl);
			token = new OAuthToken(new OAUTH_TOKEN_PK(uid, gadgetUrlKey,
					serviceName));
			token.setGadgetUrl(gadgetUrl);
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
