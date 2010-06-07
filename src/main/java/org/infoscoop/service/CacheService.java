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

package org.infoscoop.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;


import org.apache.commons.codec.binary.Base64;
import org.infoscoop.dao.CacheDAO;
import org.infoscoop.dao.model.Cache;
import org.infoscoop.util.SpringUtil;
import org.w3c.util.UUID;

public class CacheService {
	public static String PUBLIC_CACHE_USERID = "Public User";

	private CacheDAO cacheDAO;

	public static CacheService getHandle() {
		return (CacheService) SpringUtil.getBean("CacheService");
	}

	public void setCacheDAO(CacheDAO cacheDAO) {
		this.cacheDAO = cacheDAO;
	}

	public Cache getCacheByUrl(String url){
		return getCacheByUrl(PUBLIC_CACHE_USERID, url);
	}

	public Cache getCacheByUrl(String uid, String url){
		return cacheDAO.getCacheByURL(PUBLIC_CACHE_USERID, url);
	}

	public Cache getCacheById(String id){
		return cacheDAO.getCacheById(id);
	}

	public String insertCache(String id, String uid,
			String url,InputStream body,Map<String, List<String>> headers){
		return cacheDAO.insertCache(id, uid, url, body, headers);
	}

	/**
	 * insert private cache
	 *
	 * @param uid
	 * @param url
	 * @param body
	 * @param responseHeaders
	 * @return
	 * @throws DataResourceException
	 */
	public String insertCache(String uid, String url,
			InputStream body, Map<String, List<String>> headers){
		String id = new UUID().toString();
		return insertCache(id, uid, url, body, headers);
	}

	/**
	 * insert acache of public data
	 * @param url
	 * @param responseStream
	 * @param headersMap
	 * @return
	 */
	public String insertCache(String url, InputStream responseStream,
			Map<String, List<String>> headersMap) {
		return insertCache(PUBLIC_CACHE_USERID, url, responseStream, headersMap);
	}

	public void deleteCacheById(String id){
		cacheDAO.deleteCacheById(id);
	}

	public void deleteUserCache(String uid){
		cacheDAO.deleteCacheByUid(uid);
	}

	public void deleteCacheByUrl(String url){
		cacheDAO.deleteCacheByUrl(PUBLIC_CACHE_USERID, url);
	}
	
	public void deleteOldPublicCaches(){
		List list = cacheDAO.getCaches(PUBLIC_CACHE_USERID);
		long currentTime = new Date().getTime();
		for(java.util.Iterator it= list.iterator();it.hasNext();){
			Cache cache = (Cache)it.next();
			if( currentTime - cache.getTimestamp().getTime() > 86400000 ){
				cacheDAO.deleteCacheById(cache.getId());
			}
		}
	}
	
	public String insertUpdateCache(String url, ByteArrayInputStream body, Map<String, List<String>> headers) {
		return this.insertCache(PUBLIC_CACHE_USERID, url, body, headers);
	}

	public String insertUpdateCache(String uid, String url, ByteArrayInputStream body, Map<String, List<String>> headers) {
		Cache cache = getCacheByUrl(url);
		if(cache == null){
			String id = new UUID().toString();
			return insertCache(id, uid, url, body, headers);
		}else{
			cache.setBody(new String(Base64.encodeBase64( readBytes( body ))));
			cache.setHeaders(CacheDAO.makeHeaderXml(headers));
			cache.setTimestamp( new Date() );

			return cache.getId();
		}
	}

	//Copy from CacheDAO
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

}
