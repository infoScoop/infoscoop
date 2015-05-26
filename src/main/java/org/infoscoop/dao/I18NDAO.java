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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.SimpleExpression;
import org.infoscoop.dao.model.*;
import org.infoscoop.util.SpringUtil;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class I18NDAO extends HibernateDaoSupport {
	private static Log log = LogFactory.getLog(I18NDAO.class);
	private static final int FETCH_SIZE = 1000;
	
	private static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss";
	
	public static I18NDAO newInstance() {
        return (I18NDAO)SpringUtil.getContext().getBean("i18NDAO");
	}
	
	public HibernateTemplate getHibernateTemplateLimited() {
		HibernateTemplate temp = super.getHibernateTemplate();
		temp.setFetchSize( FETCH_SIZE );
		
		return temp;
	}
    
	public void insert(I18n msg) {
		//insert into ${schema}.i18n(number, type, id, country, lang, message) values (?, ?, ?, ?, ?, ?)
		
		super.getHibernateTemplate().save( msg );
		
		if (log.isInfoEnabled())
			log.info("insert successfully. : " + msg);
	}
    
	public void insertUpdate(I18n msg) {
		super.getHibernateTemplate().saveOrUpdate(msg);
		
		if (log.isInfoEnabled())
			log.info("insertUpdate successfully. : " + msg);
	}

	public void insertLocale(String type, String country, String lang, String squareId) {
		//insert into ${schema}.i18nLocale(type, country, lang) values (?, ?, ?)
		
		super.getHibernateTemplate().save( new I18nlocale( null,type,country,lang, squareId));
		
		if (log.isInfoEnabled())
			log.info("update successfully. : country=" + country
					+ ", lang=" + lang + ", squareId=" + squareId);
	}

	public void deleteByType(String type, String squareId){
		deleteI18NByType( type, squareId );
		deleteI18NLocaleByType( type, squareId );
	}
	private void deleteI18NByType( String type, String squareId ) {
		//delete from ${schema}.i18n where type = ?
		String queryString = "delete from I18n where Id.Type = ? and Id.Squareid = ?";
		
		super.getHibernateTemplate().bulkUpdate(queryString, new Object[]{type,squareId});
		if (log.isInfoEnabled())
			log.info("deleteByType successfully. : type=" + type + " , squareId=" + squareId);
	}
	private void deleteI18NLocaleByType( String type, String squareId ) {
		//delete from ${schema}.i18nLocale where type = ?
		String queryString = "delete from I18nlocale where Type = ? and Squareid = ?";
		
		super.getHibernateTemplate().bulkUpdate( queryString, new Object[]{ type, squareId } );
		
		if (log.isInfoEnabled())
			log.info("deleteI18NLocaleByType successfully. : type=" + type + " , squareId=" + squareId);
	}
	
	/**
	 * Delete the data of i18n except the locale of "ALL_ALL" by appointing the type and ID.
	 * @param type
	 * @param id
	 */
	public void deleteI18NByIDWithoutDefault( String type, String id, String squareId ) {
		//delete from ${schema}.i18nLocale where type = ?
		String queryString = "delete from I18n where Id.Type = ? and Id.Id = ? and Id.Squareid and not(Id.Country = ? and Id.Lang = ?)";
		
		super.getHibernateTemplate().bulkUpdate( queryString,
				new Object[]{ type, id, squareId, "ALL", "ALL" } );
		
		if (log.isInfoEnabled())
			log.info("deleteI18NByIDWithoutDefault successfully. : type=" + type + " ,id=" + id + " ,squareId=" + squareId);
	}

	/**
	 * Get the data.
	 * 
	 * @return
	 */
	public List selectAll(String squareId) {
		//select * from ${schema}.i18n order by type, number, id, country, lang
		String queryString = "from I18n where Id.squareId = ? order by Id.Type,Number,Id.Id,Id.Country,Id.Lang,Id.Squareid";
		
		return getHibernateTemplateLimited().find( queryString, squareId );
	}
	
	public I18n selectByPK(I18NPK pk) {
		return (I18n)getHibernateTemplate().get(I18n.class, pk);
	}
	
	/**
	 * Get the list of ID by appointing the locale.
	 * @param country
	 * @param lang
	 * @return
	 */
	public List getIdListByLocale(final String type, final String country, final String lang, final String squareId){
		return (List)getHibernateTemplateLimited().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				
				Criteria cri = session.createCriteria(I18n.class);

				LogicalExpression se = Expression.and(Expression.eq("Id.Type", type), Expression.eq("Id.Squareid", squareId));
				LogicalExpression le = Expression.and(Expression.eq("Id.Country", country), Expression.eq("Id.Lang", lang));
				LogicalExpression le2 = Expression.and(se, le);
				
				cri.add(le2);
				
				List idList = new ArrayList();
				I18n i18n;
				for(Iterator ite = cri.list().iterator();ite.hasNext();){
					i18n = (I18n)ite.next();
					idList.add(i18n.getId().getId());
				}

				if (log.isInfoEnabled())
					log.info("getIdListByLocale successfully. : country=" + country + ", lang=" + lang);

				return idList;
			}
		});
	}
	
	/**
	 * Get the data.
	 */
	public List selectByType(String type, String squareId){
		//select * from ${schema}.i18n where type = ? order by number, id, country, lang
		String queryString = "from I18n where Id.Type = ? and Id.Squareid order by Id.Type,Number,Id.Id,Id.Country,Id.Lang,Id.Squareid";
		
		List result = getHibernateTemplateLimited().find( queryString, new Object[] { type, squareId });
		if (log.isInfoEnabled())
			log.info("selectByType successfully. : type=" + type + " ,squareId=" + squareId);
		
		return result;
	}

	public List selectLocales(String squareId) {
		String queryString = "from I18nlocale where Squareid = ? order by Type,Country,Lang,Squareid";

		List result = super.getHibernateTemplate().find(queryString, squareId);
		if (log.isInfoEnabled())
			log.info("selectLocales successfully");
		
		return result;
	}

	public List selectLocales(String type, String squareId) {
		//select * from ${schema}.i18nLocale where type = ? order by country, lang
		String queryString = "from I18nlocale where Type = ? and Squareid = ? order by Country,Lang,Squareid";

		List result = super.getHibernateTemplate().find( queryString,
				new Object[] { type, squareId });
		if (log.isInfoEnabled())
			log.info("selectLocales successfully. : type=" + type + " ,squareId=" + squareId);
		
		return result;
	}

	public void updateLastmodified(String type, String squareId){
		//update ${schema}.i18nlastmodified set lastmodified = ? where type = ?
		//insert into ${schema}.i18nlastmodified(lastmodified, type) values (?, ?)
		
		I18nlastmodified lastmodified = ( I18nlastmodified )super.getHibernateTemplate().get(
				I18nlastmodified.class, new I18NlastmodifiedPK(type, squareId));
		boolean isUpdate = true;
		if( lastmodified == null ) {
			lastmodified = new I18nlastmodified( new I18NlastmodifiedPK(type, squareId) );
			isUpdate = false;
		}
		
		lastmodified.setLastmodified( new Date());
		
		super.getHibernateTemplate().saveOrUpdate( lastmodified );
		
		if( log.isInfoEnabled() ) {
			if( isUpdate ) {
				log.info("updateLastmodified successfully. : type=" + type + " ,squareId=" + squareId);
			} else {
				log.info("insertLastmodified successfully. : type=" + type + " ,squareId=" + squareId);
			}
		}
	}

	public String getLastmodified(String type, String squareId){
		I18nlastmodified lastModified = findI18nlastmodified( type, squareId );
		if( lastModified == null )
			return null;
		
		return new SimpleDateFormat( TIMESTAMP_FORMAT ).format( lastModified.getLastmodified());
	}
	public I18nlastmodified findI18nlastmodified( String type, String squareId ) {
		//select * from ${schema}.i18nlastmodified where type = ?
		String queryString = "from I18nlastmodified where Id.Id = ? and Id.Squareid = ?";
		
		List result = super.getHibernateTemplate().find( queryString,
				new Object[] { type, squareId });
		if (log.isInfoEnabled())
			log.info("selectByType successfully. : type=" + type + " ,squareId=" + squareId);
		
		if( result.isEmpty() )
			return null;
		
		return ( I18nlastmodified )result.get(0);
	}
	public List findI18n( String type, String country, String lang, String squareId ) {
		//select * from ${schema}.i18n where type = ? and country = ? and lang = ? order by number
		String queryString = "from I18n where Id.Type = ? and Id.Country = ? and Id.Lang = ? and Id.Squareid = ? order by Id.Id";
		
		List result = getHibernateTemplateLimited().find( queryString,
				new Object[] { type,country,lang,squareId } );
		if (log.isInfoEnabled())
			log.info("getResourceMap successfully : country=" + country+ ",lang=" + lang + " ,squareId=" + squareId );
		
		return result;
	}
	
	public void deleteI18NByLocale( String type, String country, String lang, String squareId ) {
		String queryString = "delete from I18n where Id.Type = ? and Id.Country = ? and Id.Lang = ? and Id.Squareid = ?";
		super.getHibernateTemplate().bulkUpdate( queryString,
				new Object[]{ type, country, lang, squareId } );
		
		if (log.isInfoEnabled())
			log.info("deleteI18NByLocale successfully. : type=" + type + " country=" + country + " ,lang=" + lang + " ,squareId=" + squareId);
	}
	
	public void deleteI18NLocale( String type, String country, String lang, String squareId) {
		String queryString = "delete from I18nlocale where Type = ? and Country = ? and Lang = ? and Squareid = ?";
		
		super.getHibernateTemplate().bulkUpdate( queryString,
				new Object[]{ type, country, lang, squareId } );
		
		if (log.isInfoEnabled())
			log.info("deleteI18NLocale successfully. : type=" + type + " ,squareId=" + squareId);
	}
	
	public static void main(String[] args) {
		List list = newInstance().selectAll("default");
		Iterator ite = list.iterator();
		while(ite.hasNext()){
			System.out.println(ite.next());
		}
	}
}
