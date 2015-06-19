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
import org.infoscoop.dao.model.Square;
import org.infoscoop.util.SpringUtil;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.util.Date;

/**
 * The DAO class to get and update the information of square.
 */
public class SquareDAO extends HibernateDaoSupport {
	
	private static Log log = LogFactory.getLog(SquareDAO.class);
	
	public static SquareDAO newInstance() {
        return (SquareDAO)SpringUtil.getContext().getBean("squareDAO");
	}
	
	public Square get(String id){
		return ( Square )super.getHibernateTemplate().get( Square.class, id );
	}

	public void create(String id, String name, String description) {
		Square square = new Square(id);
		square.setName(name);
		square.setDescription(description);
		square.setLastmodified(new Date());

		super.getHibernateTemplate().save(square);
	}
}
