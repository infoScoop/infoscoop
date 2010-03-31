package org.infoscoop.dao;

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
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
						Restrictions.and(Restrictions.eq("TO", uid),
								Restrictions.eq("IsRead", Integer.valueOf(0))))
						.addOrder(Order.desc("id")));
	}

}