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

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;


import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.infoscoop.request.ProxyRequest;

public class NoOperationFilter extends ProxyFilter{

	protected int preProcess(HttpClient client, HttpMethod method, ProxyRequest request) {
		return 0;
	}

	protected InputStream postProcess(ProxyRequest request, InputStream responseStream) throws IOException {
		
		for (String headerName: request.getRequestHeaders().keySet()) {
			if (headerName.startsWith("_response_")) {
				// The "10" means the length of "_response_".
				request.putResponseHeader(headerName.substring(10), request.getRequestHeader(headerName));
			}
		}
		return responseStream;
	}

}
