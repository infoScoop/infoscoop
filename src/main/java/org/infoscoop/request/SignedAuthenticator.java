package org.infoscoop.request;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthMessage;
import net.oauth.OAuthServiceProvider;
import net.oauth.client.OAuthClient;
import net.oauth.client.httpclient3.HttpClient3;
import net.oauth.signature.RSA_SHA1;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.acl.ISPrincipal;
import org.infoscoop.acl.SecurityController;
import org.infoscoop.dao.OAuthCertificateDAO;
import org.infoscoop.dao.model.OAuthCertificate;

public class SignedAuthenticator implements Authenticator {
	public static final OAuthClient CLIENT = new OAuthClient(new HttpClient3());

	private static Log log = LogFactory.getLog(SignedAuthenticator.class);

	public SignedAuthenticator() {
	}

	public void doAuthentication(HttpClient client, ProxyRequest request,
			HttpMethod method, String uid, String pwd)
			throws ProxyAuthenticationException {
		try {
			OAuthConsumer consumer = newConsumer();
			OAuthAccessor accessor = new OAuthAccessor(consumer);

			Map<String, List<String>> requestParameters = new HashMap<String, List<String>>();

			String targetUrlPath = analyzeUrl(request.getTargetURL(),
					requestParameters);
			
			Collection<Map.Entry<String, String>> params = request
					.getFilterParameters().entrySet();
			OAuthMessage message = new OAuthMessage("GET", targetUrlPath,
					params);
			message.addRequiredParameters(accessor);
			List<Map.Entry<String, String>> authParams = message
					.getParameters();
			List<NameValuePair> queryParams = buildQueryParams(
					requestParameters, authParams);

			String userId = SecurityController.getPrincipalByType(
					"UIDPrincipal").getName();
			queryParams.add(new NameValuePair("opensocial_viewer_id", userId));
			queryParams.add(new NameValuePair("opensocial_owner_id", userId));
			queryParams.add(new NameValuePair("opensocial_app_url", request
					.getRequestHeader("gadgetUrl")));
			queryParams.add(new NameValuePair("opensocial_app_id", request
					.getRequestHeader("moduleId")));
			method.setQueryString((NameValuePair[]) queryParams
					.toArray(new NameValuePair[0]));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getCredentialType() {
		return 3;
	}

	protected OAuthConsumer newConsumer() throws ProxyAuthenticationException {
		OAuthCertificate certificate = OAuthCertificateDAO.newInstance().get();
		if (certificate == null)
			throw new ProxyAuthenticationException(
					"a container's certificate is not set.");
		OAuthServiceProvider serviceProvider = new OAuthServiceProvider(null,
				null, null);
		OAuthConsumer consumer = new OAuthConsumer(null, certificate
				.getConsumerKey(), null, serviceProvider);
		consumer.setProperty("oauth_signature_method", "RSA-SHA1");
		try {
			String privateKey = new String(certificate.getPrivateKey(), "UTF-8");
			System.out.println(privateKey);
			consumer.setProperty(RSA_SHA1.PRIVATE_KEY, privateKey);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return consumer;
	}

	private static String getRequestUrl(URL url) {
		StringBuilder requestUrl = new StringBuilder();
		String scheme = url.getProtocol();
		int port = url.getPort();

		requestUrl.append(scheme);
		requestUrl.append("://");
		requestUrl.append(url.getHost());

		if ((port != -1)
				&& ((scheme.equals("http") && port != 80) || (scheme
						.equals("https") && port != 443))) {
			requestUrl.append(":");
			requestUrl.append(port);
		}

		requestUrl.append(url.getPath());
		return requestUrl.toString();
	}

	private String analyzeUrl(String url,
			Map<String, List<String>> requestParameters)
			throws MalformedURLException {
		URL u = new URL(url);
		String query = u.getQuery();
		if (query != null) {
			String[] params = query.split("&");
			for (int i = 0; i < params.length; i++) {
				System.out.println(params[i]);
				try {
					String[] param = params[i].split("=");
					String name = URLDecoder.decode(param[0], "UTF-8");
					if (name.startsWith("oauth") || name.startsWith("xoauth")
							|| name.startsWith("opensocial"))
						continue;
					List<String> values = requestParameters.get(name);
					if (values == null) {
						values = new ArrayList<String>();
						requestParameters.put(name, values);
					}
					values.add(URLDecoder.decode(param[1], "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}
		return getRequestUrl(u);
	}

	private List<NameValuePair> buildQueryParams(
			Map<String, List<String>> requestParameters,
			List<Map.Entry<String, String>> authParams) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		for (Map.Entry<String, List<String>> entry : requestParameters
				.entrySet()) {
			for (String value : entry.getValue()) {
				params.add(new NameValuePair(entry.getKey(), value));
			}
		}
		for (Map.Entry<String, String> entry : authParams) {
			params.add(new NameValuePair(entry.getKey(), entry.getValue()));
		}
		return params;
	}
}
