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

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.infoscoop.dao.model.Message;
import org.infoscoop.dao.model.SystemMessage;
import org.infoscoop.util.SpringUtil;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class SystemMessageDAO extends HibernateDaoSupport {
	private static Log log = LogFactory.getLog(SystemMessageDAO.class);

	public static SystemMessageDAO newInstance() {
		return (SystemMessageDAO) SpringUtil.getContext().getBean("systemMessageDAO");
	}

	public void insert(SystemMessage msg) {
		super.getHibernateTemplate().save(msg);
	}

	public Collection<SystemMessage> selectByToAndNoRead(String uid) {
		return super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(SystemMessage.class).add(
						Expression.and(
								Expression.eq(SystemMessage.PROP_TO, uid),
								Expression.eq(SystemMessage.PROP_ISREAD, Integer.valueOf(0)))).addOrder(
										Order.desc(SystemMessage.PROP_ID)));
	}

}
