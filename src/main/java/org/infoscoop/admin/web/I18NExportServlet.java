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
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.context.UserContext;
import org.infoscoop.dao.I18NDAO;
import org.infoscoop.dao.model.I18n;

import au.com.bytecode.opencsv.CSVWriter;
import org.infoscoop.service.SquareService;

public class I18NExportServlet extends HttpServlet
{
	private final static long serialVersionUID = "jp.co.beacon_it.msd.admin.I18NExportServlet"
			.hashCode();

	private static Log log = LogFactory.getLog( I18NExportServlet.class );

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		String country = request.getParameter("country");
		String lang = request.getParameter("lang");
		String type = request.getParameter("type");
		
		Writer w = null;
		try{
			response.setContentType("text/csv; charset=shift_jis; header=absent");
			
			String fileName = (type + "_" + country + "_" + lang +  ".csv");
			response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
			response.setHeader("Pragma", "no-cache");
			response.setHeader("Cache-Control", "no-cache");
			
			w = new OutputStreamWriter(response.getOutputStream(), "Windows-31J");
			
			String squareid = UserContext.instance().getUserInfo().getCurrentSquareId();
			List i18nList = null;
			do {
				i18nList = I18NDAO.newInstance().findI18n(type, country, lang, squareid);
				if(CollectionUtils.isNotEmpty(i18nList) || squareid.equals(SquareService.SQUARE_ID_DEFAULT)) break;
				squareid = SquareService.getHandle().getParentSquareId(squareid);
				if(squareid == null) squareid = SquareService.SQUARE_ID_DEFAULT;
			}while(true);

			I18n i18n;
			CSVWriter csvWriter = new CSVWriter(w);
			for(Iterator ite = i18nList.iterator(); ite.hasNext();){
				i18n = (I18n)ite.next();
				
				csvWriter.writeNext(new String[]{i18n.getId().getId(), i18n.getMessage()});
			}
			
			w.flush();
			w.close();
		} catch (Exception e) {
			if(log.isErrorEnabled())
				log.error("Unexpected Exception occurred.", e);
			response.sendError(500, e.getMessage());
		} finally{
			w.close();
		}
	}
	
}
