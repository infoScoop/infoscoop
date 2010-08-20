package org.infoscoop.dao;

import org.infoscoop.dao.model.GadgetInstance;
import org.infoscoop.util.SpringUtil;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class GadgetInstanceDAO extends HibernateDaoSupport {
	public static GadgetInstanceDAO newInstance() {
		return (GadgetInstanceDAO) SpringUtil.getContext().getBean("gadgetInstanceDAO");
	}

	public void save(GadgetInstance gadget) {
		super.getHibernateTemplate().saveOrUpdate(gadget);
	}

}
