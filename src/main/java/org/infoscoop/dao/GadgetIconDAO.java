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
import java.util.regex.Pattern;


import org.hibernate.criterion.DetachedCriteria;
import org.infoscoop.dao.model.GadgetIcon;
import org.infoscoop.util.SpringUtil;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class GadgetIconDAO extends HibernateDaoSupport {
	public static Pattern REGEX_NAME = Pattern.compile("^[\\w-.]+");
	public static Pattern REGEX_PATH = Pattern.compile("^(?:/[\\w-.]*)+");

	public static GadgetIconDAO newInstance() {
		return (GadgetIconDAO) SpringUtil.getContext().getBean("gadgetIconDAO");
	}

	public void insertUpdate(String type, String url) {
		GadgetIcon icon = (GadgetIcon) super.getHibernateTemplate().get(
				GadgetIcon.class, type);
		if (icon == null) {
			icon = new GadgetIcon(type, url);
		} else {
			icon.setUrl(url);
		}
		super.getHibernateTemplate().saveOrUpdate(icon);
	}
	
	public void deleteByType(String type) {
		GadgetIcon icon = (GadgetIcon) super.getHibernateTemplate().get(
				GadgetIcon.class, type);
		if (icon != null)
			super.getHibernateTemplate().delete(icon);
	}

	public List<GadgetIcon> all() {
		return super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(GadgetIcon.class));
	}
}
