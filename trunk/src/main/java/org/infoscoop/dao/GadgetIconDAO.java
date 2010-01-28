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