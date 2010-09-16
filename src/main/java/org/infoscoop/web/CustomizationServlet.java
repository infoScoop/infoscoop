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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.dao.model.Portallayout;
import org.infoscoop.service.PortalLayoutService;
import org.infoscoop.service.TabTemplateService;
import org.infoscoop.util.I18NUtil;
import org.infoscoop.util.SpringUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import freemarker.core.Environment;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

public class CustomizationServlet extends HttpServlet {
	private static final long serialVersionUID = "org.infoscoop.web.CustomizationServlet"
			.hashCode();
	private Configuration cfg;

	private static Log log = LogFactory.getLog(CustomizationServlet.class);

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		doPost(request, response);
	}

    public void init() {
        cfg = new Configuration();
        cfg.setServletContextForTemplateLoading(
                getServletContext(), "WEB-INF/templates");
    }

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		long start = System.currentTimeMillis();
		String uid = (String) request.getSession().getAttribute("Uid");

		if (log.isInfoEnabled()) {
			log.info("uid:[" + uid + "]: doPost");
		}

		response.setContentType("application/json; charset=UTF-8");
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");

		Writer writer = response.getWriter();
		try {
			Map<String, Object> root = new HashMap<String, Object>();
			root.put("request", request);
			root.put("session", request.getSession());

			String customFtl = getCustomizationFtl( root );

			customFtl = I18NUtil.resolve(I18NUtil.TYPE_LAYOUT,
					customFtl, request.getLocale());

			writer.write( customFtl );
		} catch (Exception e){
			log.error("--- unexpected error occurred.", e);
			response.sendError(500);
		}

		long end = System.currentTimeMillis();
		if (log.isDebugEnabled())
			log.debug("--- doPost: " + (end - start));

	}

	private String getCustomizationFtl( Map<String,Object> root ) throws ParserConfigurationException, Exception{
		JSONObject layoutJson = new JSONObject();
		Map<String, String> CustomizationMap = TabTemplateService.getHandle().getMyStaticAreaTemplate();


		//int staticPanelCount = 0;
		for(Iterator<Map.Entry<String, String>> ite = CustomizationMap.entrySet().iterator();ite.hasNext();){
			Map.Entry<String, String> entry = ite.next();
			String key = (String)entry.getKey();
			String value = (String)entry.getValue();
			if( value == null )
				value = "";

			if("commandbar".equals(key.toLowerCase())){
				layoutJson.put("commandbar", applyFreemakerTemplate(root, value));
			}else {
				layoutJson.put("staticPanel" + key, value);
			}
		}

		// get the information of static layout.
		PortalLayoutService service = (PortalLayoutService)SpringUtil.getBean("PortalLayoutService");
		List<Portallayout> layoutList = service.getPortalLayoutList();
		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();

		for(Iterator<Portallayout> layoutIt = layoutList.iterator();layoutIt.hasNext();){
			Portallayout portalLayout = layoutIt.next();

			String name = portalLayout.getName();
			if(name.equals("javascript"))
				continue;

			String layout;
			boolean isIframeToolBar = name.toLowerCase().equals("contentfooter");
			if(isIframeToolBar){
				String tempLayout = "<temp>" + portalLayout.getLayout() + "</temp>";
				Document ifdoc = db.parse(new ByteArrayInputStream(tempLayout.getBytes("UTF-8")));
				Element ifroot = ifdoc.getDocumentElement();
				NodeList icons = ifroot.getElementsByTagName("icon");

				JSONArray iconsJson = new JSONArray();
				for(int i=0;i<icons.getLength();i++){
					Element icon = ( Element )icons.item(i);

					JSONObject iconJson = new JSONObject();
					if( icon.hasAttribute("type"))
						iconJson.put("type",icon.getAttribute("type"));

					NodeList nodeList = icon.getChildNodes();
					for(int j = 0; j < nodeList.getLength(); j++){
						if(nodeList.item(j).getNodeType() == Node.CDATA_SECTION_NODE){
							iconJson.put("html",nodeList.item(j).getNodeValue());
							break;
						}
					}

					iconsJson.put( iconJson );
				}
				layout = iconsJson.toString();
			}else {
				layout = portalLayout.getLayout();
				if( layout == null )
					layout = "";
			}

			layout = applyFreemakerTemplate(root, layout);
			layoutJson.put( name,(isIframeToolBar)? new JSONArray(layout) : layout );
		}

		return "IS_Customization = " + layoutJson.toString() + ";";
	}

	private String applyFreemakerTemplate(Map<String, Object> root, String value)  {
		try {

			Writer out = new StringWriter();
			Template t = new Template("portalLayout_template", new StringReader( value ) ,cfg);

			t.setTemplateExceptionHandler(
					new TemplateExceptionHandler() {
						public void handleTemplateException(TemplateException templateexception, Environment environment, Writer writer){
							log.error("--- templete error occurred", templateexception);
						}
					});
			t.process( root, out );

			return out.toString();
		} catch( freemarker.core.ParseException e ) {
			log.error("--- templete error occurred", e);
		} catch (TemplateException e) {
			log.error("--- templete error occurred", e);
		} catch (IOException e) {
			log.error("--- templete error occurred", e);
		}
		return value;
	}

}
