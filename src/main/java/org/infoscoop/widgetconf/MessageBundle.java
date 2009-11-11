package org.infoscoop.widgetconf;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.request.ProxyRequest;
import org.infoscoop.service.GadgetResourceService;
import org.infoscoop.util.NoOpEntityResolver;
import org.infoscoop.util.XmlUtil;
import org.infoscoop.web.ProxyServlet;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

public abstract class MessageBundle implements Comparable<MessageBundle> {
	private Locale locale;
	private Direction direction;
	
	public Locale getLocale() {
		return locale;
	}
	public MessageBundle setLocale( Locale locale ) {
		this.locale = locale;
		
		return this;
	}
	
	public Direction getDirection() {
		return direction;
	}
	public MessageBundle setDirection( Direction direction ) {
		this.direction = direction;
		
		return this;
	}
	
	public abstract Map<String,String> getMessages();
	
	public int compareTo( MessageBundle o ) {
		if( locale.equals( o.locale ))
			return 0;
		
		if( !locale.getLanguage().equals( o.locale.getLanguage() ))
			return locale.getLanguage().equalsIgnoreCase("ALL") ? -1 : 1; // reverse
		
		if( !locale.getCountry().equals( o.locale.getCountry() ))
			return locale.getCountry().equalsIgnoreCase("ALL") ? -1 : 1; // reverse
		
		return 0;
	}
	
	public static enum Direction {
		LTR,RTL;
		
		public String toString() {
			return super.toString().toLowerCase();
		}
		public static Direction as( String languageDirectionStr ) {
			if("rtl".equalsIgnoreCase( languageDirectionStr ))
				return RTL;
			
			return LTR;
		}
	}
	public static abstract class Factory {
		int timeout;
		
		private static final Pattern REGEX_FILENAME_LOCALE = Pattern.compile(
				"(?:.*/)?(\\w+)_(\\w+)\\.xml");
		
		protected Factory( int timeout ) {
			this.timeout = timeout;
		}
		
		public Collection<MessageBundle> createBundles( Document doc ) {
			Collection<MessageBundle> resourceBundles = new ArrayList<MessageBundle>();
			
			NodeList localeNodes = doc.getElementsByTagName("Locale");
			for (int i = 0; i < localeNodes.getLength(); i++) {
				Element localeElm = (Element) localeNodes.item(i);
				
				resourceBundles.add( createBundle( localeElm ) );
			}
			
			return resourceBundles;
		}
		
		public MessageBundle createBundle( Element locale ) {
			String lang = locale.getAttribute("lang");
			if("".equals( lang ) || lang == null )
				lang = "ALL";
			
			String country = locale.getAttribute("country");
			if("".equals( country ) || country == null )
				country = "ALL";
			
			Direction direction = Direction.as( locale.getAttribute("language_direction"));
			
			if( locale.hasAttribute("messages")) {
				String url = locale.getAttribute("messages");
				
				return createBundle( new Locale( lang,country ),direction,url );
			} else {
				Map<String,String> messages = EmbedMessageBundle.parseMessages( locale );
				
				return createBundle( new Locale( lang,country ),direction,messages );
			}
		}
		
		public MessageBundle createBundle( Locale locale,Direction direction,
					String url ) {
			// It is infoscoop's original specification that a file name handle as locale.
//			if( ("".equals( lang ) || lang == null )&&
//				("".equals( country ) || country == null )) {
				Matcher matcher  = REGEX_FILENAME_LOCALE.matcher( url );
				if( matcher.matches() && matcher.groupCount() == 2 )
					locale = new Locale( matcher.group(1),matcher.group(2) );
//			}
			
			MessageBundle messageBundle;
			if( url.indexOf("://") >= 0 ) {
				messageBundle = new URLMessageBundle( url,timeout );
			} else {
				messageBundle = createRelativeBundle( url );
			}
			
			return messageBundle.setLocale( locale ).setDirection( direction );
		}
		protected abstract MessageBundle createRelativeBundle( String url );
		
		public MessageBundle createBundle( Locale locale,Direction direction,
				Map<String,String> messages ) {
			return new EmbedMessageBundle( messages ).setLocale( locale ).setDirection( direction );
		}
		
		public static class URL extends Factory {
			private String baseUrl;
			
			public URL( int timeout,String baseUrl ) {
				super( timeout );
				
				this.baseUrl = baseUrl;
			}
			
			@Override
			protected MessageBundle createRelativeBundle( String url ) {
				return new URLMessageBundle( ProxyRequest.getAbsoluteURL( baseUrl,url ),timeout );
			}
		}
		
		public static class Upload extends Factory {
			private String type;
			public Upload( int timeout,String type ) {
				super( timeout );
				
				this.type = type;
			}
			@Override
			protected MessageBundle createRelativeBundle( String url ) {
				return new UploadMessageBundle( type,url );
			}
		}
	}
}

class EmbedMessageBundle extends MessageBundle {
	private Map<String,String> messages;
	public EmbedMessageBundle( Map<String,String> messages ) {
		if( messages == null )
			messages = new HashMap<String,String>();
		
		this.messages = messages;
	}
	
	public Map<String,String> getMessages() {
		return messages;
	}
	
	public static Map<String,String> parseMessages( Element locale ) {
		Map<String,String> messages = new HashMap<String, String>();
		
		NodeList msgNodeList = locale.getElementsByTagName("msg");
		for( int i=0;i<msgNodeList.getLength();i++ ) {
			Element msg = ( Element )msgNodeList.item( i );
			
			String key = msg.getAttribute("name");
			String value = msg.getTextContent();
			
			messages.put( key,value );
		}
		
		return messages;
	}
}

abstract class AbstractMessageBundle extends MessageBundle {
	private static Log logger = LogFactory.getLog( AbstractMessageBundle.class );
	
	public Map<String,String> getMessages() {
		try {
			InputStream in = getResourceBundle();
			if( in != null )
				return parseResourceBundle( in );
		} catch( Exception ex ) {
			logger.error("failed parse message bundle",ex );
		}
		
		return new HashMap<String,String>();
	}
	protected abstract InputStream getResourceBundle() throws Exception;
	private static Map<String,String> parseResourceBundle( InputStream in )
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating( false );
		DocumentBuilder builder = factory.newDocumentBuilder();
		builder.setEntityResolver(NoOpEntityResolver.getInstance());
		
		Document doc = builder.parse( in );
		NodeList msgs = doc.getElementsByTagName("msg");
		
		Map<String,String> map = new HashMap<String,String>();
		for (int j = 0; j < msgs.getLength(); j++) {
			Element msg = (Element) msgs.item(j);
			String name = msg.getAttribute("name");
			String msgStr = XmlUtil.getChildText(msg).trim();
			map.put(name, msgStr);
		}
		
		return map;
	}
}
class URLMessageBundle extends AbstractMessageBundle {
	private static Log logger = LogFactory.getLog( URLMessageBundle.class );
	
	private String url;
	private int timeout;
	
	public URLMessageBundle( String url,int timeout ) {
		this.url = url;
		this.timeout = timeout;
	}
	
	@Override
	protected InputStream getResourceBundle() throws Exception {
		ProxyRequest proxyRequest = new ProxyRequest( url,"XML");
		proxyRequest.setTimeout(( timeout > 0 )? timeout : ProxyServlet.DEFAULT_TIMEOUT);
		proxyRequest.addIgnoreHeader("user-agent");
		proxyRequest.addIgnoreHeader("accept-encoding");
		
		try {
			int statusCode = proxyRequest.executeGet();

			if (logger.isInfoEnabled())
				logger.info("i18n url : " + proxyRequest.getProxy().getUrl());
			
			if ( statusCode != 200 )
				throw new Exception("i18n url=" + proxyRequest.getProxy().getUrl() +", statucCode=" + statusCode );
			
			return proxyRequest.getResponseBody();
		} finally {
			proxyRequest.close();
		}
	}
}
class UploadMessageBundle extends AbstractMessageBundle {
	private static Log logger = LogFactory.getLog( UploadMessageBundle.class );
	
	private String type;
	private String path;
	private String name;
	
	public UploadMessageBundle( String type,String url ) {
		this.type = type;
		
		if( url.indexOf("/") >= 0 ) {
			path = url.substring( 0,url.lastIndexOf("/"));
			name = url.substring( path.length() );
			if( name.startsWith("/"))
				name = name.substring(1);
			
			if( !path.startsWith("/"))
				path = "/"+path;
		} else {
			path = "/";
			name = url;
		}
	}
	
	@Override
	protected InputStream getResourceBundle() throws Exception {
		if( logger.isInfoEnabled() ) logger.info("ResourceBundle: ["+type+"] "+path+"@"+name );
		
		return new ByteArrayInputStream(
				GadgetResourceService.getHandle().selectResource( type,path,name ) );
	}
}