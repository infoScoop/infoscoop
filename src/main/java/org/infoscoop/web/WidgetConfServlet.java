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


import java.io.IOException;
import java.io.Writer;
import java.net.SocketTimeoutException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.security.auth.Subject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.acl.ISPrincipal;
import org.infoscoop.acl.SecurityController;
import org.infoscoop.service.GadgetService;
import org.infoscoop.util.I18NUtil;
import org.infoscoop.widgetconf.WidgetConfUtil;
import org.json.JSONObject;
import org.w3c.dom.Document;

public class WidgetConfServlet extends HttpServlet {

	private static final long serialVersionUID = "jp.co.beacon_it.msd.portal.web.WidgetConfServlet"
			.hashCode();

	private static Log log = LogFactory.getLog(WidgetConfServlet.class);

	private SAXParserFactory factory;
	private int CACHE_LIFE_TIME = 360;//The default time is 2 hours.

	public void init(ServletConfig config) throws ServletException {

		super.init(config);

		String cacheLifeTime = config.getInitParameter("cacheLifeTime");
		try {
			if (cacheLifeTime != null)
				CACHE_LIFE_TIME = Integer.parseInt(cacheLifeTime);
		} catch (Exception e) {
			log.warn("cacheLifeTime must be a number. cacheLifeTime = "
					+ cacheLifeTime);
		}
		if (log.isInfoEnabled())
			log.info("cacheLifeTime = " + CACHE_LIFE_TIME + " minutes");

		factory = SAXParserFactory.newInstance();
		factory.setNamespaceAware(true);
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/plain; charset=UTF-8");
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");

		//String types = request.getParameter("types");
		String type = request.getParameter("type");
		//String displayFlag = request.getParameter("displayFlag");
		//String reload = request.getParameter("reload");

		Writer writer = response.getWriter();

		Locale locale = request.getLocale();

		try {
			String json = null;
			//if (types != null) {
			//	json = getWidgetConfJSON(types.split(","), request);
			//} else if (displayFlag != null) {
			//	json = getWidgetConfRSSList(displayFlag, request);
			//} else {
			if(type != null) {
				if( type.startsWith("g_")) {
					type = type.substring(2);
					json = getGadgetConfJson( request,type,locale );
				} else {
					json = GadgetService.getHandle()
							.getGadgetJson(type, locale).toString();
				}
			} else {
				String uid = (String) request.getSession().getAttribute("Uid");
				json = GadgetService.getHandle()
						.getGadgetConfsJson(uid, locale);
			}

			writer.write(json);
		// Copy from ProxyServlet
		} catch( SocketTimeoutException ex ) {
			// When the status code was 408, Firefox did not move well.
			// Because the cords such as 10408 are converted into 500 by Apache-GlassFish cooperation, we set it in a header.Apache-GlassFish.
			log.error("",ex);
			response.setHeader(HttpStatusCode.HEADER_NAME,
					HttpStatusCode.MSD_SC_TIMEOUT);
			response.sendError(500);
		} catch(ConnectTimeoutException ex){
			log.error("",ex);
			response.sendError(500, ex.getMessage());
		} catch (Exception e) {
			log.error("Unexpected exception occurred.", e);
			response.sendError(500, e.getMessage());
		} finally {
			writer.flush();
			//writer.close();
		}
	}

	private String getGadgetConfJson(HttpServletRequest request, String type,
			Locale locale) throws Exception {
		int timeout = request.getIntHeader("MSDPortal-Timeout") - 1000;
		WidgetConfUtil.GadgetContext context = new WidgetConfUtil.GadgetContext()
				.setTimeout(timeout).setUrl(type);
		Document doc = context.getDocument(request);

		JSONObject jsonObj = WidgetConfUtil.gadget2JSONObject(doc
				.getDocumentElement(), context.getI18NConveter(locale, doc),
				true);
		jsonObj = WidgetConfUtil.gadgetJSONtoPortalGadgetJSON( jsonObj );

		String json = jsonObj.toString(1);
		String domainName = null;
		Subject loginUser = SecurityController.getContextSubject();
		for(ISPrincipal p :loginUser.getPrincipals(ISPrincipal.class))
			if(ISPrincipal.DOMAIN_PRINCIPAL == p.getType())
				domainName = p.getDisplayName();
		
		Pattern pattern = Pattern.compile( "__IS_DOMAIN_NAME__" );
		Matcher matcher = pattern.matcher(json);
		if(matcher.find())
			json = matcher.replaceAll(domainName);
		
		return I18NUtil.resolve(I18NUtil.TYPE_WIDGET, json,
				locale, true);
	}
}
