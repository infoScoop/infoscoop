package org.infoscoop.dao;

import java.io.IOException;
import java.util.List;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.dao.model.Portallayout;
import org.infoscoop.util.SpringUtil;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class PortalLayoutDAO extends HibernateDaoSupport{
	
	private static Log log = LogFactory.getLog(PortalLayoutDAO.class);

	public static PortalLayoutDAO newInstance() {
        return (PortalLayoutDAO)SpringUtil.getContext().getBean("portalLayoutDAO");
	}
		
	/**
	 * Get the data.
	 * 
	 * @return List<Portallaytou>
	 * @throws DataResourceException
	 * @throws IOException 
	 */
	public List select()  {
		return super.getHibernateTemplate().loadAll(Portallayout.class);
		
	}
	
	/**
	 * Get the layout of an appointed name.
	 * @param name name of layout
	 * @return 
	 */
	public Portallayout selectByName(String name){
		List portalLayouts = super.getHibernateTemplate().find(
				"from Portallayout where name=?", name);
		if (portalLayouts.isEmpty())
			return null;
		return (Portallayout) portalLayouts.get(0);
	}
	
	/**
	 * Update the data.
	 * 
	 * @param layout
	 * @throws DataResourceException
	 */
	public void update(Portallayout layout) {
		super.getHibernateTemplate().update(layout);

		if(log.isInfoEnabled())
				log.info("param[]: update successfully.");
	}
	
}