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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.infoscoop.dao.model.Logo;
import org.infoscoop.util.SpringUtil;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.util.List;

public class LogoDAO extends HibernateDaoSupport {
	private static Log log = LogFactory.getLog(LogoDAO.class);

	public static LogoDAO newInstance() {
		return (LogoDAO) SpringUtil.getContext().getBean("logoDAO");
	}

	//get
	public Logo getBySquareIdAndKind(String squareId, String kind) {
		List<Logo> logoList = super.getHibernateTemplate()
				.findByCriteria(DetachedCriteria.forClass(Logo.class)
						.add(Expression.eq(Logo.PROP_SQUAREID, squareId))
						.add(Expression.eq(Logo.PROP_KIND, kind)));

		if(logoList != null && logoList.size() > 0) {
			return logoList.get(0);
		} else {
			return null;
		}
	}

	//insert
	public void insert(String squareId, byte[] logo, String type, String kind) {
		// initial temp true
		Logo l = new Logo(squareId, logo, type, kind);
		super.getHibernateTemplate().save(l);
	}

	//update
	public void update (Logo logo) {
		if(logo != null)
			super.getHibernateTemplate().update(logo);
	}

	public void copySquare(String squareId, String defaultSquareId) {
		Session session = super.getSession();
		Query sq = session.getNamedQuery("is_logos.copySquare");
		sq.setString("squareId", squareId);
		sq.setString("defaultSquareId", defaultSquareId);
		sq.executeUpdate();
	}

	// delete
	public int deleteBySquareId(String squareid) {
		String queryString = "delete from Logo where Squareid = ?";

		return super.getHibernateTemplate().bulkUpdate( queryString,
				new Object[] { squareid } );
	}
}