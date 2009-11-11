package org.infoscoop.request.proxy;


import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.apache.commons.digester.Digester;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.NTCredentials;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.service.ProxyConfService;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ProxyConfig {

	private static Log log = LogFactory.getLog(ProxyConfig.class);

	private static ProxyConfig config = new ProxyConfig();

	private static boolean isInitialized = false;

	private List caseList = new Vector();

	private ProxyConfigCase defaultCase;

	private static long lastCheckedTime;
	private static String m_lastmodified;

	public static ProxyConfig getInstance() {
		long elapsedtime = new Date().getTime() - lastCheckedTime;
		if(elapsedtime > 30000){
			try {
				String date = ProxyConfService.getHandle().getLastModifiedDate();
				if (m_lastmodified != null && m_lastmodified.equals(date)) {
					if (log.isInfoEnabled())
						log.info("##### ProxyConfiguration data is \"NOT MODIFIED\".");
				} else {
					if (log.isInfoEnabled())
						log.info("##### ProxyConfiguration data is \"MODIFIED\".");

					ProxyConfig.init(ProxyConfService.getHandle().getProxyConfInSource());

					if (log.isInfoEnabled())
						log.info("##### ProxyConfiguration initialize SUCCESS!!");
				}
				lastCheckedTime = new Date().getTime();
				m_lastmodified = date;
			} catch (Exception e) {
				log.error("failed in re-acquisition of the proxy setting.", e);
			}
		}

		return config;
	}

	public static void init(InputSource is) throws IOException, SAXException {
		ProxyConfig config2 = new ProxyConfig();
		Digester digester = new Digester();
		digester.setClassLoader(Thread.currentThread().getContextClassLoader());
		digester.push(config2);
		digester.addObjectCreate("proxy-config/case", ProxyConfigCase.class);
		digester.addSetNext("proxy-config/case", "addCase");
		digester.addSetProperties("proxy-config/case");
		digester.addSetTop("proxy-config/case/headers", "setAllowedHeaders");
		digester.addCallMethod("proxy-config/case/headers/header", "addAllowedHeader",0 );
		digester.addSetTop("proxy-config/case/sendingcookies", "setSendingCookies");
		digester.addCallMethod("proxy-config/case/sendingcookies/cookie", "addSendingCookie",0 );

		digester.addObjectCreate("proxy-config/default", ProxyConfigCase.class);
		digester.addSetNext("proxy-config/default", "setDefaultCase");
		digester.addSetProperties("proxy-config/default");
		digester.addSetTop("proxy-config/default/headers", "setAllowedHeaders");
		digester.addCallMethod("proxy-config/default/headers/header", "addAllowedHeader",0 );
		digester.addSetTop("proxy-config/default/sendingcookies", "setSendingCookies");
		digester.addCallMethod("proxy-config/default/sendingcookies/cookie", "addSendingCookie",0 );

		digester.parse(is);
		config = config2;
		isInitialized = true;
		lastCheckedTime = new Date().getTime();
	}

	private ProxyConfig() {
	}

	public void addCase(ProxyConfigCase proxyCase) {
		caseList.add(proxyCase);
	}

	public List getCaseList() {
		return caseList;
	}

	public ProxyConfigCase getDefaultCase() {
		return defaultCase;
	}

	public void setDefaultCase(ProxyConfigCase defaultCase) {
		this.defaultCase = defaultCase;
	}

	public Proxy resolve(String url) {
		String resultUrl = url;
		Iterator it = config.getCaseList().iterator();
		ProxyConfigCase matchCase = null;
		while (it.hasNext()) {
			ProxyConfigCase proxyCase = (ProxyConfigCase) it.next();
			String pattern = proxyCase.getPattern();
			if (url.matches(pattern)) {
				matchCase = proxyCase;
				break;
			}
		}

		Proxy proxy = new Proxy();

		if (matchCase == null) {
			matchCase = config.getDefaultCase();
			proxy.setDefault(true);
		}

		if (matchCase != null) {
			if (matchCase.getType().equals("proxy")) {
				proxy.setProxyInfo(matchCase.getHost(), matchCase.getPort());
				resultUrl = replaceUrl(url, matchCase);
				if(matchCase.getUsername() != null){
					Credentials credentials;
					if(matchCase.getDomaincontroller() != null){
						credentials = new NTCredentials(matchCase.getUsername(), matchCase.getPassword(), matchCase.getDomaincontroller(), matchCase.getDomain());
					}else{
						credentials = new UsernamePasswordCredentials(matchCase.getUsername(), matchCase.getPassword());
					}
					proxy.setProxyCredentials(credentials);
				}
			} else if (matchCase.getType().equals("direct")) {
				resultUrl = replaceUrl(url, matchCase);
			}
			proxy.setCacheLifeTime(matchCase.getCacheLifeTime());
			proxy.setAllowedHeaders( matchCase.getAllowedHeaders());
			proxy.setSendingCookies(matchCase.getSendingCookies());
		}
		proxy.setUrl(resultUrl);
		return proxy;
	}

	private String replaceUrl(String url, ProxyConfigCase matchCase) {
		String resultUrl = url;
		String replacement = matchCase.getReplacement();
		if (replacement != null) {
			Pattern pattern = Pattern.compile(matchCase.getPattern());
			Matcher matcher = pattern.matcher(url);
			resultUrl = matcher.replaceFirst(replacement);
			if (log.isInfoEnabled())
				log.info("Replace: " + url + " to " + resultUrl);
		}
		return resultUrl;
	}
}
