package org.infoscoop.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TimeZone;

import javax.servlet.http.Cookie;

public final class RequestUtil {
	/**
	 * Delete a cookie except the designated variable name from CookieString
	 * 
	 * @param cookieString
	 * @param paramName
	 * @return
	 */
	public static String removeCookieParam(String cookieString, Set<String> paramNames) {
		StringTokenizer tok = new StringTokenizer(cookieString, ";", false);
		String resultCookieString = "";

		while (tok.hasMoreTokens()) {
			String token = tok.nextToken();
			int i = token.indexOf("=");
				if (i > -1) {
					for (String paramName : paramNames) {
						String name = token.substring(0, i).trim();
						if (paramName.equalsIgnoreCase(name)){
							if (resultCookieString.length() > 0)
								resultCookieString += ";";
							resultCookieString += token;
						}
					}
				} else {
					// we have a bad cookie.... just let it go
				}
			if(paramNames.isEmpty()){
				if (resultCookieString.length() > 0)
					resultCookieString += ";";
				resultCookieString += token;
			}
		}
		return resultCookieString.trim();
	}

	/**
	 * Encode a cookie as per RFC 2109. The resulting string can be used as the
	 * value for a <code>Set-Cookie</code> header.
	 * 
	 * @param cookie
	 *            The cookie to encode.
	 * @return A string following RFC 2109.
	 */
	public static String encodeCookie(Cookie cookie) {

		StringBuffer buf = new StringBuffer(cookie.getName());
		buf.append("=");
		buf.append(cookie.getValue());

		if (cookie.getComment() != null) {
			buf.append("; Comment=\"");
			buf.append(cookie.getComment());
			buf.append("\"");
		}

		if (cookie.getDomain() != null) {
			buf.append("; Domain=\"");
			buf.append(cookie.getDomain());
			buf.append("\"");
		}

		if (cookie.getMaxAge() >= 0) {
			buf.append("; Max-Age=\"");
			buf.append(cookie.getMaxAge());
			buf.append("\"");
		}

		if (cookie.getPath() != null) {
			buf.append("; Path=\"");
			buf.append(cookie.getPath());
			buf.append("\"");
		}

		if (cookie.getSecure()) {
			buf.append("; Secure");
		}

		if (cookie.getVersion() > 0) {
			buf.append("; Version=\"");
			buf.append(cookie.getVersion());
			buf.append("\"");
		}

		return (buf.toString());
	}

	/**
	 * Filter the specified message string for characters that are sensitive in
	 * HTML. This avoids potential attacks caused by including JavaScript codes
	 * in the request URL that is often reported in error messages.
	 * 
	 * @param message
	 *            The message string to be filtered
	 */
	public static String filter(String message) {

		if (message == null)
			return (null);

		char content[] = new char[message.length()];
		message.getChars(0, message.length(), content, 0);
		StringBuffer result = new StringBuffer(content.length + 50);
		for (int i = 0; i < content.length; i++) {
			switch (content[i]) {
			case '<':
				result.append("&lt;");
				break;
			case '>':
				result.append("&gt;");
				break;
			case '&':
				result.append("&amp;");
				break;
			case '"':
				result.append("&quot;");
				break;
			default:
				result.append(content[i]);
			}
		}
		return (result.toString());

	}

	/**
	 * Normalize a relative URI path that may have relative values ("/./",
	 * "/../", and so on ) it it. <strong>WARNING</strong> - This method is
	 * useful only for normalizing application-generated paths. It does not try
	 * to perform security checks for malicious input.
	 * 
	 * @param path
	 *            Relative path to be normalized
	 */
	public static String normalize(String path) {

		if (path == null)
			return null;

		// Create a place for the normalized path
		String normalized = path;

		if (normalized.equals("/."))
			return "/";

		// Add a leading "/" if necessary
		if (!normalized.startsWith("/"))
			normalized = "/" + normalized;

		// Resolve occurrences of "//" in the normalized path
		while (true) {
			int index = normalized.indexOf("//");
			if (index < 0)
				break;
			normalized = normalized.substring(0, index)
					+ normalized.substring(index + 1);
		}

		// Resolve occurrences of "/./" in the normalized path
		while (true) {
			int index = normalized.indexOf("/./");
			if (index < 0)
				break;
			normalized = normalized.substring(0, index)
					+ normalized.substring(index + 2);
		}

		// Resolve occurrences of "/../" in the normalized path
		while (true) {
			int index = normalized.indexOf("/../");
			if (index < 0)
				break;
			if (index == 0)
				return (null); // Trying to go outside our context
			int index2 = normalized.lastIndexOf('/', index - 1);
			normalized = normalized.substring(0, index2)
					+ normalized.substring(index + 3);
		}

		// Return the normalized path that we have completed
		return (normalized);

	}

	/**
	 * Parse the character encoding from the specified content type header. If
	 * the content type is null, or there is no explicit character encoding,
	 * <code>null</code> is returned.
	 * 
	 * @param contentType
	 *            a content type header
	 */
	public static String parseCharacterEncoding(String contentType) {

		if (contentType == null)
			return (null);
		int start = contentType.indexOf("charset=");
		if (start < 0)
			return (null);
		String encoding = contentType.substring(start + 8);
		int end = encoding.indexOf(';');
		if (end >= 0)
			encoding = encoding.substring(0, end);
		encoding = encoding.trim();
		if ((encoding.length() > 2) && (encoding.startsWith("\""))
				&& (encoding.endsWith("\"")))
			encoding = encoding.substring(1, encoding.length() - 1);
		return (encoding.trim());

	}

	/**
	 * Parse a cookie header into an array of cookies according to RFC 2109.
	 * 
	 * @param header
	 *            Value of an HTTP "Cookie" header
	 */
	public static Cookie[] parseCookieHeader(String header) {

		if ((header == null) || (header.length() < 1))
			return (new Cookie[0]);

		ArrayList cookies = new ArrayList();
		while (header.length() > 0) {
			int semicolon = header.indexOf(';');
			if (semicolon < 0)
				semicolon = header.length();
			if (semicolon == 0)
				break;
			String token = header.substring(0, semicolon);
			if (semicolon < header.length())
				header = header.substring(semicolon + 1);
			else
				header = "";
			try {
				int equals = token.indexOf('=');
				if (equals > 0) {
					String name = token.substring(0, equals).trim();
					String value = token.substring(equals + 1).trim();
					cookies.add(new Cookie(name, value));
				}
			} catch (Throwable e) {
				;
			}
		}

		return ((Cookie[]) cookies.toArray(new Cookie[cookies.size()]));

	}

	static public String removeQueryStringParam(String queryString, String paramName) {
		String resultString = "";
		
		if (queryString == null) {
			return queryString;
		}
		StringTokenizer st = new StringTokenizer(queryString, "&");
		while (st.hasMoreTokens()) {
			String pair = (String) st.nextToken();
			int pos = pair.indexOf('=');
			if (pos != -1) {
				String key = pair.substring(0, pos);
				if(paramName.equals(key)) continue;
			}
			
			if (resultString.length() > 0)
				resultString += "&";
			resultString += pair;
		}
		return resultString;
	}

	/**
	 * get a value of charset in Content-Type
	 * 
	 * @param str
	 * @return charset
	 */
	public static String getCharset(String content_type){
		if(content_type == null) return null;
		
		String[] strs = content_type.split(";");
		for(int i=0;i<strs.length;i++){
			String set = strs[i].trim().toLowerCase();
			if(set.indexOf("charset") != -1){
				String[] sets = set.split("=");
				if(sets.length>1) return sets[1];
			}
		}
		return null;
	}

}
