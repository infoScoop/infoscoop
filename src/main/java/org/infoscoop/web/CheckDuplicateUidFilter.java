package org.infoscoop.web;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Expression;
import org.infoscoop.dao.model.Preference;
import org.infoscoop.util.SpringUtil;
import org.infoscoop.util.Xml2Json;
import org.json.JSONObject;

public class CheckDuplicateUidFilter implements Filter {
	public static String IS_PREVIEW = "isSelectProfilePreview"; 
	
	private static Log log = LogFactory.getLog(CheckDuplicateUidFilter.class);
	
	public void doFilter( ServletRequest request, ServletResponse response,FilterChain chain ) 
			throws IOException, ServletException {
		HttpServletRequest req = ( HttpServletRequest )request;
		HttpServletResponse resp = ( HttpServletResponse )response;

		String isPreview = request.getParameter(CheckDuplicateUidFilter.IS_PREVIEW);
		String uid = ( String )req.getSession().getAttribute("Uid");
		if( uid == null || "true".equalsIgnoreCase( isPreview ) ) {
			chain.doFilter( req,resp );
			return;
		}
		
		SessionFactory sessionFactory = ( SessionFactory )SpringUtil.getBean("sessionFactory");
		Session session = sessionFactory.openSession();
		
		try {
			Collection preferences = session.createCriteria( Preference.class )
				.add( Expression.eq("Uid",uid ).ignoreCase() ).list();
			
			if( preferences.size() == 1 ) {
				Preference pref = ( Preference )preferences.iterator().next();
				
				if( !uid.toLowerCase().equals( pref.getUid())) {
					resp.sendRedirect("mergeprofile?Uid="+pref.getUid() );
					return;
				}
			} else if( preferences.size() > 1 ) {
				Map dupeIdMap = new HashMap();
				for( Iterator ite=preferences.iterator();ite.hasNext();) {
					Preference pref = ( Preference )ite.next();
					String dupeId = pref.getUid();
					
					String lastModified = null;
					try {
						Xml2Json x2j = new Xml2Json();
						String rootPath = "/preference";
						x2j.addSkipRule(rootPath);
						x2j.addPathRule(rootPath + "/property", "name", true, true);
						String prefJsonStr = x2j.xml2json( pref.getElement() );
						JSONObject prefJSONObj = new JSONObject(prefJsonStr);
						
						lastModified = prefJSONObj.getJSONObject("property").getString("logoffDateTime");
					} catch( Exception ex ) {
						// ignore
					}
					
					dupeIdMap.put( dupeId,lastModified );
				}
				
				req.getSession().setAttribute("dupeIdMap",dupeIdMap );
				resp.sendRedirect("selectProfile.jsp");
				return;
			}
		} finally {
			session.close();
		}
		
		chain.doFilter( req,resp );
	}

	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
		
	}
	
}