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
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.command.CommandResult;
import org.infoscoop.context.UserContext;
import org.infoscoop.dao.PreferenceDAO;
import org.infoscoop.dao.model.Preference;
import org.infoscoop.service.CommandExecutionService;
import org.infoscoop.service.PreferenceService;
import org.infoscoop.util.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * The servlet class which manages the update command of the widget information.
 * @author nakata
 */
public class CommandServlet extends HttpServlet {

	private static final long serialVersionUID = "org.infoscoop.web.CommandServlet"
			.hashCode();

	private Log log = LogFactory.getLog(this.getClass());

	// process to Get(for test)
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		String uid = (String) request.getSession().getAttribute("Uid");
		if (uid == null) {
			uid = "";
		}

		List resultList = new ArrayList();

		response.setContentType("text/xml");
		Writer writer = new OutputStreamWriter(response.getOutputStream(),
				"utf-8");
		
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.parse(request.getInputStream());
			Element root = doc.getDocumentElement();

			if(log.isDebugEnabled())
				log.debug("Commands Elememt: " + XmlUtil.xmlSerialize(root));

			if ("commands".equals(root.getNodeName())) {
				
				// version check
				String clientBuildVersion = root.getAttribute("buildVersion");
				
				String serverBuildVersion = (getServletContext().getAttribute("buildTimestamp") != null)?
						getServletContext().getAttribute("buildTimestamp").toString() : "";
				
				if(clientBuildVersion.equals(serverBuildVersion)){
					CommandExecutionService.getHandle().execute(uid, root, resultList);
				}else{
					// a flag of failed
					Preference entity = PreferenceDAO.newInstance().select(uid, UserContext.instance().getUserInfo().getCurrentSquareId());
					if(entity != null){	// When guest user login, this entity is null.
						Node prefNode = entity.getElement();
						boolean isChanged = PreferenceService.updateProperty((Element)prefNode, "failed", "true");
						if(isChanged){
				       		entity.setElement((Element)prefNode);
				       		PreferenceService.getHandle().update(entity);
						}
					};
					String errMsg = "A server module was revised. The customized information is not stored.";
					resultList.add(new CommandResult("", "warn", errMsg));
				}
			} else {
				String logMsg = "incorrect document format - expected top-level command tag";
				
				resultList.add(new CommandResult("", "failed", logMsg));

				log.error(logMsg);
			}
		} catch (Exception e) {
			resultList.add(new CommandResult("", "failed", "unexpected error occured. " + e.getMessage()));
			log.error("Unexpected exception occurred.", e);
            
		}

		writeResponse(writer, resultList);

		writer.flush();
		writer.close();

	}

	/**
	 * output the response.
	 * @param writer 
	 * @param resultList :ArrayList of an element maintaining the result of executing each command.
	 * @throws IOException
	 */
	private void writeResponse(Writer writer, List resultList)
			throws IOException {

		writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
		writer.write("<responses>");
		for (int i = 0; i < resultList.size(); i++) {
			if(resultList.get(i) != null)
				writer.write(((CommandResult) resultList.get(i)).toString());
		}
		writer.write("</responses>");
	}
	
}
