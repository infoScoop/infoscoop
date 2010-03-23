package org.infoscoop.dao;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.dao.model.MENUCACHEPK;
import org.infoscoop.dao.model.MenuCache;
import org.infoscoop.util.Crypt;
import org.infoscoop.util.SpringUtil;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class MenuCacheDAO  extends HibernateDaoSupport {
	
    private static Log log = LogFactory.getLog(MenuCacheDAO.class);

	public static MenuCacheDAO newInstance() {
        return (MenuCacheDAO)SpringUtil.getContext().getBean("menuCacheDAO");
	}
	
    public void insertOrUpdate(MenuCache cache){
		super.getHibernateTemplate().saveOrUpdate( cache );
	}
    
    public MenuCache get(String uid, String url){
    	if(log.isInfoEnabled()){
    		log.info("getCache for uid: " + uid
                + ", url: " + url + ".");
    	}
    	if (uid == null)
			throw new RuntimeException("uid must be set.");
    	
    	String url_key = Crypt.getHash(url);
    	
    	MenuCache cache = ( MenuCache )super.getHibernateTemplate().get(
    			MenuCache.class,new MENUCACHEPK( url_key,uid ) );
    	
    	return cache;
    }

}
