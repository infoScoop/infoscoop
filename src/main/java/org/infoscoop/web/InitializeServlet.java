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

import java.security.Security;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.app.Velocity;
import org.infoscoop.account.SearchUserService;
import org.infoscoop.util.SpringUtil;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class InitializeServlet extends HttpServlet {

	private static final long serialVersionUID = "org.infoscoop.web.InitializeServlet"
		.hashCode();

	private Log log = LogFactory.getLog(this.getClass());

	/**
	 * <P>initialize the servlet.</P><BR>
	 * It is a precondition to operate a servlet that a file of DAO setting (/WEB-INF/conf/dao-config.xml) is set definitely.
	 */
	public void init(ServletConfig config) throws ServletException {
		
		//loadDAOConfig(config);
		initVelocity(config);

		ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(config.getServletContext());
		SpringUtil.setContext(ctx);
		initSearchUserService();
		Security.setProperty("networkaddress.cache.ttl", "60");
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
}
