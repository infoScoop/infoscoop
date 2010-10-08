package org.infoscoop.dao;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.infoscoop.account.DomainManager;
import org.infoscoop.dao.model.CommandBar;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class CommandBarDAO extends HibernateDaoSupport {

	private static Log log = LogFactory.getLog(CommandBarDAO.class);

	public List<CommandBar> all(){
		return super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(CommandBar.class).add(
						Expression.eq(CommandBar.PROP_FK_DOMAIN_ID, DomainManager.getContextDomainId())));
	}
}
