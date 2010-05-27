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
import java.io.IOException;
import java.io.InputStream;


import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.infoscoop.request.ProxyRequest;

public class XMLFilter extends ProxyFilter {

	protected int preProcess(HttpClient client, HttpMethod method, ProxyRequest request){
		request.addIgnoreHeader("user-agent");
		return 0;
	}

	protected InputStream postProcess(ProxyRequest request, InputStream responseStream) throws IOException {
		
		BufferedInputStream bis = new BufferedInputStream(responseStream);
		int count = skipEmptyLine(bis);

		//request.setResponseBody(bis);

		request.putResponseHeader("Content-Type", "text/xml");
		String contentLengthStr = request.getResponseHeader("Content-Length");
		if (contentLengthStr != null) {
			int contentLength = Integer.parseInt(contentLengthStr);
			request.putResponseHeader("Content-Length", String
					.valueOf(contentLength - count));
		}
		return bis;
	}

	/**
	 * @param is
	 * @return skippedCount
	 * @throws IOException
	 */
	public static int skipEmptyLine(InputStream is) throws IOException{
		is.mark(1);
		int count = 0;
		for (int temp = 0; (temp = is.read()) != -1; count++) {
			char tempCh = (char) temp;
			if (tempCh == '<') {
				break;
			} else {
				is.mark(1);
			}
		}
		is.reset();
		return count;
	}
	
}
