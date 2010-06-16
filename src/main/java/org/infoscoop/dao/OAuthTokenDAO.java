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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.infoscoop.dao.model.OAUTH_TOKEN_PK;
import org.infoscoop.dao.model.OAuthToken;
import org.infoscoop.util.SpringUtil;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class OAuthTokenDAO extends HibernateDaoSupport {

	private static Log log = LogFactory.getLog(OAuthTokenDAO.class);

	public static OAuthTokenDAO newInstance() {
		return (OAuthTokenDAO) SpringUtil.getContext().getBean("oauthTokenDAO");
	}

	@SuppressWarnings("unchecked")
	public OAuthToken getAccessToken(String uid, String widgetId,
			String serviceName) {
		if (widgetId == null || serviceName == null) {
			throw new RuntimeException("widgetId and serviceName must be set.");
		}
		Iterator results = super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(OAuthToken.class).add(
						Expression.eq("Id.Uid", uid)).add(
						Expression.eq("Id.WidgetId", widgetId)).add(
						Expression.eq("Id.ServiceName", serviceName)))
				.iterator();
		if (results.hasNext()) {
			return (OAuthToken) results.next();
		}
		return null;
	}

	public void saveAccessToken(String uid, String widgetId,
			String serviceName, String accessToken, String tokenSecret) {
		OAuthToken token = getAccessToken(uid, widgetId, serviceName);
		if (token == null) {
			token = new OAuthToken(new OAUTH_TOKEN_PK(uid, widgetId,
					serviceName));
		}
		token.setAccessToken(accessToken);
		token.setTokenSecret(tokenSecret);
		super.getHibernateTemplate().saveOrUpdate(token);
	}
}
