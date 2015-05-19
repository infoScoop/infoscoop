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


import org.infoscoop.util.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class WidgetConf {
	private WidgetConfPK id;
	private String data;
	
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	
	public Element getElement() throws SAXException{
		Document doc = (Document) XmlUtil.string2Dom(this.data);
		return doc.getDocumentElement();
	}
	
	public void setElement(Element conf){
		this.data = XmlUtil.dom2String(conf);
	}
	
	public WidgetConfPK getId() {
		return id;
	}
	
	public void setId(WidgetConfPK id) {
		this.id = id;
	}
	
	public boolean equals(Object obj) {
		if(obj instanceof WidgetConf){
			WidgetConf conf = (WidgetConf)obj;
			return this.id.equals(conf.getId());
		}
		return false;
	}
	public int hashCode() {
		return this.id.hashCode();
	}
	

}
