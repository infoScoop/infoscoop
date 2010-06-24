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
import org.infoscoop.dao.model.OAuthCertificate;
import org.infoscoop.util.SpringUtil;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class OAuthCertificateDAO extends HibernateDaoSupport {

	private static Log log = LogFactory.getLog(OAuthCertificateDAO.class);

	public static OAuthCertificateDAO newInstance() {
		return (OAuthCertificateDAO) SpringUtil.getContext().getBean(
				"oauthCertificateDAO");
	}
	
	public void save(OAuthCertificate cert) {
		super.getHibernateTemplate().bulkUpdate("delete OAuthCertificate");
		super.getHibernateTemplate().save(cert);
	}

	public OAuthCertificate get() {
		List results = super.getHibernateTemplate().loadAll(OAuthCertificate.class);
		if( results.size() == 0) return null;
		return (OAuthCertificate)results.get(0);
	}

}
