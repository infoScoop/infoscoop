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
import org.infoscoop.dao.model.I18NPK;
import org.infoscoop.dao.model.I18n;
import org.infoscoop.dao.model.I18nlastmodified;
import org.infoscoop.dao.model.I18nlocale;
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

	public void insertLocale(String type,
			String country, String lang) {
		//insert into ${schema}.i18nLocale(type, country, lang) values (?, ?, ?)
		
		super.getHibernateTemplate().save( new I18nlocale( null,type,country,lang ));
		
		if (log.isInfoEnabled())
			log.info("update successfully. : country=" + country
					+ ", lang=" + lang);
	}

	public void deleteByType(String type){
		deleteI18NByType( type );
		deleteI18NLocaleByType( type );
	}
	private void deleteI18NByType( String type ) {
		//delete from ${schema}.i18n where type = ?
		String queryString = "delete from I18n where Id.Type = ?";
		
		super.getHibernateTemplate().bulkUpdate( queryString,
				new Object[]{ type } );
		
		if (log.isInfoEnabled())
			log.info("deleteByType successfully. : type=" + type);
	}
	private void deleteI18NLocaleByType( String type ) {
		//delete from ${schema}.i18nLocale where type = ?
		String queryString = "delete from I18nlocale where Type = ?";
		
		super.getHibernateTemplate().bulkUpdate( queryString,
				new Object[]{ type } );
		
		if (log.isInfoEnabled())
			log.info("deleteI18NLocaleByType successfully. : type=" + type);
	}
	
	/**
	 * Delete the data of i18n except the locale of "ALL_ALL" by appointing the type and ID.
	 * @param type
	 * @param id
	 */
	public void deleteI18NByIDWithoutDefault( String type, String id ) {
		//delete from ${schema}.i18nLocale where type = ?
		String queryString = "delete from I18n where Id.Type = ? and Id.Id = ? and not(Id.Country = ? and Id.Lang = ?)";
		
		super.getHibernateTemplate().bulkUpdate( queryString,
				new Object[]{ type, id, "ALL", "ALL" } );
		
		if (log.isInfoEnabled())
			log.info("deleteI18NByIDWithoutDefault successfully. : type=" + type + " ,id=" + id);
	}

	/**
	 * Get the data.
	 * 
	 * @param res
	 * @return
	 * @throws DataResourceException
	 */
	public List selectAll() {
		//select * from ${schema}.i18n order by type, number, id, country, lang
		String queryString = "from I18n order by Id.Type,Number,Id.Id,Id.Country,Id.Lang";
		
		return getHibernateTemplateLimited().find( queryString );
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
	public List getIdListByLocale(final String type, final String country, final String lang){
		return (List)getHibernateTemplateLimited().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				
				Criteria cri = session.createCriteria(I18n.class);
				
				SimpleExpression se = Expression.eq("Id.Type", type);
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
	 * 
	 * @param res
	 * @return
	 * @throws DataResourceException
	 */
	public List selectByType(String type){
		//select * from ${schema}.i18n where type = ? order by number, id, country, lang
		String queryString = "from I18n where Id.Type = ? order by Id.Type,Number,Id.Id,Id.Country,Id.Lang";
		
		List result = getHibernateTemplateLimited().find( queryString,
				new Object[] { type });
		if (log.isInfoEnabled())
			log.info("selectByType successfully. : type=" + type);
		
		return result;
	}

	public List selectLocales() {
		String queryString = "from I18nlocale order by Type,Country,Lang";

		List result = super.getHibernateTemplate().find(queryString);
		if (log.isInfoEnabled())
			log.info("selectLocales successfully");
		
		return result;
	}

	public List selectLocales(String type) {
		//select * from ${schema}.i18nLocale where type = ? order by country, lang
		String queryString = "from I18nlocale where Type = ? order by Country,Lang";

		List result = super.getHibernateTemplate().find( queryString,
				new Object[] { type });
		if (log.isInfoEnabled())
			log.info("selectLocales successfully. : type=" + type);
		
		return result;
	}

	public void updateLastmodified(String type){
		//update ${schema}.i18nlastmodified set lastmodified = ? where type = ?
		//insert into ${schema}.i18nlastmodified(lastmodified, type) values (?, ?)
		
		I18nlastmodified lastmodified = ( I18nlastmodified )super.getHibernateTemplate().get(
				I18nlastmodified.class,type );
		boolean isUpdate = true;
		if( lastmodified == null ) {
			lastmodified = new I18nlastmodified( type );
			isUpdate = false;
		}
		
		lastmodified.setLastmodified( new Date());
		
		super.getHibernateTemplate().saveOrUpdate( lastmodified );
		
		if( log.isInfoEnabled() ) {
			if( isUpdate ) {
				log.info("updateLastmodified successfully. : type=" + type);
			} else {
				log.info("insertLastmodified successfully. : type=" + type);
			}
		}
	}

	public String getLastmodified(String type){
		I18nlastmodified lastModified = findI18nlastmodified( type );
		if( lastModified == null )
			return null;
		
		return new SimpleDateFormat( TIMESTAMP_FORMAT ).format( lastModified.getLastmodified());
	}
	public I18nlastmodified findI18nlastmodified( String type ) {
		//select * from ${schema}.i18nlastmodified where type = ?
		String queryString = "from I18nlastmodified where Id = ?";
		
		List result = super.getHibernateTemplate().find( queryString,
				new Object[] { type });
		if (log.isInfoEnabled())
			log.info("selectByType successfully. : type=" + type);
		
		if( result.isEmpty() )
			return null;
		
		return ( I18nlastmodified )result.get(0);
	}
	public List findI18n( String type,String country,String lang ) {
		//select * from ${schema}.i18n where type = ? and country = ? and lang = ? order by number
		String queryString = "from I18n where Id.Type = ? and Id.Country = ? and Id.Lang = ? order by Id.Id";
		
		List result = getHibernateTemplateLimited().find( queryString,
				new Object[] { type,country,lang } );
		if (log.isInfoEnabled())
			log.info("getResourceMap successfully : country=" + country+ ",lang=" + lang );
		
		return result;
	}
	
	public void deleteI18NByLocale( String type, String country, String lang ) {
		String queryString = "delete from I18n where Type = ? and Country = ? and Lang = ?";
		super.getHibernateTemplate().bulkUpdate( queryString,
				new Object[]{ type, country, lang } );
		
		if (log.isInfoEnabled())
			log.info("deleteI18NByLocale successfully. : type=" + type + " country=" + country + " lang=" + lang);
	}
	
	public void deleteI18NLocale( String type, String country, String lang ) {
		String queryString = "delete from I18nlocale where Type = ? and Country = ? and Lang = ?";
		
		super.getHibernateTemplate().bulkUpdate( queryString,
				new Object[]{ type, country, lang } );
		
		if (log.isInfoEnabled())
			log.info("deleteI18NLocale successfully. : type=" + type);
	}
	
	public static void main(String[] args) {
		List list = newInstance().selectAll();
		Iterator ite = list.iterator();
		while(ite.hasNext()){
			System.out.println(ite.next());
		}
	}
}