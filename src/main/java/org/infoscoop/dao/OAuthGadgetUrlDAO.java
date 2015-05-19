package org.infoscoop.dao;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Restrictions;
import org.infoscoop.dao.model.OAuthGadgetUrl;
import org.infoscoop.util.Crypt;
import org.infoscoop.util.SpringUtil;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class OAuthGadgetUrlDAO extends HibernateDaoSupport{
	private static Log log = LogFactory.getLog(OAuthGadgetUrlDAO.class);

	public static OAuthGadgetUrlDAO newInstance() {
		return (OAuthGadgetUrlDAO) SpringUtil.getContext().getBean(
				"oauthGadgetUrlDAO");
	}
	
	@SuppressWarnings("unchecked")
	public OAuthGadgetUrl getGadgetUrl(String oauthId, String gadgetUrl, String squareid) {
		if (oauthId == null) {
			throw new RuntimeException("oauthId and gadgetUrl must be set.");
		}

		Iterator results = super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(OAuthGadgetUrl.class)
				.add(Expression.eq(OAuthGadgetUrl.PROP_FKOAUTHID,oauthId))
				.add(Expression.eq(OAuthGadgetUrl.PROP_SQUARE_ID,squareid))
				.add(Expression.eq(OAuthGadgetUrl.PROP_GADGET_URL_KEY,Crypt.getHash(gadgetUrl))))
				.iterator();
		if (results.hasNext()) {
			return (OAuthGadgetUrl)results.next();
		}

		return null;
	}
	
	public List<OAuthGadgetUrl> getGadgetUrls(String squareid) {
		return super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(OAuthGadgetUrl.class)
				.add(Expression.eq(OAuthGadgetUrl.PROP_SQUARE_ID,squareid))
				);
	}

	public List<OAuthGadgetUrl> getGadgetUrlsById(String fkOauthId, String squareid) {
		return super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(OAuthGadgetUrl.class)
				.add(Expression.eq(OAuthGadgetUrl.PROP_SQUARE_ID,squareid))
				.add(Restrictions.eq(OAuthGadgetUrl.PROP_FKOAUTHID, fkOauthId)));
	}	
	
	public List<OAuthGadgetUrl> getGadgetUrlsNotInUrl(List<String> urlKeyList, String fkOauthId, String squareid){
		return super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(OAuthGadgetUrl.class)
					.add(Expression.eq(OAuthGadgetUrl.PROP_SQUARE_ID,squareid))
					.add(
						Restrictions.and(
							Restrictions.not(Restrictions.in(OAuthGadgetUrl.PROP_GADGET_URL_KEY,urlKeyList)),
							Restrictions.eq(OAuthGadgetUrl.PROP_FKOAUTHID, fkOauthId))));
	}
	
	public void save(OAuthGadgetUrl gadgetUrl) {
		OAuthGadgetUrl newGadgetUrl = getGadgetUrl(gadgetUrl.getFkOauthId(), gadgetUrl.getGadgetUrl(), gadgetUrl.getSquareid());

		if (newGadgetUrl == null) {
			super.getHibernateTemplate().save(gadgetUrl);
		} else {
			newGadgetUrl.setFkOauthId(gadgetUrl.getFkOauthId());
			newGadgetUrl.setGadgetUrl(gadgetUrl.getGadgetUrl());
			super.getHibernateTemplate().saveOrUpdate(newGadgetUrl);
		}
	}

	public static void main(String args[]) {
		System.out.println(Crypt
				.getHash("http://localhost/oauth_test/twit_oauth2.xml"));
	}
	
	public void saveGadgetUrl(String fkOauthId, String gadgetUrl, String squareid){
		OAuthGadgetUrl newGadgetUrl = getGadgetUrl(fkOauthId, gadgetUrl, squareid);
		if (newGadgetUrl == null) {
			newGadgetUrl = new OAuthGadgetUrl();
			newGadgetUrl.setFkOauthId(fkOauthId);
			newGadgetUrl.setGadgetUrl(gadgetUrl);
			newGadgetUrl.setSquareid(squareid);
		}
		super.getHibernateTemplate().saveOrUpdate(newGadgetUrl);
	}
	
	public void saveGadgetUrls(List<OAuthGadgetUrl> urls) {
		for(OAuthGadgetUrl url: urls)
			this.save(url);
	}
	
	public void deleteAll(String squareid){
		super.getHibernateTemplate().deleteAll(getGadgetUrls(squareid));
	}
		
	public void delete(String fkOauthId, String squareid) {
		super.getHibernateTemplate().deleteAll(getGadgetUrlsById(fkOauthId, squareid));
	}
	
	public void deleteGadgetUrl(OAuthGadgetUrl gadgetUrl){
		if (gadgetUrl != null)
			super.getHibernateTemplate().delete(gadgetUrl);
	}
	
	public void deleteGadgetUrls(List<OAuthGadgetUrl> gadgetUrls){
		super.getHibernateTemplate().deleteAll(gadgetUrls);
	}
}
