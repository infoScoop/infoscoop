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

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openid4java.OpenIDException;
import org.openid4java.consumer.ConsumerException;
import org.openid4java.consumer.ConsumerManager;
import org.openid4java.consumer.InMemoryConsumerAssociationStore;
import org.openid4java.consumer.InMemoryNonceVerifier;
import org.openid4java.consumer.VerificationResult;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.discovery.Identifier;
import org.openid4java.message.AuthRequest;
import org.openid4java.message.AuthSuccess;
import org.openid4java.message.ParameterList;

public class OpenIDFilter implements Filter {
	private static Log log = LogFactory.getLog(OpenIDFilter.class);
	private Collection<String> excludePaths = new HashSet<String>();
	private String opIdentifier = null;
	private ConsumerManager consumerMgr;
	
	public void destroy() {
		// TODO Auto-generated method stub

	}

	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
			FilterChain chain) throws IOException, ServletException {
		// basically just check for openId parameters
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpSession session = request.getSession();
        
        String actionName = request.getServletPath();
        String uid = (String) session.getAttribute("Uid");
        if (uid == null){

        	uid = (String) session.getAttribute("openid");
			if(log.isInfoEnabled())
				log.info(uid + " is logged in by openid.");
			session.setAttribute("Uid", uid);
        }
        if (uid == null){
			String host_url = request.getScheme() + "://" + request.getServerName() + (request.getServerPort() != 80 && request.getServerPort() != 443 ? (":" + request.getServerPort()) : "") + request.getContextPath();

			if("/openid_login".equalsIgnoreCase(actionName)){
        		try{
					String returnToUrl = host_url  + "/openid_consumer_return";
        			String openid = request.getParameter("openid");
        			List discoveries = this.consumerMgr.discover(openid);
        			DiscoveryInformation discovered = this.consumerMgr.associate(discoveries);

        			session.setAttribute("openid-disco", discovered);

        			AuthRequest authReq = this.consumerMgr.authenticate(discovered, returnToUrl);

        			if (! discovered.isVersion2() ){
        				response.sendRedirect(authReq.getDestinationUrl(true));
        			} else {
        				response.setContentType("text/html");
        				PrintWriter out = response.getWriter();
        				out.println("<html>");
        				out.println("<body onload=\"document.forms['openid-form-redirection'].submit();\">");
        				out.println("<form method=\"POST\" name=\"openid-form-redirection\" action=\"" + authReq.getOPEndpoint()+ "\">");
        				Map pm=authReq.getParameterMap();
        				Iterator keyit=pm.keySet().iterator();
                        Object key;
                        Object value;
                        while (keyit.hasNext()){
                            key=keyit.next();
                            value=pm.get(key);
                            out.println("<input type=\"hidden\" name=\"" + key + "\" value=\"" + value + "\"/>");
                        }
        				out.println("<button type=\"submit\">Continue...</button>");
        				out.println("</form>");
        				out.println("</body>");
        				out.println("</html>");
        				out.flush();
        			}
        		} catch (OpenIDException e) {
        			log.error(e.getMessage(), e);
        		}
    			return;
        	}else if("/openid_consumer_return".equalsIgnoreCase(actionName)){
        		try{
        			ParameterList responselist =
        				new ParameterList(request.getParameterMap());

        			DiscoveryInformation discovered =
        				(DiscoveryInformation) session.getAttribute("openid-disco");

        			StringBuffer receivingURL = request.getRequestURL();
        			String queryString = request.getQueryString();
        			if (queryString != null && queryString.length() > 0)
        				receivingURL.append("?").append(request.getQueryString());

        			VerificationResult verification = this.consumerMgr.verify(
        					receivingURL.toString(),
        					responselist, discovered);

        			// examine the verification result and extract the verified identifier
        			Identifier verified = verification.getVerifiedId();
        			if (verified != null){
        				AuthSuccess authSuccess =
        					(AuthSuccess) verification.getAuthResponse();

        				session.setAttribute("openid", authSuccess.getIdentity());
        				session.setAttribute("openid-claimed", authSuccess.getClaimed());
        				System.out.println(authSuccess.getClaimed());
        				response.sendRedirect(".");  // success
        			}
        		} catch (OpenIDException e) {
        			log.error(e.getMessage(), e);
        			if(this.opIdentifier != null)
        				response.sendRedirect(this.opIdentifier);
        			else{
        				String loginUrl = host_url + "/login.jsp";
        				response.sendRedirect(loginUrl);
        			}
        		}
    			return;

        	}else if(!isExcludePath(request.getServletPath())){
        		try {
        			if(this.opIdentifier != null){
        				String loginUrl = host_url + "/openid_login?openid=" + URLEncoder.encode(this.opIdentifier , "UTF-8");
        				response.sendRedirect(loginUrl);            				
        			}else{
        				String loginUrl = host_url + "/login.jsp";
        				response.sendRedirect(loginUrl);
        			}
        		} catch(IOException e) {
        			log.error(e.getMessage(), e);
        		}
    			return;
        	}
		}
		chain.doFilter(servletRequest, servletResponse);
	}

	public void init(FilterConfig config) throws ServletException {

		String excludePathStr = config.getInitParameter("excludePath");
		if(excludePathStr != null){
			String[] pathArray = excludePathStr.split(",");
			for(int i = 0; i < pathArray.length; i++){
				String path = pathArray[i].trim();
				if( path.endsWith("*")) {
					excludePaths.add( path.substring(0,path.length() -1 ));
				} else {
					excludePaths.add( path );
				}
			}
		}
		
		String opIdentifierParam = config.getInitParameter("opIdentifier");
		if(opIdentifierParam != null){
			if(log.isInfoEnabled())
				log.info("loginUrl is set to " + opIdentifierParam);
			this.opIdentifier = opIdentifierParam;
		}
		
		int socketTimeout = 30 * 1000;
		String socketTimeoutParam = config.getInitParameter("socketTimeout");
		if(socketTimeoutParam != null){
			if(log.isInfoEnabled())
				log.info("socketTimeout is set to " + socketTimeoutParam);
			socketTimeout = Integer.parseInt(socketTimeoutParam);
		}
		
		try {
			ConsumerManager newmgr = (ConsumerManager) config.getServletContext().getAttribute("openid-consumermanager");
			if(newmgr == null){
				newmgr = new ConsumerManager();
				newmgr.setAssociations(new InMemoryConsumerAssociationStore());
				newmgr.setNonceVerifier(new InMemoryNonceVerifier(5000));
				newmgr.setSocketTimeout(socketTimeout);
				config.getServletContext().setAttribute("openid-consumermanager",newmgr);
			}
			this.consumerMgr = newmgr;
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
	}

	private boolean isExcludePath( String path ) {
		if( excludePaths.contains( path ))
			return true;

		for( String p : excludePaths ) {
			if( path.startsWith( p ))
				return true;
		}

		return false;
	}

}
