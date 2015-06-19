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
import org.infoscoop.dao.model.Searchengine;
import org.infoscoop.dao.model.SearchenginePK;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class SearchEngineDAO extends HibernateDaoSupport{
	public static final int SEARCHENGINE_FLAG_TEMP = 1;
	public static final int SEARCHENGINE_FLAG_NOT_TEMP = 0;

	private static Log log = LogFactory.getLog(SearchEngineDAO.class);
	
	private SearchEngineDAO(){
		
	}

	/**
	 * Get the data.
	 * 
	 * @param res
	 * @return
	 * @throws DataResourceException
	 */
	public Searchengine select(int tempFlag, String squareId) {
		return (Searchengine) super.getHibernateTemplate().get(Searchengine.class, new SearchenginePK(new Integer(tempFlag), squareId));
	}
	
	public Searchengine selectTemp(String squareId) {
		return (Searchengine)select(SEARCHENGINE_FLAG_TEMP, squareId);
	}
	public Searchengine selectEntity(String squareId) {
		return (Searchengine)select(SEARCHENGINE_FLAG_NOT_TEMP, squareId);
	}
	
	/**
	 * Update the data.
	 * 
	 * @param res
	 * @param node
	 * @throws DataResourceException
	 */
	public void update(Searchengine entity) {
		super.getHibernateTemplate().saveOrUpdate(entity);
	}
	
	public void copySquare(String squareId, String defaultSquareId) {
		Session session  = super.getSession();

		Query q = (Query)session.getNamedQuery("is_searchengines.copySquare");
		q.setString("squareId", squareId);
		q.setString("defaultSquareId", defaultSquareId);
		q.executeUpdate();
	}
}
