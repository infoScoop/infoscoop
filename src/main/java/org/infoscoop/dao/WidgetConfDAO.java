package org.infoscoop.dao;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.infoscoop.dao.model.WidgetConf;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class WidgetConfDAO extends HibernateDaoSupport {
	private static Log log = LogFactory.getLog(WidgetConfDAO.class);

	private WidgetConfDAO() {
	}


	public WidgetConf get(String type) {
		return (WidgetConf)super.getHibernateTemplate().get(WidgetConf.class, type);
	}
	
	/**
	 * get the data
	 * 
	 * @param res
	 * @param type Type of widget
	 * @return WidgetConfiguration document Element
	 */
	public Element getElement(String type) {
		
		WidgetConf conf = get(type);
		if(conf != null){
			try {
				return conf.getElement();
			} catch (SAXException e) {
				log.error("",e);
			}
		}
		return null;
	}
	
	public Element[] getElements(String[] types){
		
		List confs =  selectByTypes(Arrays.asList(types));
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
	public void delete(String type) {
		//getJdbcTemplate().update(getQuery("delete"), new Object[] { type });
		WidgetConf conf = new WidgetConf();
		conf.setType(type);
		super.getHibernateTemplate().delete(conf);
	}

	/**
	 * delete all data.
	 * @return
	 */
	public List<WidgetConf> selectAll() {
		return  super.getHibernateTemplate().loadAll(WidgetConf.class);
	}

	public void insert(WidgetConf conf) {
		super.getHibernateTemplate().save(conf);
	}


	public List<WidgetConf> selectByTypes(List<String> types) {
		List confs = super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(WidgetConf.class).add(Expression.in("type", types))
				);
		return confs;
	}
}