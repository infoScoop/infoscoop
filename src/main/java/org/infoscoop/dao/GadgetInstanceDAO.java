package org.infoscoop.dao;

import java.util.List;

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
}
