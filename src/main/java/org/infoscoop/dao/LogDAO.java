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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;



import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.infoscoop.account.SearchUserService;
import org.infoscoop.account.IAccount;
import org.infoscoop.context.UserContext;
import org.infoscoop.dao.model.Logs;
import org.infoscoop.dao.model.Properties;
import org.infoscoop.util.Crypt;
import org.infoscoop.util.JSONScript;
import org.infoscoop.util.SpringUtil;
import org.infoscoop.util.StringUtil;
import org.json.JSONObject;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * The DAO class to get and update the information of log.
 * 
 * @author nakata
 * 
 */
public class LogDAO extends HibernateDaoSupport {
	
    private static Log log = LogFactory.getLog(LogDAO.class);
	private Calendar logDeleteDay = Calendar.getInstance();
    private LogDAO(){}
    
	public static LogDAO newInstance() {
        return (LogDAO)SpringUtil.getContext().getBean("logDAO");
	}
	
    /**
     * Get the DOM of an appointed key.
     * 
     * @param uid
     *            an userID
     * @param logType
     *            a type
     * @param url
     *            a URL of contents
     * @param date
     *            the data whose form is "yyyy/MM/dd/hh"
     * @return The top node of DOM including the log information of an appointed key.
     */
    public boolean checkLog(String uid, String logType,
            String url, String rssUrl, String date) {
    	
    	String url_key = Crypt.getHash(url);
    	String rssurl_key = Crypt.getHash(rssUrl);
    	
    	//select count(*) from ${schema}.logs where uid=? and type=? and url_key=? and rssurl_key=? and date=?
    	String queryString = "select count(*) from Logs where uid = ? and Type = ? and urlKey = ? and rssurlKey = ? and Date = ?";
    	
    	List result = super.getHibernateTemplate().find( queryString,
    			new Object[]{ uid,new Integer( logType ),url_key,rssurl_key,date });
    	if (log.isInfoEnabled())
			log.info("param[]: count successfully.");
		
    	if( result.isEmpty())
    		return false;
    	
    	return (( Long )result.get(0)).intValue() > 0;
    }

    /**
     * Register the appointed data.<BR>
     * 
     * @param uid
     *            An userID that is targaet for operating the data.
     * @param logNode
     *            A node object that has the data of "log" of DOM to save.
     * @throws IOException
     */
    public void insertLog(String uid, String logType,
            String url, String rssUrl, String date){

    	if(log.isInfoEnabled()){
    		log.info("insertLog for uid: " + uid
                + ", type: " + logType + ", url: " + url + ", date: " + date
                + ".");
    	}
    	
    	String url_key = Crypt.getHash(url);
    	String rssurl_key = Crypt.getHash(rssUrl);
    	
		Logs logs = new Logs( null,uid,new Integer( logType ),url,url_key,rssUrl,rssurl_key,date );
		super.getHibernateTemplate().save( logs );
		
		if(log.isInfoEnabled()){
        	log.info("url" + url + "]: Insert XML successfully.");
        }
    }
    
    public void deleteOldLog() {
		Calendar currentDay = Calendar.getInstance();
		if (currentDay.get(Calendar.DAY_OF_YEAR) != logDeleteDay.get(Calendar.DAY_OF_YEAR)) {
			PropertiesDAO pdao = PropertiesDAO.newInstance();
			Properties property = pdao.findProperty("logStoragePeriod");
			int storagePeriod = 365;
			try {
				storagePeriod = Integer.parseInt(StringUtil.getNullSafe(property.getValue()));
			} catch (NumberFormatException ex) {
				log.error("Invalid logStoragePeriod property.", ex);
			}

			if (storagePeriod > 0) {
				Calendar deleteDate = Calendar.getInstance();
				deleteDate.setTimeZone(TimeZone.getTimeZone("UTC"));
				deleteDate.add(Calendar.DATE, -(storagePeriod));
				String queryString = "delete Logs where Date < ?";
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd00");
				sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
				String date = sdf.format(deleteDate.getTime());
				super.getHibernateTemplate().bulkUpdate(queryString, date);
			}
			logDeleteDay = currentDay;
		}

	}
    
    public int getRssAccessCount(String rssUrl, String startDate) {
		if (log.isInfoEnabled())
			log.info("getRssAccessCount for rssUrl: " + rssUrl
					+ ", startDate: " + startDate);
		String rssurl_key = Crypt.getHash(rssUrl);
		
		//select count(*) from ${schema}.logs where rssurl_key=? and date>?
		String queryString = "select count(*) from Logs where rssurlKey = ? and Date > ? and Type != 2";
		
		List result = super.getHibernateTemplate().find( queryString,
				new Object[]{ rssurl_key,startDate } );
		if( result.isEmpty() )
			return 0;
		
		return (( Long )result.get(0)).intValue();
	}
    
    public int getRssAccessCount(String rssUrl) {
		if (log.isInfoEnabled())
			log.info("getRssAccessCount for rssUrl: " + rssUrl);
		String rssurl_key = Crypt.getHash(rssUrl);
		
		//select count(*) from ${schema}.logs where rssurl_key=?
		String queryString = "select count(*) from Logs where rssurlKey = ? and Type != 2";
		List result = super.getHibernateTemplate().find( queryString,
				new Object[] { rssurl_key } );
		if( result.isEmpty())
			return 0;
		
		return (( Long )result.get(0)).intValue();
	}
    
    public RssAccessStats getRssAccessStats(String rssUrl, int start, int limit) {
    	if (log.isInfoEnabled())
    		log.info("getRssAccessStats for rssUrl: " + rssUrl + ", start:"
    				+ start + ", limit:" + limit);
    	final String rssurl_key = Crypt.getHash(rssUrl);
    	RssAccessStats stats = new RssAccessStats();
		Session session = super.getSession();
    	try{
    		//select uid,substr(date, 1, 8) as date,count(*) as count from ${schema}.logs where rssurl_key=? group by uid, substr(date, 1, 8) order by date desc, uid 
    		String queryString = "select uid,substring(Date,1,8),count(*) from Logs where rssurlKey = ? and Type != 2 group by uid,substring(Date,1,8) order by substring(Date,1,8) desc,Uid";
    		Query query = session.createQuery( queryString );
    		query.setFirstResult( start );
    		query.setMaxResults( limit );
    		query.setString( 0,rssurl_key );

    		List list = query.list();
    		List entries = new ArrayList();
    		
    		SimpleDateFormat parsedf = new SimpleDateFormat("yyyyMMdd");
    		parsedf.setTimeZone(TimeZone.getTimeZone("UTC"));
    		SimpleDateFormat sdf = UserContext.instance().getUserInfo().getClientDateFormat("yyyyMMdd");    		

    		for (int i = 0; i<list.size(); i++) {
    			Object[] row = ( Object[] )list.get( i );
    			Date date = parsedf.parse(( String )row[1]);
    			RssAccessStatsEntry entry = new RssAccessStatsEntry();
    			entry.setUid( ( String )row[0]);
    			entry.setDate( sdf.format(date));
    			entry.setCount( (( Long )row[2] ).intValue());
    			entries.add(entry);
    		}
    		
    		query = session.createQuery("select uid from Logs where rssurl_key = ? and Type != 2 group by uid,substring(Date,1,8)");
    		query.setString( 0,rssurl_key );
    		
    		int count = query.list().size();
    		stats.setCount(count);
    		stats.setEntries(entries);
    	} catch( Exception ex ) {
    		log.error("failed get accesslist",ex );
    	} finally {
    		session.close();
    	}
    	return stats;
	}
    public static class RssAccessStats {
    	private int count;

		private List entries;

		public int getCount() {
			return count;
		}

		public void setCount(int count) {
			this.count = count;
		}

		public List getEntries() {
			return entries;
		}

		public void setEntries(List entries) {
			this.entries = entries;
		}
	}
    
    public static class RssAccessStatsEntry {
		private String uid = null;

		private String date = null;

		private int count = 0;
		
		private IAccount user = null;

		public int getCount() {
			return count;
		}

		public void setCount(int count) {
			this.count = count;
		}

		public String getDate() {
			return date;
		}

		public void setDate(String date) {
			this.date = date;
		}

		public String getUid() {
			return uid;
		}

		public void setUid(String uid) {
			this.uid = uid;
		}
		
		public IAccount getUser() throws Exception {
			if (!SearchUserService.isAvailable())
				return null;
			if (this.user != null && this.user.getUid().equals(this.uid))
				return this.user;
			try {
				SearchUserService search = (SearchUserService) SpringUtil
						.getBean("searchUserService");
				this.user = search.getUser(this.uid);
				return this.user;
			} catch (NoSuchBeanDefinitionException e) {
				log.warn("searchUserService not found.", e);
				return null;
			}
		}

		public JSONObject toJSONObject() {
			try {
				JSONObject json = new JSONObject();
				String name = null;
				try {
					name = getUser() != null ? getUser().getName() : uid;
				} catch( Exception ex ) {
					log.warn( ex );
				}
				
				if( name == null )
					name = uid;
				
				json.put("name", name);
				json.put("accessDate", new JSONScript("new Date("
						+ date.substring(0, 4) + ","
						+ (Integer.parseInt(date.substring(4, 6)) - 1) + ","
						+ date.substring(6) + ")"));
				json.put("count", count);
				return json;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

    public static void main(String args[]) throws Exception {
		String rssUrl = "http://nikkeibp.jp/index.rdf";
		String startDate = "2008012314";
		LogDAO dao = LogDAO.newInstance();
		int count = dao.getRssAccessCount(rssUrl, startDate);
		System.out.println(count);
		int start = 1;
		int limit = 3;
		RssAccessStats stats = dao.getRssAccessStats(rssUrl, start, limit);
		System.out.println(stats.getCount());
		for(Iterator it = stats.getEntries().iterator();it.hasNext();){
			RssAccessStatsEntry entry = (RssAccessStatsEntry)it.next();
			System.out.println(entry);
		}
	}
}
