package org.infoscoop.web;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.dao.GadgetIconDAO;
import org.infoscoop.dao.model.GadgetIcon;
import org.json.JSONObject;

public class GadgetIconServlet extends HttpServlet {

	private static final long serialVersionUID = "jp.co.beacon_it.msd.portal.web.GadgetIconServlet"
			.hashCode();

	private static Log log = LogFactory.getLog(GadgetIconServlet.class);

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/plain; charset=UTF-8");
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");

		Writer writer = response.getWriter();
		try {
			List<GadgetIcon> icons = GadgetIconDAO.newInstance().all();
			JSONObject iconsJson = new JSONObject();
			for (GadgetIcon icon : icons) {
				iconsJson.put(icon.getType(), icon.getUrl());
			}
			writer.write(iconsJson.toString());
		} catch (Exception e) {
			log.error("unexpected error occured.", e);
			response.sendError(500, e.getMessage());
		} finally {
			writer.flush();
		}

	}
}
