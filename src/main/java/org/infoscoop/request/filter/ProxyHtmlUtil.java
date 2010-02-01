package org.infoscoop.request.filter;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.apache.commons.httpclient.Header;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xerces.util.AugmentationsImpl;
import org.apache.xerces.util.XMLAttributesImpl;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLDocumentFilter;
import org.cyberneko.html.parsers.SAXParser;
import org.infoscoop.request.ProxyRequest;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ProxyHtmlUtil {
	
	private static ProxyHtmlUtil instance = new ProxyHtmlUtil();
	
	public static ProxyHtmlUtil getInstance() { return instance; }
	
	static private Log log = LogFactory.getLog(ProxyHtmlUtil.class);
	
	public static boolean isHtml(String contentType ) {
			
		if(contentType == null || contentType.toLowerCase().indexOf("text/html") < 0)
			return false;
		
		return true;
	}
	
	public static void headerProcess( ProxyRequest request) throws IOException {
		
		String url = request.getEscapedOriginalURL();
		
		Collection replacedHeaders = getReplacedHeaders( url, request.getRequestHeaders() );
		for( Iterator ite=replacedHeaders.iterator();ite.hasNext();) {
			Header header = ( Header )ite.next();
			request.putRequestHeader( header.getName(),header.getValue() );
		}
	}
	private static final String[] REPLACE_HEADER = { "set-cookie" };

	private static final Pattern[] REPLACE_HEADER_PATTERN = { Pattern.compile(
			"(path=)(/[^;\\s]+)", Pattern.CASE_INSENSITIVE) };
	
	private static Collection getReplacedHeaders( String url, Map<String, List<String>> responseHeaders ) {
		int contextRoot = url.indexOf("://", 1);
		contextRoot = url.indexOf("/", contextRoot + 3);
		
		String contextUrl = (contextRoot > 0)? url.substring(0,contextRoot) : url + "/";
		String[] replacement = { "$1" + contextUrl + "$2" };
		
		Collection headers = new ArrayList();
		for (Map.Entry<String, List<String>> header: responseHeaders.entrySet()) {
			
			String name = header.getKey();
			
			int idx = contains(name, REPLACE_HEADER);
			if (idx < 0)
				continue;
			for(String value : header.getValue()){
				StringBuffer newValue = null;
				Matcher m = REPLACE_HEADER_PATTERN[idx].matcher(value);
				while ( m.find() ) {
					if (newValue == null)
						newValue = new StringBuffer();

					m.appendReplacement(newValue, replacement[idx]);
				}

				if (newValue != null) {
					m.appendTail(newValue);
					log.info("Replace response header [" + name	+ " = " + newValue + "]");

					headers.add( new Header( name, newValue.toString() ));
				}
			}
		}
		
		return headers;
	}
	

	private static int contains(String s, String[] array){
		s = s.toLowerCase();
		for (int i = 0; i < array.length; i++) {
			if (array[i].equals(s))
				return i;
		}
		return -1;
	}
	
	public void nekoProcess( InputStream body,String encoding,XMLDocumentFilter[] filters ) throws IOException {
		try {
			SAXParser parser = new SAXParser();
			parser.setFeature("http://cyberneko.org/html/features/balance-tags/document-fragment",true);
			parser.setFeature("http://apache.org/xml/features/scanner/notify-builtin-refs", true);
			parser.setFeature("http://cyberneko.org/html/features/scanner/notify-builtin-refs", true);
			parser.setProperty("http://cyberneko.org/html/properties/filters",filters );
			parser.setProperty("http://cyberneko.org/html/properties/names/elems","lower");
			parser.setProperty("http://cyberneko.org/html/properties/names/attrs","lower");
			if( encoding == null )
				encoding = "UTF-8";
			else
				parser.setFeature("http://cyberneko.org/html/features/scanner/ignore-specified-charset", true);
			parser.setProperty("http://cyberneko.org/html/properties/default-encoding",encoding );
			
			long start = System.currentTimeMillis();
			
			parser.parse( new InputSource( body ));
			
			long end = System.currentTimeMillis();
			if (log.isDebugEnabled())
				log.debug("Processing time of URLReplaceFilter:" + (end - start));
		} catch (UnsupportedEncodingException ex){
			log.error("Invalid encoding is specified.", ex);
			
			throw new RuntimeException( ex );
		} catch( SAXException ex ) {
			log.error("URL replacing is not done because of failing to load.", ex );
			
			throw new RuntimeException( ex );
		}
	}
	
	protected String getCurrentFolderUrl(String url) {
		if (!url.endsWith("/")) {
			//It is recognized as a file if the end of url is not slash.
			//However, it is returned even if it is not slash if the url is only host name.
			int protocolIdx = url.indexOf("://");
			int idx = url.indexOf('/', protocolIdx + 3);
			if (idx > 0) {
				int lastSlashIdx = url.lastIndexOf('/');
				url = url.substring(0, lastSlashIdx + 1);
			} else {
				url = url + "/";
			}
		}
		return url;
	}

	public static class AttachBaseTagFilter extends org.cyberneko.html.filters.DefaultFilter {
		private String baseUrl;
		
		private boolean attached;
		
		public AttachBaseTagFilter( String url ) {
			this.baseUrl = url;//ProxyHtmlUtil.getInstance().getCurrentFolderUrl( url );
		}
		
		public void startElement(QName element, XMLAttributes attrs,
				Augmentations args) throws XNIException {
			if( !attached && !element.localpart.equalsIgnoreCase("html")&&
					!element.localpart.equalsIgnoreCase("head") ||
					element.localpart.equalsIgnoreCase("head") ) {
				// The tag does not start with HTML|HEAD
				
				attachBase();
			}
			
			String[] urlAttrs = new String[] { "href","action"/*,"src","background" */};
			for( int i=0;i<urlAttrs.length;i++ ) {
				int index = attrs.getIndex( urlAttrs[i] );
				if( index < 0 )
					continue;
				
				String value = attrs.getValue( index );
				if( value != null && value.equals(""))
					attrs.setValue( index,baseUrl );
			}
			super.startElement(element, attrs, args);
		}
		
		private void attachBase() {
			if( attached )
				return;
			
			XMLAttributes attrs = new XMLAttributesImpl();
			attrs.addAttribute( new QName("","href","href",""),"CDATA",baseUrl );
			attrs.addAttribute( new QName("","id","id",""),"CDATA","baseUrl");

			super.emptyElement( new QName("","base","base",""),
					attrs,new AugmentationsImpl());
			
			attachOnError();
			
			attached = true;
		}
		
		private void attachOnError() {
			XMLAttributes attrs = new XMLAttributesImpl();
			//attrs.addAttribute( new QName("","type","type",""),"CDATA","text/javascript" );
			
			QName qname = new QName("","script","script","");
			super.startElement( qname, attrs,new AugmentationsImpl() );
			
			String script = new StringBuffer()
				.append("window.onerror = function( message,file,line ){")
				.append("top.msg.warn( file+'#'+line+'\\n'+message );")
				.append("return true;")
				.append("};").toString();
			super.characters( new XMLString( script.toCharArray(),0,script.length() ),new AugmentationsImpl() );
			
			super.endElement( qname,new AugmentationsImpl() );
		}
	}
}