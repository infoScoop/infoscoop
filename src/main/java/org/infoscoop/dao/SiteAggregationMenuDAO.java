package org.infoscoop.dao;


import org.infoscoop.dao.model.Siteaggregationmenu;
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
	public Siteaggregationmenu select(String menuType) {
		return (Siteaggregationmenu) super.getHibernateTemplate().get(
				Siteaggregationmenu.class,
				menuType);
	}

	/*
	public void delete(Siteaggregationmenu entity){
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
	public void update(Siteaggregationmenu entity) {
		super.getHibernateTemplate().saveOrUpdate(entity);
		
	}
	
}