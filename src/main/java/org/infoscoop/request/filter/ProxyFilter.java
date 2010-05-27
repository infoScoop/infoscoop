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


import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.infoscoop.request.ProxyRequest;

/**
 * Interface that has same role as ServletFilter of Proxy Request in portal.
 * Do not use the thread local fields in implementation because Proxyfilter is not thread safe just same as servlet.
 * 
 * @author hr-endoh
 */
abstract public class ProxyFilter{
	
		
	/**
	 * Processing that executed before the request is described.
	 * 
	 * @param client
	 * @param method
	 * @param request
	 * @return Return 0 for successful completion. Return response code that should be returned to the client if error occurred. Return 500 that is caught at doFilter if an exception is thrown.
	 */
	abstract protected int preProcess(HttpClient client, HttpMethod method, ProxyRequest request);
	
	/**
	 * Processing that executed before the request is described.
	 * 
	 * @param client
	 * @param method
	 * @param request
	 * @return Return 0 for successful completion. Return response code that should be returned to the client if error occurred. Return 500 that is caught at doFilter if an exception is thrown.
	 * @throws IOException 
	 */
	abstract protected InputStream postProcess(ProxyRequest request, InputStream responseStream) throws IOException;
	
	protected boolean allow204(){return false;}
}
