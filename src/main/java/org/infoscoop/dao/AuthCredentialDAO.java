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
import org.hibernate.criterion.Order;
import org.infoscoop.account.DomainManager;
import org.infoscoop.dao.model.AuthCredential;
import org.infoscoop.util.SpringUtil;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class AuthCredentialDAO extends HibernateDaoSupport{
	public static AuthCredentialDAO newInstance(){
		return (AuthCredentialDAO)SpringUtil.getContext().getBean("authCredentialDAO");        
	}
	
	public AuthCredential get(Long id){
		return (AuthCredential)super.getHibernateTemplate().get(AuthCredential.class, id);
	}

	public AuthCredential select(String uid, String authType, String authUid){
		List results = super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(AuthCredential.class).add(
						Expression.conjunction()
						.add(Expression.eq(AuthCredential.PROP_FK_DOMAIN_ID, DomainManager.getContextDomainId()))
						.add(Expression.eq(AuthCredential.PROP_AUTH_UID, uid))
						.add(Expression.eq(AuthCredential.PROP_AUTH_TYPE, authType))
						.add(Expression.eq(AuthCredential.PROP_AUTH_UID, authUid))
						)
						);	
		if(results.isEmpty()){
			return null;
		}else{
			return (AuthCredential)results.get(0);
		}
	}

	public AuthCredential select(String uid, Integer sysNum){
		List results = super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(AuthCredential.class).add(
						Expression.conjunction()
						.add(Expression.eq(AuthCredential.PROP_FK_DOMAIN_ID, DomainManager.getContextDomainId()))
						.add(Expression.eq(AuthCredential.PROP_UID, uid))
						.add(Expression.eq(AuthCredential.PROP_SYS_NUM, sysNum))
						)
						);	
		if(results.isEmpty()){
			return null;
		}else{
			return (AuthCredential)results.get(0);
		}
	}
	
	public Long add(AuthCredential c){
		c.setFkDomainId(DomainManager.getContextDomainId());
		return (Long)super.getHibernateTemplate().save(c);
	}

	public void update(AuthCredential c){
		c.setFkDomainId(DomainManager.getContextDomainId());
		super.getHibernateTemplate().update(c);	
	}
	
	public List select(String uid){
		return super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(AuthCredential.class)
				.add(Expression.eq(AuthCredential.PROP_FK_DOMAIN_ID, DomainManager.getContextDomainId()))
				.add(Expression.eq(AuthCredential.PROP_UID, uid))
				.addOrder(Order.asc(AuthCredential.PROP_SYS_NUM))
				.addOrder(Order.asc(AuthCredential.PROP_ID))
		);
	}
	
	public void delete(AuthCredential c){
		super.getHibernateTemplate().delete(c);	
	}
	
}
