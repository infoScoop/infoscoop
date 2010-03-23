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
