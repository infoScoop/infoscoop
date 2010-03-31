package org.infoscoop.dao.model;


import org.infoscoop.dao.model.base.BaseProxyconf;
import org.infoscoop.util.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;



public class Proxyconf extends BaseProxyconf {
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public Proxyconf () {
		super();
	}

	/**
	 * Constructor for required fields
	 */
	public Proxyconf (
		java.lang.Integer temp,
		java.lang.String data) {

		super (
			temp,
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