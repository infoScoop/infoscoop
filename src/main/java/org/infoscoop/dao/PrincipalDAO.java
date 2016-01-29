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

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.infoscoop.dao.model.AccountAttr;
import org.infoscoop.dao.model.Principal;
import org.infoscoop.util.SpringUtil;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class PrincipalDAO extends HibernateDaoSupport {

	public static PrincipalDAO newInstance() {
		return (PrincipalDAO) SpringUtil.getBean("principalDAO");
	}

	public List<Principal> getBySquareId(String squareId){
		@SuppressWarnings("unchecked")
		List<Principal> result = super.getHibernateTemplate().findByCriteria(DetachedCriteria.forClass(Principal.class)
				.add(Expression.eq(Principal.PROP_SQUARE_ID, squareId)));

		return result;
	}

}
