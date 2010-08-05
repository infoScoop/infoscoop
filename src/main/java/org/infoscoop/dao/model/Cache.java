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

import java.util.ArrayList;
import java.util.List;


import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.Header;
import org.infoscoop.dao.model.base.BaseCache;
import org.infoscoop.util.StringUtil;
import org.infoscoop.util.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Cache extends BaseCache {
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public Cache () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public Cache (java.lang.String id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public Cache (
		java.lang.String id,
		java.lang.String uid,
		java.lang.String url,
		java.lang.String urlKey,
		java.util.Date timestamp,
		java.lang.String headers,
		java.lang.String body) {

		super (
			id,
			uid,
			url,
			urlKey,
			timestamp,
			headers,
			body);
	}

/*[CONSTRUCTOR MARKER END]*/

	@Override
	public String getUid() {
		return StringUtil.getNullSafe( super.getUid() );
	}
	public byte[] getBodyBytes() {
		return Base64.decodeBase64( super.getBody().getBytes() );
	}
	public void setBodyBytes( byte[] bytes ) {
		super.setBody( new String( Base64.encodeBase64( bytes )) );
	}
	
	public List<Header> getHeaderList() throws SAXException {
		List<Header> headers = new ArrayList<Header>();
		
		Document headerDoc = (Document)XmlUtil.string2Dom( super.getHeaders());
		Element headerEl = headerDoc.getDocumentElement();
		NodeList list = headerEl.getElementsByTagName("header");
		
		for(int i=0;i<list.getLength();i++){
			Element header = (Element)list.item(i);
			headers.add(new Header(header.getAttribute("name"), header.getAttribute("value")));
		}
		
		return headers;
	}
}
