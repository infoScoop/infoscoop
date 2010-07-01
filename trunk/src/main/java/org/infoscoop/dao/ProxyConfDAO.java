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

import java.text.SimpleDateFormat;
import java.util.Date;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.dao.model.Proxyconf;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class ProxyConfDAO extends HibernateDaoSupport {
	public static final int PROXYCONF_FLAG_TEMP = 1;

	public static final int PROXYCONF_FLAG_NOT_TEMP = 0;

	private static Log log = LogFactory.getLog(ProxyConfDAO.class);

	/**
	 * Get the data.
	 * 
	 * @param res
	 * @param tempFlag
	 * @return
	 * @throws DataResourceException
	 */
	public Proxyconf select(int tempFlag){
		return (Proxyconf) super.getHibernateTemplate().get(Proxyconf.class, new Integer(tempFlag));		
	}

	public Proxyconf select() {
		return select(PROXYCONF_FLAG_TEMP);
	}

	/**
	 * Update the data.
	 * 
	 * @param res
	 * @param node
	 * @param tempFlag
	 * @throws DataResourceException
	 */
	public void update(Proxyconf entity){
		
		entity.setLastmodified(new Date());
		
		super.getHibernateTemplate().saveOrUpdate(entity);
	}

	/**
	 * Get the update date.
	 * 
	 * @param res
	 * @param tempFlag
	 * @return
	 * @throws DataResourceException
	 */
	public String selectLastModified(int tempFlag){
		Proxyconf entity = (Proxyconf)super.getHibernateTemplate().load(Proxyconf.class, new Integer(tempFlag));
		
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(entity.getLastmodified());
		
	}

}
