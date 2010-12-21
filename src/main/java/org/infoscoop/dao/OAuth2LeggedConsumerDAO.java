package org.infoscoop.dao;

import java.util.Iterator;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.infoscoop.dao.model.OAuth3LeggedConsumer;
import org.infoscoop.dao.model.OAuth2LeggedConsumer;
import org.infoscoop.util.Crypt;
import org.infoscoop.util.SpringUtil;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class OAuth2LeggedConsumerDAO extends HibernateDaoSupport {

	public static OAuth2LeggedConsumerDAO newInstance() {
		return (OAuth2LeggedConsumerDAO) SpringUtil.getContext().getBean(
		"oauth2LeggedConsumerDAO");
	}

	public OAuth2LeggedConsumer getByServiceName(String oauthServiceName) {
		// TODO Auto-generated method stub
		List<OAuth2LeggedConsumer> result = super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(OAuth2LeggedConsumer.class).add(
						Expression.eq(OAuth2LeggedConsumer.PROP_SERVICE_NAME, oauthServiceName)));
		if(result.isEmpty())
			return null;
		else
			return result.get(0);
	}
	public List<OAuth2LeggedConsumer> all() {
		// TODO Auto-generated method stub
		return super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(OAuth2LeggedConsumer.class));
	}
	
	public void saveConsumers(List<OAuth2LeggedConsumer> consumers) {
		for(OAuth2LeggedConsumer consumer: consumers)
			this.save(consumer);
	}
	
	public void deleteAll() {
		super.getHibernateTemplate().deleteAll(all());
	}
	
	public void save(OAuth2LeggedConsumer consumer) {
		OAuth2LeggedConsumer newConsumer = getConsumer(consumer.getServiceName());
		if (newConsumer == null) {
			super.getHibernateTemplate().save(consumer);
		} else {
			newConsumer.setConsumerKey(consumer.getConsumerKey());
			newConsumer.setConsumerSecret(consumer.getConsumerSecret());
			newConsumer.setSignatureMethod(consumer.getSignatureMethod());
			super.getHibernateTemplate().saveOrUpdate(newConsumer);
		}
	}
	
	public OAuth2LeggedConsumer getConsumer(String serviceName) {
		if (serviceName == null) {
			throw new RuntimeException("serviceName must be set.");
		}
		Iterator results = super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(OAuth2LeggedConsumer.class).add(
						Expression.eq(OAuth2LeggedConsumer.PROP_SERVICE_NAME,
								serviceName)))
				.iterator();
		if (results.hasNext()) {
			return (OAuth2LeggedConsumer) results.next();
		}
		return null;
	}



}
