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
