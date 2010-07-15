package org.infoscoop.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthMessage;
import net.oauth.OAuthProblemException;
import net.oauth.server.OAuthServlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.dao.OAuthTokenDAO;
import org.infoscoop.dao.model.OAuthToken;
import org.infoscoop.request.OAuthAuthenticator;
import org.infoscoop.service.OAuthService;
import org.infoscoop.util.I18NUtil;

public class OAuthCallbackServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public static final String PATH = "/OAuth/Callback";

	private static Log log = LogFactory.getLog(JsonProxyServlet.class);

	/**
	 * Exchange an OAuth request token for an access token, and store the latter
	 * in cookies.
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	throws IOException, ServletException {
		OAuthConsumer consumer = null;
		try {
			HttpSession session = request.getSession();
			String uid = (String) session.getAttribute("Uid");
			String consumerName = request.getParameter("consumer");
			String gadgetUrl = request.getParameter("__GADGET_URL__");
			
			final OAuthMessage requestMessage = OAuthServlet.getMessage(
					request, null);
			consumer = OAuthAuthenticator.getConsumer(gadgetUrl, consumerName);
			
			OAuthAccessor accessor = new OAuthAccessor(consumer);
			OAuthToken token = OAuthTokenDAO.newInstance().getAccessToken(uid,
					gadgetUrl, consumerName);
			accessor.requestToken = token.getRequestToken();
			accessor.tokenSecret = token.getTokenSecret();

			final String expectedToken = accessor.requestToken;
			String requestToken = request.getParameter(OAuth.OAUTH_TOKEN);
			if (requestToken == null || requestToken.length() <= 0) {
				log.warn(request.getMethod() + " "
						+ OAuthServlet.getRequestURL(request));
				requestToken = expectedToken;
				if (requestToken == null) {
					OAuthProblemException problem = new OAuthProblemException(OAuth.Problems.PARAMETER_ABSENT);
					problem.setParameter(OAuth.Problems.OAUTH_PARAMETERS_ABSENT, OAuth.OAUTH_TOKEN);
					throw problem;
				}
			} else if (!requestToken.equals(expectedToken)) {
				OAuthProblemException problem = new OAuthProblemException("token_rejected");
				problem.setParameter("oauth_rejected_token", requestToken);
				problem.setParameter("oauth_expected_token", expectedToken);
				throw problem;
			}
			List<OAuth.Parameter> parameters = null;
			String verifier = request.getParameter(OAuth.OAUTH_VERIFIER);
			if (verifier != null) {
				parameters = OAuth.newList(OAuth.OAUTH_VERIFIER, verifier);
			}
			OAuthMessage result = OAuthAuthenticator.CLIENT.getAccessToken(accessor, (String)consumer.getProperty("accessTokenMethod"), parameters);
			if (accessor.accessToken == null) {
				OAuthProblemException problem = new OAuthProblemException(OAuth.Problems.PARAMETER_ABSENT);
				problem.setParameter(OAuth.Problems.OAUTH_PARAMETERS_ABSENT, OAuth.OAUTH_TOKEN);
				problem.getParameters().putAll(result.getDump());
				throw problem;
			}
			// add to db.
			OAuthService.getHandle().saveOAuthToken(uid, gadgetUrl,
					consumerName, null, accessor.accessToken,
					accessor.tokenSecret);
			
			response.setContentType("text/html");
			PrintWriter out = response.getWriter();
			out.println("<html><head><script> window.close();</script></head></html>");
			out.flush();
		} catch (Exception e) {
			log.error("unexpected error has occured.", e);
			RequestDispatcher dispatcher = request
					.getRequestDispatcher("/error.jsp");
			request.setAttribute("error_msg_id", "ms_oauthFailed");
			dispatcher.forward(request, response);
		}
	}

}
