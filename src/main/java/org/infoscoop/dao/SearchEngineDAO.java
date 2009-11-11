package org.infoscoop.dao;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.dao.model.Searchengine;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class SearchEngineDAO extends HibernateDaoSupport{
	public static final int SEARCHENGINE_FLAG_TEMP = 1;
	public static final int SEARCHENGINE_FLAG_NOT_TEMP = 0;

	private static Log log = LogFactory.getLog(SearchEngineDAO.class);
	
	private SearchEngineDAO(){
		
	}

	/**
	 * Get the data.
	 * 
	 * @param res
	 * @return
	 * @throws DataResourceException
	 */
	public Searchengine select(int tempFlag) {
		return (Searchengine) super.getHibernateTemplate().get(Searchengine.class, new Integer(tempFlag));
	}
	
	public Searchengine selectTemp() {
		return (Searchengine)select(SEARCHENGINE_FLAG_TEMP);
	}
	public Searchengine selectEntity() {
		return (Searchengine)select(SEARCHENGINE_FLAG_NOT_TEMP);
	}
	
	/**
	 * Update the data.
	 * 
	 * @param res
	 * @param node
	 * @throws DataResourceException
	 */
	public void update(Searchengine entity) {
		super.getHibernateTemplate().saveOrUpdate(entity);
	}
	
}