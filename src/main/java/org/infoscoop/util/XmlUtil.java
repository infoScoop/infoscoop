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

package org.infoscoop.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * A utility class about the XML.
 * 
 * @author Atsuhiko Kimura
 */
public class XmlUtil {
	/**
	 * substitute "&lt;&gt;&quot;&apos;&amp;" for the reference of the numerical value letter.
	 * @param ch
	 * @return
	 */
	public static String escapeXmlEntities(char ch) {
		switch (ch) {
		case '&':
			return "&#38;";
		case '<':
			return "&#60;";
		case '>':
			return "&#62;";
		case '"':
			return "&#34;";
		case '\'':
			return "&#39;";
		case 0x9:
		case 0xA:
		case 0xD:
			// Effective character string less than 0x20
			return Character.toString(ch);
		default:
			if ((ch >= 0x20 && ch <= 0xd7ff) || (ch >= 0xE000 && ch <= 0xFFFD)
					|| (ch >= 0x10000 && ch <= 0x10FFFF)) {
				//  An arbitrary Unicode letter. But, except for surrogate blocks，"FFFE" and "FFFF".   
				return Character.toString(ch);
			} else {
				// An invalid letter.
				return "";
			}
		}
	}

	/**
	 * substitute "&lt;&gt;&quot;&apos;&amp;" for the reference of the numerical value letter. 
	 * @param str
	 * @return
	 */
	public static String escapeXmlEntities(String str) {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {
			result.append(escapeXmlEntities(str.charAt(i)));
		}
		return result.toString();
	}

	/**
	 * substitute "&amp;lt;&amp;gt;&amp;quot;&amp;amp;&amp;apos;" for the reference of the numerical value letter.
	 * @param str
	 * @return
	 */
	public static String translateNumEntities(String str) {
		Map map = new HashMap();
		map.put("&amp;", "&#38;");
		map.put("&lt;", "&#60;");
		map.put("&gt;", "&#62;");
		map.put("&quot;", "&#34;");
		map.put("&apos;", "&#39;");
		return StringUtil.replaceMap(str, map);
	}

	/**
	 * Obtain text from XML by deleting tags
	 * @param xml
	 * @return
	 */
	public static String removeTags(String xml) {
		String tmp = xml;
		StringBuffer result = new StringBuffer();
		int index1 = 0;
		int index2 = 0;
		while (true) {
			index2 = tmp.indexOf("<", index1);
			if (index2 < 0)
				index2 = tmp.length();
			result.append(tmp.substring(index1, index2));
			index1 = tmp.indexOf(">", index2);
			if (index1 < 0)
				break;
			index1++;
		}
		return result.toString();
	}

	/**
	 * convert the reference of the numerical value letter into ths substance.
	 * @param xml
	 * @return
	 */
	public static String resolveNumEntities(String xml) {
		StringBuffer strb = new StringBuffer(xml);

		int index;
		int index2 = 0;
		int index3 = 0;

		while ((index = strb.toString().indexOf("&#", index2)) != -1) {
			index2 = strb.toString().indexOf(';', index + 1);
			index3 = strb.toString().indexOf("&#", index + 1);
			if (index2 == -1) {
				break;
			} else if (index3 != -1 && index2 > index3) {
				//We pass the entity description that is not right.
				index2 = index3;
			} else {
				try {
					char numericChar = (char) Integer.parseInt(strb.substring(
							index + 2, index2));
					String numericStr = String.valueOf(numericChar);
					strb.replace(index, index2 + 1, numericStr);
					index2 = index + numericStr.length();
				} catch (NumberFormatException e) {
					//We pass reference of the numerical value letter that is not right.
				}
			}
		}

		return strb.toString();
	}

	/**
	 * We get a object that connected text node of the child node and the CDATA section as character string.
	 * @param node
	 * @return
	 */
	public static String getChildText(Node node) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < node.getChildNodes().getLength(); i++) {
			Node n = node.getChildNodes().item(i);
			if (n.getNodeType() == Node.TEXT_NODE
					|| n.getNodeType() == Node.CDATA_SECTION_NODE) {
				buf.append(n.getNodeValue());
			}
		}
		return buf.toString();
	}

	/**
	 * convert node object into String object.
	 * 
	 * @param node
	 * @return String
	 */
	public static String dom2String(Node node) {
		return dom2String(node, false);
	}

	/**
	 * convert node object into String object.
	 * @param node
	 * @param omitXmlDecl
	 * @return
	 */
	public static String dom2String(Node node, boolean omitXmlDecl) {
		Source source = new DOMSource(node);
		StringWriter stringWriter = new StringWriter();
		Result result = new StreamResult(stringWriter);
		try{
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();
			if (omitXmlDecl)
				transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
						"yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.transform(source, result);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return stringWriter.getBuffer().toString();
	}
	
	public static Document string2Dom(String xml) throws SAXException{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			return builder.parse(new InputSource(new StringReader(xml)));
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static Document stream2Dom(InputStream xml) {
		DocumentBuilder builder;
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			return builder.parse(xml);
		} catch (Exception e) {
			throw new RuntimeException("parsing error", e);
		}
	}
	
	/**
	 * serialize a DOM element
	 * @param el DOM element
	 * @return XML String
	 * @throws IOException
	 */
	public static String xmlSerialize(org.w3c.dom.Element el) throws IOException {

		OutputFormat format = new OutputFormat(el.getOwnerDocument());
		StringWriter stringOut = new StringWriter();
		XMLSerializer serial = new XMLSerializer(stringOut, format);
		serial.asDOMSerializer();
		serial.serialize(el);

		return stringOut.toString();
	}
}
