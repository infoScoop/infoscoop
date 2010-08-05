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


import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Security;
import java.util.Locale;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.velocity.app.Velocity;
import org.infoscoop.account.SearchUserService;
import org.infoscoop.util.SpringUtil;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

public class InitializeServlet extends HttpServlet {

	private static final long serialVersionUID = "org.infoscoop.web.InitializeServlet"
		.hashCode();

	private Log log = LogFactory.getLog(this.getClass());

	/**
	 * <P>initialize the servlet.</P><BR>
	 * It is a precondition to operate a servlet that a file of DAO setting (/WEB-INF/conf/dao-config.xml) is set definitely.
	 */
	public void init(ServletConfig config) throws ServletException {

		initLog4jProperties(config);
		//loadDAOConfig(config);
		initVelocity(config);

		ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(config.getServletContext());
		SpringUtil.setContext(ctx);
		initSearchUserService();
		Locale.setDefault(new Locale("en"));
		Security.setProperty("networkaddress.cache.ttl", "60");
	}

	/**
	 * read and the initialize the Log4j property.
	 * @param config :setting for servlet
	 */
	protected void initLog4jProperties(ServletConfig config)
			throws ServletException {
		String file = config.getInitParameter("log4j-init-file");
		// if the log4j-init-file is not set, then no point in trying
		if (file != null) {
			InputSource is = getResource(config, file);
			Element element = null;
			try{
				DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				Document doc = db.parse(is);
				element = doc.getDocumentElement();
			}catch (Exception e) {
				throw new ServletException(e);
			}

			if(element != null)
				DOMConfigurator.configure(element);
		} else {
			throw new ServletException("");
		}
		if(log.isInfoEnabled())
			log.info("initialized Log4J.");
	}

	/**
	 * initialize the Velocity.
	 * @param config :setting for servlet
	 * @throws ServletException
	 */
	protected void initVelocity(ServletConfig config) throws ServletException {
		// maintain the buildVersion in ServletContext.
		String buildVersion = config.getServletContext().getInitParameter(
				"buildTimestamp");
		config.getServletContext().setAttribute("buildTimestamp", buildVersion);

		Properties prop = new Properties();
		prop.put("resource.loader", "class");
		prop.put("runtime.log.logsystem.class",
				"org.apache.velocity.runtime.log.NullLogSystem");
		prop
				.put("class.resource.loader.class",
						"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		prop.put("input.encoding", "UTF-8");
		prop.put("output.encoding", "UTF-8");
		try {
			Velocity.init(prop);
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

	protected void initSearchUserService() {
		try {
			SearchUserService search = (SearchUserService) SpringUtil
					.getBean("searchUserService");
		} catch (NoSuchBeanDefinitionException e) {
			SearchUserService.setNotAvailable();
			log.warn("searchUserService not found.", e);
		} catch (Exception e) {
			log.warn("unexpected error occured in searchUserService.", e);
		}
	}

	/**
	 * get the resource on the same context as InputSource.
	 * @param config
	 * @param path
	 * @return
	 * @throws ServletException
	 */
	public static InputSource getResource(ServletConfig config, String path)
			throws ServletException {
		try {
			URL url = config.getServletContext().getResource(path);
			InputStream input = config.getServletContext().getResourceAsStream(
					path);
			InputSource is = new InputSource(url.toExternalForm());
			is.setByteStream(input);
			is.setSystemId(config.getServletContext().getRealPath(path));
			return is;
		} catch (MalformedURLException e) {
			throw new ServletException(e);
		}
	}

}
