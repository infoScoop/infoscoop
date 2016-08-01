package org.infoscoop.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.infoscoop.dao.model.SquareAlias;
import org.infoscoop.util.SpringUtil;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class SquareAliasDAO extends HibernateDaoSupport {
	public static SquareAliasDAO newInstance() {
		return (SquareAliasDAO)SpringUtil.getContext().getBean("squareAliasDAO");
	}

	/**
	 * Get the data.
	 *
	 * @param name
	 * @return SquareAlias
	 */
	public SquareAlias getByName(String name) {		
		@SuppressWarnings("unchecked")
		List<SquareAlias> result = super.getHibernateTemplate().findByCriteria(
														DetachedCriteria.forClass(SquareAlias.class)
														.add(Expression.eq(SquareAlias.PROP_NAME, name))
													);
		return result.size()>0? result.get(0) : null;
	}

	public SquareAlias getBySquareId(String squareId) {
		List<SquareAlias> result = super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(SquareAlias.class)
				.add(Expression.eq(SquareAlias.PROP_SQUARE_ID, squareId))
		);
		return result.size()>0? result.get(0) : null;
	}

	public void insert(String name, String squareId, Boolean system) {
		SquareAlias newObj = new SquareAlias(name, squareId, system);
		super.getHibernateTemplate().saveOrUpdate(newObj);
	}
}
