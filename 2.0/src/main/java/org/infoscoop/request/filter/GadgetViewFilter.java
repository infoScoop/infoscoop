package org.infoscoop.request.filter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;


import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.request.ProxyRequest;
import org.infoscoop.util.NoOpEntityResolver;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class GadgetViewFilter extends ProxyFilter {
	private static final Log log = LogFactory.getLog( GadgetViewFilter.class );
	
	private Templates gadget2maximize;
	private DocumentBuilderFactory docBuilderFactory;
	
	public GadgetViewFilter() {
		try {
			this.gadget2maximize = TransformerFactory.newInstance().newTemplates(
					new StreamSource( Thread.currentThread().getContextClassLoader()
							.getResourceAsStream("gadget2maximize.xsl")));
		} catch( TransformerException ex ) {
			log.error("Failed loading gadget2maximize.xsl.", ex);
		}
		
		this.docBuilderFactory = DocumentBuilderFactory.newInstance();
		this.docBuilderFactory.setValidating( false );
	}

	protected int preProcess(HttpClient client, HttpMethod method,
			ProxyRequest request) {
    	request.addIgnoreHeader("if-modified-since");
    	request.addIgnoreHeader("if-none-match");
    	
		return 0;
	}

	protected InputStream postProcess(ProxyRequest request, InputStream responseStream) throws IOException {
		
		Document module = null;
		try {
			DocumentBuilder builder = docBuilderFactory.newDocumentBuilder();
			builder.setEntityResolver(NoOpEntityResolver.getInstance());
			module = builder.parse(responseStream);
		} catch( Exception ex ) {
			log.warn("", ex );
			
			throw new IOException("Invalid Module");
		}
		
		String viewType = request.getFilterParameter("view");
		byte[] responseBytes = null;
		try {
			responseBytes = processView( module,viewType.toLowerCase() );
		} catch( Exception ex ) {
			log.warn("", ex ); //FIXME
		}

		request.putResponseHeader("Content-Type", "text/xml");
		request.putResponseHeader("Content-Length",String.valueOf( responseBytes.length ));
		
		return new ByteArrayInputStream( responseBytes );
	}

	private byte[] processView( Document module,String viewType ) throws Exception {
		if( "canvas".equals( viewType )) {
			return processMaximizeView( module );
		}
		
		return processViewTransform( module,viewType );
	}
	private byte[] processViewTransform( Document module,String viewType ) throws Exception {
		NodeList nodeList = module.getElementsByTagName("Maximize");
		while( nodeList.getLength() > 0 ) {
			nodeList.item(0).getParentNode().removeChild( nodeList.item(0));
			nodeList = module.getElementsByTagName("Maximize");
		}
		
		nodeList = module.getElementsByTagName("Content");
		Collection contents = new ArrayList();
		for( int i=0;i<nodeList.getLength();i++ )
			contents.add( nodeList.item( i ));
		
		Element matches = null;
		for( Iterator ite=contents.iterator();ite.hasNext();) {
			Element content = ( Element )ite.next();
			if("Maximize".equals( (( Element )content.getParentNode()).getTagName() ) )
				continue;
			
			if( matches == null )
				matches = content;
			
			if( content.hasAttribute("view") && !"".equals( content.getAttribute("view")) ) {
				if( content.getAttribute("view").toLowerCase().indexOf( viewType ) >= 0 ) {
					matches = content;
					break;
				}
			} else {
				matches = content;
			}
		}
		
		contents.remove( matches );
		for( Iterator ite=contents.iterator();ite.hasNext();) {
			Element content = ( Element )ite.next();
			
			content.getParentNode().removeChild( content );
		}

		ByteArrayOutputStream respOut = new ByteArrayOutputStream();
		try {
			Transformer trans = TransformerFactory.newInstance().newTransformer();
			trans.transform( new DOMSource( module ),new StreamResult( respOut ));
		} catch( Exception ex ) {
			throw ex;
		} finally {
			respOut.close();
		}
		
		return respOut.toByteArray();
	}
	
	private byte[] processMaximizeView( Document module ) throws Exception {
		NodeList nodeList = module.getElementsByTagName("Maximize");
		if( nodeList.getLength() == 0 )
			return processViewTransform( module,"canvas");
		
		ByteArrayOutputStream respOut = new ByteArrayOutputStream();
		try {
			Transformer trans = gadget2maximize.newTransformer();
			trans.transform( new DOMSource( module ),new StreamResult( respOut ));
		} catch( Exception ex ) {
			throw ex;
		} finally {
			respOut.close();
		}
		
		return respOut.toByteArray();
	}
	
	private static boolean hasContent( Document module,String viewType ) {
		NodeList nodeList = module.getElementsByTagName("Content");
		for( int i=0;i<nodeList.getLength();i++ ) {
			Element node = ( Element )nodeList.item( i );
			if( node.hasAttribute("view") && node.getAttribute("view").toLowerCase().indexOf( viewType ) >= 0 )
				return true;
		}
		
		return false;
	}
	
	public static void main( String[] args ) throws Exception {
		GadgetViewFilter filter = new GadgetViewFilter();
		
		Document module = filter.docBuilderFactory.newDocumentBuilder().parse("hoge.xml");
		
		System.out.println( new String( filter.processView( module,"Maximize"),"UTF-8") );
	}
}
