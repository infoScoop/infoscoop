package org.infoscoop.dao;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;


import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.dao.model.Cache;
import org.infoscoop.util.Crypt;
import org.infoscoop.util.HtmlUtil;
import org.infoscoop.util.SpringUtil;
import org.infoscoop.util.StringUtil;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * The DAO class to get and update the information of cash.
 * 
 * @author a-kimura
 * 
 */
public class CacheDAO extends HibernateDaoSupport {
//  private static final String DEFAULT_USER = "default";
//	private static final QName NAME = new QName("name");
//	private static final QName VALUE = new QName("value");
	
	private static Log log = LogFactory.getLog(CacheDAO.class);
	
	public static CacheDAO newInstance() {
        return (CacheDAO)SpringUtil.getContext().getBean("cacheDAO");
	}

	public static String makeHeaderXml( Map<String, List<String>> headers ) {
		StringBuffer xml = new StringBuffer();
		xml.append("<headers>");
		if (headers != null) {
			for(String key : headers.keySet()){
				 
				String escapeKey = HtmlUtil.escapeHtmlEntities(key);
				for(String value : headers.get(key)){
					value = HtmlUtil.escapeHtmlEntities(value);

					xml.append("<header name=\"").append(escapeKey).append("\"");
					xml.append(" value=\"").append(value).append("\"/>");
				}
			}
		}
		xml.append("</headers>");
		return xml.toString();
	}
	
	/*
	private static Reader createBase64Stream( InputStream in ){
		return new StringReader( new String( Base64.encodeBase64( readBytes( in )) ));
	}
	*/
	private static byte[] readBytes( InputStream in ){
		byte[] bytes = new byte[8192];
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int len;
		try {
			while ((len = in.read(bytes, 0, bytes.length)) >= 0) {
				baos.write(bytes, 0, len);
			}
			baos.close();
		} catch (Exception ex) {
			throw new RuntimeException("Failed to save body.");
		}
		
		return baos.toByteArray();
	}
	
	
	/**
	 * Add the cash.
	 * 
	 * @param id
	 * @param uid
	 * @param url
	 * @param body
	 * @param responseHeaders
	 * @return
	 * @throws DataResourceException
	 */
	public String insertCache(String id, String uid,
			String url,InputStream body,Map headers){
		if (uid == null)
			throw new RuntimeException("uid must be set.");
		String url_key = Crypt.getHash(url);
		
		try {
			url = StringUtil.getTruncatedString(url, 512, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			log.error("", e);
		}
		
		Cache cache = new Cache( id,uid,url,url_key,
				new Timestamp( new Date().getTime()),
				makeHeaderXml( headers ),
				new String(Base64.encodeBase64( readBytes( body ))) );
		
		super.getHibernateTemplate().save( cache );
		
		return id;
	}

	public Cache insertUpdateCache(String id,
			String uid, String url, InputStream body,Map headers) {
		if (uid == null)
			throw new RuntimeException("uid must be set.");
		String url_key = Crypt.getHash(url);
		
		try {
			url = StringUtil.getTruncatedString(url, 1024, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			log.error("", e);
		}
		
		Cache cache = new Cache(
				id,
				uid,
				url,
				url_key,
				new Timestamp( new Date().getTime()),
				makeHeaderXml( headers ).toString(),
				new String(Base64.encodeBase64( readBytes( body )))
			);
		super.getHibernateTemplate().saveOrUpdate( cache );

		return cache;
	}

	/**
	 * Delete all the cash of a appointed UID.
	 * 
	 * @param uid
	 * @throws DataResourceException
	 * @throws IOException
	 */
	public void deleteCacheByUid(String uid) {
		//delete from ${schema}.cache where uid = ?
		String queryString = "delete from Cache where Uid = ?";
		
		super.getHibernateTemplate().bulkUpdate( queryString,
				new Object[] { uid });
	}

	/**
	 * Delete all the cash of a appointed ID.
	 * 
	 * @param uid
	 * @throws DBAccessException
	 */
	public void deleteCacheById(String id){
		Cache cache = ( Cache )super.getHibernateTemplate().get( Cache.class,id );
		if( cache == null )
			return;
		
		super.getHibernateTemplate().delete( cache );
	}

	/**
	 * Delete the cash of appointed URL.
	 * 
	 * @param uid
	 * @param url
	 */
	public void deleteCacheByUrl(String uid, String url){
		//delete from ${schema}.cache where id = ?
		String queryString = "delete from Cache where Uid = ? and UrlKey = ?";
		
		super.getHibernateTemplate().bulkUpdate( queryString,
				new Object[] { uid, Crypt.getHash(url) });
	}
	
	/**
	 * Get the DOM object of the cash of appointed ID.
	 * 
	 * @param id
	 * @return
	 */
	public Cache getCacheById(String id){
		//select * from ${schema}.cache where id=?
		return ( Cache )super.getHibernateTemplate().get( Cache.class,id );
	}

	/**
	 * Get the DOM object of the cash of appointed URL.
	 * @param uid
	 * @param url
	 * @return
	 */
	public Cache getCacheByURL(String uid, String url){
		//select * from ${schema}.cache where url_key=?
		String queryString = "from Cache where Uid = ? and UrlKey = ?";
		List results = super.getHibernateTemplate()
				.find( queryString, new Object[]{uid, Crypt.getHash(url)} );
		if(results.isEmpty()){
			return null;
		}else{
			return (Cache) results.iterator().next();
		}
	}
	
	/**
	 * Get all  of is_caches .
	 * @param uid
	 * @param url
	 * @return
	 */
	public List getCaches(String uid){
		//select * from ${schema}.cache where url_key=?
		String queryString = "from Cache where Uid = ?";
		List results = super.getHibernateTemplate()
				.find( queryString, new Object[]{uid} );
		
		return results;
		
	}
	
}
