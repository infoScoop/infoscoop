package org.infoscoop.dao;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.infoscoop.dao.model.Accesslog;
import org.infoscoop.dao.model.Property;
import org.infoscoop.util.SpringUtil;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class AccessLogDAO extends HibernateDaoSupport {
	private static final String ACCESSLOG_DATE_FORMAT = "yyyyMMdd";
	private static Log log = LogFactory.getLog(AccessLogDAO.class);
	private Calendar logDeleteDay = Calendar.getInstance();
	
	public static AccessLogDAO newInstance() {
		return (AccessLogDAO) SpringUtil.getContext().getBean("accessLogDAO");
	}

	public void insert(String uid, Date date) {
		if (uid == null)
			throw new RuntimeException("uid must be set.");
		
		String dateStr = new SimpleDateFormat( ACCESSLOG_DATE_FORMAT ).format(date);
		Accesslog entity = new Accesslog(uid, dateStr);
		
		super.getHibernateTemplate().save(entity);
	}
	
	public int selectCountByDate(String uid, Date date){
		return (Integer)super.getHibernateTemplate().execute(new DateAccessCountCallback(uid, date));
	}
	
	public void deleteOldLog() {
		Calendar currentDay = Calendar.getInstance();
		if (currentDay.get(Calendar.DAY_OF_YEAR) != logDeleteDay.get(Calendar.DAY_OF_YEAR)) {
			PropertiesDAO pdao = PropertiesDAO.newInstance();
			Property property = pdao.findProperty("logStoragePeriod");
			int storagePeriod = 365;
			try {
				storagePeriod = Integer.parseInt(property.getValue());
			} catch (NumberFormatException ex) {
				log.error("Invalid logStoragePeriod property.", ex);
			}
			if (storagePeriod > 0) {
				Calendar deleteDate = Calendar.getInstance();
				deleteDate.add(Calendar.DATE, -(storagePeriod));
				String dateStr = new SimpleDateFormat( ACCESSLOG_DATE_FORMAT ).format(deleteDate
						.getTime());
				String queryString = "delete Accesslog where Date < ?";
				super.getHibernateTemplate().bulkUpdate(queryString, dateStr);
			}
			logDeleteDay = currentDay;
		}
	}
	
	public int getAccessCountByDate(Date date){
		return (Integer)super.getHibernateTemplate().execute(new DateAccessCountCallback(null, date));
	}
	
	private class DateAccessCountCallback implements HibernateCallback{
		private String uid;
		private Date date;
		
		public DateAccessCountCallback(String uid, Date date) {
			this.uid = uid;
			this.date = date;
		}
		
		public Object doInHibernate(Session session) throws HibernateException,
				SQLException {
			Criteria crit = session.createCriteria(Accesslog.class);
			
			if(uid != null)
				crit.add(Restrictions.eq("Uid", uid));
			
			String dateStr = new SimpleDateFormat( ACCESSLOG_DATE_FORMAT ).format(date);
			crit.add(Restrictions.eq("Date", dateStr));
			crit.setProjection(Projections.countDistinct("Uid"));
			Integer rowCount = (Integer)crit.uniqueResult();
			
			return rowCount;
		}
		
	}
}