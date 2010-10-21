package org.infoscoop.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.infoscoop.dao.model.OAuthContainerConsumer;
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

}
