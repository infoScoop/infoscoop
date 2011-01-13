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

package org.infoscoop.googleapps;

import java.io.IOException;
import java.io.Serializable;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.HashSet;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.web.SessionManagerFilter;
import org.openid4java.OpenIDException;
import org.openid4java.consumer.ConsumerManager;
import org.openid4java.consumer.InMemoryConsumerAssociationStore;
import org.openid4java.consumer.InMemoryNonceVerifier;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.discovery.html.HtmlResolver;
import org.openid4java.discovery.xri.XriDotNetProxyResolver;
import org.openid4java.discovery.xri.XriResolver;
import org.openid4java.discovery.yadis.YadisResolver;
import org.openid4java.message.AuthRequest;
import org.openid4java.message.ParameterList;
import org.openid4java.util.HttpFetcherFactory;

import com.google.step2.AuthRequestHelper;
import com.google.step2.AuthResponseHelper;
import com.google.step2.ConsumerHelper;
import com.google.step2.Step2;
import com.google.step2.discovery.DefaultHostMetaFetcher;
import com.google.step2.discovery.Discovery2;
import com.google.step2.discovery.HostMetaFetcher;
import com.google.step2.discovery.IdpIdentifier;
import com.google.step2.discovery.LegacyXrdsResolver;
import com.google.step2.discovery.XrdDiscoveryResolver;
import com.google.step2.http.DefaultHttpFetcher;
import com.google.step2.http.HttpFetcher;
import com.google.step2.openid.ui.UiMessageRequest;
import com.google.step2.xmlsimplesign.CachedCertPathValidator;
import com.google.step2.xmlsimplesign.CertValidator;
import com.google.step2.xmlsimplesign.DefaultCertValidator;
import com.google.step2.xmlsimplesign.DefaultTrustRootsProvider;
import com.google.step2.xmlsimplesign.TrustRootsProvider;
import com.google.step2.xmlsimplesign.Verifier;

/**
 * This filter supports OpenID Federated Login for Google Apps.
 * To use, add the following settings in web.xml.
 *  <pre>
 *	<filter>
 *		<filter-name>GoogleAppsOpenIDFilter</filter-name>
 *		<filter-class>org.infoscoop.googleapps.GoogleAppsOpenIDFilter</filter-class>
 *		<!-- domain (optional) : if you want to use one domain, set this parametaer.-->
 *		<init-param>
 *			<param-name>domain</param-name>
 *			<param-value>infoscoop.org</param-value>
 *		</init-param>
 *		<!-- socketTimeout(optional) : request timeout until Google OpenID Provider returns response. -->
 *		<init-param>
 *			<param-name>socketTimeout</param-name>
 *			<param-value></param-value>
 *		</init-param>
 *	</filter>
 *  </pre>
 * 
 * @author a-kimura
 */
public class GoogleAppsOpenIDFilter implements Filter {
	private static Log log = LogFactory.getLog(GoogleAppsOpenIDFilter.class);
	
	private static final String OP_IDENTIFIER_TEMPLATE = "https://www.google.com/accounts/o8/site-xrds?hd=";
	private Collection<String> excludePaths = new HashSet<String>();
	private String domain = null;
	
	protected ConsumerHelper consumerHelper;
	protected String realm = null;
	protected String returnToPath = "/openid_consumer_return";

	public void destroy() {
		// TODO Auto-generated method stub

	}

	public void doFilter(ServletRequest servletRequest,
			ServletResponse servletResponse, FilterChain chain)
			throws IOException, ServletException {
		// basically just check for openId parameters
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		HttpSession session = request.getSession();

		String actionName = request.getServletPath();
		String uid = (String) session.getAttribute("Uid");
		if (uid != null && "/openid_login".equalsIgnoreCase(actionName)) {
			response.sendRedirect(".");
			return;
		}
		if (uid == null) {
			UserInfo user = (UserInfo) session.getAttribute("user");
			if (user != null) {
				uid = user.getEmail();
				String userName = user.getFirstName() + " "
						+ user.getLastName();
				if (log.isInfoEnabled())
					log.info(uid + " is logged in by openid.");
				session.setAttribute("Uid", uid);
				String[] email = uid.split("@");
				if(email.length == 2)
					session.setAttribute("Domain", email[1]);
				else
					log.error("User id \"" + uid + "\" is invalid email address.");
				session.setAttribute(
						SessionManagerFilter.LOGINUSER_NAME_ATTR_NAME, userName);
			}
		}
		if (uid == null) {
			String host_url = request.getScheme()
					+ "://"
					+ request.getServerName()
					+ (request.getServerPort() != 80
							&& request.getServerPort() != 443 ? (":" + request
							.getServerPort()) : "") + request.getContextPath();

			if ("/openid_login".equalsIgnoreCase(actionName)) {
				try {
					String domainParam = domain != null ? domain : request
							.getParameter("hd");
					if(domainParam == null)
						throw new OpenIDException("domain must be set.");
					AuthRequest authReq = startAuthentication(domainParam, request);
					response.sendRedirect(authReq.getDestinationUrl(true));
				} catch (OpenIDException e) {
					log.error(e.getMessage(), e);
					String loginUrl = host_url + "/gapps_openid_login.jsp";
					response.sendRedirect(loginUrl);
				}
				return;
			} else if ("/openid_consumer_return".equalsIgnoreCase(actionName)) {
				try {
					UserInfo user = completeAuthentication(request);
					request.getSession().setAttribute("user", user);
					response.sendRedirect(".");
				} catch (OpenIDException e) {
					log.error(e.getMessage(), e);
					String loginUrl = host_url + "/gapps_openid_login.jsp";
					response.sendRedirect(loginUrl);
				}
				return;

			} else if (!isExcludePath(request.getServletPath())) {
				try {
        			if(this.domain != null){
        				String opIdentifier = OP_IDENTIFIER_TEMPLATE + URLEncoder.encode(domain, "UTF-8");
        				String loginUrl = host_url + "/openid_login?openid=" + URLEncoder.encode(opIdentifier , "UTF-8");
        				response.sendRedirect(loginUrl);            				
        			}else{
        				String loginUrl = host_url + "/gapps_openid_login.jsp";
        				response.sendRedirect(loginUrl);
        			}
				} catch (IOException e) {
					log.error(e.getMessage(), e);
					String loginUrl = host_url + "/gapps_openid_login.jsp";
					response.sendRedirect(loginUrl);
				}
				return;
			}
		}
		chain.doFilter(servletRequest, servletResponse);
	}

	public void init(FilterConfig config) throws ServletException {
		excludePaths.add("/gapps_openid_login.jsp");
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
		
		String domainParam = config.getInitParameter("domain");
		if(domainParam != null){
			if(log.isInfoEnabled())
				log.info("loginUrl is set to " + domainParam);
			this.domain = domainParam;
		}
		
		try {
			this.consumerHelper = createConsumerHelper(config);
		} catch (Throwable e) {
			e.printStackTrace();
			throw new ServletException(e);
		}
	}

	private boolean isExcludePath(String path) {
		if (excludePaths.contains(path))
			return true;

		for (String p : excludePaths) {
			if (path.startsWith(p))
				return true;
		}

		return false;
	}
	
	private ConsumerHelper createConsumerHelper(FilterConfig config){
		int socketTimeout = 30 * 1000;
		String socketTimeoutParam = config.getInitParameter("socketTimeout");
		if(socketTimeoutParam != null){
			if(log.isInfoEnabled())
				log.info("socketTimeout is set to " + socketTimeoutParam);
			socketTimeout = Integer.parseInt(socketTimeoutParam);
		}

		HttpFetcher httpFetcher = new DefaultHttpFetcher();
        HostMetaFetcher defaultMetaFetcher = new DefaultHostMetaFetcher(httpFetcher);
        HostMetaFetcher googleMetaFetcher = new GoogleHostedHostMetaFetcher(httpFetcher);
        HostMetaFetcher continuousMetaFetcher = new ContinuousHostMetaFetcher(googleMetaFetcher, defaultMetaFetcher);

        TrustRootsProvider trustRootsProvider = new DefaultTrustRootsProvider();
        CachedCertPathValidator cachedCertPathValidator = new CachedCertPathValidator(trustRootsProvider);
        Verifier verifier = new Verifier(cachedCertPathValidator, httpFetcher);
        CertValidator certValidator = new DefaultCertValidator();
        XrdDiscoveryResolver xrdDiscoveryResolver = new LegacyXrdsResolver(httpFetcher, verifier, certValidator);
        
        HttpFetcherFactory httpFetcherFactory = new HttpFetcherFactory();
        HtmlResolver htmlResolver = new HtmlResolver(httpFetcherFactory);
        YadisResolver yadisResolver = new YadisResolver(httpFetcherFactory);
        XriResolver xriResolver = new XriDotNetProxyResolver();
        
        Discovery2 discovery = new Discovery2(continuousMetaFetcher, xrdDiscoveryResolver, htmlResolver, yadisResolver, xriResolver);

		ConsumerManager consumerMgr = new ConsumerManager();
		consumerMgr.setAssociations(new InMemoryConsumerAssociationStore());
		consumerMgr.setNonceVerifier(new InMemoryNonceVerifier(5000));
		consumerMgr.setSocketTimeout(socketTimeout);

		return new ConsumerHelper(consumerMgr, discovery);
	}

	/**
	 * Builds an auth request for a given OpenID provider.
	 * 
	 * @param op
	 *            OpenID Provider URL. In the context of Google Apps, this can
	 *            be a naked domain name such as "saasycompany.com". The length
	 *            of the domain can exceed 100 chars.
	 * @param request
	 *            Current servlet request
	 * @return Auth request
	 * @throws org.openid4java.OpenIDException
	 *             if unable to discover the OpenID endpoint
	 */
	AuthRequest startAuthentication(String op, HttpServletRequest request)
			throws OpenIDException {
		IdpIdentifier openId = new IdpIdentifier(op);

		String realm = realm(request);
		String returnToUrl = returnTo(request);

		AuthRequestHelper helper = consumerHelper.getAuthRequestHelper(openId,
				returnToUrl);
		addAttributes(helper);

		HttpSession session = request.getSession();
		AuthRequest authReq = helper.generateRequest();
		authReq.setRealm(realm);

		UiMessageRequest uiExtension = new UiMessageRequest();
		uiExtension.setIconRequest(true);
		authReq.addExtension(uiExtension);

		session.setAttribute("discovered", helper.getDiscoveryInformation());
		return authReq;
	}

	/**
	 * Validates the response to an auth request, returning an authenticated
	 * user object if successful.
	 * 
	 * @param request
	 *            Current servlet request
	 * @return User
	 * @throws org.openid4java.OpenIDException
	 *             if unable to verify response
	 */
	UserInfo completeAuthentication(HttpServletRequest request)
			throws OpenIDException {
		HttpSession session = request.getSession();
		ParameterList openidResp = Step2.getParameterList(request);
		String receivingUrl = currentUrl(request);
		DiscoveryInformation discovered = (DiscoveryInformation) session
				.getAttribute("discovered");

		AuthResponseHelper authResponse = consumerHelper.verify(receivingUrl,
				openidResp, discovered);
		if (authResponse.getAuthResultType() == AuthResponseHelper.ResultType.AUTH_SUCCESS) {
			return onSuccess(authResponse, request);
		}
		return onFail(authResponse, request);
	}

	/**
	 * Adds the requested AX attributes to the request
	 * 
	 * @param helper
	 *            Request builder
	 */
	void addAttributes(AuthRequestHelper helper) {
		helper.requestAxAttribute(Step2.AxSchema.EMAIL, true)
				.requestAxAttribute(Step2.AxSchema.FIRST_NAME, true)
				.requestAxAttribute(Step2.AxSchema.LAST_NAME, true);
	}

	/**
	 * Reconstructs the current URL of the request, as sent by the user
	 * 
	 * @param request
	 *            Current servlet request
	 * @return URL as sent by user
	 */
	String currentUrl(HttpServletRequest request) {
		return Step2.getUrlWithQueryString(request);
	}

	/**
	 * Gets the realm to advertise to the IDP. If not specified in the servlet
	 * configuration. it dynamically constructs the realm based on the current
	 * request.
	 * 
	 * @param request
	 *            Current servlet request
	 * @return Realm
	 */
	String realm(HttpServletRequest request) {
		if (StringUtils.isNotBlank(realm)) {
			return realm;
		} else {
			return baseUrl(request);
		}
	}

	/**
	 * Gets the <code>openid.return_to</code> URL to advertise to the IDP.
	 * Dynamically constructs the URL based on the current request.
	 * 
	 * @param request
	 *            Current servlet request
	 * @return Return to URL
	 */
	String returnTo(HttpServletRequest request) {
		return new StringBuffer(baseUrl(request))
				.append(request.getContextPath()).append(returnToPath)
				.toString();
	}

	/**
	 * Dynamically constructs the base URL for the application based on the
	 * current request
	 * 
	 * @param request
	 *            Current servlet request
	 * @return Base URL (path to servlet context)
	 */
	String baseUrl(HttpServletRequest request) {
		StringBuffer url = new StringBuffer(request.getScheme()).append("://")
				.append(request.getServerName());

		if ((request.getScheme().equalsIgnoreCase("http") && request
				.getServerPort() != 80)
				|| (request.getScheme().equalsIgnoreCase("https") && request
						.getServerPort() != 443)) {
			url.append(":").append(request.getServerPort());
		}

		return url.toString();
	}

	/**
	 * Map the OpenID response into a user for our app.
	 * 
	 * @param helper
	 *            Auth response
	 * @param request
	 *            Current servlet request
	 * @return User representation
	 */
	UserInfo onSuccess(AuthResponseHelper helper, HttpServletRequest request) {
		return new UserInfo(helper.getClaimedId().toString(),
				helper.getAxFetchAttributeValue(Step2.AxSchema.EMAIL),
				helper.getAxFetchAttributeValue(Step2.AxSchema.FIRST_NAME),
				helper.getAxFetchAttributeValue(Step2.AxSchema.LAST_NAME));
	}

	/**
	 * Handles the case where authentication failed or was canceled. Just a
	 * no-op here.
	 * 
	 * @param helper
	 *            Auth response
	 * @param request
	 *            Current servlet request
	 * @return User representation
	 */
	UserInfo onFail(AuthResponseHelper helper, HttpServletRequest request) {
		return null;
	}

	public class UserInfo implements Serializable {
		private static final long serialVersionUID = 1L;

		private String claimedId;
		private String email;
		private String firstName;
		private String lastName;

		public UserInfo() {
		}

		public UserInfo(String claimedId, String email, String firstName,
				String lastName) {
			this.claimedId = claimedId;
			this.email = email;
			this.firstName = firstName;
			this.lastName = lastName;
		}

		public String getClaimedId() {
			return claimedId;
		}

		public String getEmail() {
			return email;
		}

		public String getFirstName() {
			return firstName;
		}

		public String getLastName() {
			return lastName;
		}
	}
}
