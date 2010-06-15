package org.infoscoop.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import net.oauth.ConsumerProperties;
import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthException;
import net.oauth.OAuthMessage;
import net.oauth.ParameterStyle;
import net.oauth.client.OAuthClient;
import net.oauth.client.OAuthResponseMessage;
import net.oauth.client.httpclient3.HttpClient3;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.RedirectException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class OAuthAuthenticator implements Authenticator {
	public static final OAuthClient CLIENT = new OAuthClient(new HttpClient3());

	private static String AUTH_CALLBACK_URL = "oauthcallback";
    private static Properties consumerProperties = null;
	private static ConsumerProperties consumers = null;

	private static Log log = LogFactory.getLog(OAuthAuthenticator.class);
	
	public OAuthAuthenticator(){
		String resourceName = "consumer.properties";
		try {
			consumerProperties = ConsumerProperties
				.getProperties(ConsumerProperties.getResource(
					resourceName, OAuthAuthenticator.class
					.getClassLoader()));
			consumers = new ConsumerProperties(consumerProperties);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public OAuthConsumer getConsumer(String name) throws MalformedURLException{
		return consumers.getConsumer(name);
	}
	
	public void doAuthentication(HttpClient client, ProxyRequest request,
			HttpMethod method, String uid, String pwd)
			throws ProxyAuthenticationException {
		String name = request.getRequestHeader("oauthServiceName");
		try {
			OAuthConsumer consumer = consumers.getConsumer(name);
	        	        
	        OAuthAccessor accessor = newAccessor(consumer, request);
	        if (accessor.accessToken == null) {
		       	getRequestToken(request, accessor);
	        }
	        /*
	        OAuthResponseMessage result = CLIENT.access(accessor.newRequestMessage(OAuthMessage.GET,
                    "http://www.google.com/m8/feeds/contacts/default/full", null), ParameterStyle.AUTHORIZATION_HEADER);
            int status = result.getHttpResponse().getStatusCode();
            String st = null;
            BufferedReader br = new BufferedReader(new InputStreamReader(result.getBodyAsStream()));
            while( (st = br.readLine()) != null ){
            	System.out.println(st);
            }
            */
	        Collection<Map.Entry<String, String>> parms = request.getFilterParameters().entrySet();
	        OAuthMessage message = new OAuthMessage("GET", request.getTargetURL(), parms);
	        message.addRequiredParameters(accessor);
	        String authHeader = message.getAuthorizationHeader(null);
	        request.putRequestHeader("Authorization", authHeader);
	        
            // Find the non-OAuth parameters:
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OAuthException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}
	
	public int getCredentialType() {
		// TODO Auto-generated method stub
		return 3;
	}

	/**
	 * Construct an accessor from cookies. The resulting accessor won't
     * necessarily have any tokens.
     */
    private static OAuthAccessor newAccessor(OAuthConsumer consumer, ProxyRequest request)
            throws OAuthException {
        OAuthAccessor accessor = new OAuthAccessor(consumer);
        String consumerName = (String) consumer.getProperty("name");
        //accessor.requestToken = cookies.get(consumerName + ".requestToken");
        accessor.requestToken = request.getRequestHeader("requesttoken");
        //accessor.accessToken = cookies.get(consumerName + ".accessToken");
        accessor.accessToken = request.getRequestHeader("accesstoken");
        //accessor.tokenSecret = cookies.get(consumerName + ".tokenSecret");
        accessor.tokenSecret = request.getRequestHeader("tokensecret");
        return accessor;
    }
    
	/**
     * Get from oauth example CookieConsumer.
     * @throws IOException 
     * @throws URISyntaxException 
	 * @throws ProxyAuthenticationException 
     * 
     * @throws RedirectException
     *             to obtain authorization
     */
    private static void getRequestToken(ProxyRequest request, OAuthAccessor accessor)
        throws OAuthException, IOException, URISyntaxException, ProxyAuthenticationException
    {
        final String consumerName = (String) accessor.consumer.getProperty("name");
        final String callbackURL = getCallbackURL(request, consumerName);
        List<OAuth.Parameter> parameters = OAuth.newList(OAuth.OAUTH_CALLBACK, callbackURL);
        // Google needs to know what you intend to do with the access token:
        Object scope = accessor.consumer.getProperty("request.scope");
        if (scope != null) {
            parameters.add(new OAuth.Parameter("scope", scope.toString()));
        }
        OAuthMessage response = CLIENT.getRequestTokenResponse(accessor, null, parameters);
        request.putResponseHeader(consumerName + ".requesttoken", accessor.requestToken);
        request.putResponseHeader(consumerName + ".tokensecret", accessor.tokenSecret);
        String authorizationURL = accessor.consumer.serviceProvider.userAuthorizationURL;
        
                authorizationURL = OAuth.addParameters(authorizationURL //
                , OAuth.OAUTH_TOKEN, accessor.requestToken);
        if (response.getParameter(OAuth.OAUTH_CALLBACK_CONFIRMED) == null) {
            authorizationURL = OAuth.addParameters(authorizationURL //
                    , OAuth.OAUTH_CALLBACK, callbackURL);
        }
        request.putResponseHeader("oauthApprovalUrl", authorizationURL);
        throw new ProxyAuthenticationException("Redirect to authorization url.");
    }

    private static String getCallbackURL(ProxyRequest request, String consumerName)
        throws IOException {
        URL base = new URL("http://localhost:8080/infoscoop/" + AUTH_CALLBACK_URL );
        return OAuth.addParameters(base.toExternalForm() //
                , "consumer", consumerName //
                );
    }

}
