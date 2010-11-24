package org.infoscoop.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.infoscoop.account.DomainManager;
import org.infoscoop.dao.model.GadgetInstance;
import org.infoscoop.util.SpringUtil;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class GadgetInstanceDAO extends HibernateDaoSupport {
	public static GadgetInstanceDAO newInstance() {
		return (GadgetInstanceDAO) SpringUtil.getContext().getBean(
				"gadgetInstanceDAO");
	}

	public void save(GadgetInstance gadget) {
		super.getHibernateTemplate().saveOrUpdate(gadget);
	}

	@SuppressWarnings("unchecked")
	public List<GadgetInstance> all() {
		return super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(GadgetInstance.class).add(
						Expression.eq(GadgetInstance.PROP_FK_DOMAIN_ID, DomainManager.getContextDomainId())));
	}

	@SuppressWarnings("unchecked")
	public GadgetInstance get(Integer id) {
		List<GadgetInstance> gadgets = super.getHibernateTemplate()
				.findByCriteria(
						DetachedCriteria.forClass(GadgetInstance.class).add(
								Expression.eq(GadgetInstance.PROP_ID, id)));
		if (gadgets.size() == 1)
			return gadgets.get(0);
		return null;
	}
	
	public void delete(GadgetInstance staticGadget) {
		super.getHibernateTemplate().delete(staticGadget);
	}
	
	public void deleteByIds(List<Integer> ids) {
		if (ids.size() == 0)
			return;
		Query query = super
				.getSession()
				.createQuery(
						"delete from GadgetInstance where FkDomainId = :domainId and id in (:ids)");
		query.setParameter("domainId", DomainManager.getContextDomainId());
		query.setParameterList("ids", ids);
		query.executeUpdate();
	}
	
	public void deleteById(Integer id) {
		Query query = super
				.getSession()
				.createQuery(
						"delete from GadgetInstance where FkDomainId = :domainId and id = :id");
		query.setParameter("domainId", DomainManager.getContextDomainId());
		query.setParameter("id", id);
		query.executeUpdate();
	}
}
