package org.infoscoop.admin.web;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.*;
import java.util.zip.ZipInputStream;

import javax.servlet.ServletException;
import javax.servlet.http.*;


import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.service.GadgetResourceService;
import org.infoscoop.service.GadgetService;
import org.infoscoop.service.GadgetResourceService.GadgetResourceException;
import org.json.JSONException;
import org.json.JSONObject;

public class UploadGadgetServlet extends HttpServlet {
	private static final long serialVersionUID = 6201737862447826027L;
	
	private static Log log = LogFactory.getLog(UploadGadgetServlet.class);
	public void doGet( HttpServletRequest req,HttpServletResponse resp )
			throws ServletException,IOException {
		String type = req.getParameter("type");
		
		resp.setContentType("application/zip; header=absent;");
		
		String fileName = type+".zip";
		resp.setHeader("Content-Disposition", "attachment; filename=" + fileName);
		resp.setHeader("Pragma", "no-cache");
		resp.setHeader("Cache-Control", "no-cache");
		
		byte[] data = new byte[0];
		try {
			data = GadgetResourceService.getHandle().selectResourcesZip( type );
		} catch( Exception ex ) {
			throw new RuntimeException( ex );
		}
		
		resp.getOutputStream().write( data );
		resp.getOutputStream().flush();
	}
	public void doPost(HttpServletRequest request, 
 			HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		
		response.setContentType("text/html; charset=UTF-8");
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");
		
		response.setStatus(200);
		
		Writer writer = response.getWriter();
		
		UploadFileInfo info;
		try {
			info = extractUploadFileInfo( request );
			if( info.isModuleMode() ) {
				uploadResources( info );
			} else {
				uploadResource( info );
			}
		} catch( GadgetResourceException ex ) {
			log.error("",ex );
			
			writeMessage( writer,null,ex.toJSON().toString() );
			return;
		} catch( Throwable ex ) {
			log.error("",ex );
			
			String message = ex.getMessage();
			if( ex.getCause() != null )
				message = ex.getCause().getMessage();
			
			writeMessage( writer,null,message );
			return;
		}
		
		writeMessage( writer,info.type,"success" );
		writer.flush();
	}
	
	private void uploadResource( UploadFileInfo info ) throws IOException {
		InputStream in = new BufferedInputStream( info.fileItem.getInputStream() );
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buf = new byte[5120];
		int reads = 0;
		while( !(( reads = in.read( buf )) < 0 ) )
			baos.write( buf,0,reads );
		
		GadgetResourceService service = GadgetResourceService.getHandle();
		if( info.create ) {
			service.insertResource( info.type,info.path,
					getFileName( info.fileItem.getName()),baos.toByteArray() );
		} else {
			service.updateResource( info.type,info.path,info.name,baos.toByteArray() );
		}
	}
	private String getFileName( String fileName ) {
		if( fileName.lastIndexOf("\\") >= 0 )
			fileName = fileName.substring( fileName.lastIndexOf("\\") +1 );
		
		return fileName;
	}
	private String getModuleName( String fileName ) {
		String moduleName = getFileName( fileName );
		
		if( moduleName.indexOf(".") > 0 )
			moduleName = moduleName.substring( 0,moduleName.lastIndexOf(".") );
		
		return moduleName;
	}
	private void uploadResources( UploadFileInfo info ) throws IOException {
		String moduleName = getModuleName( info.fileItem.getName() );
		if( info.type == null || "".equals( info.type ) ) {
			if( GadgetService.getHandle().selectGadget( moduleName ) == null &&
					GadgetResourceService.REGEX_NAME.matcher( moduleName ).matches()) {
				info.type = moduleName;
			} else {
				info.type = String.valueOf( new Date().getTime() );
			}
		}
		
		if( info.hasZipExt()) {
			GadgetResourceService.getHandle().updateResources( info.type,moduleName,
					new ZipInputStream( info.fileItem.getInputStream() ));
		} else {
			GadgetResourceService.getHandle().updateResources(
					info.type,info.fileItem.getInputStream() );
		}
	}
	
	private void writeMessage(Writer writer,String type, String message){
		if( message == null )
			message = "";
		
		try {
			writer.write("<html>");
			writer.write("<head>");
			writer.write("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
			writer.write("</head>");
			writer.write("<body type='"+type+"'>");
			writer.write( message );
			writer.write("</body>");
			writer.write("</html>");
		} catch (IOException e) {
			log.error("Unexpected error occurred.",e);
		}
	}
	
	private static class UploadFileInfo {
		FileItem fileItem;
		String mode;
		String type;
		String path;
		String name;
		boolean create;
		
		public boolean isModuleMode() {
			return "module".equalsIgnoreCase( mode );
		}
		public boolean hasZipExt() {
			return fileItem.getName().endsWith(".zip");
		}
	}
	
	private UploadFileInfo extractUploadFileInfo( HttpServletRequest req ) {
		UploadFileInfo info = new UploadFileInfo();
		try {
			DiskFileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(factory);
			
			//set the standard value that needs when it upload.
			factory.setSizeThreshold(1024);
			upload.setSizeMax(-1);
			
			List<FileItem> items = upload.parseRequest( req );
			
			//get the FileItem object
			for ( FileItem fItem : items ){
				if("data".equals(fItem.getFieldName()) ){
					info.fileItem = fItem;
				} else if("type".equals(fItem.getFieldName()) ){
					info.type = fItem.getString();
				} else if("mode".equals(fItem.getFieldName()) ){
					info.mode = fItem.getString();
				} else if("path".equals(fItem.getFieldName()) ){
					info.path = fItem.getString();
				} else if("name".equals(fItem.getFieldName()) ){
					info.name = fItem.getString();
				} else if("create".equals(fItem.getFieldName()) ) {
					try {
						info.create = Boolean.valueOf( fItem.getString());
					} catch( Exception ex ) {
						// ignore
					}
				}
			}
		} catch( FileUploadException ex ) {
			throw new GadgetResourceException("Unexpected error occurred while getting uplaod file.",
					"ams_gadgetResourceUnexceptedUploadFailed",ex );
		} catch( Exception ex ) {
			throw new GadgetResourceException( ex );
		}
		
		// check the file(for FireFox)
		if( info.fileItem == null || info.fileItem.getName().length() == 0 )
			throw new GadgetResourceException("Upload file not found.",
					"ams_gadgetResourceUploadFileNotFound");
		
		// check the file(for IE)
		try {
			BufferedReader br =  new BufferedReader(
					new InputStreamReader( info.fileItem.getInputStream() ));
			if( br.readLine() == null )
				throw new GadgetResourceException("The uplaod file is not found or the file is empty.",
						"ams_gadgetResourceUploadFileNotFoundOrEmpty");
		} catch( IOException ex ) {
			log.error("",ex );
			
			throw new GadgetResourceException("The upload file is not found or the file is empty.",
					"ams_gadgetResourceUploadFileNotFoundOrEmpty");
		}

		if( !info.isModuleMode() ) {
			if( info.type == null || info.path == null || info.name == null )
				throw new GadgetResourceException();
		}
		
		return info;
	}
}
