package org.infoscoop.dao;


import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Projections;
import org.infoscoop.dao.model.Preference;
import org.infoscoop.util.SpringUtil;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * The DAO class to get and update the information of the widget.
 * 
 * @author nakata
 * 
 */
public class PreferenceDAO extends HibernateDaoSupport{

    private static Log log = LogFactory.getLog(PreferenceDAO.class);

    private PreferenceDAO(){};
    
	public static PreferenceDAO newInstance() {
        return (PreferenceDAO)SpringUtil.getContext().getBean("preferenceDAO");
	}
    
    /**
     * Get the DOM of an appointed user.
     * 
     * @param uid
     *            userID
     * @return A top node Object of DOM that includes include the user setting information of an appointed user. If there is not it, we return the empty DOM.
     */
    public Preference select(String uid) {
    	if(uid == null)return null;
    	return (Preference)super.getHibernateTemplate().get(Preference.class, uid);
    }

    /**
     * Update the data of an appointed user.<BR>
     * 
     * @param uid
     *            A userID that is target for operating data.
     * @param widgetsNode
     *            A Node object having the data of "widgets" of DOM to save.
     * @throws DataResourceException
     */
    public void update(Preference entity) {
    	if(log.isInfoEnabled())
        	log.info("update preference for uid: " + entity.getUid() + ".");
    	
    	super.getHibernateTemplate().saveOrUpdate(entity);
    	if(log.isInfoEnabled())
    		log.info("uid[" + entity.getUid() + "]: Save XML successfully.");
    }

	/**
	 * Return the users count.
	 * 
	 * @return
	 */
	public int getTotalUsersCount() {
		return (Integer) super.getHibernateTemplate().execute(
				new HibernateCallback() {

					public Object doInHibernate(org.hibernate.Session session)
							throws HibernateException, SQLException {

						Criteria crit = session
								.createCriteria(Preference.class);
						crit.setProjection(Projections.rowCount());
						Integer rowCount = (Integer) crit.uniqueResult();

						return rowCount;
					}

				});
	}
}
