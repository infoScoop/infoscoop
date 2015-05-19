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

package org.infoscoop.request.filter;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;


import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIConversion;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.context.UserContext;
import org.infoscoop.dao.RssCacheDAO;
import org.infoscoop.request.ProxyRequest;
import org.infoscoop.request.filter.rss.AtomHandler;
import org.infoscoop.request.filter.rss.RssHandler;
import org.infoscoop.request.filter.rss.RssJsonResultBuilder;
import org.infoscoop.request.filter.rss.RssResultBuilder;
import org.infoscoop.util.NoOpEntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;


public class RssFilter extends ProxyFilter {

	private static Log log = LogFactory.getLog(RssFilter.class);

		
	private static SAXParserFactory factory;

	public RssFilter() {
		// SAXParserFactory factory = SAXParserFactory.newInstance();
		factory = SAXParserFactory.newInstance();
		factory.setNamespaceAware(true);// Added to obtain element of msd name space.
		factory.setValidating( false );
	}

	protected int preProcess(HttpClient client, HttpMethod method,	ProxyRequest request) {
		request.addIgnoreHeader("user-agent");
		request.addIgnoreHeader("X-IS-DATETIMEFORMAT");
		request.addIgnoreHeader("X-IS-FRESHTIME");
//		request.addIgnoreHeader("_latestdatetime");
		request.addIgnoreHeader("X-IS-RSSMAXCOUNT");
		request.addIgnoreHeader("X-IS-PAGESIZE");
		
		String pageStr = request.getRequestHeader("X-IS-PAGE");
		if(pageStr != null){
			int pageNum = Integer.parseInt(pageStr);
			String uid = request.getPortalUid();
			if(uid == null)
				uid = request.getRequestHeader("MSDPortal-SessionId");
			InputStream jsonStream = RssCacheDAO.newInstance().getCache(uid, request.getTargetURL(), pageNum, UserContext.instance().getUserInfo().getCurrentSquareId());
			if(jsonStream != null){
				request.setResponseBody(jsonStream);
				request.putResponseHeader("Content-Type", "text/plain; charset=UTF-8");
				return 200;
			}
		}
		String refresh = request.getRequestHeader("X-IS-REFRESH");
		if(refresh != null){
			//TODO:cache clear
		}
		return 0;
	}

	protected InputStream postProcess(ProxyRequest request, InputStream responseStream) throws IOException {
		int contentLength = 0;
		//if (method.getResponseBodyAsStream() != null
		//		&& method.getResponseBodyAsStream().available() >= 0) {
		///	InputStream responseStream = method.getResponseBodyAsStream();
		InputStream resultStream = null;
		if(responseStream == null){
			
			byte[] responseBytes = "{statusCode:0,itemCount:0,latestItemCount:0,items : []}".getBytes("UTF-8");
			contentLength = responseBytes.length;
			resultStream = new ByteArrayInputStream(responseBytes);
			
		}else{
			if(log.isDebugEnabled()){
				responseStream = printDebug(responseStream);
			}

			
			byte[] responseBytes = process(request, responseStream);
			contentLength = responseBytes.length;
			//request.setResponseBody(new ByteArrayInputStream(responseBytes));
			resultStream = new ByteArrayInputStream(responseBytes);
		}

		if (contentLength > 0) {
			request.putResponseHeader("Content-Length", Integer
					.toString(contentLength));
		}
		request.putResponseHeader("Content-Type", "text/plain; charset=UTF-8");

		return resultStream;
	}

	@Override
	protected boolean allow204() {
		return true;
	}

	private InputStream printDebug(InputStream responseStream) {
		BufferedInputStream inputStream = new BufferedInputStream(responseStream);
		ByteArrayOutputStream returnStream = new ByteArrayOutputStream();
		int temp;
		try {
			while( (temp = inputStream.read() ) != -1 ){
				returnStream.write(temp);
			}
		} catch (IOException e) {
			log.error("", e);
		}
		log.debug("------< Start of Stream >-------");
		log.debug(new String(returnStream.toByteArray()));
		log.debug("------< End of RSS Stream >-------");
		
		return new ByteArrayInputStream(returnStream.toByteArray());
	}

	public static byte[] process(ProxyRequest request, InputStream responseStream)
			throws IOException {

		String dateTimeFormat = request.getRequestHeader("X-IS-DATETIMEFORMAT");
		if(dateTimeFormat != null){
			dateTimeFormat = URLDecoder.decode(dateTimeFormat, "UTF-8");
		}
		String freshTime = request.getRequestHeader("X-IS-FRESHTIME");
		String maxCountString = request.getRequestHeader("X-IS-RSSMAXCOUNT");
		int maxCount = Integer.MAX_VALUE;
		if(maxCountString != null){
			try{
			    int paramMaxCount = Integer.parseInt(maxCountString);
			    if(paramMaxCount > 0){
			    	maxCount = paramMaxCount;
			    }
			}catch(NumberFormatException e){
				log.warn("rssmaxcount \"" + maxCountString + "\" isn't integer value.");
			}
		}
		
		// Filtering
		String titleFilter = request.getRequestHeader("X-IS-TITLEFILTER");
		if (titleFilter != null)
			titleFilter = URLDecoder.decode(titleFilter, "UTF-8");
		String creatorFilter = request.getRequestHeader("X-IS-CREATORFILTER");
		if (creatorFilter != null)
			creatorFilter = URLDecoder.decode(creatorFilter, "UTF-8");
		String categoryFilter = request.getRequestHeader("X-IS-CATEGORYFILTER");
		if (categoryFilter != null)
			categoryFilter = URLDecoder.decode(categoryFilter, "UTF-8");
		
//		String freshDays = request.getRequestHeader("_freshdays");
//		String logoffDateTime = request.getRequestHeader("_logoffdatetime");
		
		BufferedInputStream bis = new BufferedInputStream(responseStream);
		
		boolean isAtom = isAtom(bis);
		
		XMLFilter.skipEmptyLine(bis);

		String pageSizeStr = request.getRequestHeader("X-IS-PAGESIZE");
		int pageSize = -1;
		if( pageSizeStr != null ) {
			try {
				pageSize = Integer.parseInt( pageSizeStr );
			} catch( NumberFormatException ex ) {
				log.warn("init parameter \"rssPageSize\" has illegal value");
			}
		}
		//if( pageSize < 0 )
		//	pageSize = 20;
		
		long start = System.currentTimeMillis();
		RssResultBuilder resultBuilder = new RssJsonResultBuilder(pageSize);
		RssHandler handler;
		if (isAtom) {
			handler = new AtomHandler(resultBuilder, dateTimeFormat, freshTime,
					maxCount, titleFilter, creatorFilter, categoryFilter);
		} else {
			handler = new RssHandler(resultBuilder, dateTimeFormat, freshTime,
					maxCount, titleFilter, creatorFilter, categoryFilter);
		}
		try {
			XMLReader reader = factory.newSAXParser().getXMLReader();
			reader.setProperty("http://xml.org/sax/properties/lexical-handler",handler );
			reader.setEntityResolver(NoOpEntityResolver.getInstance());
			reader.setContentHandler(handler);
			reader.parse(new InputSource(bis));

			if(log.isDebugEnabled()){
				long end = System.currentTimeMillis();
				log.debug("Rss parse duration:" + (end - start));
			}
			int pageCount = resultBuilder.getPageCount();
			if(pageCount > 1){
				String uid = request.getPortalUid();
				if (uid == null)
					uid = request.getRequestHeader("MSDPortal-SessionId");
				for(int pageNum = 0; pageNum < pageCount;pageNum++){
					RssCacheDAO.newInstance().insertCache(uid, request.getTargetURL(), pageNum, resultBuilder.getResult(pageNum), UserContext.instance().getUserInfo().getCurrentSquareId());
				}
			}
			
			return resultBuilder.getResult(0).getBytes("UTF-8");
		} catch (SAXException e) {
			log.error("Xml file at URL [ "+request.getTargetURL()+"] is failed to be analysed.[" + e.getLocalizedMessage() + "]", e);
			resultBuilder.setStatusCode(1);
			resultBuilder.setMessage("Failed to analyse xml.: " + e.getLocalizedMessage());
			return resultBuilder.getResult().getBytes("UTF-8");
		} catch (IOException e) {
			log.error("Xml file at URL [ "+request.getTargetURL()+"] is failed to be analysed.[" + e.getLocalizedMessage() + "]", e);
			resultBuilder.setStatusCode(1);
			resultBuilder.setMessage("Failed to analyse xml.: " + e.getLocalizedMessage());
			return resultBuilder.getResult().getBytes("UTF-8");
		} catch (ParserConfigurationException e) {
			log.error("Xml file at URL [ "+request.getTargetURL()+"] is failed to be analysed.[" + e.getLocalizedMessage() + "]", e);
			resultBuilder.setStatusCode(1);
			resultBuilder.setMessage("Failed to analyse xml.: " + e.getLocalizedMessage());
			return resultBuilder.getResult().getBytes("UTF-8");
		} finally {
			bis.close();
		}

	}

	public static boolean isAtom(InputStream is) throws IOException{
		is.mark(1);
		byte[] xmldec = new byte[500];
		is.read(xmldec);

		String xmlDecStr = new String(xmldec);
		boolean isAtom = false;
		if(xmlDecStr.indexOf("<feed") > 0){
			isAtom = true;
		}
		is.reset();
		if(log.isDebugEnabled()){
			log.debug( isAtom ? "Process atom." : "Process rss." );
		}
		
		return isAtom;
	}
	
}
