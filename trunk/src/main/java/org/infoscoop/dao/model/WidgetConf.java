package org.infoscoop.dao.model;


import org.infoscoop.util.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class WidgetConf {
	private String type;
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
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public boolean equals(Object obj) {
		if(obj instanceof WidgetConf){
			WidgetConf conf = (WidgetConf)obj;
			return this.type.equals(conf.getType());
		}
		return false;
	}
	public int hashCode() {
		return this.type.hashCode();
	}
	

}
