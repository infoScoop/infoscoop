package org.infoscoop.dao.model;


import org.infoscoop.dao.model.base.BaseSiteaggregationmenu;
import org.infoscoop.util.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;



public class Siteaggregationmenu extends BaseSiteaggregationmenu {
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public Siteaggregationmenu () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public Siteaggregationmenu (java.lang.String type) {
		super(type);
	}

	/**
	 * Constructor for required fields
	 */
	public Siteaggregationmenu (
		java.lang.String type,
		java.lang.String data) {

		super (
			type,
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