package org.infoscoop.web;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.commons.httpclient.Header;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.dao.RssCacheDAO;
import org.infoscoop.dao.model.Cache;
import org.infoscoop.service.CacheService;

public class CacheServlet extends HttpServlet {

	private static final long serialVersionUID = "org.infoscoop.web.CacheServlet"
			.hashCode();

	private Log log = LogFactory.getLog(this.getClass());

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		doPost(request, response);

	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String delete = request.getParameter("delete");
		String url = request.getParameter("url");

		if (delete != null) {
			try {

				String uid = (String) request.getSession().getAttribute("Uid");
				if (uid == null)//TODO:If an uid is null, MSDPortal-SessionId is not set in deleteCahce of the first time.
					uid = request.getHeader("MSDPortal-SessionId");
				
				if(url == null){
					deleteCache(uid);
				}else{
					deleteCacheByUrl(uid, url);
				}
	            response.setStatus(204);
			} catch (Exception e) {
	        	log.error("",e);
				response.sendError(500, e.getMessage());
	        }
		} else {
			getCache(request, response);
		}
	}

	private void deleteCacheByUrl(String uid, String url) {
		RssCacheDAO.newInstance().deleteCacheByUrl(uid, url);

		if(log.isInfoEnabled())
			log.info("delete cache : uid = " + uid + ", url=" + url);

	}

	private void deleteCache(String uid) throws IOException {

		CacheService.getHandle().deleteUserCache(uid);
		RssCacheDAO.newInstance().deleteUserCache(uid);

		if(log.isInfoEnabled())
			log.info("delete cache : uid = " + uid);


	}

	private void getCache(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		String id = request.getParameter("id");
		String url = request.getParameter("url");

		if(log.isInfoEnabled())
			log.info("get cache : id = " + id + ", url = " + url);

		try {
			Cache cache = CacheService.getHandle().getCacheById(id);
			if (cache != null) {
				response.setHeader("Cache-Control","no-cache");
				
				List<Header> headers = cache.getHeaderList();
				for(Header header : headers){
					String name = header.getName();
					if (!name.equalsIgnoreCase("transfer-encoding")) {
						response.setHeader(name, header.getValue());
					}
				}

				response.flushBuffer();

				InputStream body = new ByteArrayInputStream( cache.getBodyBytes() );
				BufferedOutputStream bos = new BufferedOutputStream(response
						.getOutputStream());
				byte[] b = new byte[1024];
				int c = 0;
				while ((c = body.read(b)) != -1) {
					bos.write(b, 0, c);
					bos.flush();
				}

				bos.flush();
				bos.close();
				if(log.isInfoEnabled())
					log.info("get cache completed : id = " + id + ", url = " + cache.getUrl());
				return;
			}
		} catch (Exception e) {
			log.error("unexpected error occurred", e);
			response.sendError(500, e.getMessage());
			
			return;
		} 

		if (url != null) {
			if(log.isInfoEnabled())
				log.info("redirect : url = " + url);
			response.sendRedirect(url);
		}

		response.setStatus(404);
	}
}
