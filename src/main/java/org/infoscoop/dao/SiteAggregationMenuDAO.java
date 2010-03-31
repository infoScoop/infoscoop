package org.infoscoop.dao;


import org.infoscoop.dao.model.Menu;
import org.infoscoop.util.SpringUtil;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class SiteAggregationMenuDAO extends HibernateDaoSupport {
    public static SiteAggregationMenuDAO newInstance() {
		return (SiteAggregationMenuDAO) SpringUtil.getContext().getBean(
				"siteAggregationMenuDAO");
	}

	/**
	 * get the data
	 * 
	 * @param tempFlag
	 * @return
	 * @throws DataResourceException
	 */
	public Menu select(String menuType) {
		return (Menu) super.getHibernateTemplate().get(
				Menu.class,
				menuType);
	}

	/*
	public void delete(Menu entity){
		super.getHibernateTemplate().delete(entity);
	}
	*/
	
	/**
	 * update the data
	 * 
	 * @param menuXml
	 * @param tempFlag
	 * @throws DataResourceException
	 */
	public void update(Menu entity) {
		super.getHibernateTemplate().saveOrUpdate(entity);
		
	}
	
}