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

import org.infoscoop.dao.model.base.BaseSiteaggregationmenu_temp;
import org.infoscoop.util.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;




public class Siteaggregationmenu_temp extends BaseSiteaggregationmenu_temp {
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public Siteaggregationmenu_temp () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public Siteaggregationmenu_temp (org.infoscoop.dao.model.SITEAGGREGATIONMENU_TEMPPK id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public Siteaggregationmenu_temp (
		org.infoscoop.dao.model.SITEAGGREGATIONMENU_TEMPPK id,
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
}
