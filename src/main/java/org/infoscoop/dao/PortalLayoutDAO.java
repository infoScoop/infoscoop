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
		return (Portallayout) super.getHibernateTemplate().get(Portallayout.class, name);

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
