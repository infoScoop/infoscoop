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


import org.hibernate.Query;
import org.hibernate.Session;
import org.infoscoop.dao.model.Forbiddenurls;
import org.infoscoop.util.SpringUtil;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class ForbiddenURLDAO extends HibernateDaoSupport {
//	private static Log log = LogFactory.getLog(ForbiddenURLDAO.class);
	
	public static ForbiddenURLDAO newInstance() {
        return (ForbiddenURLDAO)SpringUtil.getContext().getBean("forbiddenURLDAO");
	}
	
	public Collection getForbiddenUrls(String squareid) {
		String queryString = "from Forbiddenurls where Squareid = ? order by Id desc";
		
		return super.getHibernateTemplate().find( queryString, squareid );
	}
	
	public boolean isForbiddenUrl( String url, String squareid ){
		String queryString = "from Forbiddenurls where Squareid = ? Url = ?";
		
		return super.getHibernateTemplate().find( queryString,
				new Object[]{ url, squareid }).iterator().hasNext();
	}
	
	
	public void delete(Forbiddenurls forbiddenUrl) {
		super.getHibernateTemplate().delete( forbiddenUrl );
	}

	public int deleteBySquareId(String squareid) {
		String queryString = "delete from Forbiddenurls where Squareid = ?";
		return super.getHibernateTemplate().bulkUpdate( queryString,
				new Object[] { squareid } );
	}

	public void update(Forbiddenurls forbiddenUrl) {
		super.getHibernateTemplate().update( forbiddenUrl );
	}

	public void insert(Forbiddenurls forbiddenUrl) {
		super.getHibernateTemplate().save( forbiddenUrl );
		
	}

	public void copySquare(String squareId, String defaultSquareId) {
		Session session  = super.getSession();

		Query q = (Query)session.getNamedQuery("is_forbiddenurls.copySquare");
		q.setString("squareId", squareId);
		q.setString("defaultSquareId", defaultSquareId);
		q.executeUpdate();

	}
}
