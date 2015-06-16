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

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.dao.model.RestrictionKey;
import org.infoscoop.util.SpringUtil;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class RestrictionKeyDAO extends HibernateDaoSupport {
	private static Log log = LogFactory.getLog(RestrictionKeyDAO.class);
	
	public static RestrictionKeyDAO newInstance() {
		return (RestrictionKeyDAO) SpringUtil.getContext().getBean("restrictionKeyDAO");
	}
	
	public RestrictionKey getById(String id){
		return super.getHibernateTemplate().get(RestrictionKey.class, id);
	}
	
	public void save(String id, Date expired, String uid){
		RestrictionKey entity = new RestrictionKey(id, expired, uid);
		super.getHibernateTemplate().save(entity);
	}
	
	public void delete(RestrictionKey entity){
		super.getHibernateTemplate().delete(entity);
	}
}
