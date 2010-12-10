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
import org.infoscoop.util.SpringUtil;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class TabTemplatePersonalizeGadgetDAO extends HibernateDaoSupport {

	private static Log log = LogFactory
			.getLog(TabTemplatePersonalizeGadgetDAO.class);

	public static TabTemplatePersonalizeGadgetDAO newInstance() {
		return (TabTemplatePersonalizeGadgetDAO) SpringUtil.getContext()
				.getBean("tabTemplatePersonalizeGadgetDAO");
	}

	public void deleteByGadgetInstanceId(Integer gadgetInstanceId) {
		Query query = super
				.getSession()
				.createQuery(
						"delete from TabTemplatePersonalizeGadget where fk_gadget_instance_id=:id");
		query.setParameter("id", gadgetInstanceId);
		query.executeUpdate();
	}

	public void deleteByTabTemplateId(Integer tabTemplateId) {
		Query query = super
				.getSession()
				.createQuery(
						"delete from TabTemplatePersonalizeGadget where fk_tabtemplate_id=:id");
		query.setParameter("id", tabTemplateId);
		query.executeUpdate();
	}
}
