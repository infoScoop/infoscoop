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

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.SequencedHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.SimpleExpression;
import org.infoscoop.dao.model.Keyword;
import org.infoscoop.util.SpringUtil;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * The DAO class to get and update the keyword used when we search.
 * 
 * @author koumoto
 * 
 */
public class KeywordLogDAO extends HibernateDaoSupport {

	private static Log log = LogFactory.getLog(KeywordLogDAO.class);
	
	private KeywordLogDAO(){
	}
	
	public static KeywordLogDAO newInstance() {
        return (KeywordLogDAO)SpringUtil.getContext().getBean("keywordLogDAO");
	}

	/**
	 * Register the appointed data.<BR>
	 * 
	 * @param uid
	 *            An userID that is target for operating data.
	 * @param keywordLogNode
	 *            A node object that has the data of "keywordLog" of the DOM to save.
	 * @throws DataResourceException 
	 */
	public void insertLog(String uid, String date, String keyword,
			String keywordLogType, String squareid) {
		Keyword bean = new Keyword( null,uid,new Integer( keywordLogType ),keyword,date,squareid );
		super.getHibernateTemplate().save( bean );
		
        if(logger.isInfoEnabled()){
        	logger.info("keyword" + keyword + "]: Insert XML successfully.");
        }
	}

	/**
	 * Check whether the keyword that has the same the user, the time, and the type has already existed or not．<BR>
	 * 
	 * @param uid
	 *            An userID that is target for operating data.
	 * @param date
	 *            A date whose form is "yyyy/MM/dd/hh"
	 * @param keyword
	 *            An URL of the contents
	 * @param keywordLogType
	 *            A type of the keyword
	 * 
	 * @return Whether the same data has already existed(exist:true、not exist:false)
	 */
	public boolean getKeyword(String uid, String date, String keyword, String keywordLogType, String squareid) {
		//select count(*) from ${schema}.keyword where uid = ? and type = ? and keyword = ? and date = ?
		String queryString = "from Keyword where Uid = ? and Type = ? and Keyword = ? and Date = ? and Squareid = ?";
		List result = super.getHibernateTemplate().find( queryString,
				new Object[] { uid,new Integer( keywordLogType ),keyword,date,squareid });
		
		return result.isEmpty();
	}

	/**
	 * Get the map to add up keyword-ranking.
	 * 
	 * @param startDate
	 * @param endDate
	 * @param keywordLogType
	 * @return
	 */
	public Map getCountMap(final String startDate, final String endDate, final Integer keywordLogType, final String squareid) {

		Map countMap = (Map)super.getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				
				Map countMap = new SequencedHashMap();
				
				Criteria cri = session.createCriteria(Keyword.class);
				
				SimpleExpression se = Expression.eq("Type", keywordLogType);
				LogicalExpression le = Expression.and(Expression.ge("Date", startDate), Expression.le("Date", endDate));
				LogicalExpression le2 = Expression.and(se, le);
				cri.add(le2);
				
				cri.add(Expression.eq("Squareid", squareid));
				
				Projection projection = Projections.projectionList()  
				    .add(Projections.property("Keyword"))
				    .add(Projections.count("Keyword").as("KwdCount"))
				    .add(Projections.groupProperty("Keyword"));
				
				cri.setProjection(projection);
				cri.addOrder(Order.desc("KwdCount"));
				
				try {
					Object[] resultObjs;
					for(Iterator ite = cri.list().iterator();ite.hasNext();){
						resultObjs = (Object[])ite.next();
						String keyword = (String)resultObjs[0];
						Integer count = (Integer)resultObjs[1];
						
						countMap.put(keyword, count);
					}

				} catch (Exception e) {
					logger.error("parsing error", e);
					throw new RuntimeException();
				}

				if (log.isInfoEnabled())
					log.info("getCountMap successfully. : startDate=" + startDate + ", endDate=" + endDate + ", keywordLogType=" + keywordLogType + ", squareid=" + squareid);
				
				return countMap;
			}
			
		});
		
		return countMap;
	}
	
}
