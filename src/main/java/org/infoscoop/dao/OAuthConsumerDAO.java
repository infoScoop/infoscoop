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
import org.infoscoop.dao.model.OAUTH_CONSUMER_PK;
import org.infoscoop.dao.model.OAuthConsumerProp;
import org.infoscoop.util.Crypt;
import org.infoscoop.util.SpringUtil;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class OAuthConsumerDAO extends HibernateDaoSupport {

	private static Log log = LogFactory.getLog(OAuthConsumerDAO.class);

	public static OAuthConsumerDAO newInstance() {
		return (OAuthConsumerDAO) SpringUtil.getContext().getBean(
				"oauthConsumerDAO");
	}

	@SuppressWarnings("unchecked")
	public OAuthConsumerProp getConsumer(String gadgetUrl, String serviceName) {
		if (gadgetUrl == null || serviceName == null) {
			throw new RuntimeException("gadgetUrl and serviceName must be set.");
		}
		String gadgetUrlKey = Crypt.getHash(gadgetUrl);
		Iterator results = super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(OAuthConsumerProp.class).add(
						Expression.eq("Id.GadgetUrlKey", gadgetUrlKey)).add(
						Expression.eq("Id.ServiceName", serviceName)))
				.iterator();
		if (results.hasNext()) {
			return (OAuthConsumerProp) results.next();
		}
		return null;
	}

	public void save(OAuthConsumerProp consumer) {
		OAuthConsumerProp newConsumer = getConsumer(consumer.getGadgetUrl(),
				consumer.getId().getServiceName());
		if (newConsumer == null) {
			super.getHibernateTemplate().save(consumer);
		} else {
			newConsumer.setConsumerKey(consumer.getConsumerKey());
			newConsumer.setConsumerSecret(consumer.getConsumerSecret());
			newConsumer.setSignatureMethod(consumer.getSignatureMethod());
			newConsumer.setPrivateKey(consumer.getPrivateKey());
			super.getHibernateTemplate().saveOrUpdate(newConsumer);
		}
	}

	public static void main(String args[]) {
		System.out.println(Crypt
				.getHash("http://localhost/oauth_test/twit_oauth2.xml"));
	}

	public List<OAuthConsumerProp> getConsumers() {
		return super.getHibernateTemplate().loadAll(OAuthConsumerProp.class);
	}

	public void saveConsumers(List<OAuthConsumerProp> consumers) {
		for(OAuthConsumerProp consumer: consumers)
			this.save(consumer);
	}

	public void delete(String gadgetUrl, String serviceName) {
		super.getHibernateTemplate().delete(this.getConsumer(gadgetUrl, serviceName));
	}
}
