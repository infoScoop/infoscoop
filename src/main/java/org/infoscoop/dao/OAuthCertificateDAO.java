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
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.infoscoop.dao.model.OAuthCertificate;
import org.infoscoop.util.SpringUtil;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class OAuthCertificateDAO extends HibernateDaoSupport {

	private static Log log = LogFactory.getLog(OAuthCertificateDAO.class);

	public static OAuthCertificateDAO newInstance() {
		return (OAuthCertificateDAO) SpringUtil.getContext().getBean(
				"oauthCertificateDAO");
	}
	
	public void save(OAuthCertificate cert, String squareid) {
		String query = "delete OAuthCertificate where Id.Squareid = ?";
		super.getHibernateTemplate().bulkUpdate(query, squareid);
		super.getHibernateTemplate().save(cert);
	}

	public OAuthCertificate get(String squareid) {
		List results = super.getHibernateTemplate()
				.findByCriteria(
						DetachedCriteria.forClass(OAuthCertificate.class)
						.add(Expression.eq("Id.Squareid", squareid)));
		
		if( results.size() == 0) return null;
		return (OAuthCertificate)results.get(0);
	}

	public int deleteBySquareId(String squareid) {
		String queryString = "delete from OAuthCertificate where Id.Squareid = ?";
		
		return super.getHibernateTemplate().bulkUpdate( queryString,
				new Object[] { squareid } );
	}
	
	public void copySquare(String squareId, String defaultSquareId) {
		Session session = super.getSession();
		Query sq = session.getNamedQuery("is_oauth_certificate.copySquare");
		sq.setString("squareId", squareId);
		sq.setString("defaultSquareId", defaultSquareId);
		sq.executeUpdate();
	}
	
	
	public void copySquareWithKeys(String squareId, String defaultSquareId) {
		Session session = super.getSession();
		Query sq = session.getNamedQuery("is_oauth_certificate.copySquareWithKeys");
		sq.setString("squareId", squareId);
		sq.setString("defaultSquareId", defaultSquareId);
		sq.executeUpdate();
	}
}
