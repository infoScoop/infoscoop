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

package org.infoscoop.service;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author t.Komatsu
 *
 */
public class AdminServiceUtil {

	private static Log log = LogFactory.getLog(AdminServiceUtil.class);

	/**
	 * convert the String object of the XML form into Document.
	 * 
	 * @param xmlSource
	 * @return Document
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws IOException
	 */
	public static Document stringToDocument(String xmlSource)
			throws SAXException, ParserConfigurationException, IOException {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			return builder.parse(new InputSource(new StringReader(xmlSource)));
		} catch (FactoryConfigurationError e) {
			if(log.isInfoEnabled()){
				log.error("############ ERROR XML SOURCE START ############");
				log.error(xmlSource);
				log.error("############ ERROR XML SOURCE END ############");
			}
			throw e;
		} catch (ParserConfigurationException e) {
			if(log.isInfoEnabled()){
				log.error("############ ERROR XML SOURCE START ############");
				log.error(xmlSource);
				log.error("############ ERROR XML SOURCE END ############");
			}
			throw e;
		} catch (SAXException e) {
			if(log.isInfoEnabled()){
				log.error("############ ERROR XML SOURCE START ############");
				log.error(xmlSource);
				log.error("############ ERROR XML SOURCE END ############");
			}
			throw e;
		} catch (IOException e) {
			if(log.isInfoEnabled()){
				log.error("############ ERROR XML SOURCE START ############");
				log.error(xmlSource);
				log.error("############ ERROR XML SOURCE END ############");
			}
			throw e;
		}
	}
	
    /**
	 * When there is a node with a value appointed by id in a Document, we return it.
	 * When id is not appointed, we return top Node.
	 * 
	 * @param document
	 * @param tagName
	 * @param id
	 * @return Node
	 * @throws Exception
	 */
	public static Node getNodeById(Node node, String xpath, String id)
			throws Exception {
		if (node == null || (xpath == null || xpath.length() == 0))
			return null;

		NodeList nodelist = XPathAPI.selectNodeList(node, xpath);

		for (int i = 0; i < nodelist.getLength(); i++) {
			Node _node = nodelist.item(i);
			if (_node.getNodeType() != Node.ELEMENT_NODE)
				continue;
			Element element = (Element) _node;
			// When id is not appointed, we return top Node.
			if (id == null || id.length() == 0) {
				return (Node) element;
			} else {
				if (id.equals(element.getAttribute("id"))) {
					return (Node) element;
				}
			}
		}

		return null;
	}

	/**
	 * delete a Node which was appointed in an argument.
	 * 
	 * @param node
	 * @return Node
	 */
	public static Node removeSelf(Node node) {
		if (node == null)
			return null;

		Node pNode = node.getParentNode();

		if (pNode != null) {
			return pNode.removeChild(node);
		}

		return null;
	}

}
