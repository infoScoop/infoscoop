package org.infoscoop.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;
import org.infoscoop.dao.model.Notification;
import org.infoscoop.dao.model.base.BaseNotification;
import org.infoscoop.util.SpringUtil;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class NotificationDAO extends HibernateDaoSupport  {

    public static NotificationDAO newInstance() {
        return (NotificationDAO)SpringUtil.getContext().getBean("notificationDAO");
    }

    public Notification selectById(int id){
        return super.getHibernateTemplate().get(Notification.class, id);
    }

    @SuppressWarnings("unchecked")
    public List<Notification> select(int offset, int limit, Date startDate, List<String> squareIdList) {
    	Session session = super.getSession();
    	Criteria criteria = session.createCriteria(Notification.class);
    	criteria.setFirstResult(offset);

		SimpleExpression typeGlobalExp = Restrictions.eq(Notification.PROP_TYPE, BaseNotification.NOTIFICATION_TYPE.GLOBAL.name());
		if(squareIdList.size() > 0) {
			Criterion inSquareExp = Restrictions.in(Notification.PROP_SQUARE_ID, squareIdList);
			criteria.add(Restrictions.or(typeGlobalExp, inSquareExp));
		} else {
			criteria.add(typeGlobalExp);
		}

        if(limit >= 0){
        	criteria.setMaxResults(limit);
    	}
    	
        if(startDate != null){
        	criteria.add(Restrictions.ge(Notification.PROP_LASTMODIFIED, startDate));
        }
        
        criteria.addOrder(Order.desc(Notification.PROP_LASTMODIFIED));
    	
    	return criteria.list();
	}
    
}
