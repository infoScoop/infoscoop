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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.CharArrayWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xml.utils.QName;
import org.infoscoop.request.ProxyRequest;
import org.infoscoop.util.JSONScript;
import org.infoscoop.util.NoOpEntityResolver;
import org.infoscoop.widgetconf.I18NConverter;
import org.infoscoop.widgetconf.MessageBundle;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class DetectTypeFilter extends ProxyFilter{
	public static void main( String[] args ) throws Exception {
		new DetectTypeFilter();
		
		DetectManager h = new DetectTypeFilter.DetectManager();
		XMLReader reader = factory.newSAXParser().getXMLReader();
		reader.setEntityResolver(NoOpEntityResolver.getInstance());
		reader.setContentHandler(h);
		reader.parse(new InputSource( new FileInputStream("src/main/webapp/generalMessages_rss.xml")));
		
	}
	private static Log log = LogFactory.getLog(DetectTypeFilter.class);

	
	private static SAXParserFactory factory;

	private static final Pattern metaPattern = Pattern.compile(
			"<\\s*meta\\s*http-equiv=[\"']?Content-Type[\"']?([^>]+)>",
					Pattern.CASE_INSENSITIVE | Pattern.DOTALL );
	private static final Pattern contentPattern = Pattern.compile(
			".+content\\s*=\\s*[\"']?.+;\\s*charset\\s*=([^;\"']+)[\"']?.+",
					Pattern.CASE_INSENSITIVE | Pattern.DOTALL );
	private static final Pattern charsetPattern = Pattern.compile(
			".+charset\\s*=\\s*([^;]+).*");
	
	public DetectTypeFilter() {
		// SAXParserFactory factory = SAXParserFactory.newInstance();
		factory = SAXParserFactory.newInstance();
		factory.setNamespaceAware(true);
		factory.setValidating( false );
	}

	protected int preProcess(HttpClient client, HttpMethod method, ProxyRequest request) {
		// TODO Auto-generated method stub
		return 0;
	}


	protected InputStream postProcess(ProxyRequest request, InputStream responseStream) throws IOException {
		JSONArray result = new JSONArray();
		//BufferedInputStream responseStream = new BufferedInputStream(_responseStream);
		
		String contentType = request.getResponseHeader("Content-Type");
		
		String encoding = getContentTypeCharset( contentType );
		responseStream = new ByteArrayInputStream(ProxyRequest.stream2Bytes(responseStream)); //FIXME
		XMLFilter.skipEmptyLine(responseStream);
		
		try {
			DetectManager handler = null;
			if(isXml(contentType, responseStream)){
				try {
					DetectManager h = new DetectManager();
					XMLReader reader = factory.newSAXParser().getXMLReader();
					reader.setEntityResolver(NoOpEntityResolver.getInstance());
					reader.setContentHandler(h);
					reader.parse(new InputSource(responseStream));
					
					handler = h;
				} catch (SAXException e) {
					log.warn("parse error", e);
					
					responseStream.reset();
				}
			}
			
			if( handler == null ) {
				handler = new DetectManager();
				
				if( encoding == null ) {
					encoding = findEncoding( responseStream );//TODO:
					responseStream.reset();
				}
				
				org.cyberneko.html.parsers.SAXParser nekoParser = new org.cyberneko.html.parsers.SAXParser();
				nekoParser.setProperty("http://cyberneko.org/html/properties/names/elems","lower");
				nekoParser.setProperty("http://cyberneko.org/html/properties/names/attrs","lower");
				
				if( encoding != null )
					nekoParser.setProperty("http://cyberneko.org/html/properties/default-encoding",encoding );
				
				Transformer transformer = TransformerFactory.newInstance().newTransformer();
				SAXSource source = new SAXSource(nekoParser, new InputSource(responseStream));
				SAXResult saxResult = new SAXResult(handler);
				transformer.transform(source, saxResult);
			}
			
			result = handler.getResult( request );
		} catch (Exception e) {
			log.error("Url: ["+request.getTargetURL()+"] detect widget type failed.",e );
			//return 500;TODO:
			return null;
		}
		byte[] resultBytes = result.toString().getBytes("UTF-8");
		request.putResponseHeader("Content-Type", "text/plain; charset=UTF-8");
		request.putResponseHeader("Content-Length", String.valueOf( resultBytes.length ) );
		//request.setResponseBody(new ByteArrayInputStream( resultBytes ));
		return new ByteArrayInputStream( resultBytes );
	}
	
	private boolean isXml(String contentType, InputStream is) throws IOException{
		if(contentType != null && 
				(contentType.indexOf("text/xml") >= 0 
				|| contentType.indexOf("application/xml") >= 0
				|| contentType.indexOf("application/rss+xml") >= 0
				|| contentType.indexOf("application/rdf+xml") >= 0
				|| contentType.indexOf("application/atom+xml") >= 0
				)
			){
			return true;
		}
		
		is.mark(1);
		byte[] xmldec = new byte[500];
		is.read(xmldec);
		
		String xmlDecStr = new String(xmldec);
		is.reset();
		
		if(xmlDecStr.indexOf("<?xml") >= 0){
			return true;
		}
		return false;
	}
	public static String getContentTypeCharset( String contentType ) {
		if( contentType == null )
			return null;
		
		String encoding = null;
		Matcher m = charsetPattern.matcher( contentType );
		if( m.matches() )
			encoding = m.group(1);
		// charset=none by mistaking settings of Apache is ignored 
		if(encoding != null && encoding.toLowerCase().equals("none"))
			encoding = null;
		
		return encoding;
	}
	public static String findEncoding( InputStream responseBody ) throws Exception {
		BufferedReader reader = new BufferedReader( new InputStreamReader( responseBody,"ISO-8859-1"));
		StringBuffer buf = new StringBuffer();
		String line;
		while( ( line = reader.readLine() ) != null ) buf.append( line );
		
		String source = buf.toString();
		
		Matcher mMatcher = metaPattern.matcher( source );
		while( mMatcher.find() ){
			Matcher cMatcher = contentPattern.matcher( mMatcher.group(1));
			if( cMatcher.matches() )
				return cMatcher.group(1);
		}
		return null;
	}
	
	private static class DetectManager extends DefaultHandler{
		Stack qNameList = new Stack();
		Map prefixMap = new HashMap();
		CharArrayWriter charBuf = new CharArrayWriter();
		
		Collection<DetectHandler> handlers;
		
		public DetectManager() {
			handlers = new ArrayList<DetectHandler>();
			handlers.add( new RSSDetectHandler( this ) );
			handlers.add( new HtmlDetectHandler( this ) );
			handlers.add( new GadgetDetectHandler( this ) );
		}
		public void startPrefixMapping(String prefix, String uri) throws SAXException {
			prefixMap.put( uri,prefix );
		}
		public void endPrefixMapping(String prefix) throws SAXException {
			Collection uris = new ArrayList();
			for( Iterator ite=prefixMap.entrySet().iterator();ite.hasNext();) {
				Map.Entry entry = ( Map.Entry )ite.next();
				if( entry.getValue().equals( prefix ))
					uris.add( entry.getKey() );
			}
			
			for( Iterator ite=uris.iterator();ite.hasNext();)
				prefixMap.remove( ite.next() );
		}
		
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			String prefix = "";
			if( prefixMap.containsKey( uri ))
				prefix = ( String )prefixMap.get( uri );
			
			qNameList.push( new QName( uri,prefix,localName ));
			charBuf.reset();
			
			for( DetectHandler handler : handlers ) 
				handler.startElement( uri,localName,qName,attributes );
		}

		public void endElement(String uri, String localName, String name)
				throws SAXException {
			for( DetectHandler handler : handlers )
				handler.endElement( uri,localName,name );
			
			charBuf.reset();
			qNameList.pop();
		}
		

		public void characters(char[] ch, int start, int length)
				throws SAXException {
			charBuf.write(ch, start, length);
		}
		
		boolean isCurrentPath( String pathString ) {
			String[] paths = pathString.substring(1).split("/");
			if( paths.length != qNameList.size())
				return false;
			
			for( int i=paths.length-1;i>=0;i-- ) {
				String path = paths[i];
				String[] pathBlock = path.split(":");
				String prefix = null,localpart;
				if( pathBlock.length > 1 )
					prefix = pathBlock[0];
				
				localpart = pathBlock[pathBlock.length -1];
				
				QName qname = ( QName )qNameList.get( i );
				if( prefix != null && qname.getPrefix() != null && !qname.getPrefix().equals( prefix ))
					return false;
				
				if( !localpart.equals( qname.getLocalPart()) )
					return false;
				
			}
			
			return true;
		}
		
		public JSONArray getResult( ProxyRequest request ) {
			for( DetectHandler handler : handlers ) {
				if( handler.isCaptured() ) {
					try {
						return handler.getResult( request );
					} catch( JSONException ex ) {
						log.error("", ex );
					}
				}
			}
			
			JSONArray result = new JSONArray();
			JSONObject typeConf = new JSONObject();
			
			try {
				typeConf.put("type","MiniBrowser");
				typeConf.put("url",request.getOriginalURL() );
			} catch( JSONException ex ) {
				throw new RuntimeException( ex );
			}
			
			return result;
		}
	}
	
	private static abstract class DetectHandler extends DefaultHandler {
		private DetectManager manager;
		boolean captured;
		
		String link;
		String title;
		
		public DetectHandler( DetectManager manager ) {
			this.manager = manager;
		}
		
		String getCharBuf() {
			return manager.charBuf.toString();
		}
		
		boolean isCurrentPath( String pathString ) {
			return manager.isCurrentPath( pathString );
		}
		boolean isCurrentPath( String ... paths ) {
			for( String path : paths ) {
				if( isCurrentPath( path ))
					return true;
			}
			
			return false;
		}
		
		public boolean isCaptured() {
			return captured;
		}
		
		public abstract JSONArray getResult( ProxyRequest request ) throws JSONException;
	}
	
	private static class GadgetDetectHandler extends DetectHandler {
		String msgKey;
		Stack<LocaleInfo> localeInfos = new Stack<LocaleInfo>();
		String directoryTitle;
		
		public GadgetDetectHandler(DetectManager manager) {
			super(manager);
		}
		
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			if( isCurrentPath("/Module/ModulePrefs/Locale") ) {
				LocaleInfo localeInfo = new LocaleInfo();
				String lang = attributes.getValue("lang");
				if("".equals( lang ) || lang == null )
					lang = "ALL";
				
				String country = attributes.getValue("country");
				if("".equals( lang ) || country == null )
					country = "ALL";
				
				localeInfo.locale = new Locale( lang,country );
				localeInfo.direction = MessageBundle.Direction.as(
						attributes.getValue("language_direction"));
				
				if( attributes.getIndex("messages") >= 0 )
					localeInfo.url = attributes.getValue("messages");
				
				localeInfos.add( localeInfo );
			} else if( isCurrentPath("/Module/ModulePrefs/Locale/msg")) {
				msgKey = attributes.getValue("name");
			} else if( isCurrentPath("/Module/ModulePrefs") ) {
				title = attributes.getValue("title");
				link = attributes.getValue("title_url");
				directoryTitle = attributes.getValue("directory_title");
			}
		}
		
		public void endElement(String uri, String localName, String name) throws SAXException {
			String value = getCharBuf().toString();
			
			if(isCurrentPath("/Module")) {
				captured = true;
			} else if( isCurrentPath("/Module/ModulePrefs/Locale/msg")) {
				LocaleInfo localeInfo = localeInfos.lastElement();
				if( localeInfo != null ) {
					if( localeInfo.msgs == null )
						localeInfo.msgs = new HashMap<String,String>();
					
					localeInfo.msgs.put( msgKey,value );
				}
				
				msgKey = null;
			}
		}
		
		public JSONArray getResult( ProxyRequest request ) throws JSONException {
			JSONObject typeConf = new JSONObject();
			typeConf.put("type","Gadget");
			typeConf.put("url", request.getOriginalURL());
			
			if( title != null && !"".equals( title)) {
				I18NConverter i18n = new I18NConverter( request.getLocale(),
						getBundles( request.getOriginalURL(),getTimeout( request )) );
				
				typeConf.put("title", i18n.replace(title));
			} else {
				typeConf.put("title",new JSONScript("IS_R.Portal_no_title"));
			}
			typeConf.put("href", link);
			
			if( directoryTitle != null && !"".equals( directoryTitle ))
				typeConf.put("directoryTitle",directoryTitle );
			
			return new JSONArray().put( typeConf );
		}
		
		private int getTimeout( ProxyRequest request ) {
			int timeout = -1;
			try{
				String timeoutHeader = request.getRequestHeader("MSDPortal-Timeout");
				timeout = Integer.parseInt( timeoutHeader ) - 1000;
			} catch(NumberFormatException e){ }
			
			return timeout;
		}
		
		private Collection<MessageBundle> getBundles( String baseUrl,int timeout ) {
			Collection<MessageBundle> bundles = new ArrayList<MessageBundle>();
			MessageBundle.Factory factory = new MessageBundle.Factory.URL( timeout,baseUrl );
			for( LocaleInfo localeInfo : localeInfos ) {
				MessageBundle bundle;
				if( localeInfo.url != null ) {
					bundle = factory.createBundle( localeInfo.locale,localeInfo.direction,
							localeInfo.url );
				} else {
					bundle = factory.createBundle( localeInfo.locale,localeInfo.direction,
							localeInfo.msgs );
				}
				bundles.add( bundle );
			}
			
			return bundles;
		}
		
		private static class LocaleInfo {
			Locale locale;
			String url;
			Map<String,String> msgs;
			MessageBundle.Direction direction;
		}
	}
	private static class RSSDetectHandler extends DetectHandler {
		public RSSDetectHandler(DetectManager manager) {
			super(manager);
		}

		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			if(isCurrentPath("/feed/link") ){
				String linkType = attributes.getValue("type");
				if("text/html".equals(linkType)){
					link = attributes.getValue("href");
				}
			}
		}

		public void endElement(String uri, String localName, String name)
				throws SAXException {
			String value = getCharBuf().toString();
			if(isCurrentPath("/rdf:RDF","/rss","/feed")){
				captured = true;
			} else if( isCurrentPath(
					"/rdf:RDF/channel/title",
					"/rss/channel/title",
					"/feed/title") ){
				title = value;
			} else if( isCurrentPath(
					"/rdf:RDF/channel/link",
					"/rss/channel/link") ){
				link = value;
			}
		}
		
		public JSONArray getResult( ProxyRequest request ) throws JSONException {
			JSONObject typeConf = new JSONObject();
			typeConf.put("type","RssReader");
			typeConf.put("url", request.getOriginalURL());
			typeConf.put("title", title );
			typeConf.put("href", link );
			
			return new JSONArray().put( typeConf );
		}
	}
	private static class HtmlDetectHandler extends DetectHandler {

		Collection<JSONObject> linkTypes = new ArrayList<JSONObject>();
		
		public HtmlDetectHandler(DetectManager manager) {
			super(manager);
		}

		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			if(isCurrentPath("/html/head/link") ){
				String linkType = attributes.getValue("type");
				if("application/rss+xml".equalsIgnoreCase(linkType) ||
						"application/rdf+xml".equalsIgnoreCase(linkType) || 
						"application/atom+xml".equalsIgnoreCase(linkType) ) {
					try {
						JSONObject typeConf = new JSONObject();
						typeConf.put("type", "RssReader");
						
//						typeConf.put("title",rssTitle );
						typeConf.put("url", attributes.getValue("href"));
						typeConf.put("feedType", attributes.getValue("title"));

						linkTypes.add(typeConf);
					} catch (JSONException e) {
						log.error("",e);
					}
				}
			}
		}

		public void endElement(String uri, String localName, String name)
				throws SAXException {
			String value = getCharBuf().toString();
			if(isCurrentPath("/html")) {
				captured = true;
			} else if( isCurrentPath("/html/head/title")) {
				title = value;
			}
		}
		
		public JSONArray getResult( ProxyRequest request ) throws JSONException {
			JSONArray result = new JSONArray();
			
			JSONObject typeConf = new JSONObject();
			typeConf.put("type","MiniBrowser");
			typeConf.put("url", request.getOriginalURL());
			typeConf.put("title", title);
			typeConf.put("href", request.getOriginalURL());
			result.put( typeConf );
			
			for( JSONObject feedConf : linkTypes )
				result.put( feedConf );
			
			return result;
		}
	}
}
