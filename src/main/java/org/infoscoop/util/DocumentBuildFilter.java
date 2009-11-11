package org.infoscoop.util;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLDocumentHandler;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLDocumentFilter;
import org.apache.xerces.xni.parser.XMLDocumentSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class DocumentBuildFilter implements XMLDocumentFilter {
	private static final Log log = LogFactory.getLog(DocumentBuildFilter.class);
	
	private XMLDocumentHandler handler;
	private Document doc;
	private Node current;
	
	private boolean cdata;
	private boolean entity;
	
	public Document getDocument() { return doc; }
	
	public XMLDocumentHandler getDocumentHandler() {
		// TODO Auto-generated method stub
		return handler;
	}

	public void setDocumentHandler(XMLDocumentHandler arg0) {
		// TODO Auto-generated method stub
		handler = arg0;
	}

	public void characters(XMLString arg0, Augmentations arg1)
			throws XNIException {
		if( cdata ) {
			current.appendChild( doc.createCDATASection( arg0.toString()));
		} else if( entity ) {
			// ignore
		} else if( current.getNodeType() != Node.DOCUMENT_NODE ){
			try {
				current.appendChild( doc.createTextNode( arg0.toString()));
			} catch( Exception ex ) {
				log.error( current+","+current.getNodeType());
				throw new RuntimeException( ex );
			}
		}
	}

	public void comment(XMLString arg0, Augmentations arg1) throws XNIException {
		current.appendChild( doc.createComment( arg0.toString()));
	}

	public void doctypeDecl(String arg0, String arg1, String arg2,
			Augmentations arg3) throws XNIException {
	}

	public void startCDATA(Augmentations arg0) throws XNIException {
		// TODO Auto-generated method stub
		cdata = true;
	}

	public void endCDATA(Augmentations arg0) throws XNIException {
		// TODO Auto-generated method stub
		cdata = false;
	}

	public void startDocument(XMLLocator arg0, String arg1,
			NamespaceContext arg2, Augmentations arg3) throws XNIException {
		// TODO Auto-generated method stub
		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			
			current = doc;
		} catch( Exception ex ) {
			throw new RuntimeException( ex );
		}
	}

	public void endDocument(Augmentations arg0) throws XNIException {
		// TODO Auto-generated method stub
	}

	public void emptyElement(QName arg0, XMLAttributes arg1, Augmentations arg2)
			throws XNIException {
		startElement( arg0,arg1,arg2 );
		endElement( arg0,arg2 );
	}

	public void startElement(QName arg0, XMLAttributes arg1, Augmentations arg2)
			throws XNIException {
		// TODO Auto-generated method stub
		Element element = doc.createElement( arg0.localpart );
		for( int i=0;i<arg1.getLength();i++ ) {
			String name = arg1.getLocalName(i);
			String value = arg1.getValue(i);
			
			element.setAttribute( name, value );
		}
		
		if( current == doc && !"html".equals( arg0.localpart )) {
			Node de = doc.getDocumentElement();
			if( de == null ) {
				de = doc.createElement("html");
				doc.appendChild( de );
			}
			
			current = de;
		}
		
		current.appendChild( element );
		
		current = element;
	}

	public void endElement(QName arg0, Augmentations arg1) throws XNIException {
		// TODO Auto-generated method stub
		current = current.getParentNode();
	}

	public void startGeneralEntity(String arg0, XMLResourceIdentifier arg1,
			String arg2, Augmentations arg3) throws XNIException {
		// TODO Auto-generated method stub
		
		//String numEntity = HtmlUtil.translateNumEntities("&"+arg0+";");
		//numEntity = numEntity.substring( 1,numEntity.length() -1 );
		//LogFactory.getLog(this.getClass()).error("entity: "+arg0+"|"+numEntity );
		
		current.appendChild(
			//doc.createEntityReference( arg0 ) );
			doc.createTextNode("&"+arg0+";"));
			//doc.createEntityReference( numEntity ) );
		
		entity = true;
		
	}

	public void endGeneralEntity(String arg0, Augmentations arg1)
			throws XNIException {
		// TODO Auto-generated method stub
		entity = false;
	}

	public XMLDocumentSource getDocumentSource() {
		// TODO Auto-generated method stub
		return null;
	}

	public void textDecl(String arg0, String arg1, Augmentations arg2)
			throws XNIException {
		// TODO Auto-generated method stub
		
	}

	public void xmlDecl(String arg0, String arg1, String arg2,
			Augmentations arg3) throws XNIException {
		// TODO Auto-generated method stub
		
	}

	public void ignorableWhitespace(XMLString arg0, Augmentations arg1)
			throws XNIException {
		// TODO Auto-generated method stub
		
	}

	public void processingInstruction(String arg0, XMLString arg1,
			Augmentations arg2) throws XNIException {
		// TODO Auto-generated method stub
		
	}

	public void setDocumentSource(XMLDocumentSource arg0) {
		// TODO Auto-generated method stub
		
	}

}
