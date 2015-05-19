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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.infoscoop.dao.model.RSSCACHEPK;
import org.infoscoop.dao.model.Rsscache;
import org.infoscoop.util.Crypt;
import org.infoscoop.util.SpringUtil;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class RssCacheDAO  extends HibernateDaoSupport {
	
    private static Log log = LogFactory.getLog(RssCacheDAO.class);

	public static RssCacheDAO newInstance() {
        return (RssCacheDAO)SpringUtil.getContext().getBean("rssCacheDAO");
	}

    public void insertCache(String uid, String url, int pageNumber, String data, String squareId){
		try {
			insertCache( uid,url,pageNumber,data.getBytes("UTF-8"),squareId );
		} catch (UnsupportedEncodingException e) {
			log.error("", e);
		}
	}

    public void insertCache(String uid, String url, int pageNumber, byte[] data, String squareId ) {
    	if(log.isInfoEnabled()){
    		log.info("insertRssCache for uid: " + uid
                + ", url: " + url + ", pageNumber: " + pageNumber
                + ", squareId: " + squareId + ".");
    	}
    	if (uid == null)
			throw new RuntimeException("uid must be set.");

    	String url_key = Crypt.getHash(url);

		Rsscache cache = new Rsscache( new RSSCACHEPK( url_key,uid,new Integer(pageNumber),squareId ));
		cache.setRss( data );

		super.getHibernateTemplate().saveOrUpdate( cache );

        if(log.isInfoEnabled()){
        	log.info("url" + url + "]: Insert XML successfully.");
        }
    }

    public void deleteUserCache( String uid, String squareId ) {
    	if(log.isInfoEnabled()){
    		log.info("deleteCache by uid = " + uid);
    	}

    	//delete from ${schema}.rsscache where uid = ?
    	String queryString = "delete from Rsscache where Id.Uid = ? and Id.Squareid = ?";
    	
    	super.getHibernateTemplate().bulkUpdate( queryString,
    			new Object[] { uid, squareId });

        if(log.isInfoEnabled())
        	log.info("deleteCacheByUid [ "+uid+"] successfully.");
    }

    public void deleteCacheByUrl( String uid, String url, String squareId ) {
    	if(log.isInfoEnabled()){
    		log.info("deleteCache by uid ");
    	}
    	String url_key = Crypt.getHash(url);

    	//delete from ${schema}.rsscache where uid = ? and url_key = ?
    	String queryString = "delete from Rsscache where Id.Uid = ? and Id.UrlKey = ? and Id.Squareid = ?";

    	super.getHibernateTemplate().bulkUpdate( queryString,
    			new Object[] { uid, url_key, squareId } );

        if(log.isInfoEnabled())
        	log.info("deleteCacheByUid [ "+uid+"] successfully.");
    }
    
    public InputStream getCache(String uid, String url, int pageNumber, String squareId){
    	if(log.isInfoEnabled()){
    		log.info("getCache for uid: " + uid
                + ", url: " + url + ", pageNumber: " + pageNumber
				+ ", squareId: " + squareId + ".");
    	}
    	if (uid == null)
			throw new RuntimeException("uid must be set.");
    	
    	String url_key = Crypt.getHash(url);
    	
    	Rsscache cache = ( Rsscache )super.getHibernateTemplate().get(
    			Rsscache.class,new RSSCACHEPK( url_key,uid,new Integer( pageNumber ),squareId) );
    	if( cache == null )
    		return null;
    	
    	return new ByteArrayInputStream( cache.getRss() );
    }

	public List getCaches(String uid, String url, String squareId) {
		if(log.isInfoEnabled()){
    		log.info("getCaches for uid: " + uid
                + ", url: " + url + ", squareId: " + squareId + ".");
    	}
		if (uid == null)
			throw new RuntimeException("uid must be set.");
    	
    	String url_key = Crypt.getHash(url);
    	
    	List cacheList = super.getHibernateTemplate().findByCriteria(
    			DetachedCriteria.forClass(Rsscache.class).add(
				Expression.conjunction()
				.add(Expression.eq("Id.Uid", uid))
				.add(Expression.eq("Id.UrlKey", url_key))
				.add(Expression.eq("Id.Squareid", squareId))));
    	
		return cacheList;
	}
    
    public static void main(String args[]){
    	InputStream is = RssCacheDAO.newInstance().getCache("admin", "http://rss.rssad.jp/rss/itm/rss.xml", 4, "default");
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();

		try {
			byte b[] = new byte[4096];
			while (true) {
				int bytes;
				bytes = is.read(b);
				if (bytes == -1) {
					break;
				}
				baos.write(b,0,bytes);
			}
			b = baos.toByteArray();
		} catch (IOException e) {
			log.error("", e);
		}
		try {
			System.out.println(new String( baos.toByteArray(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			log.error("", e);
		}
    }
}
