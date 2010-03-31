package org.infoscoop.service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.dao.ForbiddenURLDAO;
import org.infoscoop.dao.model.Forbiddenurl;
import org.infoscoop.util.SpringUtil;
import org.json.JSONObject;

public class ForbiddenURLService {
	private static Log log = LogFactory.getLog(ForbiddenURLService.class);

	private ForbiddenURLDAO forbiddenUrlDAO;
	 
	/**
	 * @param forbiddenUrlsDAO
	 */
	public void setForbiddenURLDAO(ForbiddenURLDAO forbiddenUrlsDAO) {
		this.forbiddenUrlDAO = forbiddenUrlsDAO;
	}

	/**
	 * @return
	 */
	public static ForbiddenURLService getHandle() {
		return (ForbiddenURLService) SpringUtil.getBean("ForbiddenURLService");
	}

	public void updateForbiddenURLs( Map forbiddenUrls ){
		Collection oldForbiddenUrls = this.forbiddenUrlDAO.getForbiddenUrls();
		Set oldIds = new HashSet();
		for( Iterator it = oldForbiddenUrls.iterator(); it.hasNext();){
			Forbiddenurl forbiddenurl = (Forbiddenurl)it.next();
			String id = forbiddenurl.getId().toString();
			oldIds.add(id);

			if(forbiddenUrls.containsKey(id)){
				String url = ( String )(( Map )forbiddenUrls.get(id)).get("url");
				if(log.isInfoEnabled())log.info("Update forbiddenurl: id=" + id + ", url=" + forbiddenurl.getUrl());
				forbiddenurl.setUrl(url);
				this.forbiddenUrlDAO.update(forbiddenurl);
			}else{
				if(log.isInfoEnabled())log.info("Delete forbiddenurl: id=" + id + ", url=" + forbiddenurl.getUrl());
				this.forbiddenUrlDAO.delete(forbiddenurl);
			}

		}

		for(Iterator it = forbiddenUrls.keySet().iterator(); it.hasNext();){
			String id = (String)it.next();
			if(!oldIds.contains(id)){
				Forbiddenurl forbiddenurl = new Forbiddenurl();
				String url = ( String )(( Map )forbiddenUrls.get(id)).get("url");
				if(log.isInfoEnabled())log.info("Insert forbiddenurl: id=" + id + ", url=" + url);

				forbiddenurl.setUrl(url);

				this.forbiddenUrlDAO.insert(forbiddenurl);
			}
		}
	
	}
	
	public String getForbiddenURLsJSON(){
		StringBuffer buf = new StringBuffer();
		buf.append("{\n");
		for( java.util.Iterator forbiddenURLs=this.forbiddenUrlDAO.getForbiddenUrls().iterator();forbiddenURLs.hasNext();) {
			Forbiddenurl forbiddenURL = ( Forbiddenurl )( forbiddenURLs.next());
			int id = forbiddenURL.getId().intValue();
			String url = forbiddenURL.getUrl();

			buf.append("'").append( id ).append("':");
			buf.append("{\n");
			buf.append("id:").append( id ).append(",\n");
			buf.append("url:").append( JSONObject.quote(url) ).append("\n");
			
			buf.append("}");
			
			if( forbiddenURLs.hasNext())
				buf.append(",\n");
		}
		buf.append("}");
		return buf.toString();
	}

	public static void main(String args[]){
		getHandle().getForbiddenURLsJSON();
	}
}
