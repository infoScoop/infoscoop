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

package org.infoscoop.admin.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.admin.exception.I18NImportException;
import org.infoscoop.dao.I18NDAO;
import org.infoscoop.service.I18NService;

public class I18NImportServlet extends HttpServlet {
	private final static long serialVersionUID = "jp.co.beacon_it.msd.admin.I18NImportServlet"
			.hashCode();
	private static Log log = LogFactory.getLog(I18NImportServlet.class);

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String country = request.getParameter("country");
		String lang = request.getParameter("lang");
		String type = request.getParameter("type");
		PrintWriter w = null;
		
		try {
			response.setContentType("text/html; charset=UTF-8");
			response.setHeader("Pragma", "no-cache");
			response.setHeader("Cache-Control", "no-cache");

			DiskFileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload sfu = new ServletFileUpload(factory);

			List itemsList = sfu.parseRequest(request);
			List errorList = new ArrayList(); // Result set whose result of CSV check is error.
			Map countMap = new HashMap();

			Map formItemsMap = createFormItemsmap(itemsList);
			FileItem csvFile = (FileItem) formItemsMap.get("csvFile");
			
			if(!csvFile.getName().endsWith(".csv")){
				w = new PrintWriter(response.getWriter());
				w.println("Please specify the CSV file. ");
				return;
			}
			
			String mode = (String) formItemsMap.get("mode"); // replce all or difference

			// execute import
			try{
				List defaultIdList = getDefaultIdList(type);
				I18NService.getHandle().replaceI18nByLocale(type, country, lang,
						csvFile, mode, countMap, errorList, defaultIdList);
				request.setAttribute("countMap", countMap);
			}catch(I18NImportException e){
				// execute roll back
				request.setAttribute("errorMessage", e.getMessage());
			}
			
			request.setAttribute("errorList", errorList);
			request.getRequestDispatcher("i18nImportResult.jsp").forward(
					request, response);
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error("Unexpected error occurred.", e);
			response.sendError(500, e.getMessage());
		} finally{
			if(w != null)
				w.close();
		}
	}

	private Map createFormItemsmap(List itemsList) {
		Iterator iterator = itemsList.iterator();
		FileItem fileItem;
		Map formItemsMap = new HashMap();
		while (iterator.hasNext()) { // It turns only once, because it extends only one file. 
			fileItem = (FileItem) iterator.next();
			if (fileItem.isFormField()) {
				formItemsMap.put(fileItem.getFieldName(), fileItem.getString());
			} else {
				formItemsMap.put(fileItem.getFieldName(), fileItem);
			}
		}
		return formItemsMap;
	}
	

	/**
	 * return the list of MessageID whose type is "ALL_ALL".
	 * @return
	 */
	public List getDefaultIdList(String type){
		return I18NDAO.newInstance().getIdListByLocale(type, "ALL", "ALL");
	}
}
