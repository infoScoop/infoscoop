package org.infoscoop.dao;

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.infoscoop.dao.model.Message;
import org.infoscoop.dao.model.SystemMessage;
import org.infoscoop.util.SpringUtil;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class SystemMessageDAO extends HibernateDaoSupport {
	private static Log log = LogFactory.getLog(SystemMessageDAO.class);

	public static SystemMessageDAO newInstance() {
		return (SystemMessageDAO) SpringUtil.getContext().getBean("systemMessageDAO");
	}

	public void insert(SystemMessage msg) {
		super.getHibernateTemplate().save(msg);
	}

	public Collection<SystemMessage> selectByToAndNoRead(String uid) {
		return super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(SystemMessage.class).add(
						Expression.and(
								Expression.eq(SystemMessage.PROP_TO, uid),
								Expression.eq(SystemMessage.PROP_ISREAD, Integer.valueOf(0)))).addOrder(
										Order.desc(SystemMessage.PROP_ID)));
	}

}