package org.infoscoop.dao;

import java.util.Collection;

import org.infoscoop.dao.model.Forbiddenurl;
import org.infoscoop.util.SpringUtil;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class ForbiddenURLDAO extends HibernateDaoSupport {
//	private static Log log = LogFactory.getLog(ForbiddenURLDAO.class);
	
	public static ForbiddenURLDAO newInstance() {
        return (ForbiddenURLDAO)SpringUtil.getContext().getBean("forbiddenURLDAO");
	}
	
	public Collection getForbiddenUrls() {
		String queryString = "from Forbiddenurls order by Id desc";
		
		return super.getHibernateTemplate().find( queryString );
	}
	
	public boolean isForbiddenUrl( String url ){
		String queryString = "from Forbiddenurls where Url = ?";
		
		return super.getHibernateTemplate().find( queryString,
				new Object[]{ url }).iterator().hasNext();
	}
	
	
	public void delete(Forbiddenurl forbiddenUrl) {
		super.getHibernateTemplate().delete( forbiddenUrl );
	}

	public void update(Forbiddenurl forbiddenUrl) {
		super.getHibernateTemplate().update( forbiddenUrl );
	}

	public void insert(Forbiddenurl forbiddenUrl) {
		super.getHibernateTemplate().save( forbiddenUrl );
		
	}
}
