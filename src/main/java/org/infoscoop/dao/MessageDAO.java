package org.infoscoop.dao;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.infoscoop.dao.model.Message;
import org.infoscoop.util.SpringUtil;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class MessageDAO extends HibernateDaoSupport {
	private static Log log = LogFactory.getLog(MessageDAO.class);

	public static MessageDAO newInstance() {
		return (MessageDAO) SpringUtil.getContext().getBean("messageDAO");
	}

	public void insert(Message msg) {
		if (msg.getFrom() == null)
			throw new RuntimeException("uid must be set.");
		msg.setPostedTime(new Date());
		super.getHibernateTemplate().save(msg);
	}

	public List<Message> selectAll(String myuid, long offset, int limit) {
		return super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(Message.class)
					.add( Restrictions.or(
							Restrictions.in("type",new String[] {
									Message.MESSAGE_PUBLIC,
									Message.FYI_PUBLIC
							}),
							Restrictions.eq("to", myuid)
					)).add( Restrictions.lt( "id",offset ))
					.addOrder( Order.desc("posted_time")), 0, limit);
	}

	public List<Message> selectByUid(String uid, long offset, int limit) {
		return super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(Message.class).add(
						Restrictions.eq("from", uid)).add(
								Restrictions.in("type", new String[] {
								Message.MESSAGE_PUBLIC, Message.MESSAGE_FROM,
								Message.FYI_PUBLIC, Message.FYI_PUBLIC,
								Message.MESSAGE_BROADCAST })).add(
										Restrictions.lt("id", offset)).addOrder(
						Order.desc("posted_time")),0, limit);
	}

	public List<Message> selectByTo(String to, long offset, int limit) {
		return super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(Message.class).add(
						Restrictions.or(Restrictions.eq("to", to),
								Restrictions.eq("type",
										Message.MESSAGE_BROADCAST))).add(
						Restrictions.lt("id", offset)).addOrder(
						Order.desc("posted_time")), 0, limit);
	}

	public List<Message> selectByUids(String myuid, String[] uidArr,
			long offset, int limit) {
		return super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(Message.class)
						.add( Restrictions.and(
								Restrictions.in( "from",uidArr),
								Restrictions.or(
										Restrictions.in( "type", new String[] {
												Message.MESSAGE_PUBLIC,
												Message.FYI_PUBLIC }),
										Restrictions.eq("to",myuid)
								)
						)).add( Restrictions.lt( "id",offset ))
						.addOrder(Order.desc("posted_time")),0, limit);
	}

	public List<Message> selectByType(String type, long offset, int limit) {
		return super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(Message.class).add(
						Restrictions.eq("type", type)).add(
						Restrictions.lt("id", offset)).addOrder(
						Order.desc("posted_time")), 0, limit);
	}
}