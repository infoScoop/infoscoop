package org.infoscoop.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.addon.IAddonService;
import org.infoscoop.util.SpringUtil;

public class AddonServlet extends HttpServlet{
	private static final long serialVersionUID = AddonServlet.class.getName().hashCode();
	private Log log = LogFactory.getLog(AddonServlet.class);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String beanName = getBeanName(req);

		try{
			IAddonService addon = (IAddonService)SpringUtil.getBean(beanName);

			addon.execute(req, resp);
		}catch(Exception e){
			log.error("The error occurred in add-on. [" + beanName + "]", e);
			throw new RuntimeException(e);
		}
	}

	private String getBeanName(HttpServletRequest req){
		String reqUri = req.getRequestURI();
		String srvPath = req.getServletPath();
		int beanNameStart = reqUri.lastIndexOf(srvPath) + (srvPath.length() + 1);
		int beanNameEnd = reqUri.indexOf('/', beanNameStart) ;
		if(beanNameEnd < 0)
			return reqUri.substring(beanNameStart);
		else
			return reqUri.substring(beanNameStart, beanNameEnd);
	}
}
