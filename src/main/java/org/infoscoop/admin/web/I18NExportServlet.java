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


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.dao.I18NDAO;
import org.infoscoop.dao.model.I18n;

import au.com.bytecode.opencsv.CSVWriter;

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
			List i18nList = I18NDAO.newInstance().findI18n(type, country, lang);
			
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
