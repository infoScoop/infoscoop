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

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.infoscoop.dao.model.GlobalPreference;
import org.infoscoop.util.SpringUtil;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class GlobalPreferenceDAO extends HibernateDaoSupport {
	private static Log log = LogFactory.getLog(GlobalPreferenceDAO.class);

	public static GlobalPreferenceDAO newInstance() {
		return (GlobalPreferenceDAO) SpringUtil.getContext().getBean("globalPreferenceDAO");
	}

	//get by uid and name
	@SuppressWarnings("unchecked")
	public GlobalPreference getByUidKey(String uid, String name) {
		List<GlobalPreference> list = super.getHibernateTemplate().findByCriteria(DetachedCriteria.forClass(GlobalPreference.class)
				.add(Expression.eq("Uid", uid))
				.add(Expression.eq("Name", name)));
		
		if(list.size() > 0)
			return list.get(0);
		
		return null;
	}

	//get by uid
	@SuppressWarnings("unchecked")
	public List<GlobalPreference> getByUid(String uid) {
		List<GlobalPreference> list = super.getHibernateTemplate().findByCriteria(DetachedCriteria.forClass(GlobalPreference.class)
				.add(Expression.eq("Uid", uid)));
		
		return list;
	}

	//insert
	public void insert(String uid, String name, String value) {
		GlobalPreference entity = new GlobalPreference(uid, name, value);
		entity.setId(null);
		super.getHibernateTemplate().save(entity);
	}

	//update
	public void update (GlobalPreference entity) {
		if(entity != null)
			super.getHibernateTemplate().update(entity);
	}
}