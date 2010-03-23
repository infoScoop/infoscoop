package org.infoscoop.web;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.request.filter.GadgetFilter;
import org.infoscoop.service.WidgetConfService;
import org.infoscoop.util.SpringUtil;
import org.infoscoop.widgetconf.I18NConverter;
import org.infoscoop.widgetconf.MessageBundle;
import org.w3c.dom.Element;

public class WidgetConf2HtmlServlet extends HttpServlet {

	private static final long serialVersionUID = "org.infoscoop.web.WidgetConf2HtmlServlet"
			.hashCode();

	private static Log log = LogFactory.getLog(WidgetConf2HtmlServlet.class);

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html; charset=UTF-8");
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");

		String type = request.getParameter("type");
		
		Writer writer = response.getWriter();
		try {
			WidgetConfService service = (WidgetConfService) SpringUtil.getBean("WidgetConfService");
			Element element = service.getWidgetConfByType(type );
			
			Map<String,String> parameterMap = new HashMap<String,String>();
			for( Object key : request.getParameterMap().keySet() )
				parameterMap.put( ( String )key,( String )request.getParameter(( String )key ));
			
			I18NConverter i18n = new I18NConverter(request.getLocale(),new ArrayList<MessageBundle>() );
			writer.write( new String( GadgetFilter.gadget2html(".",element.getOwnerDocument(),parameterMap,i18n )
					,"UTF-8"));
		} catch (Exception e) {
			log.error("Unexpected exception occurred.", e);
			response.sendError(500, e.getMessage());
		} finally {
			writer.flush();
			writer.close();
		}
	}
}
