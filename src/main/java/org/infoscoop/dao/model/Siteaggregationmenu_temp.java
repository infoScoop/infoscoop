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