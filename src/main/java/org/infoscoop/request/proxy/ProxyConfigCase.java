package org.infoscoop.request.proxy;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.util.Crypt;



public class ProxyConfigCase {
	private Log log = LogFactory.getLog(this.getClass());

	private String pattern;

	private String type;
	
	private int cacheLifeTime;

	private String host;

	private int port;

	private String username;
	private String password;
	private String domain;
	private String domaincontroller;
	private Set<String> sendingCookies;

	private String replacement;

	private Set<String> allowedHeaders;
	
	private boolean isIntranet = false;

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		if(pattern == null || "".equals(pattern))return;
		this.pattern = pattern;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public int getCacheLifeTime() {
		return cacheLifeTime;
	}

	public void setCacheLifeTime(int cacheLifeTime) {
		this.cacheLifeTime = cacheLifeTime;
	}

	public boolean getIntranet() {
		return isIntranet;
	}

	public void setIntranet(boolean isIntranet) {
		this.isIntranet = isIntranet;
	}

	public String getReplacement() {
		return replacement;
	}

	public void setReplacement(String replacement) {
		if(replacement == null || "".equals(replacement))return;
		this.replacement = replacement;
	}

	public String getDomaincontroller() {
		return this.domaincontroller;
	}

	public void setDomaincontroller(String dchost) {
		if(dchost == null || "".equals(dchost))return;
		this.domaincontroller = dchost;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		if(domain == null || "".equals(domain))return;
		this.domain = domain;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		if(password == null || "".equals(password))return;
		try {
			this.password = Crypt.gerCryptInstance().doCrypt(Crypt.DECRYPT, password);
		} catch (Exception e) {
			this.password = password;
			log.error("", e);
		}
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		if(username == null || "".equals(username))return;
		this.username = username;
	}

	public void setAllowedHeaders( Object dummy ) {
		if( this.allowedHeaders == null )
			this.allowedHeaders = new HashSet<String>();
	}
	public Set<String> getAllowedHeaders() {
		return allowedHeaders;
	}

	public void addAllowedHeader( String allowedHeader ) {
		if( allowedHeaders == null )
			allowedHeaders = new HashSet<String>();

		allowedHeaders.add( allowedHeader.toLowerCase() );
	}

	public void setSendingCookies( Object dummy ) {
		if( this.sendingCookies == null )
			this.sendingCookies = new HashSet<String>();
	}

	public void addSendingCookie( String cookieName ) {
		if( sendingCookies == null )
			sendingCookies = new HashSet<String>();

		sendingCookies.add( cookieName);
	}

	public Set<String> getSendingCookies() {
		return sendingCookies;
	}
}
