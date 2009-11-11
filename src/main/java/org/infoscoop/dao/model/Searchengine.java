package org.infoscoop.dao.model;


import org.infoscoop.dao.model.base.BaseSearchengine;
import org.infoscoop.util.XmlUtil;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;



public class Searchengine extends BaseSearchengine {
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public Searchengine () {
		super();
	}

	/**
	 * Constructor for required fields
	 */
	public Searchengine (
		java.lang.Integer temp,
		java.lang.String data) {

		super (
			temp,
			data);
	}

	/*[CONSTRUCTOR MARKER END]*/
	
	public Document getDocument() throws SAXException {
		Document doc = (Document) XmlUtil.string2Dom(super.getData());
		
		return doc;
	}

	public void setDocument(Document doc) {
		super.setData(XmlUtil.dom2String(doc));
	}

}