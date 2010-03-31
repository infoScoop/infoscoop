package org.infoscoop.service;

import java.io.CharArrayWriter;
import java.io.StringReader;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.dao.ProxyConfDAO;
import org.infoscoop.dao.model.Proxyconf;
import org.infoscoop.dao.model.ProxyconfId;
import org.infoscoop.util.Crypt;
import org.infoscoop.util.SpringUtil;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ProxyConfService {

	private static Log log = LogFactory.getLog(ProxyConfService.class);

	private ProxyConfDAO proxyConfDAO;

	public ProxyConfService() {
	}

	public static ProxyConfService getHandle() {
		return (ProxyConfService)SpringUtil.getBean("ProxyConfService");
	}

	public void setProxyConfDAO(ProxyConfDAO proxyConfDAO){
		this.proxyConfDAO = proxyConfDAO;
	}

	/**
	 * @param elementName
	 * @param id
	 * @param cacheLifeTime
	 * @param type
	 * @param pattern
	 * @param replacement
	 * @param host
	 * @param port
	 * @throws Exception
	 */
	public synchronized void addProxyConf(String elementName, String id, String cacheLifeTime,
			String type, String pattern, String replacement, String host,
			String port,Collection<String> headers ) throws Exception {
		if (log.isInfoEnabled()) {
			log.info("addProxyConf: id=" + id + ", cacheLifeTime=" 
					+ cacheLifeTime + ", type=" + type + ", pattern="
					+ pattern + ", replacement=" + replacement + ", host="
					+ host + ", port=" + port);
		}

		try {
			// Obtain data and transfer the result to Document.
			Proxyconf entity = this.proxyConfDAO.select();
			Document document = entity.getElement().getOwnerDocument();

			// Search for top Node.
			Node topNode = null;
			topNode = AdminServiceUtil.getNodeById(document, "/proxy-config",
					null);

			// Error
			if (topNode == null)
				throw new Exception("element not found [/proxy-config]");

			// Search for default
			Node defaultNode = null;
			defaultNode = AdminServiceUtil.getNodeById(document,
					"/proxy-config/default", null);

			// Create the Element to insert.
			if (id == null || id.length() == 0)
				id = String.valueOf(System.currentTimeMillis());
			Element element;
			element = document.createElement(elementName);
			element.setAttribute("id", id);
			if (type != null && type.length() > 0)
				element.setAttribute("type", type);
			if (cacheLifeTime != null && cacheLifeTime.length() > 0)
				element.setAttribute("cacheLifeTime", cacheLifeTime);
			if (pattern != null && pattern.length() > 0)
				element.setAttribute("pattern", pattern);
			if (replacement != null && replacement.length() > 0)
				element.setAttribute("replacement", replacement);
			if (host != null && host.length() > 0)
				element.setAttribute("host", host);
			if (port != null && port.length() > 0)
				element.setAttribute("port", port);

			if ("default".equals(elementName)) {
				// Added to the end of top Node.
				topNode.appendChild(element);
			} else if ("case".equals(elementName)) {
				if (defaultNode != null) {
					// Insert before default
					topNode.insertBefore(element, defaultNode);
				} else {
					// Added to the end of top Node.
					topNode.appendChild(element);
				}
			} else {
				throw new Exception("No elementname");
			}

			if( headers.size() > 0 ) {
				Node headersNode = document.createElement("headers");
				element.appendChild( headersNode );

				for( String header : headers ) {
					Element headerNode = document.createElement("header");
					headerNode.appendChild( document.createTextNode( header ));
					headersNode.appendChild( headerNode );
				}
			}
			entity.setElement(document.getDocumentElement());
			// Update
			proxyConfDAO.update(entity);
		} catch (Exception e) {
			log.error("Unexpected error occurred.", e);
			throw e;
		}
	}

	public synchronized void updateProxyConf( String elementName,String id,Map attrs ) throws Exception {
		for( Iterator keys=attrs.keySet().iterator();keys.hasNext();) {
			String key = ( String )keys.next();
			String value = ( String )attrs.get( key );

			updateProxyConf( elementName,id,key,value );
		}
	}
	/**
	 * @param elementName
	 * @param id
	 * @param attributeName
	 * @param value
	 * @throws Exception
	 */
	public synchronized void updateProxyConf(String elementName, String id,
			String attributeName, String value) throws Exception {

		try {
			// Obtain data and transfer the result to Document.
			Proxyconf entity = this.proxyConfDAO.select();
			Document document = entity.getElement().getOwnerDocument();

			// Search for the Node matches id.
			Node node = null;
			node = AdminServiceUtil.getNodeById(document, "/proxy-config/"
					+ elementName, id);

			// Error
			if (node == null)
				throw new Exception("element not found [/proxy-config/"
						+ elementName + "/@" + id + "]");

			// Cast Node into Element
			Element element = (Element) node;

			if("password".equals(attributeName)){
				value = Crypt.gerCryptInstance().doCrypt(Crypt.ENCRYPT, value);
			}
			// Update attribute
			element.setAttribute(attributeName, value);

			entity.setElement(document.getDocumentElement());
			// Update
			proxyConfDAO.update(entity);
		} catch (Exception e) {
			log.error("Unexpected error occurred.", e);
			throw e;
		}
	}

	public void updateProxyConfHeaders( String id,Collection<String> headers,
			Collection<String> sendingCookies) {
		Proxyconf proxyconf = proxyConfDAO.select();

		try {
			XPath xpath = XPathFactory.newInstance().newXPath();
			Element element = proxyconf.getElement();
			Document document = element.getOwnerDocument();

			Element caseNode = ( Element )xpath.evaluate("*[@id='"+id+"']",element,XPathConstants.NODE );
			if( caseNode == null )
				throw new RuntimeException("proxyConf[@id="+id+"] not found");

			Node headersNode = ( Node )xpath.evaluate("headers",caseNode,XPathConstants.NODE );
			if( headersNode != null )
				caseNode.removeChild( headersNode );

			if( headers != null ) {
				headersNode = document.createElement("headers");
				caseNode.appendChild( headersNode );

				for( String header : headers ) {
					Element headerNode = document.createElement("header");
					headerNode.appendChild( document.createTextNode( header ));
					headersNode.appendChild( headerNode );
				}
			}

			Node sendingCookiesNode = ( Node )xpath.evaluate("sendingcookies",caseNode,XPathConstants.NODE );
			if( sendingCookiesNode != null )
				caseNode.removeChild( sendingCookiesNode );

			if( sendingCookies != null ) {
				sendingCookiesNode = document.createElement("sendingcookies");
				caseNode.appendChild( sendingCookiesNode );

				for( String cookie : sendingCookies ) {
					Element sendingCookieNode = document.createElement("cookie");
					sendingCookieNode.appendChild( document.createTextNode( cookie ));
					sendingCookiesNode.appendChild( sendingCookieNode );
				}
			}
			
			proxyconf.setElement( element );

			proxyConfDAO.update( proxyconf );
		} catch( Exception ex ) {
			throw new RuntimeException( ex );
		}
	}

	/**
	 * @param elementName
	 * @param id
	 * @throws Exception
	 */
	public synchronized void removeProxyConf(String elementName, String id)
			throws Exception {

		try {

			// Obtain data and transfer the result to Document.
			Proxyconf entity = this.proxyConfDAO.select();
			Document document = entity.getElement().getOwnerDocument();

			// Search for node matches id
			Node node = null;
			node = AdminServiceUtil.getNodeById(document, "/proxy-config/"
					+ elementName, id);

			// Error
			if (node == null)
				throw new Exception("element not found [/proxy-config/"
						+ elementName + "/@" + id + "]");


			// Delete node matches id
			AdminServiceUtil.removeSelf(node);

			entity.setElement(document.getDocumentElement());
			// Update
			proxyConfDAO.update(entity);

		} catch (Exception e) {
			log.error("Unexpected error occurred.", e);
			throw e;
		}
	}

	/**
	 * @param id
	 * @param siblingId
	 * @throws Exception
	 */
	public synchronized void replaceSort(String id, String siblingId)
			throws Exception {

		try {

			// Obtain data and transfer the result to Document.
			Proxyconf entity = this.proxyConfDAO.select();
			Document document = entity.getElement().getOwnerDocument();

			// Search for default
			Node defaultNode = AdminServiceUtil.getNodeById(document,
					"/proxy-config/default", null);

			// Search for node matches id
			Node targetNode = AdminServiceUtil.getNodeById(document,
					"/proxy-config/case", id);

			// Error
			if (targetNode == null)
				throw new Exception(
						"targetNode not found [/proxy-config/case/@" + id + "]");

			if (siblingId != null && siblingId.length() != 0) {
				// Search for node matches siblingId
				Node siblingNode = AdminServiceUtil.getNodeById(document,
						"/proxy-config/case", siblingId);

				// Error
				if (siblingNode == null)
					throw new Exception(
							"siblingNode not found [/proxy-config/case/@" + siblingId + "]");

				// Insert before siblingNode
				siblingNode.getParentNode().insertBefore(targetNode,
						siblingNode);
			} else {
				if (defaultNode != null) {
					// Insert before defaultNode
					targetNode.getParentNode().insertBefore(targetNode,
							defaultNode);
				} else {
					// Added at last
					targetNode.getParentNode().appendChild(targetNode);
				}
			}

			entity.setElement(document.getDocumentElement());
			// Update
			proxyConfDAO.update(entity);

		} catch (Exception e) {
			log.error("Unexpected error occurred.", e);
			throw e;
		}
	}

	/**
	 * @return String
	 * @throws Exception
	 */
	public String getProxyConfJson() throws Exception {
		SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
		MakeProxyConfHandler handler = new MakeProxyConfHandler();
		parser.parse(new InputSource(new StringReader(getProxyConf())), handler);
		return handler.getJSONPString();
	}

	/* (Not Javadoc)
	 * @see org.infoscoop.service.IProxyConfService#getLastModifiedDate()
	 */
	public String getLastModifiedDate() throws Exception {

		try {

			// Obtain data
			String result = proxyConfDAO.selectLastModified(ProxyConfDAO.PROXYCONF_FLAG_NOT_TEMP);
			if (result == null || result.length() == 0) {
				log.error("proxyconf-lastmodified not found.");
				return "";
			}

			return result;
		} catch (Exception e) {
			log.error("Unexpected error occurred.", e);
			throw e;
		}
	}

	/* (Not Javadoc)
	 * @see org.infoscoop.service.IProxyConfService#getProxyConfDocument()
	 */
	public Document getProxyConfDocument() throws Exception {
		return AdminServiceUtil.stringToDocument(getProxyConfXml());
	}

	/* (Not Javadoc)
	 * @see org.infoscoop.service.IProxyConfService#getProxyConfInSource()
	 */
	public InputSource getProxyConfInSource() throws Exception {
		return (new InputSource(new StringReader(getProxyConfXml())));
	}

	/* (Not Javadoc)
	 * @see org.infoscoop.service.IProxyConfService#getProxyConfXml()
	 */
	private String getProxyConfXml() throws Exception {

		try {

			// Obtain data
			Proxyconf entity = this.proxyConfDAO.select(ProxyConfDAO.PROXYCONF_FLAG_NOT_TEMP);
			if (entity == null) {
				log.error("proxyconf not found.");
				return "";
			}
			String result = entity.getId().getData();


			return result;
		} catch (Exception e) {
			log.error("Unexpected error occurred.", e);
			throw e;
		}
	}

	/**
	 * For Management Page
	 * @return String
	 * @throws Exception
	 */
	private String getProxyConf() throws Exception {
		try {
			// Obtain data
			Proxyconf entity = this.proxyConfDAO.select(ProxyConfDAO.PROXYCONF_FLAG_NOT_TEMP);
			if (entity == null) {
				log.error("proxyconf not found.");
				return "";
			}
			String result = entity.getId().getData();
			// Overwrite on temporary
			Proxyconf temp = new Proxyconf(new ProxyconfId());
			temp.getId().setTemp(ProxyConfDAO.PROXYCONF_FLAG_TEMP);
			temp.getId().setData(entity.getId().getData());
			this.proxyConfDAO.update(temp);

			return result;
		} catch (Exception e) {
			log.error("Unexpected error occurred.", e);
			throw e;
		}
	}

	/**
	 * Committing temporary data
	 *
	 * @throws Exception
	 */
	public void commitProxyConf() throws Exception {

		try {
			// Obtain data
			Proxyconf tempEntity = this.proxyConfDAO.select(ProxyConfDAO.PROXYCONF_FLAG_TEMP);
			if (tempEntity == null) {
				log.error("temp proxyconf not found.");
				return;
			}
			// Overwrite from temporary
			Proxyconf entity = proxyConfDAO.select(ProxyConfDAO.PROXYCONF_FLAG_NOT_TEMP);
			entity.getId().setData(tempEntity.getId().getData());
			this.proxyConfDAO.update(entity);
		} catch (Exception e) {
			log.error("Unexpected error occurred.", e);
			throw e;
		}
	}

	/**
	 * InnerClass
	 */
	private static class MakeProxyConfHandler extends DefaultHandler{
		private CharArrayWriter buf = new CharArrayWriter();
		private StringBuffer caseArray = new StringBuffer();
		private StringBuffer defaultArray = new StringBuffer();
		boolean firstCaseElement = true;
		boolean firstDefElement = true;
		boolean close = false;
		Stack idStack = new Stack();

		private StringBuffer headersBuf = new StringBuffer();
		private boolean firstHeaderElement = true;

		long start = System.currentTimeMillis();
		public void startDocument() throws SAXException {
			caseArray.append("{");
			defaultArray.append("{");
		}

		public String getJSONPString(){
			return "ISA_ProxyConf.setProxyConf("
					+ caseArray.toString() + ","
					+ defaultArray.toString() + ");";
		}
		public void endDocument() throws SAXException {
			caseArray.append("}");
			defaultArray.append("}");
		}
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			StringBuffer stringbuffer = new StringBuffer();
			buf.reset();
			if(qName.equals("case") || qName.equals("default")){
				idStack.push(qName);

				String caseId = attributes.getValue("id");

				if(	( !firstCaseElement && qName.equals("case"))||
					( !firstDefElement && qName.equals("defualt")) ){
					if(!close){
						stringbuffer.append("}");
					}
					stringbuffer.append(",");
				}

				if( qName.equals("case")) {
					firstCaseElement = false;
				} else {
					firstDefElement = false;
				}
				close = false;

				stringbuffer.append(caseId).append(":");
				stringbuffer.append("{");
				stringbuffer.append("id:").append(JSONObject.quote(caseId));
				String cacheLifeTime = attributes.getValue("cacheLifeTime");
				if(cacheLifeTime != null){
					stringbuffer.append(",cacheLifeTime:").append(JSONObject.quote(cacheLifeTime));
				}
				String pattern = attributes.getValue("pattern");
				if(pattern != null){
					stringbuffer.append(",pattern:").append(JSONObject.quote(pattern));
				}
				String replacement = attributes.getValue("replacement");
				if(replacement != null){
					stringbuffer.append(",replacement:").append(JSONObject.quote(replacement));
				}
				String type = attributes.getValue("type");
				if(type != null){
					stringbuffer.append(",type:").append(JSONObject.quote(type));
				}
				String host = attributes.getValue("host");
				if(host != null){
					stringbuffer.append(",host:").append(JSONObject.quote(host));
				}
				String port = attributes.getValue("port");
				if(port != null){
					stringbuffer.append(",port:").append(JSONObject.quote(port));
				}
				String header = attributes.getValue("header");
				if(header != null){
					stringbuffer.append(",header:").append(JSONObject.quote(header));
				}
				String cheader = attributes.getValue("cookie");
				if(cheader != null){
					stringbuffer.append(",cookie:").append(JSONObject.quote(cheader));
				}
				String username = attributes.getValue("username");
				if(type != null){
					stringbuffer.append(",username:").append(JSONObject.quote(username));
				}
				String password = attributes.getValue("password");
				if(password != null){
					try {
						password = Crypt.gerCryptInstance().doCrypt(Crypt.DECRYPT, password);
					} catch (Exception e) {
						log.error("", e);
					}
					stringbuffer.append(",password:").append(JSONObject.quote(password));
				}
				String domaincontroller = attributes.getValue("domaincontroller");
				if(port != null){
					stringbuffer.append(",domaincontroller:").append(JSONObject.quote(domaincontroller));
				}
				String domain = attributes.getValue("domain");
				if(domain != null){
					stringbuffer.append(",domain:").append(JSONObject.quote(domain));
				}

				appendDivision(stringbuffer);
			} else if("headers".equals( qName )) {
				firstHeaderElement = true;

				headersBuf.append(",headers: [");
			} else if("sendingcookies".equals( qName )){
				firstHeaderElement = true;
				headersBuf.append(",sendingcookies: [");
			}
		}
		public void characters(char[] ch, int start, int length) throws SAXException {
			buf.write(ch, start, length);
		}

		public void endElement(String uri, String localName, String qName) throws SAXException {
			if(qName.equals("case")||qName.equals("default")){
				if( headersBuf.length() > 0 )
					appendDivision( headersBuf.toString());
				headersBuf = new StringBuffer();

				if( !close )
					appendDivision("}");

				close = true;
				idStack.clear();
			} else if( qName.equals("header")||qName.equals("cookie")) {
				if( !firstHeaderElement ) {
					headersBuf.append(",");
				} else {
					firstHeaderElement = false;
				}

				headersBuf.append( JSONObject.quote( buf.toString()));
			} else if( qName.equals("headers")||qName.equals("sendingcookies")) {
				headersBuf.append("]");
			}
			buf.reset();
		}

		/**
		 * @param string
		 */
		private void appendDivision(String string){
			appendDivision(new StringBuffer(string));
		}

		/**
		 * @param stringbuffer
		 */
		private void appendDivision(StringBuffer stringbuffer){
			String peek = "";
			if(!idStack.isEmpty())
				peek = idStack.peek().toString();

			if("case".equals(peek)){
				caseArray.append(stringbuffer);
			}else if("default".equals(peek)){
				defaultArray.append(stringbuffer);
			}
		}

	}

}