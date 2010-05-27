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
 * $Id: GetTextByIDFilter.java,v 1.4 2007/12/27 01:59:28 komatsu Exp $
 *
 * Beacon-IT inicio Project
 * Copyright (c) 2003 by Beacon Information Technology, Inc.
 * 163-1507 Tokyo-to, Shinjuku-ku, Nishi-Shinjuku 1-6-1 Shinjuku L-Tower
 * All rights reserved.
 * ====================================================================
 */
package org.infoscoop.request.filter;

import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.cyberneko.html.filters.DefaultFilter;

/**
 * Get a text in the tag of the appointed ID except the tag.
 * @author a-kimura
 */
public class GetTextByIDFilter extends DefaultFilter {
	private String id;

	private String targetTag;

	private int targetLevel = 0;

	private int level = 0;

	private StringBuffer buf = new StringBuffer();

	private boolean isFound = false;

	private boolean isCompleted = false;

	public GetTextByIDFilter(String id) {
		this.id = id;
	}

	public String getText() {
		return this.buf.toString();
	}

	/* (non-Javadoc)
	 * @see org.apache.xerces.xni.XMLDocumentHandler#characters(org.apache.xerces.xni.XMLString, org.apache.xerces.xni.Augmentations)
	 */
	public void characters(XMLString text, Augmentations args)
			throws XNIException {
		if (isCompleted)
			return;
		if (isFound)
			buf.append(text.toString());
	}

	/* (non-Javadoc)
	 * @see org.apache.xerces.xni.XMLDocumentHandler#startElement(org.apache.xerces.xni.QName, org.apache.xerces.xni.XMLAttributes, org.apache.xerces.xni.Augmentations)
	 */
	public void startElement(QName qName, XMLAttributes attrs,
			Augmentations args) throws XNIException {
		if (isCompleted)
			return;
		level++;
		String idValue = attrs.getValue("id");
		if (idValue != null && idValue.equals(id)) {
			isFound = true;
			targetLevel = level;
			targetTag = qName.rawname;
		}
	}

	/* (non-Javadoc)
	 * @see org.apache.xerces.xni.XMLDocumentHandler#endElement(org.apache.xerces.xni.QName, org.apache.xerces.xni.Augmentations)
	 */
	public void endElement(QName qName, Augmentations args) throws XNIException {
		if (isCompleted)
			return;
		if (isFound) {
			if (targetLevel == level && targetTag.equals(qName.rawname)) {
				isCompleted = true;
			}
			level--;
		}
	}
}
