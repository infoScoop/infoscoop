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

/*
 * $Id: XHtmlWriter.java,v 1.4 2007/12/27 01:59:28 komatsu Exp $
 *
 * Beacon-IT inicio Project
 * Copyright (c) 2003 by Beacon Information Technology, Inc.
 * 163-1507 Tokyo-to, Shinjuku-ku, Nishi-Shinjuku 1-6-1 Shinjuku L-Tower
 * All rights reserved.
 * ====================================================================
 */
package org.infoscoop.util;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.cyberneko.html.filters.DefaultFilter;

/**
 * @author dou
 *
 */
public class XHtmlWriter extends DefaultFilter {

	private PrintWriter printer;

	/** 
	 * Creates a writer to the standard output stream using UTF-8 
	 * encoding. 
	 */
	public XHtmlWriter() {
		this(System.out);
	}

	/** 
	 * Creates a writer with the specified output stream using UTF-8 
	 * encoding. 
	 */
	public XHtmlWriter(OutputStream stream) {
		this(stream, "UTF8");
	}

	/** Creates a writer with the specified output stream and encoding. */
	public XHtmlWriter(OutputStream stream, String encoding) {
		try {
			printer =
				new PrintWriter(new OutputStreamWriter(stream, encoding), true);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(
				"JVM must have " + encoding + " decoder");
		}
	}

	/** Creates a writer with the specified Java Writer. */
	public XHtmlWriter(java.io.Writer writer) {
		printer = new PrintWriter(writer);
	}

	/* (non-Javadoc)
	 * @see org.apache.xerces.xni.XMLDocumentHandler#characters(org.apache.xerces.xni.XMLString, org.apache.xerces.xni.Augmentations)
	 */
	public void characters(XMLString text, Augmentations args)
		throws XNIException {
		print(text);
	}
	/* (non-Javadoc)
	 * @see org.apache.xerces.xni.XMLDocumentHandler#comment(org.apache.xerces.xni.XMLString, org.apache.xerces.xni.Augmentations)
	 */
	public void comment(XMLString text, Augmentations args)
		throws XNIException {
		//do nothing
	}

	/* (non-Javadoc)
	 * @see org.apache.xerces.xni.XMLDocumentHandler#doctypeDecl(java.lang.String, java.lang.String, java.lang.String, org.apache.xerces.xni.Augmentations)
	 */
	public void doctypeDecl(
		String root,
		String pubid,
		String sysid,
		Augmentations args)
		throws XNIException {
		//we do nothing here
	}

	/* (non-Javadoc)
	 * @see org.apache.xerces.xni.XMLDocumentHandler#emptyElement(org.apache.xerces.xni.QName, org.apache.xerces.xni.XMLAttributes, org.apache.xerces.xni.Augmentations)
	 */
	public void emptyElement(
		QName element,
		XMLAttributes attrs,
		Augmentations args)
		throws XNIException {
		startElement(element, attrs, args);
		endElement(element, args);
	}

	/* (non-Javadoc)
	 * @see org.apache.xerces.xni.XMLDocumentHandler#endDocument(org.apache.xerces.xni.Augmentations)
	 */
	public void endDocument(Augmentations args) throws XNIException {
		//we do nothing here
	}
	/* (non-Javadoc)
	 * @see org.apache.xerces.xni.XMLDocumentHandler#endElement(org.apache.xerces.xni.QName, org.apache.xerces.xni.Augmentations)
	 */
	public void endElement(QName element, Augmentations args)
		throws XNIException {
		printer.print("</");
		printer.print(element.rawname);
		printer.print('>');
		printer.flush();
	}

	/* (non-Javadoc)
	 * @see org.apache.xerces.xni.XMLDocumentHandler#processingInstruction(java.lang.String, org.apache.xerces.xni.XMLString, org.apache.xerces.xni.Augmentations)
	 */
	public void processingInstruction(
		String target,
		XMLString data,
		Augmentations args)
		throws XNIException {
		printer.print("<?");
		printer.print(target);
		if (data != null && data.length > 0) {
			printer.print(' ');
			printer.print(data.toString());
		}
		printer.print(" ?>");
		printer.println();
		printer.flush();
	}

	/* (non-Javadoc)
	 * @see org.cyberneko.html.filters.DefaultFilter#startDocument(org.apache.xerces.xni.XMLLocator, java.lang.String, org.apache.xerces.xni.Augmentations)
	 */
	public void startDocument(
		XMLLocator locator,
		String encoding,
		Augmentations args)
		throws XNIException {
		startDocument(locator, encoding, null, args);
	}

	/* (non-Javadoc)
	 * @see org.apache.xerces.xni.XMLDocumentHandler#startDocument(org.apache.xerces.xni.XMLLocator, java.lang.String, org.apache.xerces.xni.NamespaceContext, org.apache.xerces.xni.Augmentations)
	 */
	public void startDocument(
		XMLLocator locator,
		String encoding,
		NamespaceContext nscontext,
		Augmentations args)
		throws XNIException {
		//we do nothing here
	}

	/* (non-Javadoc)
	 * @see org.apache.xerces.xni.XMLDocumentHandler#startElement(org.apache.xerces.xni.QName, org.apache.xerces.xni.XMLAttributes, org.apache.xerces.xni.Augmentations)
	 */
	public void startElement(
		QName element,
		XMLAttributes attrs,
		Augmentations args)
		throws XNIException {
		printStartElement(element, attrs);
	}

	/* (non-Javadoc)
	 * @see org.apache.xerces.xni.XMLDocumentHandler#xmlDecl(java.lang.String, java.lang.String, java.lang.String, org.apache.xerces.xni.Augmentations)
	 */
	public void xmlDecl(
		String version,
		String encoding,
		String standalone,
		Augmentations args)
		throws XNIException {
		//we will do nothing here
	}

	private void printStartElement(QName element, XMLAttributes attributes) {
		this.printer.print('<');
		printer.print(element.rawname);
		int attrCount = attributes != null ? attributes.getLength() : 0;
		Map attrMap = new HashMap();
		for (int i = 0; i < attrCount; i++) {
			String aname = attributes.getQName(i);
			String avalue = attributes.getValue(i);
			String value = (String)attrMap.get(aname);
			if(value != null){
				attrMap.put(aname, value + avalue);
			}else{
				attrMap.put(aname, avalue);	
			}
		}
		for(Iterator it = attrMap.entrySet().iterator(); it.hasNext();){
			Map.Entry attr = (Map.Entry)it.next();
			printer.print(' ');
			printer.print(attr.getKey());
			printer.print("=\"");
			print((String)attr.getValue());
			printer.print("\"");
		}
		printer.print('>');
		printer.flush();
	}

	private void print(XMLString text) {
		print(text.toString());
	}

	/** Prints the specified string . */
	private void print(String s, boolean resolveHtmlEntity) {
		String str = resolveHtmlEntity ? HtmlUtil.resolveHtmlEntities(s) : s;
		if (resolveHtmlEntity) {
			str = XmlUtil.escapeXmlEntities(str);
			printer.print(str);
		} else {
			printer.print(str);
		}
		printer.flush();
	}

	private void print(String s) {
		print(s, true);
	}
}
