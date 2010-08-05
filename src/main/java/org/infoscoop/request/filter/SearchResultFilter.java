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
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xerces.xni.parser.XMLDocumentFilter;
import org.cyberneko.html.filters.Writer;
import org.infoscoop.request.ProxyRequest;

public class SearchResultFilter extends ProxyFilter {
	private Log log = LogFactory.getLog(this.getClass());
	
	protected int preProcess(HttpClient client, HttpMethod method, ProxyRequest request) {
		return 0;
	}
	
	protected InputStream postProcess(ProxyRequest request, InputStream responseStream) throws IOException {
		if( !ProxyHtmlUtil.isHtml(request.getResponseHeader("content-type")))
			return null;

		String collectMethod = "";
		String collectValue = null;
		
		String select = request.getRequestHeader("msdportal-select");
		if (select != null) {
			String[] selects = select.split("=");
			if (selects.length != 2)
				throw new IllegalArgumentException("invalid msdportal-select header.");
			
			collectMethod = selects[0];
			collectValue = URLDecoder.decode(selects[1], "utf-8");
			
			log.info("method = " + collectMethod + ", value = " + collectValue );
		}
		
		String url = request.getRedirectURL();
		if( url == null )
			url = request.getEscapedOriginalURL();
		
		String encoding = request.getFilterEncoding();
		String outputEncoding = encoding != null && encoding.length() > 0 ? encoding
				: "UTF-8";
		
		String match = null;
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		if( collectMethod.equals("id")) {
			GetTextByIDFilter matcher = new GetTextByIDFilter( collectValue ) ;
			
			ProxyHtmlUtil.getInstance().nekoProcess(responseStream,
				encoding,new XMLDocumentFilter[] {
					new ProxyHtmlUtil.AttachBaseTagFilter( url ),
					new Writer(baos, outputEncoding),
					matcher,
				});
			
			match = matcher.getText();
		} else if( collectMethod.equals("regexp") ){
			byte[] responseBody = ProxyRequest.stream2Bytes(responseStream);
			ProxyHtmlUtil.getInstance().nekoProcess( new ByteArrayInputStream(responseBody),
				encoding,new XMLDocumentFilter[] {
					new ProxyHtmlUtil.AttachBaseTagFilter( url ),
					new Writer(baos, outputEncoding)
				});
			Pattern pattern = Pattern.compile( collectValue );
			
			Matcher matcher = pattern.matcher( new String( responseBody,outputEncoding ));
			if( matcher.find() ){
				for (int i = 1; i <= matcher.groupCount(); i++) {
					match = matcher.group(i);
					if (match != null)
						break;
				}
			}
		} else {
			ProxyHtmlUtil.getInstance().nekoProcess(responseStream,
				encoding,new XMLDocumentFilter[] {
					new ProxyHtmlUtil.AttachBaseTagFilter( url ),
					new Writer(baos, outputEncoding)
				});
		}
		
		if ( match != null ) {
			log.info("match = " + match );
			request.putResponseHeader("MSDPortal-Match", match);
		}
		
		byte[] responseBody = baos.toByteArray();

		request.putResponseHeader("Content-Type", "text/html; charset=" + outputEncoding);
		request.putResponseHeader("Content-Length", Integer.toString( responseBody.length ) );
		//request.setResponseBody( new ByteArrayInputStream( responseBody ));
		
		return new ByteArrayInputStream( responseBody );
	}
	
}
