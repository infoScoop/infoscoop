package org.infoscoop.web;

import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.dao.WidgetDAO;
import org.infoscoop.dao.model.Preference;
import org.infoscoop.dao.model.Tab;
import org.infoscoop.dao.model.Widget;
import org.infoscoop.service.PreferenceService;
import org.infoscoop.service.TabService;
import org.infoscoop.util.I18NUtil;
import org.infoscoop.util.Xml2Json;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class WidgetServlet extends HttpServlet {

	private static final long serialVersionUID = "org.infoscoop.web.WidgetServlet"
			.hashCode();
	
	private static String defaultUid = "default";
	private static final String formatFullDate = "yyyy/MM/dd HH:mm:ss 'GMT'Z";
	private static final String formatW3C = "yyyy-MM-dd'T'HH:mm:ssZ";

	private static Log log = LogFactory.getLog(WidgetServlet.class);
	
	public static String getDefaultUid() {
		return defaultUid;
	}

	public void init(ServletConfig config) throws ServletException {
		defaultUid = config.getInitParameter("defaultUid");
		if(defaultUid == null) defaultUid = "default";
		
		super.init(config);
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		doPost(request, response);

	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		long start = System.currentTimeMillis();
		
		String uid = (String) request.getSession().getAttribute("Uid");
		
		String tabOrderStr = request.getParameter("tabOrder");
		
		String resetStr = request.getParameter("reset");
		if("true".equalsIgnoreCase( resetStr )) {
			try{
				TabService.getHandle().clearConfigurations( uid );
			}catch (Exception e) {
				log.error("An exception occeurred.", e);
				response.sendError(500, e.getMessage());
			} 
			return;
		}
		
		if(log.isInfoEnabled()){
			log.info("uid:[" + uid + "]: doPost");
		}		
		
		response.setContentType("text/plain; charset=UTF-8");
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");
		
		Writer writer = response.getWriter();
		JSONArray responseAray = new JSONArray();
		
		try {
			
			// add Preference
			long start2 = System.currentTimeMillis();
			Preference entity = PreferenceService.getHandle().getPrefEntity(uid);
			Node node = entity.getElement();

			if(log.isTraceEnabled())
				log.trace("--- PereferenceDAO getPreference: " + (System.currentTimeMillis() - start2));

			if(node != null){
				Xml2Json x2j = new Xml2Json();
				String rootPath = "/preference";
				x2j.addSkipRule(rootPath);
				x2j.addPathRule(rootPath + "/property", "name", true, true);
				String prefJsonStr = x2j.xml2json((Element)node);
				JSONObject prefObj = new JSONObject();
				JSONObject prefJSONObj = new JSONObject(prefJsonStr);

				// convert the logoffDateTime to the format for javascript.
				if(prefJSONObj.has("property")){
					JSONObject prefPropObj = prefJSONObj.getJSONObject("property");
					if(prefPropObj.has("logoffDateTime")){
						String logoffDateTime = prefPropObj.getString("logoffDateTime");
						Date logoffDate = new SimpleDateFormat( formatW3C ).parse(logoffDateTime);
						prefPropObj.put("logoffDateTime",
								new SimpleDateFormat( formatFullDate ).format(logoffDate));
						//prefPropObj.put("logoffDateTime",logoffDateTime );
					}
				}

				prefObj.put("preference", prefJSONObj);
				responseAray.put(prefObj);
				
	            
				// remove failed flag
				boolean isChanged = PreferenceService.updateProperty((Element)node, "failed", "false");
				if(isChanged && uid !=null){
					entity.setElement((Element)node);
					PreferenceService.getHandle().update(entity);
				}
			}
			
			JSONObject bvObj = new JSONObject();
			bvObj.append("buildVersion", getServletContext().getAttribute("buildTimestamp"));
			responseAray.put(bvObj);
			
			Collection widgetsList;
			if(tabOrderStr == null)
				widgetsList = getDisplayContents(uid, request);
			else{
				int tabOrder = Integer.parseInt(tabOrderStr);
				widgetsList = getDisplayContents(uid, tabOrder, request);
			}
			
			if (widgetsList == null || widgetsList.isEmpty()) {
				if(log.isInfoEnabled())
					log.info("widget not found.");
				response.sendError(500, "widget not found.");
				return;
			}
			
			Map resMap = I18NUtil.getResourceMap( I18NUtil.TYPE_LAYOUT,request.getLocale() );
			Set dynamicPanelWidgetIds = new HashSet();
			for(Iterator it = widgetsList.iterator(); it.hasNext();){
				Object[] t = ( Object[] )it.next();
				
				Tab tab = (Tab)t[0];
				Collection dynamicWidgets = ( Collection )t[1];
				Collection staticWidgets = ( Collection )t[2];
				
				//Because there is the possibility that the widgetID repeats depending on the setting situation of the dynamic panel of the initial screen setting, we remove it.
				List removeWidgetList = new ArrayList();
				for(Iterator widgets = dynamicWidgets.iterator(); widgets.hasNext();){
					Widget wid = (Widget)widgets.next();
					if(dynamicPanelWidgetIds.contains(wid.getWidgetid())){
						removeWidgetList.add(wid);
					}
					if(!"MultiRssReader".equals(wid.getType())){
						dynamicPanelWidgetIds.add(wid.getWidgetid());
					}
				}
				
				//FIXME dirty
				WidgetDAO.newInstance().getHibernateTemplate().deleteAll( removeWidgetList );
				
				responseAray.put( tab.toJSONObject( dynamicWidgets,staticWidgets,resMap ));
			}
			
			String jsonStr = responseAray.toString();
			writer.write(jsonStr);
			//writer.write(responseAray.toString());
			
		
		}catch (Exception e) {
			log.error("An exception occeurred.", e);
			response.sendError(500, e.getMessage());
		} 
		if(log.isTraceEnabled()){
			long end = System.currentTimeMillis();
			log.trace("--- WidgetServlet doPost: " + (end - start));
		}
		long end = System.currentTimeMillis();
	}
	
	protected Collection getDisplayContents(String uid, HttpServletRequest request) throws Exception{
		return TabService.getHandle().getWidgetsNode(uid, defaultUid);
	}
	
	protected Collection getDisplayContents(String uid, int tabOrder,
			HttpServletRequest req) throws Exception {
		
		Object tabObj = TabService.getHandle().getWidgetsNodeByTabOrder(uid, getDefaultUid(), tabOrder);
		if(tabObj == null)
			return null;
		
		List result = new ArrayList();
		result.add(tabObj);
		return (Collection)result;
	}

	
}
