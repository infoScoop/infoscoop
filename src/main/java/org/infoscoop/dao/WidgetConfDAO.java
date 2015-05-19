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

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.infoscoop.dao.model.WidgetConf;
import org.infoscoop.dao.model.WidgetConfPK;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class WidgetConfDAO extends HibernateDaoSupport {
	private static Log log = LogFactory.getLog(WidgetConfDAO.class);

	private WidgetConfDAO() {
	}


	public WidgetConf get(String type, String squareid) {
		WidgetConfPK pk = new WidgetConfPK(type, squareid);
		return (WidgetConf)super.getHibernateTemplate().get(WidgetConf.class, pk);
	}
	
	/**
	 * get the data
	 * 
	 * @param res
	 * @param type Type of widget
	 * @return WidgetConfiguration document Element
	 */
	public Element getElement(String type, String squareid) {
		
		WidgetConf conf = get(type, squareid);
		if(conf != null){
			try {
				return conf.getElement();
			} catch (SAXException e) {
				log.error("",e);
			}
		}
		return null;
	}
	
	public Element[] getElements(String[] types, String squareid){
		
		List confs =  selectByTypes(Arrays.asList(types), squareid);
		Element[] typeConfs = new Element[confs.size()];
		try {
			int i = 0;
			for(Iterator it = confs.iterator(); it.hasNext();i++){
				WidgetConf conf = (WidgetConf)it.next();
				typeConfs[i] = conf.getElement();
			}
		} catch (SAXException e) {
			log.error("",e);
			return null;
		}
		return typeConfs;
	}

	/**
	 * update the data.
	 * @param node
	 * @param type
	 */
	public void update(WidgetConf conf) {
		//getJdbcTemplate().update(getQuery("update"),
		//		new Object[] { XmlUtil.dom2String(node), type });
		super.getHibernateTemplate().update(conf);
	}

	/**
	 * delete the data.
	 * @param type
	 */
	public void delete(String type, String squareid) {
		//getJdbcTemplate().update(getQuery("delete"), new Object[] { type });
		WidgetConf conf = new WidgetConf();
		conf.setId(new WidgetConfPK(type, squareid));
		super.getHibernateTemplate().delete(conf);
	}

	/**
	 * delete all data.
	 * @return
	 */
	public List<WidgetConf> selectAll(String squareid) {
//		return  super.getHibernateTemplate().loadAll(WidgetConf.class);
		return super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(WidgetConf.class)
					.add(Expression.eq("Id.Squareid", squareid)));
	}

	public void insert(WidgetConf conf) {
		super.getHibernateTemplate().save(conf);
	}


	public List<WidgetConf> selectByTypes(List<String> types, String squareid) {
		List confs = super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(WidgetConf.class)
					.add(Expression.in("Id.type", types))
					.add(Expression.eq("Id.Squareid", squareid))
				);
		return confs;
	}
}
