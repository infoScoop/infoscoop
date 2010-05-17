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
	public Preference (java.lang.String id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public Preference (
		java.lang.String id,
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