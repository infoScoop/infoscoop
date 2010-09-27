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
import org.infoscoop.dao.model.Cache;
import org.infoscoop.service.CacheService;

public class CacheServlet extends HttpServlet {

	private static final long serialVersionUID = "org.infoscoop.web.CacheServlet"
			.hashCode();

	private static Log log = LogFactory.getLog(CacheServlet.class);

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		doPost(request, response);

	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String delete = request.getParameter("delete");

		if (delete != null) {
			try {

				String uid = (String) request.getSession().getAttribute("Uid");
				if (uid == null)//TODO:If an uid is null, MSDPortal-SessionId is not set in deleteCahce of the first time.
					uid = request.getHeader("MSDPortal-SessionId");
				
				deleteCache(uid);
				
	            response.setStatus(204);
			} catch (Exception e) {
	        	log.error("",e);
				response.sendError(500, e.getMessage());
	        }
		} else {
			getCache(request, response);
		}
	}

	private void deleteCache(String uid) throws IOException {

		CacheService.getHandle().deleteUserCache(uid);

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
