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

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.dao.model.Logo;
import org.infoscoop.service.LogoService;
import org.infoscoop.service.PropertiesService;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Iterator;
import java.util.List;

public class LogoImageServlet extends HttpServlet {
	private static final long serialVersionUID = 6201737862447826027L;
	private static Log log = LogFactory.getLog(LogoImageServlet.class);

	private static final String EXIST_PORTAL_LOGO_PATH = "/existsPortalLogo";
	private static final String EXIST_FAVICON_PATH = "/existsFavicon";
	private static final String GET_PORTAL_LOGO_PATH = "/getPortalLogo";
	private static final String GET_FAVICON_PATH = "/getFavicon";
	private static final String POST_PORTAL_LOGO_PATH = "/postPortalLogo";
	private static final String POST_FAVICON_PATH = "/postFavicon";

	public void doGet( HttpServletRequest req,HttpServletResponse resp ) throws ServletException,IOException {
		String action = ((HttpServletRequest)req).getPathInfo();
		resp.setHeader("Pragma", "no-cache");
		resp.setHeader("Cache-Control", "no-cache");

		if(GET_PORTAL_LOGO_PATH.equals(action)) {
			getAction(resp, "portal_logo");
		} else if(GET_FAVICON_PATH.equals(action)) {
			getAction(resp, "favicon");
		} else if(EXIST_PORTAL_LOGO_PATH.equals(action)) {
			existsAction(resp, "portal_logo");
		} else if(EXIST_FAVICON_PATH.equals(action)) {
			existsAction(resp, "favicon");
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = ((HttpServletRequest)request).getPathInfo();
		request.setCharacterEncoding("UTF-8");

		response.setContentType("text/html;charset=utf-8");
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setStatus(200);

		if(POST_PORTAL_LOGO_PATH.equals(action)) {
			postAction(request, response, "portal_logo");
		} else if(POST_FAVICON_PATH.equals(action)) {
			postAction(request, response, "favicon");
		}
	}

	private void getAction(HttpServletResponse response, String kind) throws IOException {
		Logo logo = LogoService.getHandle().getLogo(kind);
		byte[] data = new byte[0];
		try {
			if(logo != null) {
				response.setContentType(logo.getType() + ";");
				data = logo.getLogo();
			}
		} catch( Exception ex ) {
			throw new RuntimeException( ex );
		}
		response.getOutputStream().write(data);
		response.getOutputStream().flush();
	}

	private void existsAction(HttpServletResponse response, String kind) throws IOException {
		Logo logo = LogoService.getHandle().getLogo(kind);
		response.setContentType("text/plain;charset=utf-8");
		boolean result = false;
		if(logo != null)
			result = true;

		response.getWriter().write(String.valueOf(result));
	}

	private void postAction(HttpServletRequest request, HttpServletResponse response, String kind) throws IOException{
		try {
			byte[] result = null;
			String contentType = null;

			DiskFileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(factory);
			factory.setSizeThreshold(1048576);
			upload.setSizeMax(1048576);
			upload.setHeaderEncoding("UTF-8");

			List list = upload.parseRequest(request);
			Iterator iterator = list.iterator();
			while(iterator.hasNext()){
				FileItem fItem = (FileItem)iterator.next();
				result = extractLogoImage(fItem);
				contentType = fItem.getContentType();
			}
			if(result != null)
				LogoService.getHandle().saveLogo(result, contentType, kind);
		}catch (Exception e) {
			log.error("Exception Image Upload.", e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			JSONObject json = new JSONObject();
			try {
				json.put("message", "ams_gadgetResourceUpdateFailed");
				json.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
				response.getWriter().write(json.toString());
			} catch (JSONException ex) {}
			return;
		}
	}

	private byte[] extractLogoImage(FileItem fItem) throws Exception{
		byte[] result = null;
		if(!(fItem.isFormField()) && fItem.getContentType().matches("image/(jpeg|png|gif|x-icon|vnd\\.microsoft\\.icon)")) {
			InputStream is = fItem.getInputStream();
			ByteArrayOutputStream b = new ByteArrayOutputStream();
			OutputStream os = new BufferedOutputStream(b);
			int c;
			try {
				while ((c = is.read()) != -1) {
					os.write(c);
				}
			} catch (IOException e) {
				throw new IOException(e);
			} finally {
				if (os != null) {
					try {
						os.flush();
						os.close();
						result = b.toByteArray();
					} catch (IOException e) {
						throw new IOException(e);
					}
				}
			}
		}
		return result;
	}
}
