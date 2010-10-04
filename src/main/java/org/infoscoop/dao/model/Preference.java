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

package org.infoscoop.dao.model;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;


import org.infoscoop.dao.model.base.BasePreference;
import org.infoscoop.util.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;



public class Preference extends BasePreference {
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public Preference () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public Preference (org.infoscoop.dao.model.PreferencePK id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public Preference (
		org.infoscoop.dao.model.PreferencePK id,
		java.lang.String data) {

		super (
			id,
			data);
	}

/*[CONSTRUCTOR MARKER END]*/

	public Element getElement() throws SAXException {
		Document doc = (Document) XmlUtil.string2Dom(super.getData());
		return doc.getDocumentElement();
	}

	public void setElement(Element conf) {
		super.setData(XmlUtil.dom2String(conf));
	}

	public static Element newElement(String uid) throws ParserConfigurationException, FactoryConfigurationError{

		DocumentBuilder docb = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = docb.newDocument();
		Element newEl = doc.createElement("preference");
		newEl.setAttribute("uid", uid);
		doc.appendChild(newEl);
		return doc.getDocumentElement();
	}
}
