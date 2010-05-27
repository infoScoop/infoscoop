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
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
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
import javax.xml.parsers.SAXParserFactory;


import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.dao.RssCacheDAO;
import org.infoscoop.dao.model.Rsscache;
import org.infoscoop.request.ProxyRequest;
import org.infoscoop.request.filter.RssFilter;
import org.infoscoop.request.filter.XMLFilter;
import org.infoscoop.request.filter.rss.AtomHandler;
import org.infoscoop.request.filter.rss.RssHandler;
import org.infoscoop.request.filter.rss.RssItem;
import org.infoscoop.request.filter.rss.RssJsonResultBuilder;
import org.infoscoop.request.filter.rss.RssRefineUtil;
import org.infoscoop.request.filter.rss.SortedRssJsonResultBuilder;
import org.infoscoop.util.NoOpEntityResolver;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;


/**
 * The servlet to display the sorted MultiRssReader.
 * <multiRss widgetId = "">
 *   <rss method="get" url="http://hoge/hoge.rss"/>
 * </multiRss>
 * @author hr-endoh
 *
 */
public class MultiRssServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	
	private static SAXParserFactory factory;
	private static DocumentBuilderFactory builderFactory;
	
	public MultiRssServlet() {
		// SAXParserFactory factory = SAXParserFactory.newInstance();
		factory = SAXParserFactory.newInstance();
		factory.setNamespaceAware(true);// We add it to pick up the element of the msd namespace.
		factory.setValidating( false );
		
		builderFactory = DocumentBuilderFactory.newInstance();
		builderFactory.setValidating( false );
	}
	
	private static Log log = LogFactory.getLog(MultiRssServlet.class);
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
		response.setStatus(403);
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
		String uid = (String)request.getSession().getAttribute("Uid");
    	if (uid == null)
			uid = request.getHeader("MSDPortal-SessionId");
    	
    	String widgetId = null;
    	NodeList urlList = null;
    	boolean clearCache = false;
    	try{
    		DocumentBuilder builder = builderFactory.newDocumentBuilder();
    		builder.setEntityResolver(NoOpEntityResolver.getInstance());
    		
    		Document requestDoc = builder.parse(request.getInputStream());
    		Element root = requestDoc.getDocumentElement();
    		
    		widgetId = root.getAttribute("widgetId");
    		urlList = root.getElementsByTagName("rss");
    		clearCache = "true".equalsIgnoreCase( root.getAttribute("clearCache"));
    	}catch(Exception e){
    		log.error(e.getMessage(), e);
    		response.sendError(500, e.getMessage());
    		return;
		}
    	
    	if(widgetId == null){
    		log.error("Must specify widgetId in request body.");
    		response.sendError(500, "Must specify widgetId in request body.");
    		return;
    	}
		
    	int pageSize = -1;
    	String pageSizeStr = request.getHeader("X-IS-PAGESIZE");
		if( pageSizeStr != null ) {
			try {
				pageSize = Integer.parseInt( pageSizeStr );
			} catch( NumberFormatException ex ) {
				log.warn("init parameter \"rssPageSize\" has unjust value");
			}
		}
		
//		if( pageSize < 0 )
//			pageSize = 20;
		
		String pageStr = request.getHeader("X-IS-PAGE");
		try{
			if(pageStr != null){
				int pageNum = Integer.parseInt(pageStr);
				getPageJson(response, uid, widgetId, pageNum);
			}else{
				if( clearCache )
					RssCacheDAO.newInstance().deleteCacheByUrl( uid,widgetId );
				
				mergeRssAnd2JSON(request, response, uid, widgetId, pageSize, urlList);//TODO: Should not be passed by NodeList.....
			}
		}catch(Exception e){
			log.error("",e);
			response.sendError(500, e.getMessage());
		}
	}

	private void getPageJson(HttpServletResponse response, String uid, String widgetId, int pageNum)throws Exception{
		InputStream jsonStream = RssCacheDAO.newInstance().getCache(uid, widgetId, pageNum);
		if(jsonStream != null){
			response.setHeader("Content-Type", "text/plain; charset=UTF-8");
			response.setContentLength(getStreamLength(jsonStream));

			BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream());
			byte[] b = new byte[1024];
			int c = 0;
			while ((c = jsonStream.read(b)) != -1) {
				bos.write(b, 0, c);
			}
			bos.flush();
		}
		return;
	}

	private void mergeRssAnd2JSON(HttpServletRequest request, HttpServletResponse response, String uid, String widgetId, int pageSize, NodeList urlList)throws Exception{
		
		try{
			RssJsonResultBuilder resultBuilder = new SortedRssJsonResultBuilder( pageSize );
			
			String dateTimeFormat = request.getHeader("X-IS-DATETIMEFORMAT");
			if(dateTimeFormat != null){
				dateTimeFormat = URLDecoder.decode(dateTimeFormat, "UTF-8");
			}
			String freshTime = request.getHeader("X-IS-FRESHTIME");
			String maxCountString = request.getHeader("X-IS-RSSMAXCOUNT");
			int maxCount = 1000;
			if(maxCountString != null){
				try{
				    int paramMaxCount = Integer.parseInt(maxCountString);
				    if(paramMaxCount >= 0){
				    	maxCount = paramMaxCount;
				    }
				}catch(NumberFormatException e){
					log.warn("rssmaxcount \"" + maxCountString + "\" isn't integer value.");
				}
			}
			
			// Norrowing
			String titleFilter = request.getHeader("X-IS-TITLEFILTER");
			if (titleFilter != null)
				titleFilter = URLDecoder.decode(titleFilter, "UTF-8");
			String creatorFilter = request.getHeader("X-IS-CREATORFILTER");
			if (creatorFilter != null)
				creatorFilter = URLDecoder.decode(creatorFilter, "UTF-8");
			String categoryFilter = request.getHeader("X-IS-CATEGORYFILTER");
			if (categoryFilter != null)
				categoryFilter = URLDecoder.decode(categoryFilter, "UTF-8");
						
			int DEFAULT_TIMEOUT = 15 *1000;
			
			boolean modified = false;
			Map cacheHeaders = new HashMap();
			Map errorMap = new HashMap();
			List siteCacheHeaders = new ArrayList();
			for(int i = 0; i < urlList.getLength();i++){
				Element rssEl = (Element)urlList.item(i);
				String url = rssEl.getAttribute("url");
				
				ProxyRequest proxyRequest = new ProxyRequest(url, "NoOperation");
				proxyRequest.setLocales(request.getLocales());
				proxyRequest.setPortalUid(uid);
	
				int timeout = request.getIntHeader("MSDPortal-Timeout") - 1000;
				proxyRequest.setTimeout((timeout > 0)? timeout : DEFAULT_TIMEOUT);
				//proxyRequest.setTimeout(timeout);

				proxyRequest.addIgnoreHeader("user-agent");
				proxyRequest.addIgnoreHeader("X-IS-DATETIMEFORMAT");
				proxyRequest.addIgnoreHeader("X-IS-FRESHTIME");
				proxyRequest.addIgnoreHeader("X-IS-REFRESH");
				proxyRequest.addIgnoreHeader("X-IS-RSSMAXCOUNT");
				proxyRequest.addIgnoreHeader("X-IS-PAGESIZE");
				
				Enumeration headers = request.getHeaderNames();
				while(headers.hasMoreElements()){
					String headerName = (String)headers.nextElement();
					proxyRequest.putRequestHeader(headerName, request.getHeader(headerName));
				}
				
				NodeList rssChildNodes = rssEl.getElementsByTagName("header");
				for( int j=0;j<rssChildNodes.getLength();j++ ) {
					Element header = ( Element )rssChildNodes.item( j );
					if( header.getFirstChild() != null ) {
						String name = header.getAttribute("name");
						String value = header.getFirstChild().getNodeValue();
						if( name == null || name.trim().length() == 0 || value == null || value.trim().length() == 0 )
							continue;
	
						proxyRequest.putRequestHeader( name, value );
					}
				}
				
				int statusCode = 0;
				String methodType = rssEl.getAttribute("method");
				try {
					if("post".equals(methodType)){
						statusCode = proxyRequest.executePost();
					}else{
						statusCode = proxyRequest.executeGet();
					}
				} catch( SocketTimeoutException ex ) {
					log.error("url: ["+url+"] socket timeout.", ex);
					errorMap.put( url,new Integer( HttpStatusCode.MSD_SC_TIMEOUT ));
				} catch(ConnectTimeoutException ex){
					log.error("url: ["+url+"] connection timeout.", ex);
					errorMap.put( url,new Integer( 500 ));
				} catch( SocketException ex ) {
					log.error("url: ["+url+"] socket error.", ex);
					errorMap.put( url,new Integer(HttpStatus.SC_NOT_FOUND ));
				} catch( IOException ex ) {
					log.error("url: ["+url+"] I/O error.", ex);
					errorMap.put( url,new Integer(HttpStatus.SC_NOT_FOUND ));
				} catch(Exception ex){
					log.error("url: ["+url+"]" + ex.getMessage(), ex);
					errorMap.put( url,new Integer( 500 ));
				}
				
				BufferedInputStream bis = null;
				if( errorMap.containsKey( url )) {
					// nothing

				} else if( statusCode == 204 ) {
					log.warn("url:[" + url + "] is no content #" + statusCode);
					modified = true;
				} else if( statusCode == 304 ) {
					log.warn("url:[" + url + "] is not modified #" + statusCode);
				} else if( statusCode != 200 ){
					log.error("url:["+url+"] had error status code #"+statusCode );
					errorMap.put(url, new Integer(statusCode));
				} else {
					log.info("url:[" + url + "] is succed #" + statusCode);
					
					try {
						modified = true;
						
						bis = new BufferedInputStream( proxyRequest.getResponseBody());
						
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						byte[] buf = new byte[10240];
						int c;
						while( ( c = bis.read( buf )) != -1 ) {
							baos.write( buf,0,c );
							baos.flush();
						}
						bis.close();
						
						byte[] data = baos.toByteArray();
						baos.close();
						
						//RssCacheDAO.newInstance().insertCache(uid, widgetId+url, -1,data );
						
						Map responseHeaders = proxyRequest.getResponseHeaders();
						String lastModifiedName = "Last-Modified".toLowerCase();
						if( responseHeaders.containsKey( lastModifiedName )) {
							siteCacheHeaders.add( lastModifiedName );
							siteCacheHeaders.add( responseHeaders.get( lastModifiedName ));
						}
						
						String etagName = "ETag".toLowerCase();
						if( responseHeaders.containsKey( etagName )) {
							siteCacheHeaders.add( etagName );
							siteCacheHeaders.add( responseHeaders.get( etagName ));
						}
						
						if( siteCacheHeaders.size() > 0 ) {
							cacheHeaders.put( url, siteCacheHeaders.toArray() );
							siteCacheHeaders.clear();
						}
						
						bis = new BufferedInputStream( new ByteArrayInputStream( data ));
					} catch( IOException ex ) {
						log.error("rss reading " + url + " is failed.",ex );
						cacheHeaders.remove( url );
						errorMap.put( url,new Integer( 500 ));
						
						bis.close();
						bis = null;
					}
				}
				
				if( bis == null )
					continue;
				
				RssHandler handler;
				
				boolean isAtom = RssFilter.isAtom(bis);
	
				XMLFilter.skipEmptyLine(bis);
				if (isAtom) {
					handler = new AtomHandler(resultBuilder, dateTimeFormat,
							freshTime, maxCount, titleFilter, creatorFilter,
							categoryFilter, i);
				} else {
					handler = new RssHandler(resultBuilder, dateTimeFormat,
							freshTime, maxCount, titleFilter, creatorFilter,
							categoryFilter, i);
				}
				
				try {
					XMLReader reader = factory.newSAXParser().getXMLReader();
					reader.setEntityResolver( NoOpEntityResolver.getInstance() );
					reader.setContentHandler( handler );
					reader.parse( new InputSource( bis ));
				} catch (SAXException e) {
					log.info("Parsing rss " +url +" is failed.", e);
					cacheHeaders.remove( url );
					errorMap.put( url,new Integer( HttpStatusCode.MSD_SC_CONTENT_PARSE_ERROR ));
				}
			}
			
			if( !modified && errorMap.isEmpty() ) {
				log.warn("multi rss is not modified.");
				response.setStatus( 304 );
				
				return;
			} else {
					try {
						long freshTimeLong = new Date().getTime();
						if(freshTime != null)
							freshTimeLong = Long.parseLong( freshTime.trim());
						setOldData(resultBuilder, uid, widgetId, freshTimeLong,
							titleFilter, creatorFilter, categoryFilter);
						
					} catch( NumberFormatException e ) {
						log.error("", e);
					}
					
				//}
				
				int pageCount = resultBuilder.getPageCount();
				// We create the result cash by all means.
				//if( pageCount > 1 ) {
					for(int pageNum = 0; pageNum < pageCount;pageNum++){
						RssCacheDAO.newInstance().insertCache(uid, widgetId, pageNum, resultBuilder.getResult(pageNum));
					}
				//}
			}
			
			response.addHeader("Content-Type", "text/plain; charset=UTF-8");
			
			String result = resultBuilder.getResult();
	
			if( !errorMap.isEmpty() ){
				JSONObject errors = new JSONObject(errorMap);
				result = "{errors:"+ errors.toString() +","+result.substring( result.indexOf("{")+1);
			}
			if( !cacheHeaders.isEmpty() ) {
				StringBuffer cacheHeadersBuf = new StringBuffer();
				cacheHeadersBuf.append("cacheHeaders : {");
				for( Iterator keys=cacheHeaders.keySet().iterator();keys.hasNext();) {
					String url = ( String )keys.next();
					Object[] headers = ( Object[] )cacheHeaders.get( url );
					
					cacheHeadersBuf.append("\"").append( url ).append("\" : {");
					for( int i=0;i<headers.length;i+=2) {
						cacheHeadersBuf.append("\"").append( headers[i]).append("\"");
						cacheHeadersBuf.append(" : '").append( headers[i+1]).append("'");
						if( i +2 < headers.length )
							cacheHeadersBuf.append(",");
					}
					cacheHeadersBuf.append("}");
					if( keys.hasNext())
						cacheHeadersBuf.append(",");
				}
				cacheHeadersBuf.append("}");
				
				result = "{"+cacheHeadersBuf.toString()+","+result.substring( result.indexOf("{")+1);
			}
			
			response.setContentLength(result.getBytes("UTF-8").length);
			
			OutputStreamWriter out = new OutputStreamWriter(response.getOutputStream(), "UTF-8");
			try {
				out.write(result);
				
				out.flush();
			} catch( SocketException ex ) {
				// ignore client abort exception
			} finally {
				if( out != null ) {
					try {
						out.close();
					} catch( IOException ex ) {
						// ignore
					}
				}
			}
		} catch (Exception e) {
			log.error("unexpected error occurred.", e);
			response.sendError(500, e.getMessage());
		}

	}
	
	public static void main(String args[]){
		
		RssJsonResultBuilder resultBuilder = new SortedRssJsonResultBuilder( 5 );
		long start = System.currentTimeMillis();
		List cacheList = RssCacheDAO.newInstance().getCaches("test", "tab10001_p_nikkeiBP");
		start = System.currentTimeMillis();
		//setOldData(resultBuilder, "test", "tab10001_p_nikkeiBP", 1);
		System.out.println(System.currentTimeMillis()- start);
		System.out.println(resultBuilder.getItemCount());
	}
	
	private static void setOldData(RssJsonResultBuilder resultBuilder,
			String uid, String widgetId, long freshTime, String titleFilter,
			String creatorFilter, String categoryFilter) {
		List cacheList = RssCacheDAO.newInstance().getCaches(uid, widgetId);
		
		for(int i = 0; i < cacheList.size(); i++){
			Rsscache cache = (Rsscache)cacheList.get(i);
			try {
				JSONObject rssJson = new JSONObject(new String(cache.getRss(),"UTF-8"));
				
				if(!rssJson.has("items"))continue;

				JSONArray items = rssJson.getJSONArray("items");
				for(int j = 0; j < items.length();j++){ 
					JSONObject itemJson = items.getJSONObject(j);
					if(itemJson.has("otherProperties")){
						JSONObject props = itemJson.getJSONObject("otherProperties");
					}
					
					String creator = itemJson.has("creator") ? getJSONString(itemJson
							.getString("creator"))
							: null;
					if (!RssRefineUtil.matchCreator(creator, creatorFilter))
						continue;

					String title = getJSONString(itemJson.getString("title"));
					if (!RssRefineUtil.matchTitle(title, titleFilter))
						continue;
					
					Map propMap = new HashMap();
					RssItem item = new RssItem(
							title,
							getJSONString( itemJson.getString("link")),
							itemJson.has("description") ? getJSONString( itemJson.getString("description") ) : null,
							itemJson.has("dateLong") ? new Date(itemJson.getLong("dateLong")) : null,
							itemJson.has("date") ? itemJson.getString("date") : null,
							creator != null ? creator : "",
							itemJson.has("creatorImg") ? getJSONString( itemJson.getString("creatorImg")) : null,
							toList(itemJson.optJSONArray("category")),
							propMap
					);
					itemJson.has("rssUrlIndex");
					JSONArray urlIndex = itemJson.getJSONArray("rssUrlIndex");
					for(int k = 0; k < urlIndex.length();k++){ 
						item.addRssUrlIndex(new Integer(urlIndex.getInt(k)));
					}
					resultBuilder.addItem(freshTime, item);
				}
			} catch (UnsupportedEncodingException e) {
				log.error(e.getMessage(), e);
			} catch (JSONException e) {
				log.error(e.getMessage(), e);
				try {
					PrintWriter pw = new PrintWriter(new FileWriter("d:\\errorJson.txt"));
					pw.println(new String(cache.getRss(),"UTF-8"));
					pw.close();
				} catch (IOException e1) {
					log.error("", e1);
				}
			}
		}
	}
	
	private static List<String> toList(JSONArray array) {
		if (array == null || array.length() == 0)
			return null;
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < array.length(); i++) {
			list.add(array.optString(i));
		}
		return list;
	}
	
	private static String getJSONString(String _value) {
		String value = JSONObject.quote(_value);
		return value.substring(1, value.length() - 1);
	}
	
	private int getStreamLength(InputStream is) throws IOException{
		is.mark(1);
		byte[] b = new byte[1024];
		int i = 0;
		int length = 0;
		while((i = is.read(b)) != -1){
			length += i;
		}
		
		is.reset();
		
		return length;
	}
	
}
