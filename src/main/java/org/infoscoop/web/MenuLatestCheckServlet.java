package org.infoscoop.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.dao.MenuCacheDAO;
import org.infoscoop.dao.model.MENUCACHEPK;
import org.infoscoop.dao.model.MenuCache;
import org.infoscoop.util.Crypt;
import org.json.JSONArray;

public class MenuLatestCheckServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3442930551294149947L;
	private static Log log = LogFactory.getLog(MenuLatestCheckServlet.class);

	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		StringBuffer buf = new StringBuffer();
		BufferedReader reader = req.getReader();
		String s = null;
		while((s = reader.readLine()) != null){
			buf.append(s);
		}
		
		String uid = (String) req.getSession().getAttribute("Uid");
		if (uid == null)return;//The alert is unnecessary in the case of non-login.
		
		String url = (String) req.getParameter("url");
		try {
			res.setHeader("Content-Type", "text/xml; charset=UTF-8");
			Writer w = res.getWriter();
			MenuCacheDAO dao = MenuCacheDAO.newInstance();
			MenuCache cache = dao.get(uid,url);
			if(cache != null && cache.getMenuIds() != null ){
				Set oldIds = new HashSet(Arrays.asList(new String(cache.getMenuIds()).split(",")));
				Set newIds = new HashSet(Arrays.asList(buf.toString().split(",")));
				newIds.removeAll(oldIds);
				JSONArray array = new JSONArray();
				for(Iterator it = newIds.iterator(); it.hasNext();){
					String id = (String)it.next();
					array.put(id);
				}
				w.write(array.toString());
			}else{
				cache = new MenuCache(new MENUCACHEPK(Crypt.getHash(url),uid ));
				w.write("[]");
			}
			cache.setMenuIds( buf.toString().getBytes());
			dao.insertOrUpdate(cache);
			
			w.flush();
			w.close();
		} catch (Exception e) {
			log .error("unexcepted error",e);
			res.sendError(500, e.getMessage());
		}
	}

}
