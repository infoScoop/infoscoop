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

package org.infoscoop.api.dao;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.infoscoop.api.dao.model.OAuth2ProviderClientDetail;
import org.infoscoop.api.dao.model.OAuth2ProviderClientDetailPK;
import org.infoscoop.util.SpringUtil;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class OAuth2ProviderClientDetailDAO extends HibernateDaoSupport {
	private static Log log = LogFactory.getLog(OAuth2ProviderClientDetailDAO.class);
	
	private static final String DEFAULT_AUTHORITY = "ROLE_CLIENT";
	private static final boolean DEFAULT_DELETE_FLG = true;
	private static final int DEFAULT_ACCESSTOKEN_VALIDITY = 60 * 60 * 12; // default 12 hours.
	private static final int DEFAULT_REFRESHTOKEN_VALIDITY =  60 * 60 * 24 * 30; // default 30 days.
	
	public static OAuth2ProviderClientDetailDAO newInstance() {
		return (OAuth2ProviderClientDetailDAO) SpringUtil.getContext().getBean("oauth2ProviderClientDetailDAO");
	}

	@SuppressWarnings("unchecked")
	public Collection<OAuth2ProviderClientDetail> getClientDetails(String squareid){
		List<OAuth2ProviderClientDetail> results = super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(OAuth2ProviderClientDetail.class,"ocd")
					.add(Expression.eq("Id.Squareid", squareid))
					.addOrder(Order.asc("ocd.title")));
		
		return results; 
	}
	
	@SuppressWarnings("unchecked")
	public OAuth2ProviderClientDetail getClientDetailById(String clientId, String squareId){
		if (clientId == null) {
			throw new RuntimeException("clientId must be set.");
		}
		
		Iterator<OAuth2ProviderClientDetail> results = super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(OAuth2ProviderClientDetail.class, "ocd")
						.add(Restrictions.eq("ocd.Id.id", clientId))
						.add(Restrictions.eq("ocd.Id.Squareid", squareId)))
				.iterator();

		if(results.hasNext()) {
			return (OAuth2ProviderClientDetail)results.next();
		}
		
		return null; 
	}
	
	public void saveClientDetail(OAuth2ProviderClientDetail clientDetail) {
		if(clientDetail != null)
			super.getHibernateTemplate().saveOrUpdate(clientDetail);
	}

	public void saveClientDetail(String clientId, String title, String resourceIds, String secret, String scope, String grantType, String redirectUrl, String authorities, Boolean deleteFlg, Integer accessTokenValidity, Integer refreshTokenValidity, String additionalInformation, String squareId) {
		OAuth2ProviderClientDetail clientDetail = getClientDetailById(clientId, squareId);
		
		if(clientDetail == null){
			clientDetail = new OAuth2ProviderClientDetail(new OAuth2ProviderClientDetailPK(clientId, squareId));
		}
		
		clientDetail.setTitle(title);
		clientDetail.setSecret(secret);
		clientDetail.setScope(scope);
		clientDetail.setResourceIds(resourceIds);
		clientDetail.setGrantTypes(grantType);
		clientDetail.setRedirectUrl(redirectUrl);
		if(authorities!=null && authorities.length()>0){
			clientDetail.setAuthorities(authorities);
		}else{
			clientDetail.setAuthorities(DEFAULT_AUTHORITY);
		}
		if(deleteFlg!=null){
			clientDetail.setDeleteFlg(deleteFlg);
		}else{
			clientDetail.setDeleteFlg(DEFAULT_DELETE_FLG);
		}
		if(accessTokenValidity!=null){
			clientDetail.setAccessTokenValidity(accessTokenValidity.intValue());
		}else{
			clientDetail.setAccessTokenValidity(DEFAULT_ACCESSTOKEN_VALIDITY);
		}
		if(refreshTokenValidity!=null){
			clientDetail.setRefreshTokenValidity(refreshTokenValidity.intValue());
		}else{
			clientDetail.setRefreshTokenValidity(DEFAULT_REFRESHTOKEN_VALIDITY);
		}
		clientDetail.setAdditionalInformation(additionalInformation);
		
		super.getHibernateTemplate().saveOrUpdate(clientDetail);
	}

	public void deleteClientDetail(String clientId, String squareId) {
		OAuth2ProviderClientDetail clientDetail = getClientDetailById(clientId, squareId);
		
		if(clientDetail != null)
			super.getHibernateTemplate().delete(clientDetail);
	}

	public void copySquare(String squareId, String defaultSquareId) {
		Session session = super.getSession();
		Query sq = session.getNamedQuery("is_oauthprovider_client.copySquare");
		sq.setString("squareId", squareId);
		sq.setString("defaultSquareId", defaultSquareId);
		sq.executeUpdate();
	}
}