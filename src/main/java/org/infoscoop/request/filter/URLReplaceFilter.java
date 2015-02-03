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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;


import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xerces.xni.parser.XMLDocumentFilter;
import org.cyberneko.html.filters.Writer;
import org.infoscoop.request.ProxyRequest;

import java.nio.charset.MalformedInputException;

/**
 * If the site currently displayed is http://aaa/bbb/ccc,<br>
 * "/xxx" is changed to "http://aaa/xxx",and "xxx"is changed to "http://aaa/bbb/xxx"<br>
 * "http://xxx" is not changed<br>
 * The attributes that is changed are src,href,action,background.<br>
 * @author a-kimura
 */
public class URLReplaceFilter extends ProxyFilter {
	
	private static Log log = LogFactory.getLog(URLReplaceFilter.class);
	
	public URLReplaceFilter() {
		super();
	}
	
	protected int preProcess(HttpClient client, HttpMethod method,
			ProxyRequest request) {
		request.putRequestHeader("Accept", "text/html,application/xhtml+xml");
		
		request.addIgnoreHeader("if-modified-since");
		request.addIgnoreHeader("if-none-match");
		
		return 0;
	}
	
	protected InputStream postProcess(ProxyRequest request, InputStream responseStream) throws IOException {
		//if( !ProxyHtmlUtil.getInstance().isHtml(method.getResponseHeaders()))
		//	return 500;
		
		ProxyHtmlUtil.headerProcess( request );

		request.putResponseHeader("Pragma", "no-cache");
		request.putResponseHeader("Cache-Control", "no-cache");
		
		String contentType = request.getResponseHeader("Content-Type");
		if(contentType != null && !(contentType.toLowerCase().indexOf("text/html") == 0 || contentType.toLowerCase().indexOf("application/xhtml+xml") == 0) ){
			request.putResponseHeader("Content-Type", "text/html; charset=\"UTF-8\"");
			byte[] errorMsg = createErrorMessage("Error: Part select function can be only used for HTML.", "UTF-8");
			request.putResponseHeader("Content-Length", String.valueOf( errorMsg.length ) );
			//request.setResponseBody(new ByteArrayInputStream( errorMsg ) );
			
			return new ByteArrayInputStream( errorMsg ) ;
		}
		
		String encoding = request.getFilterEncoding();
		String outputEncoding = encoding != null && encoding.length() > 0 ? encoding
				: "UTF-8";
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try{
			String requestURL = request.getRedirectURL();
			if( requestURL == null )
				requestURL = request.getEscapedOriginalURL();

			ProxyHtmlUtil.getInstance().nekoProcess( responseStream,
					encoding,new XMLDocumentFilter[]{
					new ProxyHtmlUtil.AttachBaseTagFilter( requestURL ),
					new Writer(baos, outputEncoding)
				});
		}catch(MalformedInputException e){
			String message = "Invalid page is specifeid to the page.[" + encoding + "]\n UTF-8 is applied as default if any encoding is not specified.";
			log.error(message, e);
			
			byte[] errorMsg = createErrorMessage(message, "UTF-8");
			request.putResponseHeader("Content-Type", "text/html; charset=\"UTF-8\"");
			request.putResponseHeader("Content-Length", String.valueOf( errorMsg.length ) );
			//request.setResponseBody(new ByteArrayInputStream(errorMsg));
			return new ByteArrayInputStream(errorMsg);
		}
		
		byte[] responseBody = baos.toByteArray();
		request.putResponseHeader("Content-Type", "text/html; charset=" + outputEncoding);
		request.putResponseHeader("Content-Length",String.valueOf( responseBody.length ));
		//request.setResponseBody( new ByteArrayInputStream( responseBody ));
		
		return new ByteArrayInputStream( responseBody );
	}
	
	public byte[] createErrorMessage(String message, String encode) throws UnsupportedEncodingException{
		return 	("<html><head>"+
				"<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\">"+
				"</head><body>"+
				"<div style=\"color:red;\" id='url_replace_error_msg'>" + message + "</div>"+
				"</body></html>").getBytes(encode);
	}
	
}
