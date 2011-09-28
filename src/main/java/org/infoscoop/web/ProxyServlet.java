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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.request.ProxyRequest;
import org.infoscoop.service.CacheService;

public class ProxyServlet extends HttpServlet{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1359843604411251185L;

	private static Log log = LogFactory.getLog(ProxyServlet.class);

	private static final int METHOD_GET = 0;
	private static final int METHOD_POST = 1;

	public static int DEFAULT_TIMEOUT;
    
	private static long lastDeleteCachesTime;
	/**
	 * The doGet method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doProcess(request, response, METHOD_GET);
	}

	/**
	 * The doPost method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to
	 * post.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doProcess(request, response, METHOD_POST);
	}

	public void doProcess(
        HttpServletRequest request,
        HttpServletResponse response,
        int methodType)
        throws ServletException, IOException {
		int statusCode = 0;
		String url = request.getParameter("url");
		ProxyRequest proxyRequest = null;
        
        BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			
			String filterType = request.getParameter("filter");

			proxyRequest = new ProxyRequest(url, filterType);

			String filterEncoding = request.getParameter("filterEncoding");
			proxyRequest.setFilterEncoding(filterEncoding);
			
			proxyRequest.setLocales(request.getLocales());
			proxyRequest.setPortalUid((String) request.getSession().getAttribute("Uid"));
			
			int timeout = request.getIntHeader("MSDPortal-Timeout") - 1000;
			proxyRequest.setTimeout((timeout > 0)? timeout : DEFAULT_TIMEOUT);
			
			Enumeration headers = request.getHeaderNames();
			while(headers.hasMoreElements()){
				String headerName = (String)headers.nextElement();
				proxyRequest.putRequestHeader(headerName, request.getHeader(headerName));
			}
			
			//The certification for iframe
			String authTypeParam = request.getParameter("authType");
			if(authTypeParam != null && !"".equals(authTypeParam)){
				proxyRequest.putRequestHeader("authType", authTypeParam);
				proxyRequest.putRequestHeader("authuserid", request.getParameter("authuserid"));
				proxyRequest.putRequestHeader("authpassword", request.getParameter("authpassword"));
			}
			
			for( Enumeration names=request.getParameterNames();names.hasMoreElements();) {
				String name = ( String )names.nextElement();
				
				proxyRequest.setFilterParameter( name,request.getParameter( name ));
			}
			
			try {
				String otherMethod = request.getHeader("MSDPortal-method");
				if( otherMethod == null )
					otherMethod = request.getParameter("method");
				
				if(methodType == METHOD_GET){
					if (otherMethod != null && otherMethod.equalsIgnoreCase("delete")) {
						statusCode = proxyRequest.executeDelete();
					} else if("postCredential".equals(authTypeParam)) {
						statusCode = proxyRequest.executePost();
					} else {
						statusCode = proxyRequest.executeGet();
					}
		        } else {
		        	if ("get".equalsIgnoreCase( otherMethod )) {
	        			statusCode = proxyRequest.executeGet();
		        	} else {
		        		proxyRequest.setReqeustBody(request.getInputStream());
		        		if( otherMethod == null || "post".equalsIgnoreCase( otherMethod )) {
							statusCode = proxyRequest.executePost();
		        		} else if("put".equalsIgnoreCase( otherMethod )) {
		        			statusCode = proxyRequest.executePut();
		        		} else if("report".equalsIgnoreCase( otherMethod )) {
		        			statusCode = proxyRequest.executeReport();
		        			if(statusCode == 207)statusCode =200;
		        		}
		        	}
		        }
				
			} catch( SocketTimeoutException ex ) {
				// When the status code was 408, Firefox did not move well.
				// ABecause the cords such as 10408 are converted into 500 by Apache-GlassFish cooperation, we set it in a header.Apache-GlassFish.
				response.setHeader(HttpStatusCode.HEADER_NAME,
						HttpStatusCode.MSD_SC_TIMEOUT);
				throw ex;
			} catch(ConnectTimeoutException ex){
				// In the case of connection-timeout,  we don't try it again.
				//response.setHeader(HttpStatusCode.HEADER_NAME,
				//		HttpStatusCode.MSD_SC_TIMEOUT);
				throw ex;
			} 
			
			Map<String, List<String>> responseHeaders = proxyRequest.getResponseHeaders();
			if(statusCode == 401){
				
				String wwwAuthHeader = proxyRequest.getResponseHeader("WWW-Authenticate");
				if(wwwAuthHeader == null){
					statusCode = 403;
				}else if(wwwAuthHeader.toLowerCase().startsWith("basic")){
					statusCode = 200;
					response.setHeader("MSDPortal-AuthType", "basic");
				}else if(wwwAuthHeader.toLowerCase().startsWith("ntlm") || wwwAuthHeader.toLowerCase().startsWith("negotiate")){
					statusCode = 200;
					response.setHeader("MSDPortal-AuthType", "ntlm");
				}
			}
			response.setStatus(statusCode);
			
			if(log.isInfoEnabled()){
				log.info("Response-Status: " + statusCode);
			}
			
			StringBuffer headersSb = new StringBuffer();
			for(Map.Entry<String, List<String>> entry : responseHeaders.entrySet()){
				
				String name = entry.getKey();
				if (name.equalsIgnoreCase("Transfer-Encoding") || name.equalsIgnoreCase("X-Powered-By")) {
					continue;
				}
				
				for(String value : entry.getValue()){
					response.setHeader(entry.getKey(), value);
					headersSb.append(name + "=" + value + ",  ");
				}
			}
						
			if (!response.containsHeader("Connection")) {
				response.setHeader("Connection", "close");
				headersSb.append("Connection=close,  ");
			}
			
			if (log.isInfoEnabled())
				log.info("ResponseHeader: " + headersSb);

	
			String cacheHeader = request.getHeader("MSDPortal-Cache");
			
			InputStream responseBody = proxyRequest.getResponseBody();
			
			bos = new BufferedOutputStream(response
					.getOutputStream());

			if(responseBody != null){

				//bis = new BufferedInputStream(
				//		new ByteArrayInputStream(bytes));
				bis = new BufferedInputStream(responseBody);
				
				if(log.isDebugEnabled()){
					/*
					bis.mark(10240000);
					BufferedReader br =  new BufferedReader(new InputStreamReader(bis,"UTF-8"));
					
					StringBuffer logStr = new StringBuffer();
					String out = null;
					while((out = br.readLine())!= null){
						logStr.append(out);
					}
					
					log.debug(logStr);
					bis.reset();
					*/
					bis = printDebug(bis);
				}
				
				String cacheID = null;
				if (cacheHeader != null
						&& cacheHeader.equals("Cache-NoResponse")) {

					//Process to save the cash
					String uid = (String) request.getSession().getAttribute("Uid");
					if (uid == null)
						uid = request.getHeader("MSDPortal-SessionId");

					Map<String, List<String>> headerMap = proxyRequest.getResponseHeaders();
					
					try {
						cacheID = CacheService.getHandle().insertCache(
								uid,url /*proxyRequest.getTargetURL() + "?"
								+ request.getQueryString()*/,
								bis,
								headerMap);
						if(log.isInfoEnabled())
							log.info("save cache : id = " + cacheID);
					} catch (Exception e){
						log.error(e);
						//response.sendError(500, e.getMessage());
					}
				}
				
				if (cacheHeader != null && cacheHeader.equals("Cache-NoResponse") &&
						cacheID != null) {
					response.setHeader("MSDPortal-Cache-ID", cacheID);
					response.setHeader("Pragma", "no-cache");
					response.setHeader("Cache-Control", "no-cache");
					response.setHeader("Content-Length", "1");
					//response.setHeader("Content-Length", "0");
					//response.setHeader("Connection", "close");
					bos.write(0);
					bos.flush();
					//response.setStatus(204);
				} else {
					if(cacheHeader != null && cacheHeader.equals("No-Cache")){
						response.setHeader("Pragma", "no-cache");
						response.setHeader("Cache-Control", "no-cache");
					}
					if( !response.containsHeader("Content-Length") || statusCode == 500 ) {
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						
						byte[] b = new byte[1024];
						int c = 0;
						while ((c = bis.read(b)) != -1) {
							baos.write(b, 0, c);
						}
						
						byte[] data = baos.toByteArray();
						response.addHeader("Content-Length",String.valueOf( data.length ));
						
						bis = new BufferedInputStream( new ByteArrayInputStream( data ));
					}
					
					byte[] b = new byte[1024];
					int c = 0;
					while ((c = bis.read(b)) != -1) {
						bos.write(b, 0, c);
					}
					bos.flush();
				}
			}else{
				if(statusCode == HttpStatus.SC_NO_CONTENT){
					response.setHeader("Content-Length", "0");
				}else{
					response.setHeader("Content-Length", "1");
					bos.write(0);
					bos.flush();
				}
			}
			
			long elapsedtime = new Date().getTime() - lastDeleteCachesTime;
			if(elapsedtime > 86400000){
				log.info("Delete old public caches.");
				lastDeleteCachesTime = new Date().getTime();
				CacheService.getHandle().deleteOldPublicCaches();
			}
		} catch (Exception e) {
			
			log.error("Failed to get the URL. "
					+ buildMessage(statusCode, proxyRequest, url), e);
			response.sendError(500, e.getMessage());
		} finally {
			if(log.isInfoEnabled()){
				log.info("Succeeded in getting the URL. "
						+ buildMessage(statusCode, proxyRequest, url));
			}

			if(proxyRequest != null)
				proxyRequest.close();
			if (bis != null)
				bis.close();
			if (bos != null)
				bos.close();
		}
    }
	
	private String buildMessage(int statusCode, ProxyRequest proxyRequest,
			String url) {
		if (proxyRequest != null)
			return "[ url=" + proxyRequest.getTargetURL() + " ]"
					+ ":[status code="+ statusCode + " ]";
		return "[ url=" + url + " ]";
	}

	/**
	 * Initialization of the servlet. <br>
	 * 
	 * @throws ServletException
	 *             if an error occure
	 */
	public void init(ServletConfig config) throws ServletException {
		try {
			int to = Integer.parseInt(config.getInitParameter("timeout"));
			DEFAULT_TIMEOUT = to;
		} catch (Throwable t) {
			// just ignore.
		}
	}

	private BufferedInputStream printDebug(BufferedInputStream inputStream) {
		ByteArrayOutputStream returnStream = new ByteArrayOutputStream();
		int temp;
		try {
			while( (temp = inputStream.read() ) != -1 ){
				returnStream.write(temp);
			}
		} catch (IOException e) {
			log.error("", e);
		}
		log.debug("------< Start of response stream >-------");
		log.debug(new String(returnStream.toByteArray()));
		log.debug("------< End of response Stream >-------");
		
		return new BufferedInputStream(new ByteArrayInputStream(returnStream.toByteArray()));
	}

}
