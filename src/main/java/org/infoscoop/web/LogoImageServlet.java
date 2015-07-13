package org.infoscoop.web;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.dao.model.Logo;
import org.infoscoop.service.LogoService;
import org.infoscoop.service.PropertiesService;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Iterator;
import java.util.List;

public class LogoImageServlet extends HttpServlet {
	private static final long serialVersionUID = 6201737862447826027L;
	private static Log log = LogFactory.getLog(LogoImageServlet.class);

	private static final String EXIST_PATH = "/existsImage";
	private static final String GET_PATH = "/get";

	public void doGet( HttpServletRequest req,HttpServletResponse resp ) throws ServletException,IOException {
		String action = ((HttpServletRequest)req).getPathInfo();
		Logo logo = LogoService.getHandle().getLogo();

		resp.setHeader("Pragma", "no-cache");
		resp.setHeader("Cache-Control", "no-cache");

		// get square name
		if(GET_PATH.equals(action)) {
			byte[] data = new byte[0];
			try {
				if(logo != null) {
					resp.setContentType(logo.getType() + ";");
					data = logo.getLogo();
				}
			} catch( Exception ex ) {
				throw new RuntimeException( ex );
			}
			resp.getOutputStream().write( data );
			resp.getOutputStream().flush();
		} else if(EXIST_PATH.equals(action)) {
			resp.setContentType("text/plain;charset=utf-8");
			boolean result = false;
			if(logo != null)
				result = true;

			resp.getWriter().write(String.valueOf(result));
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");

		response.setContentType("text/html;charset=utf-8");
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setStatus(200);

		try {
			byte[] result = null;
			String contentType = null;

			DiskFileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(factory);
			factory.setSizeThreshold(1048576);
			upload.setSizeMax(1048576);
			upload.setHeaderEncoding("UTF-8");

			List list = upload.parseRequest(request);
			Iterator iterator = list.iterator();
			while(iterator.hasNext()){
				FileItem fItem = (FileItem)iterator.next();
				result = extractLogoImage(fItem);
				contentType = fItem.getContentType();
			}
			if(result != null)
				LogoService.getHandle().saveLogo(result, contentType);
		}catch (Exception e) {
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			JSONObject json = new JSONObject();
			try {
				json.put("message", "ams_gadgetResourceUpdateFailed");
				json.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
				response.getWriter().write(json.toString());
			} catch (JSONException ex) {}
			return;
		}
	}

	private byte[] extractLogoImage(FileItem fItem) throws Exception{
		byte[] result = null;
		if(!(fItem.isFormField()) && fItem.getContentType().matches("image/(jpeg|png|gif)")){
			InputStream is = fItem.getInputStream();
			ByteArrayOutputStream b = new ByteArrayOutputStream();
			OutputStream os = new BufferedOutputStream(b);
			int c;
			try {
				while ((c = is.read()) != -1) {
					os.write(c);
				}
			} catch (IOException e) {
				throw new IOException(e);
			} finally {
				if (os != null) {
					try {
						os.flush();
						os.close();
						result = b.toByteArray();
					} catch (IOException e) {
						throw new IOException(e);
					}
				}
			}
		}
		return result;
	}
}
