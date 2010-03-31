/*
 * $Id: HtmlTextWriter.java,v 1.1 2007/12/27 01:59:28 komatsu Exp $
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
import java.util.ArrayList;
import java.util.List;

import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.cyberneko.html.filters.DefaultFilter;

/**
 * @author dou
 *
 */
public class HtmlTextWriter extends DefaultFilter {
	private static List ignoreTagList = new ArrayList();
	static {
		ignoreTagList.add("style");
		ignoreTagList.add("script");
	}

	private PrintWriter printer;

	private boolean isTarget = true;

	/** 
	 * Creates a writer to the standard output stream using UTF-8 
	 * encoding. 
	 */
	public HtmlTextWriter() {
		this(System.out);
	}

	/** 
	 * Creates a writer with the specified output stream using UTF-8 
	 * encoding. 
	 */
	public HtmlTextWriter(OutputStream stream) {
		this(stream, "UTF8");
	}

	/** Creates a writer with the specified output stream and encoding. */
	public HtmlTextWriter(OutputStream stream, String encoding) {
		try {
			printer = new PrintWriter(new OutputStreamWriter(stream, encoding),
					true);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("JVM must have " + encoding + " decoder");
		}
	}

	/** Creates a writer with the specified Java Writer. */
	public HtmlTextWriter(java.io.Writer writer) {
		printer = new PrintWriter(writer);
	}

	public void characters(XMLString text, Augmentations args)
			throws XNIException {
		if (isTarget) {
			String s = text.toString();
			if (s.trim().length() > 0) {
				print(s);
			}
		}
	}

	public void startElement(QName element, XMLAttributes attributes,
			Augmentations args) throws XNIException {
		String elemname = element.rawname;
		if (ignoreTagList.contains(elemname)) {
			isTarget = false;
			return;
		} else {
			isTarget = true;
		}
		for (int i = 0; i < attributes.getLength(); i++) {
			String name = attributes.getQName(i);
			if (name.equals("title") || name.equals("alt")) {
				print(attributes.getValue(i));
			}
		}
	}

	private void print(String s) {
		printer.print(s);
		printer.flush();
	}
}
