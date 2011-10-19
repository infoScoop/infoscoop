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
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Restrictions;
import org.infoscoop.dao.model.OAuthConsumerProp;
import org.infoscoop.dao.model.OAuthGadgetUrl;
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
	public OAuthConsumerProp getConsumer(String consumerId) {
		if (consumerId == null) {
			throw new RuntimeException("consumerId must be set.");
		}

		Iterator results = super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(OAuthConsumerProp.class).add(
						Expression.eq(OAuthConsumerProp.PROP_ID,
								consumerId)))
				.iterator();
		if (results.hasNext()) {
			return (OAuthConsumerProp) results.next();
		}

		return null;
	}

	public OAuthConsumerProp getConsumer(String gadgetUrl, String serviceName) {
		if (gadgetUrl == null || serviceName == null) {
			throw new RuntimeException("gadgetUrl and serviceName must be set.");
		}

		Iterator results = super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(OAuthConsumerProp.class,"ocp").createAlias(
						"OAuthGadgetUrl", "ogu", CriteriaSpecification.LEFT_JOIN)
						.add(Restrictions.conjunction()
							.add(Restrictions.eq("ocp.ServiceName", serviceName))
							.add(Restrictions.eq("ogu.GadgetUrl", gadgetUrl))))
				.iterator();
		
		if (results.hasNext()) {
			return (OAuthConsumerProp) results.next();
		}

		return null;
	}
	
	public Boolean validateConsumerByIdAndServiceAndURL(String id, String serviceName, String gadgetUrl){
		// Refactoring: create hibernate object (mapping? criteria? and o)
		Iterator results = super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(OAuthConsumerProp.class,"ocp").createAlias(
						"OAuthGadgetUrl", "ogu", CriteriaSpecification.LEFT_JOIN).add(
								Restrictions.conjunction()
								.add(Restrictions.ne("ocp.Id", id))
								.add(Restrictions.eq("ocp.ServiceName", serviceName))
								.add(Restrictions.eq("ogu.GadgetUrl", gadgetUrl))))
				.iterator();
		
		if(results.hasNext()){
			// if same contents are had, database is not updated.
			return true;
		}
		
		return false;
	}
	
	public void save(OAuthConsumerProp consumer) {		
		String consumerId = consumer.getId();
		OAuthConsumerProp newConsumer = getConsumer(consumerId);
		Boolean bool = false;
		
		if (newConsumer == null) {
			super.getHibernateTemplate().save(consumer);
		} else {
			newConsumer.setServiceName(consumer.getServiceName());
			newConsumer.setConsumerKey(consumer.getConsumerKey());
			newConsumer.setConsumerSecret(consumer.getConsumerSecret());
			newConsumer.setSignatureMethod(consumer.getSignatureMethod());
			super.getHibernateTemplate().saveOrUpdate(newConsumer);
		}
		
		//validate duplication URLs
		Set<OAuthGadgetUrl> gadgetUrls = consumer.getOAuthGadgetUrl();
		for(Iterator<OAuthGadgetUrl> i = gadgetUrls.iterator();i.hasNext();){
			OAuthGadgetUrl tmp = i.next();
			bool = validateConsumerByIdAndServiceAndURL(consumerId, consumer.getServiceName(), tmp.getGadgetUrl());
			if(!bool){
				OAuthGadgetUrlDAO.newInstance().deleteGadgetUrl(tmp);
				OAuthGadgetUrlDAO.newInstance().save(tmp);
			}
		}		
	}

	public static void main(String args[]) {
		System.out.println(Crypt
				.getHash("http://localhost/oauth_test/twit_oauth2.xml"));
	}

	public List<OAuthConsumerProp> getConsumers() {
		return super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(OAuthConsumerProp.class));
	}

	public List<OAuthConsumerProp> getConsumersByUid(String uid){
		return super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(OAuthConsumerProp.class)
				.createAlias("OAuthToken", "ot", CriteriaSpecification.LEFT_JOIN)
				.createAlias("OAuthGadgetUrl", "ogu", CriteriaSpecification.LEFT_JOIN)
				.add(Restrictions.eq("ot.Id.Uid", uid)));
	}
	
	public List<OAuthConsumerProp> getConsumersJoinGadgetUrl() {
		return super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(OAuthConsumerProp.class).createAlias(
						"OAuthGadgetUrl", "ogu", CriteriaSpecification.LEFT_JOIN));
	}
	
	public List<OAuthConsumerProp> getConsumersNotInId(List<String> idList){
		return super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(OAuthConsumerProp.class).add(
						Expression.not(Expression.in("id", idList))));
	}
	
	public void saveConsumers(List<OAuthConsumerProp> consumers) {
		for(OAuthConsumerProp consumer: consumers)
			this.save(consumer);
	}
	
	public void deleteAll() {
		super.getHibernateTemplate().deleteAll(getConsumers());
	}
	
	public void deleteUpdate(List<String> idList) {
		super.getHibernateTemplate().deleteAll(getConsumersNotInId(idList));
	}
}
