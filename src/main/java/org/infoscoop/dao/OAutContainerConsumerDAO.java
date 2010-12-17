package org.infoscoop.dao;

import java.util.Iterator;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.infoscoop.dao.model.OAuthConsumerProp;
import org.infoscoop.dao.model.OAuthContainerConsumer;
import org.infoscoop.util.Crypt;
import org.infoscoop.util.SpringUtil;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class OAutContainerConsumerDAO extends HibernateDaoSupport {

	public static OAutContainerConsumerDAO newInstance() {
		return (OAutContainerConsumerDAO) SpringUtil.getContext().getBean(
		"oauthContainerConsumerDAO");
	}

	public OAuthContainerConsumer getByServiceName(String oauthServiceName) {
		// TODO Auto-generated method stub
		List<OAuthContainerConsumer> result = super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(OAuthContainerConsumer.class).add(
						Expression.eq(OAuthContainerConsumer.PROP_SERVICE_NAME, oauthServiceName)));
		if(result.isEmpty())
			return null;
		else
			return result.get(0);
	}
	public List<OAuthContainerConsumer> all() {
		// TODO Auto-generated method stub
		return super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(OAuthContainerConsumer.class));
	}
	
	public void saveConsumers(List<OAuthContainerConsumer> consumers) {
		for(OAuthContainerConsumer consumer: consumers)
			this.save(consumer);
	}
	
	public void deleteAll() {
		super.getHibernateTemplate().deleteAll(all());
	}
	
	public void save(OAuthContainerConsumer consumer) {
		OAuthContainerConsumer newConsumer = getConsumer(consumer.getServiceName());
		if (newConsumer == null) {
			super.getHibernateTemplate().save(consumer);
		} else {
			newConsumer.setConsumerKey(consumer.getConsumerKey());
			newConsumer.setConsumerSecret(consumer.getConsumerSecret());
			newConsumer.setSignatureMethod(consumer.getSignatureMethod());
			super.getHibernateTemplate().saveOrUpdate(newConsumer);
		}
	}
	
	public OAuthContainerConsumer getConsumer(String serviceName) {
		if (serviceName == null) {
			throw new RuntimeException("serviceName must be set.");
		}
		Iterator results = super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(OAuthContainerConsumer.class).add(
						Expression.eq(OAuthContainerConsumer.PROP_SERVICE_NAME,
								serviceName)))
				.iterator();
		if (results.hasNext()) {
			return (OAuthContainerConsumer) results.next();
		}
		return null;
	}



}
